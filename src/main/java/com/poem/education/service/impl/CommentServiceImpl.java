package com.poem.education.service.impl;

import com.poem.education.dto.request.CommentRequest;
import com.poem.education.dto.response.CommentDTO;
import com.poem.education.dto.response.PageResult;
import com.poem.education.entity.mongodb.Comment;
import com.poem.education.entity.mysql.User;
import com.poem.education.exception.BusinessException;
import com.poem.education.repository.mongodb.CommentRepository;
import com.poem.education.repository.mysql.UserRepository;
import com.poem.education.service.CommentService;
import com.poem.education.service.ContentStatsService;
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
import org.springframework.util.StringUtils;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContentStatsService contentStatsService;
    
    @Override
    @Transactional
    public CommentDTO createComment(Long userId, CommentRequest request) {
        logger.info("=== CommentService.createComment 开始 ===");
        logger.info("输入参数: userId={}, request={}", userId, request);
        
        // 验证用户是否存在
        logger.info("验证用户是否存在: userId={}", userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            logger.error("用户不存在: userId={}", userId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        logger.info("用户验证成功: userId={}", userId);
        
        // 验证并转换targetId为有效的ObjectId格式
        logger.info("开始验证targetId: {}", request.getTargetId());
        ObjectId targetId = validateAndConvertObjectId(request.getTargetId(), "目标ID");
        logger.info("targetId验证成功，转换后: {}", targetId);

        // 验证并转换parentId为有效的ObjectId格式（如果存在）
        ObjectId parentId = null;
        logger.info("🔍 parentId原始值检查: value='{}', type={}, hasText={}",
                   request.getParentId(),
                   request.getParentId() != null ? request.getParentId().getClass().getSimpleName() : "null",
                   StringUtils.hasText(request.getParentId()));

        if (StringUtils.hasText(request.getParentId())) {
            logger.info("✅ 开始验证parentId: '{}'", request.getParentId());
            try {
                parentId = validateAndConvertObjectId(request.getParentId(), "父评论ID");
                logger.info("✅ parentId验证成功，转换后: {}", parentId);
            } catch (Exception e) {
                logger.error("❌ parentId验证失败: {}", e.getMessage(), e);
                throw e; // 重新抛出异常
            }
        } else {
            logger.warn("⚠️ parentId为空或无效，将创建顶级评论: parentId='{}'", request.getParentId());
        }
        
        Comment comment = new Comment();
        comment.setTargetId(targetId);
        comment.setTargetType(request.getTargetType());
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentId(parentId);
        comment.setStatus(1);
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        // 计算层级和路径
        if (parentId != null) {
            // 回复评论
            Optional<Comment> parentOptional = commentRepository.findById(parentId.toHexString());
            if (!parentOptional.isPresent()) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "父评论不存在");
            }

            Comment parent = parentOptional.get();
            comment.setLevel(parent.getLevel() + 1);
            comment.setPath(calculateCommentPath(parentId.toHexString()));

            // 更新父评论的回复数
            updateReplyCount(parentId.toHexString(), 1);
        } else {
            // 顶级评论
            comment.setLevel(1);
            // 路径将在保存后设置
        }
        
        // 保存评论
        Comment savedComment = commentRepository.save(comment);
        
        // 如果是顶级评论，设置路径为自己的ID
        if (comment.getLevel() == 1) {
            savedComment.setPath(savedComment.getId());
            savedComment = commentRepository.save(savedComment);
        }
        
        // 更新内容统计：评论数 +1（包含回复）
        try {
            String targetIdHex = targetId.toHexString();
            boolean ok = contentStatsService.updateContentStatsSync(targetIdHex, request.getTargetType(), "comment");
            logger.info("内容评论统计自增: targetId={}, targetType={}, success={}", targetIdHex, request.getTargetType(), ok);
        } catch (Exception e) {
            logger.warn("创建评论后更新评论统计失败: targetId={}, targetType={}, err={}", request.getTargetId(), request.getTargetType(), e.getMessage());
        }
        
        logger.info("评论创建成功: {}", savedComment.getId());
        return convertToDTO(savedComment);
    }
    
    @Override
    public PageResult<CommentDTO> getCommentsByTarget(String targetId, String targetType, Integer page, Integer size) {
        logger.info("获取评论列表: targetId={}, targetType={}, page={}, size={}", targetId, targetType, page, size);

        // 验证并转换targetId为ObjectId
        ObjectId targetObjectId = validateAndConvertObjectId(targetId, "targetId");
        logger.info("转换后的ObjectId: {}", targetObjectId);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findByTargetIdAndTargetTypeAndStatus(targetObjectId, targetType, 1, pageable);

        logger.info("查询结果: 找到{}条评论", commentPage.getTotalElements());

        List<CommentDTO> commentDTOs = commentPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 构建评论树结构
        List<CommentDTO> commentTree = buildCommentTree(commentDTOs);
        logger.info("构建评论树完成: 顶级评论{}条，总评论{}条", commentTree.size(), commentDTOs.size());

        return new PageResult<CommentDTO>(
                commentTree,
                page,
                size,
                commentPage.getTotalElements()
        );
    }
    
    @Override
    public CommentDTO getCommentById(String id) {
        logger.info("获取评论详情: id={}", id);
        
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (!commentOptional.isPresent()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }
        
        return convertToDTO(commentOptional.get());
    }
    
    @Override
    @Transactional
    public void deleteComment(String id, Long userId) {
        logger.info("删除评论: id={}, userId={}", id, userId);
        
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (!commentOptional.isPresent()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评论不存在");
        }
        
        Comment comment = commentOptional.get();
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限删除此评论");
        }

        // 硬删除：删除当前评论与所有子评论
        // 1) 记录目标统计信息所需字段
        final String targetId = comment.getTargetId() != null ? comment.getTargetId().toHexString() : null;
        final String targetType = comment.getTargetType();
        final String parentId = comment.getParentId() != null ? comment.getParentId().toHexString() : null;

        // 2) 先删除后代评论（path 以 当前评论path 为前缀）
        String pathPrefix = comment.getPath();
        long deletedChildren = 0L;
        if (pathPrefix != null && !pathPrefix.isEmpty()) {
            // 子孙节点路径形如：<当前path>.<索引>
            String descendantsPrefix = pathPrefix + ".";
            deletedChildren = commentRepository.deleteByPathStartingWith(descendantsPrefix);
            logger.info("已删除子评论数量: {} (prefix={})", deletedChildren, descendantsPrefix);
        }

        // 3) 删除当前评论
        commentRepository.deleteById(id);
        long totalDeleted = deletedChildren + 1;
        logger.info("硬删除完成，总计删除{}条评论(含自身)", totalDeleted);

        // 4) 维护父评论的回复数（父评论可能不存在或已被删）
        if (parentId != null) {
            updateReplyCount(parentId, -1);
        }

        // 5) 更新内容统计中的评论计数（MySQL），精确按删除总数递减
        try {
            if (targetId != null && targetType != null) {
                long decrement = -totalDeleted; // 负数递减
                boolean ok = contentStatsService.incrementContentStats(targetId, targetType, "comment", decrement);
                logger.info("内容评论统计精确递减: targetId={}, targetType={}, decrement={}, success={}",
                        targetId, targetType, decrement, ok);
            }
        } catch (Exception ex) {
            logger.warn("更新内容评论统计失败: targetId={}, targetType={}, err= {}", targetId, targetType, ex.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean likeComment(String id, Long userId) {
        logger.info("点赞评论: id={}, userId={}", id, userId);
        
        // 检查是否已经点赞
        // 这里应该检查用户行为表，暂时简化处理
        updateLikeCount(id, 1);
        return true;
    }
    
    @Override
    @Transactional
    public boolean unlikeComment(String id, Long userId) {
        logger.info("取消点赞评论: id={}, userId={}", id, userId);
        
        updateLikeCount(id, -1);
        return true;
    }
    
    @Override
    public PageResult<CommentDTO> getUserComments(Long userId, Integer page, Integer size) {
        logger.info("获取用户评论: userId={}, page={}, size={}", userId, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findByUserIdAndStatus(userId, 1, pageable);
        
        List<CommentDTO> commentDTOs = commentPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResult<CommentDTO>(
                commentDTOs,
                page,
                size,
                commentPage.getTotalElements()
        );
    }
    
    @Override
    public List<CommentDTO> getHotComments(String targetId, String targetType, Integer limit) {
        logger.info("获取热门评论: targetId={}, targetType={}, limit={}", targetId, targetType, limit);

        // 验证并转换targetId为ObjectId
        ObjectId targetObjectId = validateAndConvertObjectId(targetId, "targetId");

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "likeCount"));
        Page<Comment> commentPage = commentRepository.findByTargetIdAndTargetTypeAndStatus(targetObjectId, targetType, 1, pageable);

        return commentPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDTO> getLatestComments(String targetId, String targetType, Integer limit) {
        logger.info("获取最新评论: targetId={}, targetType={}, limit={}", targetId, targetType, limit);

        // 验证并转换targetId为ObjectId
        ObjectId targetObjectId = validateAndConvertObjectId(targetId, "targetId");

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findByTargetIdAndTargetTypeAndStatus(targetObjectId, targetType, 1, pageable);

        return commentPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long getCommentCount(String targetId, String targetType) {
        logger.info("统计评论数量: targetId={}, targetType={}", targetId, targetType);
        return commentRepository.countByTargetIdAndTargetTypeAndStatus(targetId, targetType, 1);
    }
    
    @Override
    public List<CommentDTO> buildCommentTree(List<CommentDTO> comments) {
        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 构建父子关系映射
        Map<String, List<CommentDTO>> childrenMap = new HashMap<>();
        for (CommentDTO comment : comments) {
            if (comment.getParentId() != null) {
                childrenMap.computeIfAbsent(comment.getParentId(), k -> new ArrayList<>()).add(comment);
            }
        }
        
        // 设置子评论
        for (CommentDTO comment : comments) {
            List<CommentDTO> children = childrenMap.get(comment.getId());
            if (children != null) {
                comment.setChildren(children);
            }
        }
        
        // 返回顶级评论
        return comments.stream()
                .filter(comment -> comment.getLevel() == 1)
                .collect(Collectors.toList());
    }
    
    @Override
    public String calculateCommentPath(String parentId) {
        Optional<Comment> parentOptional = commentRepository.findById(parentId);
        if (!parentOptional.isPresent()) {
            return parentId;
        }
        
        Comment parent = parentOptional.get();
        if (parent.getPath() == null) {
            return parentId;
        }
        
        // 计算同级评论数量
        long siblingCount = commentRepository.countByParentId(parentId);
        
        return parent.getPath() + "." + (siblingCount + 1);
    }
    
    @Override
    @Transactional
    public void updateReplyCount(String parentId, int increment) {
        if (!StringUtils.hasText(parentId)) {
            return;
        }
        
        Optional<Comment> parentOptional = commentRepository.findById(parentId);
        if (parentOptional.isPresent()) {
            Comment parent = parentOptional.get();
            int newCount = Math.max(0, parent.getReplyCount() + increment);
            parent.setReplyCount(newCount);
            parent.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(parent);
        }
    }
    
    @Override
    public void updateLikeCount(String commentId, int increment) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            int newCount = Math.max(0, comment.getLikeCount() + increment);
            comment.setLikeCount(newCount);
            comment.setUpdatedAt(LocalDateTime.now());
            commentRepository.save(comment);
        }
    }
    
    /**
     * 验证并转换ObjectId字符串
     * 确保字符串是有效的ObjectId格式，并返回ObjectId对象
     *
     * @param objectIdStr ObjectId字符串
     * @param fieldName 字段名称（用于错误信息）
     * @return ObjectId对象
     * @throws BusinessException 如果ObjectId格式无效
     */
    private ObjectId validateAndConvertObjectId(String objectIdStr, String fieldName) {
        logger.info("=== validateAndConvertObjectId 开始 ===");
        logger.info("输入参数: fieldName={}, objectIdStr={}", fieldName, objectIdStr);
        
        if (objectIdStr == null || objectIdStr.trim().isEmpty()) {
            logger.error("ObjectId为空: fieldName={}, objectIdStr={}", fieldName, objectIdStr);
            throw new BusinessException(ErrorCode.BAD_REQUEST, fieldName + "不能为空");
        }
        
        try {
            // 验证ObjectId格式并创建ObjectId对象
            logger.info("尝试创建ObjectId: {}", objectIdStr.trim());
            ObjectId objectId = new ObjectId(objectIdStr.trim());
            logger.info("ObjectId验证成功: 输入={}, 输出={}", objectIdStr, objectId);
            return objectId;
        } catch (IllegalArgumentException e) {
            logger.error("无效的ObjectId格式: {} = {}", fieldName, objectIdStr, e);
            logger.error("异常详情: {}", e.getMessage());
            throw new BusinessException(ErrorCode.BAD_REQUEST, 
                fieldName + "格式无效，必须是24位十六进制字符串");
        } catch (Exception e) {
            logger.error("ObjectId转换时发生未知异常: {} = {}", fieldName, objectIdStr, e);
            throw new BusinessException(ErrorCode.BAD_REQUEST, 
                fieldName + "处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 将Comment实体转换为CommentDTO
     *
     * @param comment Comment实体
     * @return CommentDTO
     */
    private CommentDTO convertToDTO(Comment comment) {
        logger.debug("🔍 转换评论实体到DTO: commentId={}, parentId={}",
                    comment.getId(), comment.getParentId());

        CommentDTO commentDTO = new CommentDTO();
        BeanUtils.copyProperties(comment, commentDTO);

        // 手动处理 ObjectId 字段转换
        if (comment.getTargetId() != null) {
            commentDTO.setTargetId(comment.getTargetId().toHexString());
        }

        if (comment.getParentId() != null) {
            String parentIdStr = comment.getParentId().toHexString();
            commentDTO.setParentId(parentIdStr);
            logger.debug("✅ 设置 parentId: {} -> {}", comment.getParentId(), parentIdStr);
        } else {
            logger.debug("ℹ️ parentId 为空，这是顶级评论");
        }

        // 设置用户信息
        if (comment.getUserId() != null) {
            Optional<User> userOptional = userRepository.findById(comment.getUserId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                CommentDTO.UserInfo userInfo = new CommentDTO.UserInfo(
                        user.getNickname(), user.getAvatar());
                commentDTO.setUserInfo(userInfo);
            }
        }

        logger.debug("📤 转换完成: commentDTO.parentId={}", commentDTO.getParentId());
        return commentDTO;
    }
}
