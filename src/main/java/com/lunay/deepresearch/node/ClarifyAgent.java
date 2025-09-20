package com.lunay.deepresearch.node;

import com.yomahub.liteflow.ai.annotation.AIComponent;
import com.yomahub.liteflow.ai.annotation.model.io.AIOutput;
import com.yomahub.liteflow.ai.annotation.model.io.OutputField;
import com.yomahub.liteflow.ai.annotation.model.node.AIChat;
import com.yomahub.liteflow.ai.engine.interact.transport.TransportType;
import com.yomahub.liteflow.ai.engine.model.output.ResponseType;
import com.yomahub.liteflow.ai.util.TriState;

/**
 * 该 Agent 用于澄清用户问题
 * 如果问题不清晰，生成一个澄清问题，向用户确认需要补充的信息，并在 ClarifyRouterNode 中直接结束流程
 *
 * @author 苍镜月
 */

@AIComponent(
        nodeId = "clarifyAgent",
        nodeName = "ClarifyAgentNode",
        provider = "dashscope",
        apiUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1",
        model = "qwen-flash",
        enableThinking = TriState.FALSE
)
@AIChat(
        systemPrompt = "classpath:deepresearch/clarify_system_prompt.txt",
        userPrompt = "{{question}}",
        streaming = false,
        transportType = TransportType.HTTP
)
@AIOutput(
        responseType = ResponseType.JSON,
        typeName = "com.lunay.deepresearch.domain.dto.ClarifyDto",
        methodExpress = "clarifyDto",
        mapping = {
                // 将 question 字段映射到 finalReport 字段，目的是如果需要用户澄清问题，那么直接结束流程，输出最终报告
                @OutputField(sourceField = "question", methodExpress = "setFinalReport")
        }
)
public interface ClarifyAgent {
}
