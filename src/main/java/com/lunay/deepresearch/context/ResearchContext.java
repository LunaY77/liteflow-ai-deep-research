package com.lunay.deepresearch.context;

import com.yomahub.liteflow.ai.context.ChatContext;
import com.yomahub.liteflow.ai.engine.model.chat.message.AssistantMessage;
import com.yomahub.liteflow.ai.engine.model.chat.message.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * ResearchChain 上下文
 *
 * @author 苍镜月
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResearchContext extends ChatContext {

    private Date date;

    private String toolCallId;

    private String toolCallName;

    private String researchTopic;

    private Integer maxResearchIterations;

    private Integer researchIterations;

    private List<Message> researcherMessages = new ArrayList<>();

    private String compressedResult;

    /**
     * 添加一条 Researcher 消息
     *
     * @param message 消息
     */
    public void addResearcherMessage(Message message) {
        this.researcherMessages.add(message);
    }

    /**
     * 添加多条 Researcher 消息
     *
     * @param messages 多条消息
     */
    public void addResearcherMessages(List<Message> messages) {
        this.researcherMessages.addAll(messages);
    }

    /**
     * 获取最后一条 AssistantMessage 类型的 Researcher 消息
     *
     * @return 最后一条 AssistantMessage 类型的 Researcher 消息，如果没有则报错
     */
    public AssistantMessage getLastResearcherAssistantMessage() {
        for (int i = researcherMessages.size() - 1; i >= 0; i--) {
            Message message = researcherMessages.get(i);
            if (message instanceof AssistantMessage) {
                return (AssistantMessage) message;
            }
        }
        throw new RuntimeException("No AssistantMessage found in researcherMessages");
    }

    public void incrementResearchIterations() {
        if (Objects.isNull(this.researchIterations)) {
            this.researchIterations = 0;
        }
        this.researchIterations++;
    }
}
