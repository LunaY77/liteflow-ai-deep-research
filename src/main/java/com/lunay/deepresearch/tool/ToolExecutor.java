package com.lunay.deepresearch.tool;

import com.yomahub.liteflow.ai.engine.model.chat.message.ToolMessage;
import com.yomahub.liteflow.ai.engine.tool.ToolCall;
import com.yomahub.liteflow.ai.engine.tool.ToolCallBack;
import com.yomahub.liteflow.ai.engine.tool.registry.ToolRegistry;

import java.util.Objects;

/**
 * 工具执行器
 *
 * @author 苍镜月
 */

public class ToolExecutor {

    /**
     * 执行工具
     *
     * @param toolCall     工具调用信息
     * @param toolRegistry 工具注册表
     * @return 工具调用结果消息
     */
    public static ToolMessage executeTool(ToolCall toolCall, ToolRegistry toolRegistry) {
        // 找到对应的工具回调
        ToolCallBack toolCallBack = toolRegistry.getAllTools()
                .stream()
                .filter(tool -> Objects.equals(tool.getName(), toolCall.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("工具未注册: " + toolCall.getName()));
        // 调用工具
        String toolResult = toolCallBack.call(toolCall.getArguments().toString());
        // 返回工具调用结果
        return new ToolMessage(toolResult, toolCall.getId(), toolCall.getName());
    }
}
