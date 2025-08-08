// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "ba11a9d3-6ea1-4b0b-b3d9-86fc30515df8"
//   Timestamp: "2025-08-08T13:45:20+08:00"
//   Authoring_Subagent: "PM-标准协作模式"
//   Principle_Applied: "Service层最佳实践，异步处理和错误处理"
//   Quality_Check: "编译通过，异步调用和Mock模式完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service;

import com.poem.education.entity.mongodb.Creation;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

/**
 * AI评分服务接口
 * 负责调用外部AI模型API进行诗词评分
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
public interface AIScoreService {
    
    /**
     * 调用AI模型进行诗词评分
     * 同步方法，用于需要立即获取结果的场景
     * 
     * @param title 诗词标题
     * @param content 诗词内容
     * @param style 诗词风格
     * @return AI评分结果
     */
    Creation.AiScore callAIModel(String title, String content, String style);
    
    /**
     * 异步调用AI模型进行诗词评分
     * 用于不阻塞主线程的场景
     * 
     * @param title 诗词标题
     * @param content 诗词内容
     * @param style 诗词风格
     * @return 异步AI评分结果
     */
    @Async
    CompletableFuture<Creation.AiScore> callAIModelAsync(String title, String content, String style);
    
    /**
     * 生成Mock评分数据
     * 用于开发测试环境
     * 
     * @param title 诗词标题
     * @param content 诗词内容
     * @param style 诗词风格
     * @return Mock评分结果
     */
    Creation.AiScore generateMockScore(String title, String content, String style);
    
    /**
     * 检查AI服务是否可用
     * 
     * @return 服务可用性状态
     */
    boolean isServiceAvailable();

    /**
     * 生成AI修改建议（同步）
     * 根据作品标题、内容、风格给出可执行的修改建议，便于作者改进作品
     *
     * @param title  标题
     * @param content 内容
     * @param style 风格
     * @return 修改建议文本（纯文本/Markdown）
     */
    String generateRevisionSuggestions(String title, String content, String style);

    /**
     * 解析含<think>...</think>的模型输出，提取可展示的正文与思考过程
     */
    default ParsedAdvice parseAdviceWithThinking(String raw) {
        if (raw == null || raw.isEmpty()) return new ParsedAdvice("", null);
        String thinking = null;
        String content = raw;
        // 支持多层<think>嵌套，逐步剥离
        StringBuilder allThinking = new StringBuilder();
        String remaining = raw;
        while (remaining.contains("<think>") && remaining.contains("</think>")) {
            int s = remaining.indexOf("<think>") + 7;
            int e = remaining.indexOf("</think>");
            if (s - 7 < 0 || e <= s) break;
            String segment = remaining.substring(s, e).trim();
            if (!segment.isEmpty()) {
                if (allThinking.length() > 0) allThinking.append("\n\n");
                allThinking.append(segment);
            }
            remaining = remaining.substring(0, s - 7) + remaining.substring(e + 8);
        }
        content = remaining.trim();
        thinking = allThinking.length() > 0 ? allThinking.toString() : null;
        return new ParsedAdvice(content, thinking);
    }

    class ParsedAdvice {
        public final String content;
        public final String thinking;
        public ParsedAdvice(String content, String thinking) {
            this.content = content;
            this.thinking = thinking;
        }
    }

    /**
     * 生成AI修改建议（异步）
     */
    @Async
    CompletableFuture<String> generateRevisionSuggestionsAsync(String title, String content, String style);
}
// {{END_MODIFICATIONS}}
