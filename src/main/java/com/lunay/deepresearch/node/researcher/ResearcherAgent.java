package com.lunay.deepresearch.node.researcher;

import com.yomahub.liteflow.ai.annotation.AIComponent;
import com.yomahub.liteflow.ai.annotation.model.io.AIOutput;
import com.yomahub.liteflow.ai.annotation.model.node.AIChat;
import com.yomahub.liteflow.ai.engine.interact.transport.TransportType;
import com.yomahub.liteflow.ai.engine.model.output.ResponseType;
import com.yomahub.liteflow.ai.util.TriState;

/**
 * ResearchAgent，由 SupervisorAgent 委派，执行具体的研究任务
 *
 * @author 苍镜月
 */

@AIComponent(
        nodeId = "researcherAgent",
        nodeName = "researcherAgentNode",
        provider = "openai",
        apiUrl = "https://ark.cn-beijing.volces.com/api/v3",
        model = "deepseek-r1-250528",
        enableThinking = TriState.TRUE,
        // 关闭自动工具调用，在 ResearcherToolNode 中手动调用工具
        autoToolCallEnabled = TriState.FALSE,
        readTimeout = "10m",
        connectTimeout = "10m"
)
@AIChat(
        // 这里使用 上下文对象(List<Message>) 构建提示词，启用后将关闭 systemPrompt 和 userPrompt
        history = "researcherMessages",
        streaming = false,
        transportType = TransportType.HTTP,
        toolNames = {"thinkTool", "webSearchTool", "researchCompleteTool"}
)
@AIOutput(
        // 如果输出格式为 TEXT，那么最终输出对象为 AssistantMessage, 将其添加到 ResearcherMessage 列表中
        responseType = ResponseType.TEXT,
        methodExpress = "addResearcherMessage"
)
public interface ResearcherAgent {
}
