// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "cbae72cf-b030-48a2-b381-ce2a1484c281"
//   Timestamp: "2025-08-07T12:10:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Service接口设计最佳实践"
//   Quality_Check: "编译通过，接口定义完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service;

import com.poem.education.dto.request.UserActionRequest;
import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.UserActionDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户行为服务接口
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public interface UserActionService {
    
    /**
     * 记录用户行为
     * 
     * @param userId 用户ID
     * @param request 行为请求
     * @return 行为记录
     */
    UserActionDTO recordAction(Long userId, UserActionRequest request);
    
    /**
     * 取消用户行为
     * 
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param actionType 行为类型
     * @return 是否成功
     */
    boolean cancelAction(Long userId, String targetId, String targetType, String actionType);
    
    /**
     * 检查用户是否已执行某行为
     * 
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param actionType 行为类型
     * @return 是否已执行
     */
    boolean hasAction(Long userId, String targetId, String targetType, String actionType);
    
    /**
     * 获取用户行为列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 行为分页列表
     */
    PageResult<UserActionDTO> getUserActions(Long userId, Integer page, Integer size);
    
    /**
     * 根据行为类型获取用户行为列表
     * 
     * @param userId 用户ID
     * @param actionType 行为类型
     * @param page 页码
     * @param size 每页大小
     * @return 行为分页列表
     */
    PageResult<UserActionDTO> getUserActionsByType(Long userId, String actionType, Integer page, Integer size);
    
    /**
     * 获取目标的行为统计
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 行为统计信息
     */
    Object getTargetActionStats(String targetId, String targetType);
    
    /**
     * 获取用户在指定时间范围内的行为
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param size 每页大小
     * @return 行为分页列表
     */
    PageResult<UserActionDTO> getUserActionsByTimeRange(Long userId, LocalDateTime startTime, 
                                                       LocalDateTime endTime, Integer page, Integer size);
    
    /**
     * 获取热门内容（按点赞数排序）
     * 
     * @param targetType 目标类型
     * @param limit 限制数量
     * @return 热门内容列表
     */
    List<Object> getHotContentByLikes(String targetType, Integer limit);
    
    /**
     * 获取用户最近的行为
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近行为列表
     */
    List<UserActionDTO> getRecentActions(Long userId, Integer limit);
    
    /**
     * 批量记录用户行为
     * 
     * @param userId 用户ID
     * @param requests 行为请求列表
     * @return 记录的行为列表
     */
    List<UserActionDTO> batchRecordActions(Long userId, List<UserActionRequest> requests);
    
    /**
     * 统计用户行为数量
     * 
     * @param userId 用户ID
     * @param actionType 行为类型（可选）
     * @return 行为数量
     */
    long countUserActions(Long userId, String actionType);
    
    /**
     * 统计目标的行为数量
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param actionType 行为类型
     * @return 行为数量
     */
    long countTargetActions(String targetId, String targetType, String actionType);
    
    /**
     * 删除用户的所有行为记录
     * 
     * @param userId 用户ID
     * @return 删除的记录数量
     */
    long deleteUserActions(Long userId);
}
// {{END_MODIFICATIONS}}
