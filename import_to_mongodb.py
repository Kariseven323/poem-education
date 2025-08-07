#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
MongoDB数据导入脚本
用于将古文、句子和作者数据导入MongoDB数据库
"""

import json
import os
import glob
from pymongo import MongoClient
from pymongo.errors import DuplicateKeyError, BulkWriteError
from bson import ObjectId
import logging
from typing import List, Dict, Any
from datetime import datetime

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('mongodb_import.log', encoding='utf-8'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)


class MongoDBImporter:
    """MongoDB数据导入器"""
    
    def __init__(self, connection_string: str = "mongodb://localhost:27017/", 
                 database_name: str = "poem_education"):
        """
        初始化MongoDB连接
        
        Args:
            connection_string: MongoDB连接字符串
            database_name: 数据库名称
        """
        try:
            self.client = MongoClient(connection_string)
            self.db = self.client[database_name]
            logger.info(f"成功连接到MongoDB数据库: {database_name}")
        except Exception as e:
            logger.error(f"连接MongoDB失败: {e}")
            raise
    
    def convert_objectid(self, data: Dict[Any, Any]) -> Dict[Any, Any]:
        """
        转换MongoDB ObjectId格式

        Args:
            data: 原始数据字典

        Returns:
            转换后的数据字典
        """
        if isinstance(data, dict):
            if "_id" in data and isinstance(data["_id"], dict) and "$oid" in data["_id"]:
                try:
                    # 转换 {"$oid": "..."} 格式为 ObjectId
                    data["_id"] = ObjectId(data["_id"]["$oid"])
                except Exception as e:
                    logger.warning(f"ObjectId转换失败: {e}, 删除_id字段")
                    # 如果转换失败，删除_id字段让MongoDB自动生成
                    del data["_id"]

            # 递归处理嵌套字典
            for key, value in data.items():
                if isinstance(value, dict):
                    data[key] = self.convert_objectid(value)
                elif isinstance(value, list):
                    data[key] = [self.convert_objectid(item) if isinstance(item, dict) else item for item in value]

        return data

    def load_json_files(self, file_pattern: str) -> List[Dict[Any, Any]]:
        """
        加载JSON文件数据

        Args:
            file_pattern: 文件路径模式

        Returns:
            包含所有数据的列表
        """
        all_data = []
        files = glob.glob(file_pattern)

        if not files:
            logger.warning(f"未找到匹配的文件: {file_pattern}")
            return all_data

        for file_path in sorted(files):
            try:
                logger.info(f"正在读取文件: {file_path}")
                file_count = 0
                with open(file_path, 'r', encoding='utf-8') as f:
                    for line_num, line in enumerate(f, 1):
                        line = line.strip()
                        if line:
                            try:
                                data = json.loads(line)
                                # 转换ObjectId格式
                                data = self.convert_objectid(data)
                                all_data.append(data)
                                file_count += 1
                            except json.JSONDecodeError as e:
                                logger.error(f"文件 {file_path} 第 {line_num} 行JSON解析错误: {e}")
                                continue
                            except Exception as e:
                                logger.error(f"文件 {file_path} 第 {line_num} 行数据处理错误: {e}")
                                continue

                logger.info(f"文件 {file_path} 读取完成，本文件 {file_count} 条记录，累计 {len(all_data)} 条记录")

            except Exception as e:
                logger.error(f"读取文件 {file_path} 失败: {e}")
                continue

        return all_data
    
    def import_collection(self, collection_name: str, data: List[Dict[Any, Any]], 
                         batch_size: int = 1000) -> bool:
        """
        导入数据到指定集合
        
        Args:
            collection_name: 集合名称
            data: 要导入的数据
            batch_size: 批量插入大小
            
        Returns:
            导入是否成功
        """
        if not data:
            logger.warning(f"集合 {collection_name} 没有数据需要导入")
            return True
        
        collection = self.db[collection_name]
        
        try:
            # 清空现有数据（可选）
            # collection.delete_many({})
            # logger.info(f"已清空集合 {collection_name} 的现有数据")
            
            # 批量插入数据
            total_inserted = 0
            total_errors = 0
            
            for i in range(0, len(data), batch_size):
                batch = data[i:i + batch_size]
                try:
                    result = collection.insert_many(batch, ordered=False)
                    total_inserted += len(result.inserted_ids)
                    logger.info(f"集合 {collection_name}: 已插入 {len(result.inserted_ids)} 条记录 "
                              f"(总计: {total_inserted}/{len(data)})")
                    
                except BulkWriteError as e:
                    # 处理重复键错误
                    inserted_count = e.details.get('nInserted', 0)
                    total_inserted += inserted_count

                    write_errors = e.details.get('writeErrors', [])
                    duplicate_errors = sum(1 for error in write_errors
                                         if error.get('code') == 11000)
                    total_errors += len(write_errors)

                    # 记录详细错误信息
                    if write_errors and len(write_errors) <= 5:  # 只显示前5个错误
                        for error in write_errors[:5]:
                            logger.error(f"写入错误详情: {error}")

                    logger.warning(f"集合 {collection_name}: 批次插入部分成功，"
                                 f"插入 {inserted_count} 条，"
                                 f"重复 {duplicate_errors} 条，"
                                 f"其他错误 {len(write_errors) - duplicate_errors} 条")
                
                except Exception as e:
                    logger.error(f"集合 {collection_name}: 批次插入失败: {e}")
                    total_errors += len(batch)
                    continue
            
            logger.info(f"集合 {collection_name} 导入完成: "
                       f"成功 {total_inserted} 条，失败 {total_errors} 条")
            
            return total_errors == 0
            
        except Exception as e:
            logger.error(f"导入集合 {collection_name} 失败: {e}")
            return False
    
    def create_indexes(self):
        """创建索引以提高查询性能"""
        try:
            # 古文集合索引
            guwen_collection = self.db['guwen']
            guwen_collection.create_index("title")
            guwen_collection.create_index("writer")
            guwen_collection.create_index("dynasty")
            guwen_collection.create_index([("title", 1), ("writer", 1)])
            logger.info("古文集合索引创建完成")
            
            # 句子集合索引
            sentence_collection = self.db['sentences']
            sentence_collection.create_index("name")
            sentence_collection.create_index("from")
            logger.info("句子集合索引创建完成")
            
            # 作者集合索引
            writer_collection = self.db['writers']
            writer_collection.create_index("name", unique=True)
            logger.info("作者集合索引创建完成")
            
        except Exception as e:
            logger.error(f"创建索引失败: {e}")
    
    def get_collection_stats(self):
        """获取集合统计信息"""
        try:
            collections = ['guwen', 'sentences', 'writers']
            for collection_name in collections:
                count = self.db[collection_name].count_documents({})
                logger.info(f"集合 {collection_name}: {count} 条记录")
        except Exception as e:
            logger.error(f"获取统计信息失败: {e}")
    
    def close(self):
        """关闭数据库连接"""
        if self.client:
            self.client.close()
            logger.info("MongoDB连接已关闭")


def main():
    """主函数"""
    # 数据文件路径配置
    BASE_DIR = "database"
    GUWEN_PATTERN = os.path.join(BASE_DIR, "guwen", "*.json")
    SENTENCE_FILE = os.path.join(BASE_DIR, "sentence", "sentence1-10000.json")
    WRITER_PATTERN = os.path.join(BASE_DIR, "writer", "*.json")
    
    # MongoDB配置
    MONGODB_URI = "mongodb://localhost:27017/"
    DATABASE_NAME = "poem_education"
    
    try:
        # 创建导入器实例
        importer = MongoDBImporter(MONGODB_URI, DATABASE_NAME)
        
        logger.info("开始导入数据到MongoDB...")
        start_time = datetime.now()
        
        # 导入古文数据
        logger.info("=" * 50)
        logger.info("导入古文数据...")
        guwen_data = importer.load_json_files(GUWEN_PATTERN)
        if guwen_data:
            importer.import_collection('guwen', guwen_data)
        
        # 导入句子数据
        logger.info("=" * 50)
        logger.info("导入句子数据...")
        sentence_data = importer.load_json_files(SENTENCE_FILE)
        if sentence_data:
            importer.import_collection('sentences', sentence_data)
        
        # 导入作者数据
        logger.info("=" * 50)
        logger.info("导入作者数据...")
        writer_data = importer.load_json_files(WRITER_PATTERN)
        if writer_data:
            importer.import_collection('writers', writer_data)
        
        # 创建索引
        logger.info("=" * 50)
        logger.info("创建数据库索引...")
        importer.create_indexes()
        
        # 显示统计信息
        logger.info("=" * 50)
        logger.info("导入完成，统计信息:")
        importer.get_collection_stats()
        
        end_time = datetime.now()
        duration = end_time - start_time
        logger.info(f"总耗时: {duration}")
        
        # 关闭连接
        importer.close()
        
    except Exception as e:
        logger.error(f"导入过程发生错误: {e}")
        return 1
    
    return 0


if __name__ == "__main__":
    exit(main())
