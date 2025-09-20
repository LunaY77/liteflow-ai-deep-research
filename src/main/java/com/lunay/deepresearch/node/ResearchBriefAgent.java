package com.lunay.deepresearch.node;

import com.yomahub.liteflow.ai.annotation.AIComponent;
import com.yomahub.liteflow.ai.annotation.model.io.AIOutput;
import com.yomahub.liteflow.ai.annotation.model.node.AIChat;
import com.yomahub.liteflow.ai.engine.interact.transport.TransportType;
import com.yomahub.liteflow.ai.engine.model.output.ResponseType;
import com.yomahub.liteflow.ai.util.TriState;

/**
 * 研究简报 Agent
 * 当 ClarifyAgentNode 确认问题清晰后，生成一个简要的研究简报，描述研究的背景、目的和意义
 *
 * @author 苍镜月
 */

@AIComponent(
        nodeId = "researchBriefAgent",
        nodeName = "ResearchBriefAgentNode",
        provider = "dashscope",
        apiUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1",
        model = "qwen-flash",
        enableThinking = TriState.TRUE
)
@AIChat(
        systemPrompt = "classpath:deepresearch/research_brief_system_prompt.txt",
        userPrompt = "{{clarifyDto.verification}}",
        streaming = false,
        transportType = TransportType.HTTP
)
@AIOutput(
        responseType = ResponseType.JSON,
        typeName = "com.lunay.deepresearch.domain.dto.ResearchQuestionDto",
        methodExpress = "researchQuestionDto"
)
public interface ResearchBriefAgent {
}
