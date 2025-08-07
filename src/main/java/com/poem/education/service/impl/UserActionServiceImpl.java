// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "cbae72cf-b030-48a2-b381-ce2a1484c281"
//   Timestamp: "2025-08-07T12:10:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Service实现最佳实践，严格按照数据库表结构"
//   Quality_Check: "编译通过，业务逻辑完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service.impl;

import com.poem.education.dto.request.UserActionRequest;
import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.UserActionDTO;
import com.poem.education.entity.mysql.UserAction;
import com.poem.education.exception.BusinessException;
import com.poem.education.repository.mysql.UserActionRepository;
import com.poem.education.service.UserActionService;
import com.poem.education.constant.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户行为服务实现类
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Service
@Transactional(readOnly = true)
public class UserActionServiceImpl implements UserActionService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserActionServiceImpl.class);
    
    @Autowired
    private UserActionRepository userActionRepository;
    
    @Override
    @Transactional
    public UserActionDTO recordAction(Long userId, UserActionRequest request) {
        logger.info("记录用户行为: userId={}, request={}", userId, request);
        
        // 检查是否已存在相同的行为记录
        Optional<UserAction> existingAction = userActionRepository
                .findByUserIdAndTargetIdAndTargetTypeAndActionType(
                        userId, request.getTargetId(), request.getTargetType(), request.getActionType());
        
        if (existingAction.isPresent()) {
            // 如果已存在，返回现有记录
            return convertToDTO(existingAction.get());
        }
        
        // 创建新的行为记录
        UserAction userAction = new UserAction();
        userAction.setUserId(userId);
        userAction.setTargetId(request.getTargetId());
        userAction.setTargetType(request.getTargetType());
        userAction.setActionType(request.getActionType());
        
        UserAction savedAction = userActionRepository.save(userAction);
        
        return convertToDTO(savedAction);
    }
    
    @Override
    @Transactional
    public boolean cancelAction(Long userId, String targetId, String targetType, String actionType) {
        logger.info("取消用户行为: userId={}, targetId={}, targetType={}, actionType={}", 
                   userId, targetId, targetType, actionType);
        
        Optional<UserAction> actionOptional = userActionRepository
                .findByUserIdAndTargetIdAndTargetTypeAndActionType(userId, targetId, targetType, actionType);
        
        if (actionOptional.isPresent()) {
            userActionRepository.delete(actionOptional.get());
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean hasAction(Long userId, String targetId, String targetType, String actionType) {
        return userActionRepository.existsByUserIdAndTargetIdAndTargetTypeAndActionType(
                userId, targetId, targetType, actionType);
    }
    
    @Override
    public PageResult<UserActionDTO> getUserActions(Long userId, Integer page, Integer size) {
        logger.info("获取用户行为列表: userId={}, page={}, size={}", userId, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserAction> actionPage = userActionRepository.findByUserId(userId, pageable);
        
        List<UserActionDTO> actionDTOList = actionPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(actionDTOList, page, size, actionPage.getTotalElements());
    }
    
    @Override
    public PageResult<UserActionDTO> getUserActionsByType(Long userId, String actionType, Integer page, Integer size) {
        logger.info("根据行为类型获取用户行为: userId={}, actionType={}, page={}, size={}", 
                   userId, actionType, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserAction> actionPage = userActionRepository.findByUserIdAndActionType(userId, actionType, pageable);
        
        List<UserActionDTO> actionDTOList = actionPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(actionDTOList, page, size, actionPage.getTotalElements());
    }
    
    @Override
    public Object getTargetActionStats(String targetId, String targetType) {
        logger.info("获取目标行为统计: targetId={}, targetType={}", targetId, targetType);
        
        // 统计各种行为类型的数量
        long likeCount = userActionRepository.countByTargetIdAndTargetTypeAndActionType(
                targetId, targetType, UserAction.ActionType.LIKE);
        long favoriteCount = userActionRepository.countByTargetIdAndTargetTypeAndActionType(
                targetId, targetType, UserAction.ActionType.FAVORITE);
        long viewCount = userActionRepository.countByTargetIdAndTargetTypeAndActionType(
                targetId, targetType, UserAction.ActionType.VIEW);
        long shareCount = userActionRepository.countByTargetIdAndTargetTypeAndActionType(
                targetId, targetType, UserAction.ActionType.SHARE);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("targetId", targetId);
        stats.put("targetType", targetType);
        stats.put("likeCount", likeCount);
        stats.put("favoriteCount", favoriteCount);
        stats.put("viewCount", viewCount);
        stats.put("shareCount", shareCount);
        stats.put("totalCount", likeCount + favoriteCount + viewCount + shareCount);
        
        return stats;
    }
    
    @Override
    public PageResult<UserActionDTO> getUserActionsByTimeRange(Long userId, LocalDateTime startTime, 
                                                              LocalDateTime endTime, Integer page, Integer size) {
        logger.info("获取时间范围内用户行为: userId={}, startTime={}, endTime={}, page={}, size={}", 
                   userId, startTime, endTime, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserAction> actionPage = userActionRepository.findByUserIdAndCreatedAtBetween(
                userId, startTime, endTime, pageable);
        
        List<UserActionDTO> actionDTOList = actionPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(actionDTOList, page, size, actionPage.getTotalElements());
    }
    
    @Override
    public List<Object> getHotContentByLikes(String targetType, Integer limit) {
        logger.info("获取热门内容: targetType={}, limit={}", targetType, limit);

        // 使用Repository中的方法获取热门内容
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(30); // 最近30天

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = userActionRepository.findHotContentsByAction(
                targetType, UserAction.ActionType.LIKE, startTime, endTime, pageable);

        return results.stream()
                .map(result -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("targetId", result[0]);
                    item.put("likeCount", result[1]);
                    return item;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserActionDTO> getRecentActions(Long userId, Integer limit) {
        logger.info("获取用户最近行为: userId={}, limit={}", userId, limit);
        
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserAction> actionPage = userActionRepository.findByUserId(userId, pageable);
        
        return actionPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public List<UserActionDTO> batchRecordActions(Long userId, List<UserActionRequest> requests) {
        logger.info("批量记录用户行为: userId={}, count={}", userId, requests.size());
        
        return requests.stream()
                .map(request -> recordAction(userId, request))
                .collect(Collectors.toList());
    }
    
    @Override
    public long countUserActions(Long userId, String actionType) {
        if (actionType != null) {
            return userActionRepository.countByUserIdAndActionType(userId, actionType);
        } else {
            return userActionRepository.countByUserId(userId);
        }
    }
    
    @Override
    public long countTargetActions(String targetId, String targetType, String actionType) {
        return userActionRepository.countByTargetIdAndTargetTypeAndActionType(targetId, targetType, actionType);
    }
    
    @Override
    @Transactional
    public long deleteUserActions(Long userId) {
        logger.info("删除用户所有行为记录: userId={}", userId);
        
        return userActionRepository.deleteByUserId(userId);
    }
    
    /**
     * 将UserAction实体转换为UserActionDTO
     * 
     * @param userAction UserAction实体
     * @return UserActionDTO
     */
    private UserActionDTO convertToDTO(UserAction userAction) {
        UserActionDTO dto = new UserActionDTO();
        BeanUtils.copyProperties(userAction, dto);
        return dto;
    }
}
// {{END_MODIFICATIONS}}
