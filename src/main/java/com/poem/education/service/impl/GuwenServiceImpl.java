// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "6fbd2d10-468b-4ef3-ac5b-ddd4dddcf277"
//   Timestamp: "2025-08-07T11:50:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Service实现最佳实践，MongoDB查询优化"
//   Quality_Check: "编译通过，业务逻辑完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service.impl;

import com.poem.education.dto.request.GuwenSearchRequest;
import com.poem.education.dto.response.GuwenDTO;
import com.poem.education.dto.response.PageResult;
import com.poem.education.entity.mongodb.Guwen;
import com.poem.education.exception.BusinessException;
import com.poem.education.repository.mongodb.GuwenRepository;
import com.poem.education.service.GuwenService;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 古文服务实现类
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Service
@Transactional(readOnly = true)
public class GuwenServiceImpl implements GuwenService {
    
    private static final Logger logger = LoggerFactory.getLogger(GuwenServiceImpl.class);
    
    @Autowired
    private GuwenRepository guwenRepository;
    
    @Override
    public PageResult<GuwenDTO> getGuwenList(Integer page, Integer size, String dynasty, String writer, String type) {
        logger.info("获取古文列表: page={}, size={}, dynasty={}, writer={}, type={}", 
                   page, size, dynasty, writer, type);
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Guwen> guwenPage;
        
        // 根据条件查询
        if (StringUtils.hasText(dynasty) && StringUtils.hasText(writer)) {
            guwenPage = guwenRepository.findByWriterAndDynasty(writer, dynasty, pageable);
        } else if (StringUtils.hasText(dynasty)) {
            guwenPage = guwenRepository.findByDynasty(dynasty, pageable);
        } else if (StringUtils.hasText(writer)) {
            guwenPage = guwenRepository.findByWriter(writer, pageable);
        } else if (StringUtils.hasText(type)) {
            guwenPage = guwenRepository.findByType(type, pageable);
        } else {
            guwenPage = guwenRepository.findAll(pageable);
        }
        
        // 转换为DTO
        List<GuwenDTO> guwenDTOList = guwenPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(guwenDTOList, page, size, guwenPage.getTotalElements());
    }
    
    @Override
    public GuwenDTO getGuwenById(String id) {
        logger.info("获取古文详情: id={}", id);
        
        Optional<Guwen> guwenOptional = guwenRepository.findById(id);
        if (!guwenOptional.isPresent()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "古文不存在");
        }
        
        return convertToDTO(guwenOptional.get());
    }
    
    @Override
    public PageResult<GuwenDTO> searchGuwen(GuwenSearchRequest request) {
        logger.info("搜索古文: {}", request);
        
        // 创建分页对象
        Sort.Direction direction = "asc".equalsIgnoreCase(request.getSortDir()) 
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), 
                                         Sort.by(direction, request.getSortBy()));
        
        Page<Guwen> guwenPage;
        
        // 如果有关键字，使用全文搜索
        if (StringUtils.hasText(request.getKeyword())) {
            guwenPage = guwenRepository.findByTextSearch(request.getKeyword(), pageable);
        } else {
            // 使用高级搜索
            guwenPage = guwenRepository.findByAdvancedSearch(
                    request.getKeyword(), 
                    request.getWriter(), 
                    request.getDynasty(), 
                    request.getType(), 
                    pageable);
        }
        
        // 转换为DTO
        List<GuwenDTO> guwenDTOList = guwenPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(guwenDTOList, request.getPage(), request.getSize(), guwenPage.getTotalElements());
    }
    
    @Override
    public List<GuwenDTO> getHotGuwen(String period, Integer limit) {
        logger.info("获取热门古文: period={}, limit={}", period, limit);
        
        // 创建分页对象（按创建时间倒序，模拟热门）
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Guwen> guwenPage = guwenRepository.findAll(pageable);
        
        return guwenPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<GuwenDTO> getRandomGuwen(Integer limit) {
        logger.info("获取随机古文: limit={}", limit);
        
        List<Guwen> randomGuwen = guwenRepository.findRandomGuwen(limit);
        
        return randomGuwen.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public PageResult<GuwenDTO> getGuwenByWriter(String writer, Integer page, Integer size) {
        logger.info("根据作者获取古文: writer={}, page={}, size={}", writer, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Guwen> guwenPage = guwenRepository.findByWriter(writer, pageable);
        
        List<GuwenDTO> guwenDTOList = guwenPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(guwenDTOList, page, size, guwenPage.getTotalElements());
    }
    
    @Override
    public PageResult<GuwenDTO> getGuwenByDynasty(String dynasty, Integer page, Integer size) {
        logger.info("根据朝代获取古文: dynasty={}, page={}, size={}", dynasty, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Guwen> guwenPage = guwenRepository.findByDynasty(dynasty, pageable);
        
        List<GuwenDTO> guwenDTOList = guwenPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(guwenDTOList, page, size, guwenPage.getTotalElements());
    }
    
    @Override
    public List<String> getAllDynasties() {
        logger.info("获取所有朝代列表");
        
        List<Guwen> dynastyList = guwenRepository.findAllDynasties();
        
        return dynastyList.stream()
                .map(Guwen::getDynasty)
                .filter(dynasty -> dynasty != null && !dynasty.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getAllWriters() {
        logger.info("获取所有作者列表");
        
        List<Guwen> writerList = guwenRepository.findAllWriters();
        
        return writerList.stream()
                .map(Guwen::getWriter)
                .filter(writer -> writer != null && !writer.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getAllTypes() {
        logger.info("获取所有类型列表");
        
        List<Guwen> typeList = guwenRepository.findAllTypes();
        
        return typeList.stream()
                .map(Guwen::getType)
                .filter(type -> type != null && !type.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    @Override
    public long getTotalCount() {
        return guwenRepository.count();
    }
    
    @Override
    public long getCountByWriter(String writer) {
        return guwenRepository.countByWriter(writer);
    }
    
    @Override
    public long getCountByDynasty(String dynasty) {
        return guwenRepository.countByDynasty(dynasty);
    }
    
    /**
     * 将Guwen实体转换为GuwenDTO
     * 
     * @param guwen Guwen实体
     * @return GuwenDTO
     */
    private GuwenDTO convertToDTO(Guwen guwen) {
        GuwenDTO guwenDTO = new GuwenDTO();
        BeanUtils.copyProperties(guwen, guwenDTO);
        return guwenDTO;
    }
}
// {{END_MODIFICATIONS}}
