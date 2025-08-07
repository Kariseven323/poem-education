// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "b4a42fdc-2d03-4831-9d30-9278970f029a"
//   Timestamp: "2025-08-07T12:00:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Service接口设计最佳实践"
//   Quality_Check: "编译通过，接口定义完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service;

import com.poem.education.dto.request.CommentRequest;
import com.poem.education.dto.response.CommentDTO;
import com.poem.education.dto.response.PageResult;

import java.util.List;

/**
 * 评论服务接口
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public interface CommentService {
    
    /**
     * 获取评论列表（支持层级嵌套）
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param page 页码
     * @param size 每页大小
     * @return 评论分页列表
     */
    PageResult<CommentDTO> getCommentsByTarget(String targetId, String targetType, Integer page, Integer size);
    
    /**
     * 发表评论
     * 
     * @param userId 用户ID
     * @param request 评论请求
     * @return 评论详情
     */
    CommentDTO createComment(Long userId, CommentRequest request);
    
    /**
     * 根据ID获取评论详情
     * 
     * @param id 评论ID
     * @return 评论详情
     */
    CommentDTO getCommentById(String id);
    
    /**
     * 删除评论
     * 
     * @param id 评论ID
     * @param userId 用户ID
     */
    void deleteComment(String id, Long userId);
    
    /**
     * 点赞评论
     * 
     * @param id 评论ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean likeComment(String id, Long userId);
    
    /**
     * 取消点赞评论
     * 
     * @param id 评论ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean unlikeComment(String id, Long userId);
    
    /**
     * 获取用户的评论列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论分页列表
     */
    PageResult<CommentDTO> getUserComments(Long userId, Integer page, Integer size);
    
    /**
     * 获取热门评论
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param limit 限制数量
     * @return 热门评论列表
     */
    List<CommentDTO> getHotComments(String targetId, String targetType, Integer limit);
    
    /**
     * 获取最新评论
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param limit 限制数量
     * @return 最新评论列表
     */
    List<CommentDTO> getLatestComments(String targetId, String targetType, Integer limit);
    
    /**
     * 统计评论数量
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 评论数量
     */
    long getCommentCount(String targetId, String targetType);
    
    /**
     * 构建评论树结构
     * 
     * @param comments 评论列表
     * @return 树形结构的评论列表
     */
    List<CommentDTO> buildCommentTree(List<CommentDTO> comments);
    
    /**
     * 计算评论路径
     * 
     * @param parentId 父评论ID
     * @return 评论路径
     */
    String calculateCommentPath(String parentId);
    
    /**
     * 更新回复数量
     * 
     * @param parentId 父评论ID
     * @param increment 增量（1或-1）
     */
    void updateReplyCount(String parentId, int increment);
    
    /**
     * 更新点赞数量
     * 
     * @param commentId 评论ID
     * @param increment 增量（1或-1）
     */
    void updateLikeCount(String commentId, int increment);
}
// {{END_MODIFICATIONS}}
