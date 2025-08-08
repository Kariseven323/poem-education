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
}
// {{END_MODIFICATIONS}}
