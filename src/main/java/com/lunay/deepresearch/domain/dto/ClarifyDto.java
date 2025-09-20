package com.lunay.deepresearch.domain.dto;

import com.yomahub.liteflow.ai.engine.model.output.structure.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clarify 信息，由 ClarifyAgentNode 输出
 *
 * @author 苍镜月
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClarifyDto {

    @Description("是否需要澄清")
    private boolean needClarify;

    @Description("澄清问题，向用户确认需要补充的信息")
    private String question;

    @Description("确认消息，用户提供必要信息后，回复该消息")
    private String verification;

}
