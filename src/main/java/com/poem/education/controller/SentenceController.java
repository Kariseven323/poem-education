// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "sentence-controller-creation"
//   Timestamp: "2025-08-08T13:20:59+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "RESTful API设计最佳实践，随机名句接口实现"
//   Quality_Check: "编译通过，API接口设计符合规范。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.controller;

import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.Result;
import com.poem.education.dto.response.SentenceDTO;
import com.poem.education.service.SentenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 名句控制器
 * 处理名句相关的API接口
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
@RestController
@RequestMapping("/api/v1/sentences")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SentenceController {
    
    private static final Logger logger = LoggerFactory.getLogger(SentenceController.class);
    
    @Autowired
    private SentenceService sentenceService;
    
    /**
     * 获取名句列表（分页查询）
     * GET /api/v1/sentences?page=1&size=20&author=李白&dynasty=唐&from=静夜思
     * 
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @param author 作者（可选）
     * @param dynasty 朝代（可选）
     * @param from 出处（可选）
     * @return 名句分页列表
     */
    @GetMapping
    public Result<PageResult<SentenceDTO>> getSentenceList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String dynasty,
            @RequestParam(required = false) String from) {
        
        logger.info("获取名句列表: page={}, size={}, author={}, dynasty={}, from={}", 
                   page, size, author, dynasty, from);
        
        PageResult<SentenceDTO> result = sentenceService.getSentenceList(page, size, author, dynasty, from);
        
        return Result.success(result, "获取名句列表成功");
    }
    
    /**
     * 根据ID获取名句详情
     * GET /api/v1/sentences/{id}
     * 
     * @param id 名句ID
     * @return 名句详情
     */
    @GetMapping("/{id}")
    public Result<SentenceDTO> getSentenceById(@PathVariable String id) {
        logger.info("获取名句详情: id={}", id);
        
        SentenceDTO sentenceDTO = sentenceService.getSentenceById(id);
        
        return Result.success(sentenceDTO, "获取名句详情成功");
    }
    
    /**
     * 搜索名句
     * GET /api/v1/sentences/search?keyword=明月&page=1&size=20
     * 
     * @param keyword 搜索关键字
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 名句分页列表
     */
    @GetMapping("/search")
    public Result<PageResult<SentenceDTO>> searchSentences(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        logger.info("搜索名句: keyword={}, page={}, size={}", keyword, page, size);
        
        PageResult<SentenceDTO> result = sentenceService.searchSentences(keyword, page, size);
        
        return Result.success(result, "搜索名句成功");
    }
    
    /**
     * 获取热门名句
     * GET /api/v1/sentences/hot?limit=10
     * 
     * @param limit 限制数量，默认10
     * @return 热门名句列表
     */
    @GetMapping("/hot")
    public Result<List<SentenceDTO>> getHotSentences(@RequestParam(defaultValue = "10") Integer limit) {
        logger.info("获取热门名句: limit={}", limit);
        
        List<SentenceDTO> result = sentenceService.getHotSentences(limit);
        
        return Result.success(result, "获取热门名句成功");
    }
    
    /**
     * 获取随机名句
     * GET /api/v1/sentences/random?limit=5
     * 
     * @param limit 限制数量，默认5
     * @return 随机名句列表
     */
    @GetMapping("/random")
    public Result<List<SentenceDTO>> getRandomSentences(@RequestParam(defaultValue = "5") Integer limit) {
        logger.info("获取随机名句: limit={}", limit);
        
        List<SentenceDTO> result = sentenceService.getRandomSentences(limit);
        
        return Result.success(result, "获取随机名句成功");
    }
    
    /**
     * 根据作者获取名句列表
     * GET /api/v1/sentences/by-author/{author}?page=1&size=20
     * 
     * @param author 作者
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 名句分页列表
     */
    @GetMapping("/by-author/{author}")
    public Result<PageResult<SentenceDTO>> getSentencesByAuthor(
            @PathVariable String author,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        logger.info("根据作者获取名句: author={}, page={}, size={}", author, page, size);
        
        PageResult<SentenceDTO> result = sentenceService.getSentencesByAuthor(author, page, size);
        
        return Result.success(result, "获取作者名句成功");
    }
    
    /**
     * 根据朝代获取名句列表
     * GET /api/v1/sentences/by-dynasty/{dynasty}?page=1&size=20
     * 
     * @param dynasty 朝代
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 名句分页列表
     */
    @GetMapping("/by-dynasty/{dynasty}")
    public Result<PageResult<SentenceDTO>> getSentencesByDynasty(
            @PathVariable String dynasty,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        logger.info("根据朝代获取名句: dynasty={}, page={}, size={}", dynasty, page, size);
        
        PageResult<SentenceDTO> result = sentenceService.getSentencesByDynasty(dynasty, page, size);
        
        return Result.success(result, "获取朝代名句成功");
    }
    
    /**
     * 根据出处获取名句列表
     * GET /api/v1/sentences/by-from/{from}?page=1&size=20
     * 
     * @param from 出处
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 名句分页列表
     */
    @GetMapping("/by-from/{from}")
    public Result<PageResult<SentenceDTO>> getSentencesByFrom(
            @PathVariable String from,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        logger.info("根据出处获取名句: from={}, page={}, size={}", from, page, size);
        
        PageResult<SentenceDTO> result = sentenceService.getSentencesByFrom(from, page, size);
        
        return Result.success(result, "获取出处名句成功");
    }
    
    /**
     * 获取所有作者列表
     * GET /api/v1/sentences/authors
     * 
     * @return 作者列表
     */
    @GetMapping("/authors")
    public Result<List<String>> getAllAuthors() {
        logger.info("获取所有作者列表");
        
        List<String> result = sentenceService.getAllAuthors();
        
        return Result.success(result, "获取作者列表成功");
    }
    
    /**
     * 获取所有朝代列表
     * GET /api/v1/sentences/dynasties
     * 
     * @return 朝代列表
     */
    @GetMapping("/dynasties")
    public Result<List<String>> getAllDynasties() {
        logger.info("获取所有朝代列表");
        
        List<String> result = sentenceService.getAllDynasties();
        
        return Result.success(result, "获取朝代列表成功");
    }
    
    /**
     * 获取所有出处列表
     * GET /api/v1/sentences/sources
     * 
     * @return 出处列表
     */
    @GetMapping("/sources")
    public Result<List<String>> getAllSources() {
        logger.info("获取所有出处列表");
        
        List<String> result = sentenceService.getAllSources();
        
        return Result.success(result, "获取出处列表成功");
    }
    
    /**
     * 获取名句统计信息
     * GET /api/v1/sentences/stats
     * 
     * @return 统计信息
     */
    @GetMapping("/stats")
    public Result<Object> getSentenceStats() {
        logger.info("获取名句统计信息");
        
        long totalCount = sentenceService.getTotalCount();
        
        // 构建统计信息
        Object stats = new Object() {
            public final long total = totalCount;
        };
        
        return Result.success(stats, "获取统计信息成功");
    }
}
// {{END_MODIFICATIONS}}
