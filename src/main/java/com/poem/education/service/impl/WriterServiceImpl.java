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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Autowired
    private MongoTemplate mongoTemplate;
    
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

        // 使用聚合查询去重并分页
        List<WriterDTO> writerDTOList = getUniqueWritersWithPagination(page, size, dynasty, null);
        long totalCount = getUniqueWritersCount(dynasty, null);

        return PageResult.of(writerDTOList, page, size, totalCount);
    }
    
    @Override
    public PageResult<WriterDTO> searchWriters(String keyword, Integer page, Integer size) {
        logger.info("搜索作者: keyword={}, page={}, size={}", keyword, page, size);

        // 如果有关键字，使用智能搜索（精确匹配优先 + 去重）
        if (StringUtils.hasText(keyword)) {
            // 使用智能搜索并实现精确匹配优先排序
            List<WriterDTO> writerDTOList = searchWritersWithSmartSort(keyword, page, size);
            long totalCount = getUniqueWritersCount(null, keyword);
            return PageResult.of(writerDTOList, page, size, totalCount);
        } else {
            // 没有关键字时，使用普通列表查询
            return getWriterList(page, size, null);
        }
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

        // 处理detailIntro字段的JSON解析和文本格式化
        if (writer.getDetailIntro() != null && !writer.getDetailIntro().trim().isEmpty()) {
            String formattedDetailIntro = formatDetailIntro(writer.getDetailIntro());
            writerDTO.setDetailIntro(formattedDetailIntro);
        }

        return writerDTO;
    }

    /**
     * 格式化detailIntro字段，解析JSON并将换行符转换为HTML标签
     *
     * @param jsonDetailIntro JSON格式的详细介绍字符串
     * @return 格式化后的HTML字符串
     */
    private String formatDetailIntro(String jsonDetailIntro) {
        try {
            // 解析JSON字符串
            com.alibaba.fastjson2.JSONObject jsonObject = com.alibaba.fastjson2.JSON.parseObject(jsonDetailIntro);

            if (jsonObject == null || jsonObject.isEmpty()) {
                return jsonDetailIntro; // 如果解析失败或为空，返回原始字符串
            }

            StringBuilder formattedHtml = new StringBuilder();

            // 定义字段显示顺序和中文标题
            String[] fieldOrder = {"人物生平", "主要成就", "轶事典故", "后世纪念", "家庭成员", "评价", "介绍"};

            for (String fieldName : fieldOrder) {
                String content = jsonObject.getString(fieldName);
                if (content != null && !content.trim().isEmpty()) {
                    formattedHtml.append("<div class=\"detail-section\">")
                               .append("<h4 class=\"section-title\">").append(fieldName).append("</h4>")
                               .append("<div class=\"section-content\">")
                               .append(formatTextContent(content))
                               .append("</div>")
                               .append("</div>");
                }
            }

            // 处理其他未在预定义顺序中的字段
            for (String key : jsonObject.keySet()) {
                if (!java.util.Arrays.asList(fieldOrder).contains(key)) {
                    String content = jsonObject.getString(key);
                    if (content != null && !content.trim().isEmpty()) {
                        formattedHtml.append("<div class=\"detail-section\">")
                                   .append("<h4 class=\"section-title\">").append(key).append("</h4>")
                                   .append("<div class=\"section-content\">")
                                   .append(formatTextContent(content))
                                   .append("</div>")
                                   .append("</div>");
                    }
                }
            }

            return formattedHtml.toString();

        } catch (Exception e) {
            logger.warn("解析detailIntro JSON失败，返回原始字符串: {}", e.getMessage());
            // 如果JSON解析失败，尝试简单的文本格式化
            return formatTextContent(jsonDetailIntro);
        }
    }

    /**
     * 格式化文本内容，将换行符转换为HTML标签
     *
     * @param text 原始文本
     * @return 格式化后的HTML文本
     */
    private String formatTextContent(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        // 将换行符转换为HTML换行标签
        String formatted = text.replace("\\n", "<br/>")
                              .replace("\n", "<br/>")
                              .replace("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;") // 制表符转换为空格
                              .trim();

        // 基本的HTML转义，防止XSS攻击
        formatted = formatted.replace("&", "&amp;")
                           .replace("<script", "&lt;script")
                           .replace("</script>", "&lt;/script&gt;")
                           .replace("javascript:", "")
                           .replace("onclick", "")
                           .replace("onload", "")
                           .replace("onerror", "");

        // 恢复我们需要的HTML标签
        formatted = formatted.replace("&lt;br/&gt;", "<br/>")
                           .replace("&lt;br&gt;", "<br/>")
                           .replace("&lt;/br&gt;", "");

        return formatted;
    }

    /**
     * 使用聚合查询获取去重的作者列表（分页）
     *
     * @param page 页码
     * @param size 每页大小
     * @param dynasty 朝代筛选
     * @param keyword 关键词搜索
     * @return 去重的作者列表
     */
    private List<WriterDTO> getUniqueWritersWithPagination(Integer page, Integer size, String dynasty, String keyword) {
        logger.info("执行去重分页查询: page={}, size={}, dynasty={}, keyword={}", page, size, dynasty, keyword);

        // 构建聚合管道
        Aggregation aggregation = buildUniqueWritersAggregation(page, size, dynasty, keyword, true);

        // 执行聚合查询
        AggregationResults<Writer> results = mongoTemplate.aggregate(aggregation, "writers", Writer.class);

        // 转换为DTO
        return results.getMappedResults().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取去重后的作者总数
     *
     * @param dynasty 朝代筛选
     * @param keyword 关键词搜索
     * @return 去重后的总数
     */
    private long getUniqueWritersCount(String dynasty, String keyword) {
        logger.info("获取去重总数: dynasty={}, keyword={}", dynasty, keyword);

        // 构建聚合管道（不分页）
        Aggregation aggregation = buildUniqueWritersAggregation(null, null, dynasty, keyword, false);

        // 执行聚合查询
        AggregationResults<Writer> results = mongoTemplate.aggregate(aggregation, "writers", Writer.class);

        return results.getMappedResults().size();
    }

    /**
     * 构建去重聚合查询管道
     *
     * @param page 页码（null表示不分页）
     * @param size 每页大小（null表示不分页）
     * @param dynasty 朝代筛选
     * @param keyword 关键词搜索
     * @param withPagination 是否包含分页
     * @return 聚合查询对象
     */
    private Aggregation buildUniqueWritersAggregation(Integer page, Integer size, String dynasty, String keyword, boolean withPagination) {
        // 构建聚合操作列表
        List<AggregationOperation> operations = new ArrayList<>();

        // 第一步：可选的筛选条件
        if (StringUtils.hasText(dynasty) || StringUtils.hasText(keyword)) {
            Criteria criteria = new Criteria();

            if (StringUtils.hasText(dynasty) && StringUtils.hasText(keyword)) {
                // 同时有朝代和关键词筛选
                criteria = criteria.andOperator(
                    Criteria.where("dynasty").is(dynasty),
                    new Criteria().orOperator(
                        Criteria.where("name").regex(keyword, "i"),
                        Criteria.where("simpleIntro").regex(keyword, "i")
                    )
                );
            } else if (StringUtils.hasText(dynasty)) {
                // 只有朝代筛选
                criteria = Criteria.where("dynasty").is(dynasty);
            } else {
                // 只有关键词筛选
                criteria = new Criteria().orOperator(
                    Criteria.where("name").regex(keyword, "i"),
                    Criteria.where("simpleIntro").regex(keyword, "i")
                );
            }

            operations.add(Aggregation.match(criteria));
        }

        // 第二步：智能排序（精确匹配优先）
        if (StringUtils.hasText(keyword)) {
            // 使用简单的排序策略：精确匹配的姓名排在前面
            operations.add(Aggregation.sort(Sort.by(
                Sort.Order.asc("name").with(Sort.NullHandling.NULLS_LAST)
            )));
        } else {
            // 没有关键词时，按姓名排序
            operations.add(Aggregation.sort(Sort.by(Sort.Order.asc("name"))));
        }

        // 第三步：按姓名分组，选择第一个记录（去重）
        operations.add(Aggregation.group("name").first("$$ROOT").as("doc"));

        // 第四步：恢复文档结构
        operations.add(Aggregation.replaceRoot("doc"));

        // 第五步：最终排序（保持智能排序）
        if (StringUtils.hasText(keyword)) {
            operations.add(Aggregation.sort(Sort.by(Sort.Order.asc("sortWeight"), Sort.Order.asc("name"))));
        } else {
            operations.add(Aggregation.sort(Sort.by(Sort.Order.asc("name"))));
        }

        // 第六步：分页（如果需要）
        if (withPagination && page != null && size != null) {
            operations.add(Aggregation.skip((long) (page - 1) * size));
            operations.add(Aggregation.limit(size));
        }

        return Aggregation.newAggregation(operations);
    }

    /**
     * 智能搜索作者 - 精确匹配优先，去重
     *
     * @param keyword 搜索关键字
     * @param page 页码
     * @param size 每页大小
     * @return 作者DTO列表
     */
    private List<WriterDTO> searchWritersWithSmartSort(String keyword, Integer page, Integer size) {
        logger.info("智能搜索作者: keyword={}, page={}, size={}", keyword, page, size);

        // 使用智能搜索Repository方法
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Writer> writerPage = writerRepository.findBySmartSearch(keyword, pageable);

        // 转换为DTO并去重
        List<WriterDTO> allWriters = writerPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 手动实现精确匹配优先排序和去重
        List<WriterDTO> exactMatches = new ArrayList<>();
        List<WriterDTO> partialMatches = new ArrayList<>();
        Set<String> seenNames = new HashSet<>();

        for (WriterDTO writer : allWriters) {
            // 去重：如果已经见过这个姓名，跳过
            if (seenNames.contains(writer.getName())) {
                continue;
            }
            seenNames.add(writer.getName());

            // 分类：精确匹配 vs 部分匹配
            if (keyword.equalsIgnoreCase(writer.getName())) {
                exactMatches.add(writer);
            } else {
                partialMatches.add(writer);
            }
        }

        // 合并结果：精确匹配在前，部分匹配在后
        List<WriterDTO> result = new ArrayList<>();
        result.addAll(exactMatches);
        result.addAll(partialMatches);

        return result;
    }
}
// {{END_MODIFICATIONS}}
