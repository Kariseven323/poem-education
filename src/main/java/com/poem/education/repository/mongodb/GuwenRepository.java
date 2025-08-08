// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "0e54f46e-6ced-46bf-9b54-3a6819f266b3"
//   Timestamp: "2025-08-07T11:25:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Spring Data MongoDB Repository最佳实践"
//   Quality_Check: "编译通过，查询方法符合业务需求。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.repository.mongodb;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.poem.education.entity.mongodb.Guwen;

/**
 * 古文Repository接口
 * 提供古文相关的数据访问方法
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Repository
public interface GuwenRepository extends MongoRepository<Guwen, String> {
    
    /**
     * 根据标题查找古文
     * 
     * @param title 标题
     * @return 古文信息
     */
    Optional<Guwen> findByTitle(String title);
    
    /**
     * 根据作者查找古文列表
     * 
     * @param writer 作者
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    Page<Guwen> findByWriter(String writer, Pageable pageable);
    
    /**
     * 根据朝代查找古文列表
     * 
     * @param dynasty 朝代
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    Page<Guwen> findByDynasty(String dynasty, Pageable pageable);
    
    /**
     * 根据类型查找古文列表
     * 
     * @param type 类型
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    Page<Guwen> findByType(String type, Pageable pageable);
    
    /**
     * 根据作者和朝代查找古文列表
     * 
     * @param writer 作者
     * @param dynasty 朝代
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    Page<Guwen> findByWriterAndDynasty(String writer, String dynasty, Pageable pageable);
    
    /**
     * 根据标题和作者查找古文
     * 
     * @param title 标题
     * @param writer 作者
     * @return 古文信息
     */
    Optional<Guwen> findByTitleAndWriter(String title, String writer);
    
    /**
     * 根据标题模糊查询古文
     * 
     * @param title 标题关键字
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    Page<Guwen> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * 根据作者模糊查询古文
     * 
     * @param writer 作者关键字
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    Page<Guwen> findByWriterContainingIgnoreCase(String writer, Pageable pageable);
    
    /**
     * 根据内容模糊查询古文
     * 
     * @param content 内容关键字
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    Page<Guwen> findByContentContainingIgnoreCase(String content, Pageable pageable);
    
    /**
     * 统计特定作者的古文数量
     * 
     * @param writer 作者
     * @return 古文数量
     */
    long countByWriter(String writer);
    
    /**
     * 统计特定朝代的古文数量
     * 
     * @param dynasty 朝代
     * @return 古文数量
     */
    long countByDynasty(String dynasty);
    
    /**
     * 统计特定类型的古文数量
     * 
     * @param type 类型
     * @return 古文数量
     */
    long countByType(String type);
    
    /**
     * 查找所有朝代列表
     * 
     * @return 朝代列表
     */
    @Query(value = "{}", fields = "{ 'dynasty' : 1 }")
    List<Guwen> findAllDynasties();
    
    /**
     * 查找所有作者列表
     * 
     * @return 作者列表
     */
    @Query(value = "{}", fields = "{ 'writer' : 1, 'dynasty' : 1 }")
    List<Guwen> findAllWriters();
    
    /**
     * 查找所有类型列表
     * 
     * @return 类型列表
     */
    @Query(value = "{}", fields = "{ 'type' : 1 }")
    List<Guwen> findAllTypes();
    
    /**
     * 全文搜索古文
     * 使用MongoDB的文本索引进行搜索
     *
     * @param keyword 搜索关键字
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    @Query("{ $text: { $search: ?0 } }")
    Page<Guwen> findByTextSearch(String keyword, Pageable pageable);

    /**
     * 增强的模糊搜索 - 支持标题、内容、作者的模糊匹配
     *
     * @param keyword 搜索关键字
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    @Query("{ $or: [ " +
           "{ 'title': { $regex: ?0, $options: 'i' } }, " +
           "{ 'content': { $regex: ?0, $options: 'i' } }, " +
           "{ 'writer': { $regex: ?0, $options: 'i' } }, " +
           "{ 'remark': { $regex: ?0, $options: 'i' } }, " +
           "{ 'shangxi': { $regex: ?0, $options: 'i' } }, " +
           "{ 'translation': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<Guwen> findByKeywordFuzzySearch(String keyword, Pageable pageable);

    /**
     * 按类型模糊搜索
     * 注意：type字段是字符串数组，使用$regex直接匹配数组元素
     *
     * @param type 类型关键字
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    @Query("{ 'type': { $regex: ?0, $options: 'i' } }")
    Page<Guwen> findByTypeFuzzySearch(String type, Pageable pageable);

    /**
     * 智能搜索 - 综合多种搜索策略
     * 支持标题、内容、作者、类型的智能匹配
     * 注意：type字段是字符串数组，使用$regex直接匹配数组元素
     *
     * @param keyword 搜索关键字
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    @Query("{ $or: [ " +
           "{ 'title': { $regex: ?0, $options: 'i' } }, " +
           "{ 'content': { $regex: ?0, $options: 'i' } }, " +
           "{ 'writer': { $regex: ?0, $options: 'i' } }, " +
           "{ 'type': { $regex: ?0, $options: 'i' } }, " +
           "{ 'remark': { $regex: ?0, $options: 'i' } }, " +
           "{ 'shangxi': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<Guwen> findBySmartSearch(String keyword, Pageable pageable);


    
    /**
     * 高级搜索古文
     * 支持多条件组合搜索
     * 
     * @param title 标题关键字
     * @param writer 作者关键字
     * @param dynasty 朝代
     * @param type 类型
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    /**
     * 根据作者和朝代查找古文列表 - 支持模糊匹配
     *
     * @param writer 作者（支持模糊匹配）
     * @param dynasty 朝代（精确匹配）
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    @Query("{ 'writer': { $regex: ?0, $options: 'i' }, 'dynasty': ?1 }")
    Page<Guwen> findByWriterRegexAndDynasty(String writer, String dynasty, Pageable pageable);

    /**
     * 根据作者和类型查找古文列表 - 支持模糊匹配
     * 注意：type字段是数组，需要使用数组匹配语法
     *
     * @param writer 作者（支持模糊匹配）
     * @param type 类型（精确匹配）
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    @Query("{ 'writer': { $regex: ?0, $options: 'i' }, 'type': { $in: [?1] } }")
    Page<Guwen> findByWriterRegexAndType(String writer, String type, Pageable pageable);

    /**
     * 根据朝代和类型查找古文列表
     *
     * @param dynasty 朝代（精确匹配）
     * @param type 类型（精确匹配）
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    Page<Guwen> findByDynastyAndType(String dynasty, String type, Pageable pageable);

    /**
     * 根据作者、朝代和类型查找古文列表 - 支持作者模糊匹配
     * 注意：type字段是数组，需要使用数组匹配语法
     *
     * @param writer 作者（支持模糊匹配）
     * @param dynasty 朝代（精确匹配）
     * @param type 类型（精确匹配）
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    @Query("{ 'writer': { $regex: ?0, $options: 'i' }, 'dynasty': ?1, 'type': { $in: [?2] } }")
    Page<Guwen> findByWriterRegexAndDynastyAndType(String writer, String dynasty, String type, Pageable pageable);

    /**
     * 根据作者模糊查询古文 - 改进版
     *
     * @param writer 作者关键字
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    @Query("{ 'writer': { $regex: ?0, $options: 'i' } }")
    Page<Guwen> findByWriterRegex(String writer, Pageable pageable);
    
    /**
     * 查找随机古文
     * 
     * @param size 数量
     * @return 古文列表
     */
    @Query("{ $sample: { size: ?0 } }")
    List<Guwen> findRandomGuwen(int size);
    
    /**
     * 查找特定作者的代表作品
     * 
     * @param writer 作者
     * @param limit 限制数量
     * @return 古文列表
     */
    List<Guwen> findByWriterOrderByCreatedAtDesc(String writer, Pageable pageable);
    
    /**
     * 查找最新添加的古文
     * 
     * @param pageable 分页参数
     * @return 古文分页列表
     */
    Page<Guwen> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 检查标题和作者的组合是否存在
     * 
     * @param title 标题
     * @param writer 作者
     * @return 是否存在
     */
    boolean existsByTitleAndWriter(String title, String writer);
}
// {{END_MODIFICATIONS}}
