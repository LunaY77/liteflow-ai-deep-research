package com.lunay.deepresearch.node;

import com.yomahub.liteflow.ai.annotation.AIComponent;
import com.yomahub.liteflow.ai.annotation.model.io.AIOutput;
import com.yomahub.liteflow.ai.annotation.model.io.OutputField;
import com.yomahub.liteflow.ai.annotation.model.node.AIChat;
import com.yomahub.liteflow.ai.engine.interact.transport.TransportType;
import com.yomahub.liteflow.ai.engine.model.output.ResponseType;
import com.yomahub.liteflow.ai.util.TriState;

/**
 * 报告生成 Agent
 *
 * @author 苍镜月
 */

@AIComponent(
        nodeId = "reportAgent",
        nodeName = "reportAgentNode",
        provider = "openai",
        apiUrl = "https://ark.cn-beijing.volces.com/api/v3",
        model = "deepseek-v3-1-250821",
        enableThinking = TriState.TRUE,
        readTimeout = "10m",
        connectTimeout = "10m"
)
@AIChat(
        userPrompt = "classpath:deepresearch/final_report_prompt.txt",
        streaming = false,
        transportType = TransportType.HTTP
)
@AIOutput(
        // 如果输出格式为 TEXT，那么最终输出对象为 AssistantMessage, 将其 Content 字段映射到 AgentContext 的 finalReport 字段
        responseType = ResponseType.TEXT,
        mapping = {
                @OutputField(sourceField = "content", methodExpress = "setFinalReport")
        }
)
public interface ReportAgent {
}
