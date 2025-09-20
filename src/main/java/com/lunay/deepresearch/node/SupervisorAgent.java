package com.lunay.deepresearch.node;

import com.yomahub.liteflow.ai.annotation.AIComponent;
import com.yomahub.liteflow.ai.annotation.model.io.AIOutput;
import com.yomahub.liteflow.ai.annotation.model.node.AIChat;
import com.yomahub.liteflow.ai.engine.interact.transport.TransportType;
import com.yomahub.liteflow.ai.engine.model.output.ResponseType;
import com.yomahub.liteflow.ai.util.TriState;

/**
 * SupervisorAgent
 *
 * @author 苍镜月
 */

@AIComponent(
        nodeId = "supervisorAgent",
        nodeName = "supervisorAgentNode",
        provider = "dashscope",
        apiUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1",
        model = "qwen-plus",
        enableThinking = TriState.TRUE,
        // 关闭自动工具调用，在 SupervisorToolNode 中手动调用工具
        autoToolCallEnabled = TriState.FALSE
)
@AIChat(
        // 这里使用 上下文对象(List<Message>) 构建提示词，启用后将关闭 systemPrompt 和 userPrompt
        history = "supervisorMessages",
        streaming = false,
        transportType = TransportType.HTTP,
        toolNames = {"thinkTool", "conductResearchTool", "researchCompleteTool"}
)
@AIOutput(
        // 如果输出格式为 TEXT，那么最终输出对象为 AssistantMessage, 将其添加到 SupervisorMessage 列表中
        responseType = ResponseType.TEXT,
        methodExpress = "addSupervisorMessage"
)
public interface SupervisorAgent {
}
