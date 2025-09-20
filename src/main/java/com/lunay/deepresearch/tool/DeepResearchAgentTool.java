package com.lunay.deepresearch.tool;

import com.lunay.deepresearch.util.TavilyApiClient;
import com.yomahub.liteflow.ai.engine.tool.annotation.Tool;
import com.yomahub.liteflow.ai.engine.tool.annotation.ToolParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * DeepResearch 相关工具
 *
 * @author 苍镜月
 */

@Component
@RequiredArgsConstructor
public class DeepResearchAgentTool {

    private final TavilyApiClient tavilyApiClient;

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
    public String thinkTool(@ToolParam("关于研究进展、发现、差距和下一步的详细反思") String reflection) {
        return "Reflection recorded: " + reflection;
    }

    @Tool(name = "conductResearchTool", value = "调用此工具对特定主题进行研究的")
    public void conductResearchTool(@ToolParam("要研究的主题。应该是单一主题，并且应该被详细描述（至少一段话）。") String researchTopic) {
        // 空实现
    }

    @Tool(name = "researchCompleteTool", value = "调用此工具表示研究已经完成，可以生成最终报告")
    public void researchCompleteTool() {
        // 空实现
    }

    @Tool(name = "webSearchTool", value = {"一个为提供全面、准确、可信的搜索结果而优化的搜索引擎", "当您需要回答关于时事的问题时，它会非常有用。"})
    public String webSearchTool(@ToolParam("搜索查询，应该是一个简短的句子或短语") String query) {
        return "...";
    }
}
