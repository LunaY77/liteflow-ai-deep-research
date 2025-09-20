package com.lunay.deepresearch.node.supervisor;

import cn.hutool.core.collection.CollectionUtil;
import com.lunay.deepresearch.context.AgentContext;
import com.lunay.deepresearch.context.ResearchContext;
import com.yomahub.liteflow.ai.engine.model.chat.message.AssistantMessage;
import com.yomahub.liteflow.ai.engine.model.chat.message.Message;
import com.yomahub.liteflow.ai.engine.model.chat.message.ToolMessage;
import com.yomahub.liteflow.ai.engine.tool.ToolCall;
import com.yomahub.liteflow.ai.engine.tool.ToolCallBack;
import com.yomahub.liteflow.ai.engine.tool.registry.ToolRegistry;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * SupervisorAgent 工具执行节点
 * 一定在 SupervisorAgent 之后执行，用于手动执行对应的工具
 *
 * @author 苍镜月
 */

@LiteflowComponent("supervisorToolNode")
public class SupervisorToolNode extends NodeBooleanComponent {

    @Resource
    private FlowExecutor flowExecutor;

    @Override
    public boolean processBoolean() throws Exception {
        AgentContext context = this.getContextBean(AgentContext.class);
        Integer researchIteration = context.getResearchIterations();
        // 获取最后一条 AssistantMessage 类型的 Supervisor 消息, 即 SupervisorAgent 的最新回复
        AssistantMessage lastMessage = context.getLastSupervisorAssistantMessage();

        // 判断是否达到结束条件
        // 1. 超过最大迭代次数 2. 没有更多工具可用 3. 使用了 researchCompleteTool 工具
        boolean exceededMaxIterations = researchIteration >= context.getMaxResearchIterations();
        boolean noMoreTools = CollectionUtil.isEmpty(lastMessage.getToolCalls());
        boolean researchCompleted = lastMessage.getToolCalls()
                .stream()
                .anyMatch(toolCall -> "researchCompleteTool".equals(toolCall.getName()));
        if (exceededMaxIterations || noMoreTools || researchCompleted) {
            return false;
        }

        // 2. 执行工具
        List<Message> toolMessages = new ArrayList<>();

        // 执行 thinkTool 工具
        lastMessage.getToolCalls()
                .stream()
                .filter(toolCall -> "thinkTool".equals(toolCall.getName()))
                .forEach(toolCall -> {
                    ToolMessage toolMessage = executeTool(toolCall, context.getToolRegistry());
                    toolMessages.add(toolMessage);
                });

        // 执行 conductResearchTool 工具
        List<ToolCall> researchCalls = lastMessage.getToolCalls()
                .stream()
                .filter(toolCall -> "conductResearchTool".equals(toolCall.getName()))
                .toList();

        if (!researchCalls.isEmpty()) {
            // 限制最大并发研究任务数
            List<ToolCall> limitedResearchCalls = researchCalls.stream()
                    .limit(context.getMaxConcurrentResearchTasks())
                    .toList();
            List<ToolCall> overflowResearchCalls = researchCalls.stream()
                    .skip(context.getMaxConcurrentResearchTasks())
                    .toList();

            // 执行范围内的研究任务
            CompletableFuture<List<ResearchContext>> researchFutures = limitedResearchCalls.stream()
                    .map(toolCall -> CompletableFuture.supplyAsync(() -> conductResearch(toolCall)))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), futures -> {
                        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                        return allOf.thenApply(v -> futures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList()));
                    }));

            List<ResearchContext> researchResults = researchFutures.get();
            researchResults.forEach(researchContext -> {
                ToolMessage toolMessage = new ToolMessage(
                        researchContext.getCompressedResult(),
                        researchContext.getToolCallId(),
                        researchContext.getToolCallName());
                toolMessages.add(toolMessage);
            });

            // 对于超出并发限制的研究任务，添加提示信息
            overflowResearchCalls.forEach(toolCall -> {
                toolMessages.add(new ToolMessage(
                        "研究任务过多，当前仅支持同时进行 " + context.getMaxConcurrentResearchTasks() + " 个研究任务，" +
                                "请稍后在下一轮迭代中继续进行该研究任务。",
                        toolCall.getId(),
                        toolCall.getName()
                ));
            });

            // TODO
        }

        // 3. 将工具执行结果添加到上下文中，供下一轮对话使用
        context.addSupervisorMessage(toolMessages);
        context.incrementResearchIterations();
        return true;
    }

    /**
     * 调用 ResearchChain 进行研究
     *
     * @param toolCall 工具调用信息
     * @return 研究上下文
     */
    private ResearchContext conductResearch(ToolCall toolCall) {
        ResearchContext researchContext = new ResearchContext();
        researchContext.setToolCallId(toolCall.getId());
        researchContext.setToolCallName(toolCall.getName());
        researchContext.setResearchTopic(toolCall.getArguments().toString());
        flowExecutor.execute2Resp("researchChain", null, researchContext);
        return researchContext;
    }

    /**
     * 执行工具
     *
     * @param toolCall     工具调用信息
     * @param toolRegistry 工具注册表
     * @return 工具调用结果消息
     */
    private ToolMessage executeTool(ToolCall toolCall, ToolRegistry toolRegistry) {
        // 找到对应的工具回调
        ToolCallBack toolCallBack = toolRegistry.getAllTools()
                .stream()
                .filter(tool -> Objects.equals(tool.getName(), toolCall.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("工具未注册: " + toolCall.getName()));
        // 调用工具
        String toolResult = toolCallBack.call(toolCall.getArguments().toString());
        // 返回工具调用结果
        return new ToolMessage(toolResult, toolCall.getId(), toolCall.getName());
    }
}
