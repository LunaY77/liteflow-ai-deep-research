package com.lunay.deepresearch.node.researcher;

import cn.hutool.core.collection.CollectionUtil;
import com.lunay.deepresearch.context.ResearchContext;
import com.lunay.deepresearch.tool.ToolExecutor;
import com.yomahub.liteflow.ai.engine.model.chat.message.AssistantMessage;
import com.yomahub.liteflow.ai.engine.model.chat.message.Message;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ResearcherAgent 工具执行节点
 * 一定在 ResearcherAgent 之后执行，用于手动执行对应的工具
 *
 * @author 苍镜月
 */

@LiteflowComponent("researcherToolNode")
@Slf4j
public class ResearcherToolNode extends NodeBooleanComponent {

    @Override
    public boolean processBoolean() throws Exception {
        ResearchContext context = this.getContextBean(ResearchContext.class);
        Integer researchIterations = context.getResearchIterations();
        AssistantMessage lastMessage = context.getLastResearcherAssistantMessage();
        log.info("进入 ResearcherAgent 工具执行节点, 当前是第 {} 轮research", researchIterations);

        // 判断是否达到结束条件
        // 1. 超过最大迭代次数 2. 没有更多工具可用 3. 使用了 researchCompleteTool 工具
        boolean exceededMaxIterations = researchIterations >= context.getMaxResearchIterations();
        boolean noMoreTools = CollectionUtil.isEmpty(lastMessage.getToolCalls());
        boolean researchCompleted = lastMessage.getToolCalls()
                .stream()
                .anyMatch(toolCall -> "researchCompleteTool".equals(toolCall.getName()));
        if (exceededMaxIterations || noMoreTools || researchCompleted) {
            log.info("达到结束条件，结束搜索。");
            return false;
        }

        // 2. 执行工具
        log.info("执行工具，共有 {} 个工具调用", lastMessage.getToolCalls().size());
//        // 并行执行所有工具
//        CompletableFuture<List<Message>> researchFutures = lastMessage.getToolCalls().stream()
//                .map(toolCall -> CompletableFuture.supplyAsync(() -> ToolExecutor.executeTool(toolCall, context.getToolRegistry())))
//                .collect(Collectors.collectingAndThen(Collectors.toList(), futures -> {
//                    CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//                    return allOf.thenApply(v -> futures.stream()
//                            .map(CompletableFuture::join)
//                            .collect(Collectors.toList()));
//                }));
//
//        List<Message> toolResults = researchFutures.get();
        // 串行执行
        List<Message> toolResults = lastMessage.getToolCalls().stream()
                .map(toolCall -> ToolExecutor.executeTool(toolCall, context.getToolRegistry()))
                .collect(Collectors.toList());
        log.info("工具执行完成，共获得 {} 条工具结果", toolResults.size());

        // 3. 将工具结果加入到上下文中
        context.addResearcherMessages(toolResults);
        context.incrementResearchIterations();
        return true;
    }
}
