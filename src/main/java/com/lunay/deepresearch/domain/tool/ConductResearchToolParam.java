package com.lunay.deepresearch.domain.tool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ConductResearchTool 工具参数
 * <p>
 * !!!注意：对于工具调用的参数，即使只存在一个基本类型的参数，也最好将其包装为一个参数类，否则可能会导致反序列化失败!!!
 *
 * @author 苍镜月
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConductResearchToolParam {

    private String researchTopic;
}
