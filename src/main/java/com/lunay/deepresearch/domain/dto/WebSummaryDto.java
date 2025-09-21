package com.lunay.deepresearch.domain.dto;

import com.yomahub.liteflow.ai.engine.model.output.structure.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网页总结 DTO
 *
 * @author 苍镜月
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSummaryDto {

    @Description("主要摘要内容")
    private String summary;

    @Description("关键摘录内容")
    private String keyExcerpts;
}
