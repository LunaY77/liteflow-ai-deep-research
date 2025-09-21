package com.lunay.deepresearch.node.researcher;

import com.lunay.deepresearch.context.ResearchContext;
import com.yomahub.liteflow.ai.engine.model.chat.message.SystemMessage;
import com.yomahub.liteflow.ai.engine.model.chat.message.UserMessage;
import com.yomahub.liteflow.ai.parse.prompt.PromptTemplateParser;
import com.yomahub.liteflow.ai.parse.prompt.loader.DefaultPromptResourceLoader;
import com.yomahub.liteflow.ai.parse.prompt.loader.PromptResourceLoader;
import com.yomahub.liteflow.ai.parse.prompt.resource.PromptResource;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.beans.factory.annotation.Value;

/**
 * Researcher Prompt 构建节点
 *
 * @author 苍镜月
 */

@LiteflowComponent("researcherPromptBuildNode")
public class ResearcherPromptBuildNode extends NodeComponent {

    @Value("${deepresearch.maxResearchIterations}")
    private Integer maxResearchIterations;

    @Override
    public void process() throws Exception {
        ResearchContext context = this.getContextBean(ResearchContext.class);
        String researchTopic = context.getResearchTopic();

        PromptResourceLoader loader = new DefaultPromptResourceLoader();
        PromptResource resource = loader.getResource("classpath:deepresearch/research_system_prompt.txt");
        String systemPrompt = PromptTemplateParser.parseTemplate(resource.getContent(), null, this);

        SystemMessage systemMessage = new SystemMessage(systemPrompt);
        UserMessage userMessage = new UserMessage(researchTopic);

        context.addResearcherMessage(systemMessage);
        context.addResearcherMessage(userMessage);
        context.setMaxResearchIterations(maxResearchIterations);
        context.setResearchIterations(0);
    }
}
