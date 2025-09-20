package com.lunay.deepresearch.domain.dto;

import com.yomahub.liteflow.ai.engine.model.output.structure.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 研究问题, 由 ResearchBriefAgentNode 输出
 *
 * @author 苍镜月
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResearchQuestionDto {

    @Description("研究简报，简要描述研究的背景、目的和意义")
    private String researchBrief;
}
