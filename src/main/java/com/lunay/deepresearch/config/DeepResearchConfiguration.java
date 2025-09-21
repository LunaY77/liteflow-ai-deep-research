package com.lunay.deepresearch.config;

import com.yomahub.liteflow.ai.context.StreamHandler;
import com.yomahub.liteflow.ai.engine.model.chat.message.AssistantMessage;
import com.yomahub.liteflow.ai.engine.tool.ToolCall;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DeepResearch 相关配置
 *
 * @author 苍镜月
 */

@Configuration
public class DeepResearchConfiguration {

    @Bean
    public StreamHandler streamHandler() {
        return StreamHandler.builder()
                .onStart(context -> System.out.println("chat start"))
                .onClose(context -> System.out.println("chat close"))
                // 只进行阻塞式调用，故不处理 onText 和 onThinking 等流式回调
                .onCompletion((response, context) -> {
                    AssistantMessage message = response.getOutput();
                    if (message.getContent() != null && !message.getContent().trim().isEmpty()) {
                        System.out.println("内容长度: " + message.getContent().length());
                        if (message.getContent().length() > 200) {
                            System.out.println("内容预览: " + message.getContent().substring(0, 200) + "...");
                        } else {
                            System.out.println("内容: " + message.getContent());
                        }
                    }
                    if (response.hasToolCalls()) {
                        System.out.println("工具调用数量: " + message.getToolCalls().size());
                        for (int i = 0; i < message.getToolCalls().size(); i++) {
                            ToolCall toolCall = message.getToolCalls().get(i);
                            System.out.println("工具调用 " + (i + 1) + ":");
                            System.out.println("  ID: " + toolCall.getId());
                            System.out.println("  名称: " + toolCall.getName());
                            System.out.println("  类型: " + toolCall.getType());
                            System.out.println("  参数: " + toolCall.getArguments());
                        }
                    }
                    System.out.println("Token使用情况: " + response.getTokenUsage());
                    System.out.println("完成原因: " + response.getFinishReason());
                    return response;
                })
                .build();
    }
}
