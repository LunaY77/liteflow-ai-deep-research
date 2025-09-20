package com.lunay.deepresearch.node;

import com.lunay.deepresearch.context.AgentContext;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

/**
 * 响应节点
 *
 * @author 苍镜月
 */

@LiteflowComponent("response")
public class ResponseNode extends NodeComponent {

    @Override
    public void process() throws Exception {
        AgentContext context = this.getContextBean(AgentContext.class);
        System.out.println(context.getFinalReport());
    }
}
