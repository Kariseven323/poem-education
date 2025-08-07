// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "6fbd2d10-468b-4ef3-ac5b-ddd4dddcf277"
//   Timestamp: "2025-08-07T11:50:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "RESTful API设计最佳实践，严格按照API文档定义"
//   Quality_Check: "编译通过，接口定义完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.controller;

import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.Result;
import com.poem.education.dto.response.WriterDTO;
import com.poem.education.service.WriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作者控制器
 * 处理作者相关的API接口
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@RestController
@RequestMapping("/api/v1/writers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WriterController {
    
    private static final Logger logger = LoggerFactory.getLogger(WriterController.class);
    
    @Autowired
    private WriterService writerService;
    
    /**
     * 根据ID获取作者详情
     * GET /api/v1/writers/{id}
     * 
     * @param id 作者ID
     * @return 作者详情
     */
    @GetMapping("/{id}")
    public Result<WriterDTO> getWriterById(@PathVariable String id) {
        logger.info("获取作者详情: id={}", id);
        
        WriterDTO writerDTO = writerService.getWriterById(id);
        
        return Result.success(writerDTO, "获取作者详情成功");
    }
    
    /**
     * 根据姓名获取作者信息
     * GET /api/v1/writers/by-name/{name}
     * 
     * @param name 作者姓名
     * @return 作者信息
     */
    @GetMapping("/by-name/{name}")
    public Result<WriterDTO> getWriterByName(@PathVariable String name) {
        logger.info("根据姓名获取作者: name={}", name);
        
        WriterDTO writerDTO = writerService.getWriterByName(name);
        
        return Result.success(writerDTO, "获取作者信息成功");
    }
    
    /**
     * 获取作者列表（分页查询）
     * GET /api/v1/writers?page=1&size=20&dynasty=唐
     * 
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @param dynasty 朝代（可选）
     * @return 作者分页列表
     */
    @GetMapping
    public Result<PageResult<WriterDTO>> getWriterList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String dynasty) {
        
        logger.info("获取作者列表: page={}, size={}, dynasty={}", page, size, dynasty);
        
        PageResult<WriterDTO> result = writerService.getWriterList(page, size, dynasty);
        
        return Result.success(result, "获取作者列表成功");
    }
    
    /**
     * 搜索作者
     * GET /api/v1/writers/search?keyword=李白&page=1&size=20
     * 
     * @param keyword 搜索关键字
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 作者分页列表
     */
    @GetMapping("/search")
    public Result<PageResult<WriterDTO>> searchWriters(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        logger.info("搜索作者: keyword={}, page={}, size={}", keyword, page, size);
        
        PageResult<WriterDTO> result = writerService.searchWriters(keyword, page, size);
        
        return Result.success(result, "搜索作者成功");
    }
    
    /**
     * 根据朝代获取作者列表
     * GET /api/v1/writers/by-dynasty/{dynasty}?page=1&size=20
     * 
     * @param dynasty 朝代
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 作者分页列表
     */
    @GetMapping("/by-dynasty/{dynasty}")
    public Result<PageResult<WriterDTO>> getWritersByDynasty(
            @PathVariable String dynasty,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        logger.info("根据朝代获取作者: dynasty={}, page={}, size={}", dynasty, page, size);
        
        PageResult<WriterDTO> result = writerService.getWritersByDynasty(dynasty, page, size);
        
        return Result.success(result, "获取朝代作者成功");
    }
    
    /**
     * 获取热门作者
     * GET /api/v1/writers/hot?limit=10
     * 
     * @param limit 限制数量，默认10
     * @return 热门作者列表
     */
    @GetMapping("/hot")
    public Result<List<WriterDTO>> getHotWriters(@RequestParam(defaultValue = "10") Integer limit) {
        logger.info("获取热门作者: limit={}", limit);
        
        List<WriterDTO> result = writerService.getHotWriters(limit);
        
        return Result.success(result, "获取热门作者成功");
    }
    
    /**
     * 获取随机作者
     * GET /api/v1/writers/random?limit=5
     * 
     * @param limit 限制数量，默认5
     * @return 随机作者列表
     */
    @GetMapping("/random")
    public Result<List<WriterDTO>> getRandomWriters(@RequestParam(defaultValue = "5") Integer limit) {
        logger.info("获取随机作者: limit={}", limit);
        
        List<WriterDTO> result = writerService.getRandomWriters(limit);
        
        return Result.success(result, "获取随机作者成功");
    }
    
    /**
     * 获取所有朝代列表
     * GET /api/v1/writers/dynasties
     * 
     * @return 朝代列表
     */
    @GetMapping("/dynasties")
    public Result<List<String>> getAllDynasties() {
        logger.info("获取所有朝代列表");
        
        List<String> result = writerService.getAllDynasties();
        
        return Result.success(result, "获取朝代列表成功");
    }
    
    /**
     * 获取作者统计信息
     * GET /api/v1/writers/stats
     * 
     * @return 统计信息
     */
    @GetMapping("/stats")
    public Result<Object> getWriterStats() {
        logger.info("获取作者统计信息");
        
        long totalCount = writerService.getTotalCount();
        
        // 构建统计信息
        Object stats = new Object() {
            public final long total = totalCount;
        };
        
        return Result.success(stats, "获取统计信息成功");
    }
}
// {{END_MODIFICATIONS}}
