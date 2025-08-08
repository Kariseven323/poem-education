// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "11ae4c98-4ca6-4e38-a76f-6770f885c723"
//   Timestamp: "2025-08-08T14:00:15+08:00"
//   Authoring_Subagent: "PM-标准协作模式"
//   Principle_Applied: "Service实现最佳实践，遵循现有模式"
//   Quality_Check: "编译通过，业务逻辑完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service.impl;

import com.poem.education.dto.request.CreationRequest;
import com.poem.education.dto.response.CreationDTO;
import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.RadarDataDTO;
import com.poem.education.entity.mongodb.Creation;
import com.poem.education.exception.BusinessException;
import com.poem.education.repository.mongodb.CreationRepository;
import com.poem.education.service.AIScoreService;
import com.poem.education.service.CreationService;
import com.poem.education.service.UserActionService;
import com.poem.education.dto.request.UserActionRequest;
import com.poem.education.constant.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 创作服务实现类
 * 处理诗词创作相关业务逻辑
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
@Service
@Transactional(readOnly = true)
public class CreationServiceImpl implements CreationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CreationServiceImpl.class);
    
    @Autowired
    private CreationRepository creationRepository;
    
    @Autowired
    private AIScoreService aiScoreService;

    @Autowired
    private UserActionService userActionService;
    
    @Override
    @Transactional
    public CreationDTO createCreation(Long userId, CreationRequest request) {
        logger.info("用户{}创建新作品，标题：{}", userId, request.getTitle());
        
        // 创建Creation实体
        Creation creation = new Creation();
        creation.setTitle(request.getTitle());
        creation.setContent(request.getContent());
        creation.setStyle(request.getStyle());
        creation.setDescription(request.getDescription());
        creation.setUserId(userId);
        creation.setCreatedAt(LocalDateTime.now());
        creation.setUpdatedAt(LocalDateTime.now());
        creation.setStatus(1); // 1-正常
        creation.setIsPublic(false); // 默认私有
        creation.setLikeCount(0);
        creation.setCommentCount(0);
        
        // 保存到数据库
        Creation savedCreation = creationRepository.save(creation);
        
        logger.info("创作保存成功，ID：{}", savedCreation.getId());
        
        // 转换为DTO并返回
        return convertToDTO(savedCreation);
    }
    
    @Override
    public CreationDTO getCreationById(String id) {
        logger.debug("获取创作详情，ID：{}", id);

        Optional<Creation> creationOpt = creationRepository.findById(id);
        if (!creationOpt.isPresent()) {
            throw new BusinessException(ErrorCode.CREATION_NOT_FOUND, "创作不存在");
        }

        Creation creation = creationOpt.get();
        if (creation.getStatus() != 1) {
            throw new BusinessException(ErrorCode.CREATION_NOT_FOUND, "创作已被删除");
        }

        // 调试信息：记录数据库中的实际值
        logger.info("数据库中创作{}的isPublic值：{}", id, creation.getIsPublic());

        CreationDTO dto = convertToDTO(creation);

        // 调试信息：记录DTO中的值
        logger.info("DTO中创作{}的isPublic值：{}", id, dto.getIsPublic());

        return dto;
    }
    
    @Override
    public PageResult<CreationDTO> getUserCreations(Long userId, Integer page, Integer size, String style, Integer status) {
        logger.debug("获取用户{}的创作列表，页码：{}，大小：{}", userId, page, size);
        
        // 创建分页参数
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Creation> creationPage;
        if (status != null) {
            creationPage = creationRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            creationPage = creationRepository.findByUserId(userId, pageable);
        }
        
        // 转换为DTO列表
        List<CreationDTO> creationDTOs = creationPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(
                creationDTOs,
                page,
                size,
                creationPage.getTotalElements()
        );
    }
    
    @Override
    public PageResult<CreationDTO> getPublicCreations(Integer page, Integer size, String style) {
        logger.info("=== Service层：获取公开创作列表 ===");
        logger.info("参数: page={}, size={}, style={}", page, size, style);

        // 创建分页参数
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Creation> creationPage;
        if (style != null && !style.trim().isEmpty()) {
            // 按风格和公开状态查询
            logger.info("执行查询: findByStyleAndStatusAndIsPublic(style={}, status=1, isPublic=true)", style);
            creationPage = creationRepository.findByStyleAndStatusAndIsPublic(style, 1, true, pageable);
        } else {
            // 只按状态和公开状态查询
            logger.info("执行查询: findByStatusAndIsPublic(status=1, isPublic=true)");
            creationPage = creationRepository.findByStatusAndIsPublic(1, true, pageable);
        }

        logger.info("数据库查询结果: 总数={}, 当前页数据量={}", creationPage.getTotalElements(), creationPage.getContent().size());

        // 打印前几条原始数据
        if (creationPage.getContent().size() > 0) {
            Creation firstCreation = creationPage.getContent().get(0);
            logger.info("第一条原始数据: id={}, title={}, isPublic={}, status={}",
                firstCreation.getId(), firstCreation.getTitle(), firstCreation.getIsPublic(), firstCreation.getStatus());
        }

        // 转换为DTO列表
        List<CreationDTO> creationDTOs = creationPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        logger.info("DTO转换完成: 转换后数量={}", creationDTOs.size());

        return new PageResult<>(
                creationDTOs,
                page,
                size,
                creationPage.getTotalElements()
        );
    }
    
    @Override
    @Transactional
    public CreationDTO updateCreation(Long userId, String id, CreationRequest request) {
        logger.info("用户{}更新创作{}，标题：{}", userId, id, request.getTitle());
        
        // 获取现有创作
        Creation creation = getCreationEntity(id);
        
        // 检查权限
        if (!creation.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.CREATION_NO_PERMISSION, "无权限修改此创作");
        }
        
        // 更新字段
        creation.setTitle(request.getTitle());
        creation.setContent(request.getContent());
        creation.setStyle(request.getStyle());
        creation.setDescription(request.getDescription());
        creation.setUpdatedAt(LocalDateTime.now());
        
        // 清除AI评分（内容变更后需要重新评分）
        creation.setAiScore(null);
        
        // 保存更新
        Creation updatedCreation = creationRepository.save(creation);
        
        logger.info("创作更新成功，ID：{}", updatedCreation.getId());
        
        return convertToDTO(updatedCreation);
    }
    
    @Override
    @Transactional
    public void deleteCreation(Long userId, String id) {
        logger.info("用户{}删除创作{}", userId, id);
        
        // 获取现有创作
        Creation creation = getCreationEntity(id);
        
        // 检查权限
        if (!creation.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.CREATION_NO_PERMISSION, "无权限删除此创作");
        }
        
        // 软删除
        creation.setStatus(0); // 0-已删除
        creation.setUpdatedAt(LocalDateTime.now());
        
        creationRepository.save(creation);
        
        logger.info("创作删除成功，ID：{}", id);
    }
    
    @Override
    @Async
    @Transactional
    public CompletableFuture<Boolean> requestAIScore(Long userId, String id) {
        logger.info("用户{}请求AI评分，创作ID：{}", userId, id);
        
        try {
            // 获取创作
            Creation creation = getCreationEntity(id);
            
            // 检查权限
            if (!creation.getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.CREATION_NO_PERMISSION, "无权限对此创作进行评分");
            }
            
            // 调用AI评分服务
            Creation.AiScore aiScore = aiScoreService.callAIModel(
                    creation.getTitle(),
                    creation.getContent(),
                    creation.getStyle()
            );
            
            // 更新创作的AI评分
            creation.setAiScore(aiScore);

            // 生成并设置雷达图数据（按照数据库设计）
            if (aiScore.getDimensions() != null) {
                Creation.RadarData radarData = new Creation.RadarData(
                    Arrays.asList("韵律", "意象", "情感", "技法", "创新"),
                    Arrays.asList(
                        aiScore.getDimensions().getRhythm(),
                        aiScore.getDimensions().getImagery(),
                        aiScore.getDimensions().getEmotion(),
                        aiScore.getDimensions().getTechnique(),
                        aiScore.getDimensions().getInnovation()
                    )
                );
                creation.setRadarData(radarData);
            }

            creation.setUpdatedAt(LocalDateTime.now());
            
            creationRepository.save(creation);
            
            logger.info("AI评分完成，创作ID：{}，总分：{}", id, aiScore.getTotalScore());
            
            return CompletableFuture.completedFuture(true);
            
        } catch (Exception e) {
            logger.error("AI评分失败，创作ID：{}", id, e);
            return CompletableFuture.completedFuture(false);
        }
    }
    
    @Override
    public RadarDataDTO getRadarData(String id) {
        logger.debug("获取雷达图数据，创作ID：{}", id);
        
        Creation creation = getCreationEntity(id);
        
        RadarDataDTO radarData = new RadarDataDTO(id, creation.getTitle());
        
        // 检查是否有AI评分
        if (creation.getAiScore() == null || creation.getAiScore().getDimensions() == null) {
            radarData.setHasScore(false);
            return radarData;
        }
        
        // 构建雷达图数据
        Creation.AiScore.ScoreDimensions dimensions = creation.getAiScore().getDimensions();
        
        // 设置指标
        List<RadarDataDTO.IndicatorDTO> indicators = Arrays.asList(
                new RadarDataDTO.IndicatorDTO("韵律", 100),
                new RadarDataDTO.IndicatorDTO("意象", 100),
                new RadarDataDTO.IndicatorDTO("情感", 100),
                new RadarDataDTO.IndicatorDTO("技法", 100),
                new RadarDataDTO.IndicatorDTO("创新", 100)
        );
        
        // 设置数据系列
        List<Integer> values = Arrays.asList(
                dimensions.getRhythm() != null ? dimensions.getRhythm() : 0,
                dimensions.getImagery() != null ? dimensions.getImagery() : 0,
                dimensions.getEmotion() != null ? dimensions.getEmotion() : 0,
                dimensions.getTechnique() != null ? dimensions.getTechnique() : 0,
                dimensions.getInnovation() != null ? dimensions.getInnovation() : 0
        );
        
        RadarDataDTO.SeriesDataDTO seriesData = new RadarDataDTO.SeriesDataDTO("评分", values);
        seriesData.setItemStyle(new RadarDataDTO.ItemStyleDTO("#1890ff"));

        radarData.setIndicators(indicators);
        radarData.setSeries(Arrays.asList(seriesData));
        radarData.setTotalScore(creation.getAiScore().getTotalScore());
        radarData.setHasScore(true);

        logger.debug("雷达图数据生成完成，创作ID：{}，数据：{}", id, radarData);

        return radarData;
    }
    
    /**
     * 获取创作实体（内部方法）
     */
    private Creation getCreationEntity(String id) {
        Optional<Creation> creationOpt = creationRepository.findById(id);
        if (!creationOpt.isPresent()) {
            throw new BusinessException(ErrorCode.CREATION_NOT_FOUND, "创作不存在");
        }
        
        Creation creation = creationOpt.get();
        if (creation.getStatus() != 1) {
            throw new BusinessException(ErrorCode.CREATION_NOT_FOUND, "创作已被删除");
        }
        
        return creation;
    }
    
    @Override
    @Transactional
    public CreationDTO togglePublicStatus(Long userId, String id, Boolean isPublic) {
        logger.info("用户{}切换创作{}公开状态为：{}", userId, id, isPublic);

        // 获取创作
        Creation creation = getCreationEntity(id);

        // 检查权限
        if (!creation.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.CREATION_NO_PERMISSION, "无权限修改此创作的公开状态");
        }

        // 更新公开状态
        creation.setIsPublic(isPublic);
        creation.setUpdatedAt(LocalDateTime.now());

        Creation updatedCreation = creationRepository.save(creation);

        logger.info("创作公开状态更新成功，ID：{}，公开：{}", id, isPublic);

        return convertToDTO(updatedCreation);
    }

    @Override
    @Transactional
    public CreationDTO toggleLike(Long userId, String id) {
        logger.info("用户{}切换创作{}点赞状态", userId, id);

        // 获取创作
        Creation creation = getCreationEntity(id);

        // 检查用户是否已经点赞过
        boolean hasLiked = userActionService.hasAction(userId, id, "creation", "like");

        Integer currentLikes = creation.getLikeCount() != null ? creation.getLikeCount() : 0;

        if (hasLiked) {
            // 用户已点赞，执行取消点赞
            boolean cancelled = userActionService.cancelAction(userId, id, "creation", "like");
            if (cancelled) {
                creation.setLikeCount(Math.max(0, currentLikes - 1));
                logger.info("用户{}取消点赞创作{}", userId, id);
            }
        } else {
            // 用户未点赞，执行点赞
            UserActionRequest actionRequest = new UserActionRequest();
            actionRequest.setTargetId(id);
            actionRequest.setTargetType("creation");
            actionRequest.setActionType("like");

            userActionService.recordAction(userId, actionRequest);
            creation.setLikeCount(currentLikes + 1);
            logger.info("用户{}点赞创作{}", userId, id);
        }

        creation.setUpdatedAt(LocalDateTime.now());
        Creation updatedCreation = creationRepository.save(creation);

        logger.info("创作点赞状态更新成功，ID：{}，点赞数：{}", id, updatedCreation.getLikeCount());

        return convertToDTO(updatedCreation);
    }

    @Override
    public PageResult<CreationDTO> searchCreations(String keyword, Integer page, Integer size, String style) {
        logger.debug("搜索公开创作，关键词：{}，页码：{}，大小：{}，风格：{}", keyword, page, size, style);

        // 创建分页参数
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Creation> creationPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // 有关键词时进行搜索
            if (style != null && !style.trim().isEmpty()) {
                // 同时按关键词、风格和公开状态搜索
                creationPage = creationRepository.searchByKeywordAndStyleAndIsPublic(keyword, style, 1, true, pageable);
            } else {
                // 按关键词和公开状态搜索
                creationPage = creationRepository.searchByKeywordAndIsPublic(keyword, 1, true, pageable);
            }
        } else {
            // 无关键词时按条件筛选
            if (style != null && !style.trim().isEmpty()) {
                // 按风格、状态和公开状态筛选
                creationPage = creationRepository.findByStyleAndStatusAndIsPublic(style, 1, true, pageable);
            } else {
                // 按状态和公开状态筛选
                creationPage = creationRepository.findByStatusAndIsPublic(1, true, pageable);
            }
        }

        // 转换为DTO列表
        List<CreationDTO> creationDTOs = creationPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResult<>(
                creationDTOs,
                page,
                size,
                creationPage.getTotalElements()
        );
    }

    /**
     * 将Creation实体转换为DTO
     */
    private CreationDTO convertToDTO(Creation creation) {
        CreationDTO dto = new CreationDTO();
        BeanUtils.copyProperties(creation, dto);

        // 手动设置authorId字段（因为Creation中是userId）
        dto.setAuthorId(creation.getUserId());

        // 转换AI评分
        if (creation.getAiScore() != null) {
            CreationDTO.AiScoreDTO aiScoreDTO = new CreationDTO.AiScoreDTO();
            BeanUtils.copyProperties(creation.getAiScore(), aiScoreDTO);

            // 转换多维度评分
            if (creation.getAiScore().getDimensions() != null) {
                CreationDTO.ScoreDimensionsDTO dimensionsDTO = new CreationDTO.ScoreDimensionsDTO();
                BeanUtils.copyProperties(creation.getAiScore().getDimensions(), dimensionsDTO);
                aiScoreDTO.setDimensions(dimensionsDTO);
            }

            dto.setAiScore(aiScoreDTO);
        }

        return dto;
    }
}
// {{END_MODIFICATIONS}}
