package com.lunay.deepresearch.tool;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.lunay.deepresearch.context.SummarizeContext;
import com.lunay.deepresearch.domain.tool.ConductResearchToolParam;
import com.lunay.deepresearch.domain.tool.ThinkToolParam;
import com.lunay.deepresearch.domain.tool.WebSearchToolParam;
import com.lunay.deepresearch.util.TavilyApiClient;
import com.yomahub.liteflow.ai.engine.tool.annotation.Tool;
import com.yomahub.liteflow.ai.engine.tool.annotation.ToolParam;
import com.yomahub.liteflow.core.FlowExecutor;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DeepResearch 相关工具
 *
 * @author 苍镜月
 */

@Component
@RequiredArgsConstructor
public class DeepResearchAgentTool {

    private final TavilyApiClient tavilyApiClient;

    @Resource
    private FlowExecutor flowExecutor;

    @Tool(name = "thinkTool", value = """
            研究进展和决策制定的战略反思工具。
            
            在每次搜索后使用此工具分析结果并系统地规划下一步。
            这在研究工作流程中创建了一个深思熟虑的暂停，以便做出质量决策。
            
            使用时机：
            - 收到搜索结果后：我找到了什么关键信息？
            - 决定下一步之前：我是否有足够信息全面回答？
            - 评估研究差距时：我还缺少什么具体信息？
            - 结束研究前：我现在能否提供完整答案？
            
            反思应该涵盖：
            1. 当前发现分析 - 我收集了什么具体信息？
            2. 差距评估 - 还缺少什么关键信息？
            3. 质量评估 - 我是否有足够的证据/例子来给出好答案？
            4. 战略决策 - 我应该继续搜索还是提供答案？
            """)
    public String thinkTool(@ToolParam("关于研究进展、发现、差距和下一步的详细反思") ThinkToolParam thinkToolParam) {
        return "Reflection recorded: " + thinkToolParam.getReflection();
    }

    @Tool(name = "conductResearchTool", value = "调用此工具对特定主题进行研究的")
    public void conductResearchTool(@ToolParam("要研究的主题。应该是单一主题，并且应该被详细描述（至少一段话）。") ConductResearchToolParam conductResearchToolParam) {
        // 空实现
    }

    @Tool(name = "researchCompleteTool", value = "调用此工具表示研究已经完成，可以生成最终报告")
    public void researchCompleteTool() {
        // 空实现
    }

    @SneakyThrows
    @Tool(name = "webSearchTool", value = {"一个为提供全面、准确、可信的搜索结果而优化的搜索引擎", "当您需要回答关于时事的问题时，它会非常有用。"})
    public String webSearchTool(@ToolParam("搜索查询，应该是一个简短的句子或短语") WebSearchToolParam webSearchToolParam) {
        TavilyApiClient.TavilyResponse response = tavilyApiClient.search(
                TavilyApiClient.TavilyRequest.builder()
                        .query(webSearchToolParam.getQuery())
                        .includeAnswer(true)
                        .includeRawContent(true)
                        .build()
        );

        if (Objects.isNull(response) || CollectionUtil.isEmpty(response.getResults())) {
            return "No valid search results found. Please try a different search query";
        }

        LinkedHashMap<String, TavilyApiClient.TavilyResponse.Result> uniqueResults = response.getResults().stream()
                .collect(Collectors.toMap(
                        TavilyApiClient.TavilyResponse.Result::getUrl,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        List<CompletableFuture<SummarizedResult>> futures = uniqueResults.values().stream()
                .map(result -> CompletableFuture.supplyAsync(() -> {
                    if (StrUtil.isNotBlank(result.getRawContent())) {
                        String summary = summarizeWebContent(result);
                        return new SummarizedResult(result.getUrl(), result.getTitle(), summary);
                    } else {
                        return new SummarizedResult(result.getUrl(), result.getTitle(), result.getContent());
                    }
                }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<SummarizedResult> summarizedResults = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        if (summarizedResults.isEmpty()) {
            return "No valid search results found. Please try a different search query";
        }

        StringBuilder sb = new StringBuilder("Search Results: \n\n");
        int index = 1;
        for (SummarizedResult res : summarizedResults) {
            sb.append(String.format("\n\n--- SOURCE %d: %s ---\n", index++, res.title));
            sb.append(String.format("URL: %s\n\n", res.url));
            sb.append(String.format("SUMMARY:\n%s\n\n", res.content));
            sb.append("\n\n").append(String.join("", Collections.nCopies(80, "-"))).append("\n");
        }

        return sb.toString();
    }

    private String summarizeWebContent(TavilyApiClient.TavilyResponse.Result result) {
        SummarizeContext summarizeContext = new SummarizeContext();
        summarizeContext.setDate(new Date());
        summarizeContext.setUrl(result.getUrl());
        summarizeContext.setRawContent(result.getRawContent().length() > 90000 ? result.getRawContent().substring(0, 90000) : result.getRawContent());
        flowExecutor.execute2Resp("webSummaryAgent", null, summarizeContext);
        return String.format("<summary>\n%s\n</summary>\n\n<key_excerpts>\n%s\n</key_excerpts>\n\n",
                summarizeContext.getWebSummaryDto().getSummary(),
                summarizeContext.getWebSummaryDto().getKeyExcerpts()
        );
    }

    @RequiredArgsConstructor
    @Getter
    private static class SummarizedResult {
        final String url;
        final String title;
        final String content;
    }
}
