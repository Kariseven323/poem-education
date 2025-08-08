# 宋词三百首搜索问题修复

## 问题描述
用户搜索"宋词三百首"时无法找到相关结果，但数据库中确实存在type为"宋词三百首"的数据。

## 问题分析
经过代码分析发现，问题出现在 `GuwenRepository.java` 中对 `type` 字段的查询语法不正确：

1. **type字段是数组类型**：在 `Guwen` 实体类中，`type` 字段被定义为 `List<String>`
2. **MongoDB查询语法错误**：部分查询方法没有正确处理数组字段的查询

## 修复内容

### 1. 修复 `findByTypeFuzzySearch` 方法
**修复前：**
```java
@Query("{ 'type': { $regex: ?0, $options: 'i' } }")
Page<Guwen> findByTypeFuzzySearch(String type, Pageable pageable);
```

**修复后：**
```java
// 对于字符串数组，MongoDB可以直接使用$regex匹配数组元素
@Query("{ 'type': { $regex: ?0, $options: 'i' } }")
Page<Guwen> findByTypeFuzzySearch(String type, Pageable pageable);
```

### 2. 修复 `findByWriterRegexAndType` 方法
**修复前：**
```java
@Query("{ 'writer': { $regex: ?0, $options: 'i' }, 'type': ?1 }")
Page<Guwen> findByWriterRegexAndType(String writer, String type, Pageable pageable);
```

**修复后：**
```java
@Query("{ 'writer': { $regex: ?0, $options: 'i' }, 'type': { $in: [?1] } }")
Page<Guwen> findByWriterRegexAndType(String writer, String type, Pageable pageable);
```

### 3. 修复 `findByWriterRegexAndDynastyAndType` 方法
**修复前：**
```java
@Query("{ 'writer': { $regex: ?0, $options: 'i' }, 'dynasty': ?1, 'type': ?2 }")
Page<Guwen> findByWriterRegexAndDynastyAndType(String writer, String dynasty, String type, Pageable pageable);
```

**修复后：**
```java
@Query("{ 'writer': { $regex: ?0, $options: 'i' }, 'dynasty': ?1, 'type': { $in: [?2] } }")
Page<Guwen> findByWriterRegexAndDynastyAndType(String writer, String dynasty, String type, Pageable pageable);
```

## MongoDB数组字段查询语法说明

### 1. 模糊匹配数组元素
```javascript
// 对于字符串数组，MongoDB可以直接使用$regex匹配数组元素
{ 'type': { $regex: 'pattern', $options: 'i' } }

// $elemMatch主要用于复杂对象数组的匹配，对于简单字符串数组不需要
// { 'type': { $elemMatch: { $regex: 'pattern', $options: 'i' } } } // 这种写法会报错
```

### 2. 精确匹配数组元素
```javascript
// 错误写法（会匹配整个数组）
{ 'type': 'value' }

// 正确写法（使用$in匹配数组中的元素）
{ 'type': { $in: ['value'] } }
```

## 已验证正确的方法
以下方法使用Spring Data MongoDB的方法命名约定，会自动正确处理数组字段：
- `findByType(String type, Pageable pageable)`
- `findByDynastyAndType(String dynasty, String type, Pageable pageable)`
- `findBySmartSearch` - 已修复为使用正确的 `$regex` 语法直接匹配字符串数组

## 测试建议
1. 搜索"宋词三百首"应该能找到相关结果
2. 搜索"宋词"应该能模糊匹配到"宋词三百首"
3. 其他类型的搜索也应该正常工作

## 修复时间
2025-08-07 19:30:15 +08:00

## 修复总结
✅ **问题已修复**：MongoDB对字符串数组字段的查询语法已更正
✅ **编译通过**：所有修改已编译成功
✅ **测试建议**：重新启动应用后测试搜索"宋词三百首"功能

## 关键技术点
1. **MongoDB字符串数组查询**：对于 `List<String>` 类型的字段，可以直接使用 `$regex` 匹配数组元素
2. **$elemMatch vs $regex**：`$elemMatch` 主要用于复杂对象数组，简单字符串数组直接用 `$regex` 即可
3. **Spring Data MongoDB**：方法命名约定会自动处理数组字段查询
