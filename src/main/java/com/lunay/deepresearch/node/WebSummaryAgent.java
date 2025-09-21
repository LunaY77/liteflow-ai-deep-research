package com.lunay.deepresearch.node;

import com.yomahub.liteflow.ai.annotation.AIComponent;
import com.yomahub.liteflow.ai.annotation.model.io.AIOutput;
import com.yomahub.liteflow.ai.annotation.model.node.AIChat;
import com.yomahub.liteflow.ai.engine.interact.transport.TransportType;
import com.yomahub.liteflow.ai.engine.model.output.ResponseType;
import com.yomahub.liteflow.ai.util.TriState;

/**
 * WebSummary Agent
 *
 * @author 苍镜月
 */

@AIComponent(
        nodeId = "webSummaryAgent",
        nodeName = "webSummaryAgentNode",
        provider = "openai",
        apiUrl = "https://ark.cn-beijing.volces.com/api/v3",
        model = "deepseek-v3-1-250821",
        enableThinking = TriState.FALSE,
        readTimeout = "10m",
        connectTimeout = "10m"
)
@AIChat(
        userPrompt = "classpath:deepresearch/summarize_webpage_prompt.txt",
        streaming = false,
        transportType = TransportType.HTTP
)
@AIOutput(
        responseType = ResponseType.JSON,
        typeName = "com.lunay.deepresearch.domain.dto.WebSummaryDto",
        methodExpress = "setWebSummaryDto"
)
public interface WebSummaryAgent {
}
