package com.lunay.deepresearch.node;

import com.lunay.deepresearch.context.AgentContext;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import lombok.extern.slf4j.Slf4j;

/**
 * 判断是否需要澄清的路由节点
 *
 * @author 苍镜月
 */

@LiteflowComponent("clarifyRouter")
@Slf4j
public class ClarifyRouterNode extends NodeBooleanComponent {

    @Override
    public boolean processBoolean() {
        AgentContext context = this.getContextBean(AgentContext.class);
        boolean needClarify = context.getClarifyDto().isNeedClarify();
        log.info("ClarifyRouterNode: needClarify = {}", needClarify);
        return !needClarify;
    }
}
