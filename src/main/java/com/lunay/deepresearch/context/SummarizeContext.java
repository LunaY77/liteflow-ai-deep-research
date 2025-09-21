package com.lunay.deepresearch.context;

import com.lunay.deepresearch.domain.dto.WebSummaryDto;
import com.yomahub.liteflow.ai.context.ChatContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Web Search总结上下文
 *
 * @author 苍镜月
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class SummarizeContext extends ChatContext {

    private Date date;

    private String url;

    private String rawContent;

    private WebSummaryDto webSummaryDto;
}
