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

import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.WriterDTO;
import com.poem.education.entity.mongodb.Writer;
import com.poem.education.exception.BusinessException;
import com.poem.education.repository.mongodb.WriterRepository;
import com.poem.education.service.WriterService;
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
 * 作者服务实现类
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Service
@Transactional(readOnly = true)
public class WriterServiceImpl implements WriterService {
    
    private static final Logger logger = LoggerFactory.getLogger(WriterServiceImpl.class);
    
    @Autowired
    private WriterRepository writerRepository;
    
    @Override
    public WriterDTO getWriterById(String id) {
        logger.info("获取作者详情: id={}", id);
        
        Optional<Writer> writerOptional = writerRepository.findById(id);
        if (!writerOptional.isPresent()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "作者不存在");
        }
        
        return convertToDTO(writerOptional.get());
    }
    
    @Override
    public WriterDTO getWriterByName(String name) {
        logger.info("根据姓名获取作者: name={}", name);
        
        Optional<Writer> writerOptional = writerRepository.findByName(name);
        if (!writerOptional.isPresent()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "作者不存在");
        }
        
        return convertToDTO(writerOptional.get());
    }
    
    @Override
    public PageResult<WriterDTO> getWriterList(Integer page, Integer size, String dynasty) {
        logger.info("获取作者列表: page={}, size={}, dynasty={}", page, size, dynasty);
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "name"));
        
        Page<Writer> writerPage;
        
        // 根据条件查询
        if (StringUtils.hasText(dynasty)) {
            writerPage = writerRepository.findByDynasty(dynasty, pageable);
        } else {
            writerPage = writerRepository.findAll(pageable);
        }
        
        // 转换为DTO
        List<WriterDTO> writerDTOList = writerPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(writerDTOList, page, size, writerPage.getTotalElements());
    }
    
    @Override
    public PageResult<WriterDTO> searchWriters(String keyword, Integer page, Integer size) {
        logger.info("搜索作者: keyword={}, page={}, size={}", keyword, page, size);
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "name"));
        
        Page<Writer> writerPage;
        
        // 如果有关键字，使用全文搜索
        if (StringUtils.hasText(keyword)) {
            writerPage = writerRepository.findByTextSearch(keyword, pageable);
        } else {
            writerPage = writerRepository.findAll(pageable);
        }
        
        // 转换为DTO
        List<WriterDTO> writerDTOList = writerPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(writerDTOList, page, size, writerPage.getTotalElements());
    }
    
    @Override
    public PageResult<WriterDTO> getWritersByDynasty(String dynasty, Integer page, Integer size) {
        logger.info("根据朝代获取作者: dynasty={}, page={}, size={}", dynasty, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<Writer> writerPage = writerRepository.findByDynasty(dynasty, pageable);
        
        List<WriterDTO> writerDTOList = writerPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(writerDTOList, page, size, writerPage.getTotalElements());
    }
    
    @Override
    public List<WriterDTO> getHotWriters(Integer limit) {
        logger.info("获取热门作者: limit={}", limit);
        
        // 创建分页对象（按创建时间倒序，模拟热门）
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Writer> writerPage = writerRepository.findAll(pageable);
        
        return writerPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<WriterDTO> getRandomWriters(Integer limit) {
        logger.info("获取随机作者: limit={}", limit);
        
        List<Writer> randomWriters = writerRepository.findRandomWriters(limit);
        
        return randomWriters.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getAllDynasties() {
        logger.info("获取所有朝代列表");
        
        List<Writer> dynastyList = writerRepository.findAllDynasties();
        
        return dynastyList.stream()
                .map(Writer::getDynasty)
                .filter(dynasty -> dynasty != null && !dynasty.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    @Override
    public long getTotalCount() {
        return writerRepository.count();
    }
    
    @Override
    public long getCountByDynasty(String dynasty) {
        return writerRepository.countByDynasty(dynasty);
    }
    
    /**
     * 将Writer实体转换为WriterDTO
     * 
     * @param writer Writer实体
     * @return WriterDTO
     */
    private WriterDTO convertToDTO(Writer writer) {
        WriterDTO writerDTO = new WriterDTO();
        BeanUtils.copyProperties(writer, writerDTO);
        return writerDTO;
    }
}
// {{END_MODIFICATIONS}}
