package com.lunay.deepresearch.node.researcher;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import com.yomahub.liteflow.core.NodeComponent;

/**
 * ResearcherAgent 工具执行节点
 * 一定在 ResearcherAgent 之后执行，用于手动执行对应的工具
 *
 * @author 苍镜月
 */

@LiteflowComponent("researcherToolNode")
public class ResearcherToolNode extends NodeBooleanComponent {

    @Override
    public boolean processBoolean() throws Exception {
        return false;
    }
}
