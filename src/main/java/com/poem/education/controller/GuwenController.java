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

import com.poem.education.dto.request.GuwenSearchRequest;
import com.poem.education.dto.response.GuwenDTO;
import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.Result;
import com.poem.education.service.GuwenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 古文控制器
 * 处理古文相关的API接口
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@RestController
@RequestMapping("/api/v1/guwen")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GuwenController {
    
    private static final Logger logger = LoggerFactory.getLogger(GuwenController.class);
    
    @Autowired
    private GuwenService guwenService;
    
    /**
     * 获取古文列表（分页查询）
     * GET /api/v1/guwen?page=1&size=20&dynasty=唐&writer=李白&type=诗
     * 
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @param dynasty 朝代（可选）
     * @param writer 作者（可选）
     * @param type 类型（可选）
     * @return 古文分页列表
     */
    @GetMapping
    public Result<PageResult<GuwenDTO>> getGuwenList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String dynasty,
            @RequestParam(required = false) String writer,
            @RequestParam(required = false) String type) {
        
        logger.info("获取古文列表: page={}, size={}, dynasty={}, writer={}, type={}", 
                   page, size, dynasty, writer, type);
        
        PageResult<GuwenDTO> result = guwenService.getGuwenList(page, size, dynasty, writer, type);
        
        return Result.success(result, "获取古文列表成功");
    }
    
    /**
     * 根据ID获取古文详情
     * GET /api/v1/guwen/{id}
     * 
     * @param id 古文ID
     * @return 古文详情
     */
    @GetMapping("/{id}")
    public Result<GuwenDTO> getGuwenById(@PathVariable String id) {
        logger.info("获取古文详情: id={}", id);
        
        GuwenDTO guwenDTO = guwenService.getGuwenById(id);
        
        return Result.success(guwenDTO, "获取古文详情成功");
    }
    
    /**
     * 搜索古文
     * POST /api/v1/guwen/search
     * 
     * @param request 搜索请求
     * @return 古文分页列表
     */
    @PostMapping("/search")
    public Result<PageResult<GuwenDTO>> searchGuwen(@Valid @RequestBody GuwenSearchRequest request) {
        logger.info("搜索古文: {}", request);
        
        PageResult<GuwenDTO> result = guwenService.searchGuwen(request);
        
        return Result.success(result, "搜索古文成功");
    }
    
    /**
     * 获取热门古文
     * GET /api/v1/guwen/hot?period=daily&limit=10
     * 
     * @param period 时间周期（daily、weekly、monthly），默认daily
     * @param limit 限制数量，默认10
     * @return 热门古文列表
     */
    @GetMapping("/hot")
    public Result<List<GuwenDTO>> getHotGuwen(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        logger.info("获取热门古文: period={}, limit={}", period, limit);
        
        List<GuwenDTO> result = guwenService.getHotGuwen(period, limit);
        
        return Result.success(result, "获取热门古文成功");
    }
    
    /**
     * 获取随机古文
     * GET /api/v1/guwen/random?limit=5
     * 
     * @param limit 限制数量，默认5
     * @return 随机古文列表
     */
    @GetMapping("/random")
    public Result<List<GuwenDTO>> getRandomGuwen(@RequestParam(defaultValue = "5") Integer limit) {
        logger.info("获取随机古文: limit={}", limit);
        
        List<GuwenDTO> result = guwenService.getRandomGuwen(limit);
        
        return Result.success(result, "获取随机古文成功");
    }
    
    /**
     * 根据作者获取古文列表
     * GET /api/v1/guwen/by-writer/{writer}?page=1&size=20
     * 
     * @param writer 作者
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 古文分页列表
     */
    @GetMapping("/by-writer/{writer}")
    public Result<PageResult<GuwenDTO>> getGuwenByWriter(
            @PathVariable String writer,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        logger.info("根据作者获取古文: writer={}, page={}, size={}", writer, page, size);
        
        PageResult<GuwenDTO> result = guwenService.getGuwenByWriter(writer, page, size);
        
        return Result.success(result, "获取作者古文成功");
    }
    
    /**
     * 根据朝代获取古文列表
     * GET /api/v1/guwen/by-dynasty/{dynasty}?page=1&size=20
     * 
     * @param dynasty 朝代
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 古文分页列表
     */
    @GetMapping("/by-dynasty/{dynasty}")
    public Result<PageResult<GuwenDTO>> getGuwenByDynasty(
            @PathVariable String dynasty,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        logger.info("根据朝代获取古文: dynasty={}, page={}, size={}", dynasty, page, size);
        
        PageResult<GuwenDTO> result = guwenService.getGuwenByDynasty(dynasty, page, size);
        
        return Result.success(result, "获取朝代古文成功");
    }
    
    /**
     * 获取所有朝代列表
     * GET /api/v1/guwen/dynasties
     * 
     * @return 朝代列表
     */
    @GetMapping("/dynasties")
    public Result<List<String>> getAllDynasties() {
        logger.info("获取所有朝代列表");
        
        List<String> result = guwenService.getAllDynasties();
        
        return Result.success(result, "获取朝代列表成功");
    }
    
    /**
     * 获取所有作者列表
     * GET /api/v1/guwen/writers
     * 
     * @return 作者列表
     */
    @GetMapping("/writers")
    public Result<List<String>> getAllWriters() {
        logger.info("获取所有作者列表");
        
        List<String> result = guwenService.getAllWriters();
        
        return Result.success(result, "获取作者列表成功");
    }
    
    /**
     * 获取所有类型列表
     * GET /api/v1/guwen/types
     * 
     * @return 类型列表
     */
    @GetMapping("/types")
    public Result<List<String>> getAllTypes() {
        logger.info("获取所有类型列表");
        
        List<String> result = guwenService.getAllTypes();
        
        return Result.success(result, "获取类型列表成功");
    }
    
    /**
     * 获取古文统计信息
     * GET /api/v1/guwen/stats
     * 
     * @return 统计信息
     */
    @GetMapping("/stats")
    public Result<Object> getGuwenStats() {
        logger.info("获取古文统计信息");
        
        long totalCount = guwenService.getTotalCount();
        
        // 构建统计信息
        Object stats = new Object() {
            public final long total = totalCount;
        };
        
        return Result.success(stats, "获取统计信息成功");
    }
}
// {{END_MODIFICATIONS}}
