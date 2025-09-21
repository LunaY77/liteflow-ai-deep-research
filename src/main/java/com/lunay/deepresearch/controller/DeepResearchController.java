package com.lunay.deepresearch.controller;

import com.lunay.deepresearch.context.AgentContext;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Deep Research API Controller
 */

@Slf4j
@RestController()
@RequestMapping("/deep-research")
public class DeepResearchController {

    @Resource
    private FlowExecutor flowExecutor;

    @Value("${deepresearch.maxResearchIterations}")
    private Integer maxResearchIterations;

    @Value("${deepresearch.maxConcurrentResearchTasks}")
    private Integer maxConcurrentResearchTasks;

    @GetMapping()
    public String query(@RequestParam String query) {
        AgentContext context = new AgentContext();
        context.setQuestion(query);
        context.setDate(new Date());
        context.setMaxResearchIterations(maxResearchIterations);
        context.setMaxConcurrentResearchTasks(maxConcurrentResearchTasks);
        LiteflowResponse response = flowExecutor.execute2Resp("deepResearchChain", null, context);
        return response.getContextBean(AgentContext.class).getFinalReport();
    }
}