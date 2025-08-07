# 古诗文数据MongoDB导入工具

这个工具用于将古诗文教育数据导入MongoDB数据库。

## 数据结构

### 1. 古文数据 (guwen)
- **文件位置**: `database/guwen/*.json`
- **数据结构**:
  ```json
  {
    "_id": {"$oid": "..."},
    "title": "诗词标题",
    "dynasty": "朝代",
    "writer": "作者",
    "content": "诗词内容",
    "type": ["标签1", "标签2"],
    "remark": "注释",
    "shangxi": "赏析",
    "translation": "翻译",
    "audioUrl": "音频链接"
  }
  ```

### 2. 句子数据 (sentences)
- **文件位置**: `database/sentence/sentence1-10000.json`
- **数据结构**:
  ```json
  {
    "_id": {"$oid": "..."},
    "name": "诗句内容",
    "from": "出处"
  }
  ```

### 3. 作者数据 (writers)
- **文件位置**: `database/writer/*.json`
- **数据结构**:
  ```json
  {
    "_id": {"$oid": "..."},
    "name": "作者姓名",
    "headImageUrl": "头像链接",
    "simpleIntro": "简介",
    "detailIntro": "详细介绍(JSON字符串)"
  }
  ```

## 安装依赖

```bash
pip install -r requirements.txt
```

## 使用方法

### 1. 确保MongoDB服务运行
```bash
# 启动MongoDB服务
mongod
```

### 2. 运行导入脚本
```bash
python import_to_mongodb.py
```

### 3. 自定义配置
如需修改MongoDB连接配置，请编辑脚本中的以下变量：
```python
MONGODB_URI = "mongodb://localhost:27017/"  # MongoDB连接字符串
DATABASE_NAME = "poem_education"            # 数据库名称
```

## 功能特性

- ✅ **批量导入**: 支持大文件批量导入，避免内存溢出
- ✅ **错误处理**: 完善的错误处理和日志记录
- ✅ **重复检测**: 自动处理重复数据
- ✅ **索引创建**: 自动创建查询索引提高性能
- ✅ **进度显示**: 实时显示导入进度
- ✅ **统计信息**: 导入完成后显示统计信息

## 创建的集合

1. **guwen** - 古文诗词集合
2. **sentences** - 诗句集合  
3. **writers** - 作者集合

## 创建的索引

### guwen集合
- `title` - 标题索引
- `writer` - 作者索引
- `dynasty` - 朝代索引
- `title + writer` - 复合索引

### sentences集合
- `name` - 句子内容索引
- `from` - 出处索引

### writers集合
- `name` - 作者姓名唯一索引

## 日志文件

导入过程会生成 `mongodb_import.log` 日志文件，记录详细的导入信息和错误。

## 查询示例

导入完成后，可以使用以下MongoDB查询：

```javascript
// 查询李白的所有作品
db.guwen.find({"writer": "李白"})

// 查询唐代诗词
db.guwen.find({"dynasty": "唐代"})

// 查询包含"月"字的诗句
db.sentences.find({"name": /月/})

// 查询作者信息
db.writers.find({"name": "李白"})
```

## 注意事项

1. 确保MongoDB服务正在运行
2. 确保有足够的磁盘空间存储数据
3. 首次运行可能需要较长时间，请耐心等待
4. 如果需要重新导入，可以先删除数据库或集合

## 故障排除

### 连接失败
- 检查MongoDB服务是否启动
- 检查连接字符串是否正确
- 检查防火墙设置

### 导入失败
- 检查文件路径是否正确
- 检查文件格式是否为有效的JSON
- 查看日志文件获取详细错误信息

### 性能优化
- 可以调整批量插入大小 (`batch_size`)
- 考虑在导入前关闭索引，导入后重建
- 使用SSD硬盘可以提高导入速度
