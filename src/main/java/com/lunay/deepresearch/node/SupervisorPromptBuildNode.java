package com.lunay.deepresearch.node;

import com.lunay.deepresearch.context.AgentContext;
import com.lunay.deepresearch.domain.dto.ResearchQuestionDto;
import com.yomahub.liteflow.ai.engine.model.chat.message.SystemMessage;
import com.yomahub.liteflow.ai.engine.model.chat.message.UserMessage;
import com.yomahub.liteflow.ai.parse.prompt.PromptTemplateParser;
import com.yomahub.liteflow.ai.parse.prompt.loader.DefaultPromptResourceLoader;
import com.yomahub.liteflow.ai.parse.prompt.loader.PromptResourceLoader;
import com.yomahub.liteflow.ai.parse.prompt.resource.PromptResource;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

import java.util.List;

/**
 * SupervisorAgent 提示词构建
 *
 * @author 苍镜月
 */

@LiteflowComponent("supervisorPromptBuildNode")
public class SupervisorPromptBuildNode extends NodeComponent {

    @Override
    public void process() throws Exception {
        AgentContext context = this.getContextBean(AgentContext.class);
        ResearchQuestionDto researchQuestionDto = context.getResearchQuestionDto();
        String researchBrief = researchQuestionDto.getResearchBrief();

        PromptResourceLoader loader = new DefaultPromptResourceLoader();
        PromptResource resource = loader.getResource("classpath:deepresearch/supervisor_system_prompt.txt");
        String systemPrompt = PromptTemplateParser.parseTemplate(resource.getContent(), null, this);

        SystemMessage systemMessage = new SystemMessage(systemPrompt);
        UserMessage userMessage = new UserMessage(researchBrief);

        context.setSupervisorMessages(List.of(systemMessage, userMessage));
    }
}
