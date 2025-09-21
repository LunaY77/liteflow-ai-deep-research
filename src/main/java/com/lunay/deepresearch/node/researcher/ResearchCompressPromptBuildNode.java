package com.lunay.deepresearch.node.researcher;

import com.lunay.deepresearch.context.ResearchContext;
import com.yomahub.liteflow.ai.engine.model.chat.message.UserMessage;
import com.yomahub.liteflow.ai.parse.prompt.PromptTemplateParser;
import com.yomahub.liteflow.ai.parse.prompt.loader.DefaultPromptResourceLoader;
import com.yomahub.liteflow.ai.parse.prompt.loader.PromptResourceLoader;
import com.yomahub.liteflow.ai.parse.prompt.resource.PromptResource;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

/**
 * ResearchCompressPrompt 构建节点
 *
 * @author 苍镜月
 */

@LiteflowComponent("researchCompressPromptBuildNode")
public class ResearchCompressPromptBuildNode extends NodeComponent {

    @Override
    public void process() throws Exception {
        ResearchContext context = this.getContextBean(ResearchContext.class);

        PromptResourceLoader loader = new DefaultPromptResourceLoader();
        PromptResource resource = loader.getResource("classpath:deepresearch/research_compress_system_prompt.txt");
        String prompt = PromptTemplateParser.parseTemplate(resource.getContent(), null, this);

        UserMessage userMessage = new UserMessage(prompt);
        context.addResearcherMessage(userMessage);
    }
}
