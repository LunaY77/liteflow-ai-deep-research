package com.lunay.deepresearch.context;

import com.yomahub.liteflow.ai.context.ChatContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

    private String toolCallId;

    private String toolCallName;

    private String researchTopic;

    private String compressedResult;
}
