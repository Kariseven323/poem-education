// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "11ae4c98-4ca6-4e38-a76f-6770f885c723"
//   Timestamp: "2025-08-08T14:00:15+08:00"
//   Authoring_Subagent: "PM-标准协作模式"
//   Principle_Applied: "Service接口设计最佳实践，遵循现有模式"
//   Quality_Check: "编译通过，接口定义完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service;

import com.poem.education.dto.request.CreationRequest;
import com.poem.education.dto.response.CreationDTO;
import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.RadarDataDTO;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

/**
 * 创作服务接口
 * 处理诗词创作相关业务逻辑
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
public interface CreationService {
    
    /**
     * 创建新的诗词创作
     * 
     * @param userId 用户ID
     * @param request 创作请求
     * @return 创作详情
     */
    CreationDTO createCreation(Long userId, CreationRequest request);
    
    /**
     * 根据ID获取创作详情
     * 
     * @param id 创作ID
     * @return 创作详情
     */
    CreationDTO getCreationById(String id);
    
    /**
     * 获取用户创作列表（分页查询）
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param style 风格（可选）
     * @param status 状态（可选）
     * @return 创作分页列表
     */
    PageResult<CreationDTO> getUserCreations(Long userId, Integer page, Integer size, String style, Integer status);
    
    /**
     * 获取公开创作列表（分页查询）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param style 风格（可选）
     * @return 创作分页列表
     */
    PageResult<CreationDTO> getPublicCreations(Integer page, Integer size, String style);
    
    /**
     * 更新创作信息
     * 
     * @param userId 用户ID
     * @param id 创作ID
     * @param request 更新请求
     * @return 更新后的创作详情
     */
    CreationDTO updateCreation(Long userId, String id, CreationRequest request);
    
    /**
     * 删除创作
     * 
     * @param userId 用户ID
     * @param id 创作ID
     */
    void deleteCreation(Long userId, String id);
    
    /**
     * 触发AI评分（异步）
     * 
     * @param userId 用户ID
     * @param id 创作ID
     * @return 异步结果
     */
    @Async
    CompletableFuture<Boolean> requestAIScore(Long userId, String id);
    
    /**
     * 获取雷达图数据
     * 
     * @param id 创作ID
     * @return 雷达图数据
     */
    RadarDataDTO getRadarData(String id);
    
    /**
     * 切换创作公开状态
     * 
     * @param userId 用户ID
     * @param id 创作ID
     * @param isPublic 是否公开
     * @return 更新后的创作详情
     */
    CreationDTO togglePublicStatus(Long userId, String id, Boolean isPublic);
    
    /**
     * 点赞/取消点赞创作
     * 
     * @param userId 用户ID
     * @param id 创作ID
     * @return 更新后的创作详情
     */
    CreationDTO toggleLike(Long userId, String id);
    
    /**
     * 搜索创作
     * 
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @param style 风格（可选）
     * @return 创作分页列表
     */
    PageResult<CreationDTO> searchCreations(String keyword, Integer page, Integer size, String style);
}
// {{END_MODIFICATIONS}}
