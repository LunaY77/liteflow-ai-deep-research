package com.lunay.deepresearch.context;

import com.lunay.deepresearch.domain.dto.ClarifyDto;
import com.lunay.deepresearch.domain.dto.ResearchQuestionDto;
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
 * DeepResearch Agent 上下文
 *
 * @author 苍镜月
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AgentContext extends ChatContext {

    // ========== start ==========
    private String question;

    private Date date;

    private Integer maxResearchIterations;

    private Integer maxConcurrentResearchTasks;
    // ========== start ==========

    // ========== clarifyAgent ==========
    private ClarifyDto clarifyDto;
    // ========== clarifyAgent ==========

    // ========== researchBriefAgent ==========
    private ResearchQuestionDto researchQuestionDto;
    // ========== researchBriefAgent ==========

    // ========== supervisorChain ==========
    private List<Message> supervisorMessages = new ArrayList<>();

    private Integer researchIterations;

    private String notes;
    // ========== supervisorChain ==========

    private String finalReport;

    /**
     * 添加一条 Supervisor 消息
     *
     * @param message 消息
     */
    public void addSupervisorMessage(Message message) {
        this.supervisorMessages.add(message);
    }

    /**
     * 添加多条 Supervisor 消息
     *
     * @param messages 消息列表
     */
    public void addSupervisorMessages(List<Message> messages) {
        this.supervisorMessages.addAll(messages);
    }

    /**
     * 获取最后一条 AssistantMessage 类型的 Supervisor 消息
     *
     * @return 最后一条 AssistantMessage 类型的 Supervisor 消息，如果没有则报错
     */
    public AssistantMessage getLastSupervisorAssistantMessage() {
        for (int i = supervisorMessages.size() - 1; i >= 0; i--) {
            Message message = supervisorMessages.get(i);
            if (message instanceof AssistantMessage) {
                return (AssistantMessage) message;
            }
        }
        throw new RuntimeException("No AssistantMessage found in supervisorMessages");
    }

    public void incrementResearchIterations() {
        if (Objects.isNull(this.researchIterations)) {
            this.researchIterations = 0;
        }
        this.researchIterations++;
    }
}
