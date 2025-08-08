#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymongo
from pymongo import MongoClient
import json
from datetime import datetime

def check_mongodb_pagination():
    """检查MongoDB中的分页数据问题"""
    try:
        # 连接MongoDB
        client = MongoClient('mongodb://localhost:27017/')
        db = client['poem_education']
        collection = db['guwen']
        
        print("=== MongoDB连接成功 ===")
        
        # 检查总数据量
        total_count = collection.count_documents({})
        print(f"总数据量: {total_count}")
        
        # 检查前5条数据的createdAt字段
        print("\n=== 检查前5条数据的createdAt字段 ===")
        docs = collection.find({}, {"title": 1, "createdAt": 1, "_id": 1}).limit(5)
        for i, doc in enumerate(docs, 1):
            print(f"{i}. ID: {doc.get('_id')}")
            print(f"   Title: {doc.get('title', 'N/A')}")
            print(f"   CreatedAt: {doc.get('createdAt', 'N/A')}")
            print()
        
        # 检查createdAt字段的分布情况
        print("=== 检查createdAt字段分布 ===")
        pipeline = [
            {"$group": {
                "_id": "$createdAt",
                "count": {"$sum": 1}
            }},
            {"$sort": {"count": -1}},
            {"$limit": 10}
        ]
        
        created_at_stats = list(collection.aggregate(pipeline))
        print("CreatedAt值分布（前10个）:")
        for stat in created_at_stats:
            print(f"  {stat['_id']}: {stat['count']}条记录")
        
        # 模拟分页查询 - 第1页
        print("\n=== 模拟分页查询 - 第1页 ===")
        page1 = collection.find({}).sort("createdAt", -1).skip(0).limit(3)
        print("第1页数据:")
        for i, doc in enumerate(page1, 1):
            print(f"{i}. {doc.get('title', 'N/A')} (ID: {str(doc.get('_id'))[:8]}...)")
        
        # 模拟分页查询 - 第2页
        print("\n=== 模拟分页查询 - 第2页 ===")
        page2 = collection.find({}).sort("createdAt", -1).skip(3).limit(3)
        print("第2页数据:")
        for i, doc in enumerate(page2, 1):
            print(f"{i}. {doc.get('title', 'N/A')} (ID: {str(doc.get('_id'))[:8]}...)")
        
        # 模拟分页查询 - 第3页
        print("\n=== 模拟分页查询 - 第3页 ===")
        page3 = collection.find({}).sort("createdAt", -1).skip(6).limit(3)
        print("第3页数据:")
        for i, doc in enumerate(page3, 1):
            print(f"{i}. {doc.get('title', 'N/A')} (ID: {str(doc.get('_id'))[:8]}...)")
        
        # 检查是否有重复的_id
        print("\n=== 检查数据唯一性 ===")
        pipeline_id_check = [
            {"$group": {
                "_id": "$_id",
                "count": {"$sum": 1}
            }},
            {"$match": {"count": {"$gt": 1}}}
        ]
        
        duplicate_ids = list(collection.aggregate(pipeline_id_check))
        if duplicate_ids:
            print(f"发现重复ID: {len(duplicate_ids)}个")
            for dup in duplicate_ids[:5]:
                print(f"  ID {dup['_id']}: {dup['count']}次")
        else:
            print("没有发现重复的_id")
        
        # 尝试不同的排序字段
        print("\n=== 尝试按_id排序的分页 ===")
        page1_by_id = collection.find({}).sort("_id", 1).skip(0).limit(3)
        print("按_id排序第1页:")
        for i, doc in enumerate(page1_by_id, 1):
            print(f"{i}. {doc.get('title', 'N/A')} (ID: {str(doc.get('_id'))[:8]}...)")
        
        page2_by_id = collection.find({}).sort("_id", 1).skip(3).limit(3)
        print("按_id排序第2页:")
        for i, doc in enumerate(page2_by_id, 1):
            print(f"{i}. {doc.get('title', 'N/A')} (ID: {str(doc.get('_id'))[:8]}...)")
        
    except Exception as e:
        print(f"错误: {e}")
    finally:
        if 'client' in locals():
            client.close()

if __name__ == "__main__":
    check_mongodb_pagination()
