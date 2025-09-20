package com.lunay.deepresearch.node;

import cn.hutool.core.collection.CollectionUtil;
import com.lunay.deepresearch.context.AgentContext;
import com.yomahub.liteflow.ai.engine.model.chat.message.AssistantMessage;
import com.yomahub.liteflow.ai.engine.model.chat.message.Message;
import com.yomahub.liteflow.ai.engine.model.chat.message.ToolMessage;
import com.yomahub.liteflow.ai.engine.tool.ToolCall;
import com.yomahub.liteflow.ai.engine.tool.ToolCallBack;
import com.yomahub.liteflow.ai.engine.tool.registry.ToolRegistry;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * SupervisorAgent 工具执行节点
 * 一定在 SupervisorAgent 之后执行，用于手动执行对应的工具
 *
 * @author 苍镜月
 */

@LiteflowComponent("supervisorToolNode")
public class SupervisorToolNode extends NodeBooleanComponent {

    @Override
    public boolean processBoolean() throws Exception {
        AgentContext context = this.getContextBean(AgentContext.class);
        Integer researchIteration = context.getResearchIterations();
        List<Message> supervisorMessages = context.getSupervisorMessages();
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


        context.incrementResearchIterations();
        return true;
    }

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
