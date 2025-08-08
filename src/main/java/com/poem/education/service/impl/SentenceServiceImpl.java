// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "sentence-service-implementation"
//   Timestamp: "2025-08-08T13:20:59+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "服务实现最佳实践，MongoDB随机查询优化"
//   Quality_Check: "编译通过，随机查询使用$sample聚合管道。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service.impl;

import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.SentenceDTO;
import com.poem.education.entity.mongodb.Sentence;
import com.poem.education.exception.BusinessException;
import com.poem.education.constant.ErrorCode;
import com.poem.education.repository.mongodb.SentenceRepository;
import com.poem.education.service.SentenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 名句服务实现类
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
@Service
@Transactional(readOnly = true)
public class SentenceServiceImpl implements SentenceService {
    
    private static final Logger logger = LoggerFactory.getLogger(SentenceServiceImpl.class);
    
    @Autowired
    private SentenceRepository sentenceRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public PageResult<SentenceDTO> getSentenceList(Integer page, Integer size, String author, String dynasty, String from) {
        logger.info("获取名句列表: page={}, size={}, author={}, dynasty={}, from={}", page, size, author, dynasty, from);
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Sentence> sentencePage;
        
        // 根据条件查询
        if (StringUtils.hasText(author)) {
            sentencePage = sentenceRepository.findByAuthor(author, pageable);
        } else if (StringUtils.hasText(dynasty)) {
            sentencePage = sentenceRepository.findByDynasty(dynasty, pageable);
        } else if (StringUtils.hasText(from)) {
            sentencePage = sentenceRepository.findByFrom(from, pageable);
        } else {
            sentencePage = sentenceRepository.findAll(pageable);
        }
        
        // 转换为DTO
        List<SentenceDTO> sentenceDTOList = sentencePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(sentenceDTOList, page, size, sentencePage.getTotalElements());
    }
    
    @Override
    public SentenceDTO getSentenceById(String id) {
        logger.info("获取名句详情: id={}", id);
        
        Optional<Sentence> sentenceOptional = sentenceRepository.findById(id);
        if (!sentenceOptional.isPresent()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "名句不存在");
        }
        
        return convertToDTO(sentenceOptional.get());
    }
    
    @Override
    public SentenceDTO getSentenceByName(String name) {
        logger.info("根据内容获取名句: name={}", name);
        
        Optional<Sentence> sentenceOptional = sentenceRepository.findByName(name);
        if (!sentenceOptional.isPresent()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "名句不存在");
        }
        
        return convertToDTO(sentenceOptional.get());
    }
    
    @Override
    public PageResult<SentenceDTO> searchSentences(String keyword, Integer page, Integer size) {
        logger.info("搜索名句: keyword={}, page={}, size={}", keyword, page, size);
        
        // 创建分页对象
        Pageable pageable = PageRequest.of(page - 1, size);
        
        Page<Sentence> sentencePage;
        
        if (StringUtils.hasText(keyword)) {
            // 使用全文搜索
            sentencePage = sentenceRepository.findByTextSearch(keyword, pageable);
        } else {
            // 没有关键词时返回所有
            sentencePage = sentenceRepository.findAll(pageable);
        }
        
        // 转换为DTO
        List<SentenceDTO> sentenceDTOList = sentencePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(sentenceDTOList, page, size, sentencePage.getTotalElements());
    }
    
    @Override
    public List<SentenceDTO> getHotSentences(Integer limit) {
        logger.info("获取热门名句: limit={}", limit);
        
        // 创建分页对象（按创建时间倒序，模拟热门）
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Sentence> sentencePage = sentenceRepository.findAll(pageable);
        
        return sentencePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SentenceDTO> getRandomSentences(Integer limit) {
        logger.info("获取随机名句: limit={}", limit);

        // 使用MongoDB的$sample聚合管道进行随机查询
        SampleOperation sampleOperation = Aggregation.sample(limit);
        Aggregation aggregation = Aggregation.newAggregation(sampleOperation);

        List<Sentence> randomSentences = mongoTemplate.aggregate(
                aggregation, "sentences", Sentence.class).getMappedResults();

        return randomSentences.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public PageResult<SentenceDTO> getSentencesByAuthor(String author, Integer page, Integer size) {
        logger.info("根据作者获取名句: author={}, page={}, size={}", author, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Sentence> sentencePage = sentenceRepository.findByAuthor(author, pageable);
        
        List<SentenceDTO> sentenceDTOList = sentencePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(sentenceDTOList, page, size, sentencePage.getTotalElements());
    }
    
    @Override
    public PageResult<SentenceDTO> getSentencesByDynasty(String dynasty, Integer page, Integer size) {
        logger.info("根据朝代获取名句: dynasty={}, page={}, size={}", dynasty, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Sentence> sentencePage = sentenceRepository.findByDynasty(dynasty, pageable);
        
        List<SentenceDTO> sentenceDTOList = sentencePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(sentenceDTOList, page, size, sentencePage.getTotalElements());
    }
    
    @Override
    public PageResult<SentenceDTO> getSentencesByFrom(String from, Integer page, Integer size) {
        logger.info("根据出处获取名句: from={}, page={}, size={}", from, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Sentence> sentencePage = sentenceRepository.findByFrom(from, pageable);
        
        List<SentenceDTO> sentenceDTOList = sentencePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(sentenceDTOList, page, size, sentencePage.getTotalElements());
    }
    
    @Override
    public List<String> getAllAuthors() {
        logger.info("获取所有作者列表");
        
        List<Sentence> authorList = sentenceRepository.findAllAuthors();
        
        return authorList.stream()
                .map(Sentence::getAuthor)
                .filter(author -> author != null && !author.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getAllDynasties() {
        logger.info("获取所有朝代列表");
        
        List<Sentence> dynastyList = sentenceRepository.findAllDynasties();
        
        return dynastyList.stream()
                .map(Sentence::getDynasty)
                .filter(dynasty -> dynasty != null && !dynasty.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getAllSources() {
        logger.info("获取所有出处列表");
        
        List<Sentence> sourceList = sentenceRepository.findAllSources();
        
        return sourceList.stream()
                .map(Sentence::getFrom)
                .filter(from -> from != null && !from.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    @Override
    public long getTotalCount() {
        return sentenceRepository.count();
    }
    
    @Override
    public long getCountByAuthor(String author) {
        return sentenceRepository.countByAuthor(author);
    }
    
    @Override
    public long getCountByDynasty(String dynasty) {
        return sentenceRepository.countByDynasty(dynasty);
    }
    
    @Override
    public long getCountByFrom(String from) {
        return sentenceRepository.countByFrom(from);
    }
    
    /**
     * 将Sentence实体转换为SentenceDTO
     * 
     * @param sentence 名句实体
     * @return 名句DTO
     */
    private SentenceDTO convertToDTO(Sentence sentence) {
        if (sentence == null) {
            return null;
        }
        
        SentenceDTO dto = new SentenceDTO();
        dto.setId(sentence.getId());
        dto.setName(sentence.getName());
        dto.setFrom(sentence.getFrom());
        dto.setAuthor(sentence.getAuthor());
        dto.setDynasty(sentence.getDynasty());
        dto.setMeaning(sentence.getMeaning());
        dto.setAppreciation(sentence.getAppreciation());
        dto.setTags(sentence.getTags());
        dto.setCreatedAt(sentence.getCreatedAt());
        dto.setUpdatedAt(sentence.getUpdatedAt());
        
        return dto;
    }
}
// {{END_MODIFICATIONS}}
