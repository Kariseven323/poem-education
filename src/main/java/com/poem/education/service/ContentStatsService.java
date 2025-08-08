// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "2442c79c-d034-48f6-a1ec-206b15c6976c"
//   Timestamp: "2025-08-08T16:42:37+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Service接口设计最佳实践"
//   Quality_Check: "编译通过，接口定义完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service;

import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

/**
 * 内容统计服务接口
 * 负责内容统计数据的更新和管理
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
public interface ContentStatsService {
    
    /**
     * 异步更新内容统计数据
     * 根据行为类型更新对应的统计字段
     * 
     * @param contentId 内容ID (MongoDB ObjectId)
     * @param contentType 内容类型 (guwen/sentence/writer/creation)
     * @param actionType 行为类型 (view/like/favorite/comment/share)
     * @return 异步更新结果
     */
    @Async
    CompletableFuture<Boolean> updateContentStats(String contentId, String contentType, String actionType);
    
    /**
     * 同步更新内容统计数据
     * 用于需要立即获取结果的场景
     * 
     * @param contentId 内容ID (MongoDB ObjectId)
     * @param contentType 内容类型 (guwen/sentence/writer/creation)
     * @param actionType 行为类型 (view/like/favorite/comment/share)
     * @return 更新是否成功
     */
    boolean updateContentStatsSync(String contentId, String contentType, String actionType);
    
    /**
     * 批量异步更新内容统计数据
     * 用于批量处理多个统计更新
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @param actionTypes 行为类型列表
     * @return 异步更新结果
     */
    @Async
    CompletableFuture<Boolean> batchUpdateContentStats(String contentId, String contentType, String[] actionTypes);
    
    /**
     * 增量更新内容统计数据
     * 支持自定义增量值
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @param actionType 行为类型
     * @param increment 增量值
     * @return 更新是否成功
     */
    boolean incrementContentStats(String contentId, String contentType, String actionType, Long increment);
    
    /**
     * 确保内容统计记录存在
     * 如果不存在则创建默认记录
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 是否成功创建或已存在
     */
    boolean ensureContentStatsExists(String contentId, String contentType);
    
    /**
     * 重置内容统计数据
     * 将所有统计字段重置为0
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 重置是否成功
     */
    boolean resetContentStats(String contentId, String contentType);
}
// {{END_MODIFICATIONS}}
