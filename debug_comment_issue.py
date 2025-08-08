#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
评论回复功能问题调试脚本
分析为什么回复评论被认为是首评论的问题
"""

from pymongo import MongoClient
import json
from bson import ObjectId
from datetime import datetime

def debug_comment_issue():
    """调试评论回复功能问题"""
    
    # 连接MongoDB
    try:
        client = MongoClient("mongodb://localhost:27017/")
        db = client["poem_education"]
        print("✅ 成功连接到MongoDB")
    except Exception as e:
        print(f"❌ 连接失败: {e}")
        return
    
    print("\n" + "="*60)
    print("🔍 评论数据结构分析")
    print("="*60)
    
    # 检查comments集合是否存在
    collections = db.list_collection_names()
    if 'comments' not in collections:
        print("❌ comments集合不存在")
        return
    
    # 统计评论数据
    total_comments = db.comments.count_documents({})
    print(f"📊 总评论数: {total_comments}")
    
    if total_comments == 0:
        print("❌ 没有评论数据")
        return
    
    # 分析评论结构
    print("\n🔍 评论数据结构分析:")
    sample_comment = db.comments.find_one()
    if sample_comment:
        print("📝 样本评论结构:")
        for key, value in sample_comment.items():
            print(f"  {key}: {type(value).__name__} = {value}")
    
    print("\n" + "="*60)
    print("🔍 parentId字段分析")
    print("="*60)
    
    # 检查parentId字段的情况
    comments_with_parent = db.comments.count_documents({"parentId": {"$exists": True, "$ne": None}})
    comments_without_parent = db.comments.count_documents({"parentId": {"$exists": False}})
    comments_with_null_parent = db.comments.count_documents({"parentId": None})
    
    print(f"📊 parentId字段统计:")
    print(f"  有parentId字段且不为null: {comments_with_parent}")
    print(f"  没有parentId字段: {comments_without_parent}")
    print(f"  parentId为null: {comments_with_null_parent}")
    
    # 查看具体的parentId数据
    print(f"\n🔍 parentId字段详细分析:")
    parent_id_analysis = db.comments.aggregate([
        {
            "$group": {
                "_id": {
                    "hasParentId": {"$ne": ["$parentId", None]},
                    "parentIdType": {"$type": "$parentId"}
                },
                "count": {"$sum": 1},
                "samples": {"$push": {"_id": "$_id", "parentId": "$parentId", "content": "$content"}}
            }
        }
    ])
    
    for group in parent_id_analysis:
        print(f"  类型: {group['_id']}, 数量: {group['count']}")
        if group['count'] <= 3:  # 只显示少量样本
            for sample in group['samples'][:3]:
                print(f"    样本: _id={sample['_id']}, parentId={sample['parentId']}, content={sample['content'][:50]}...")
    
    print("\n" + "="*60)
    print("🔍 层级结构分析")
    print("="*60)
    
    # 分析level字段
    level_stats = db.comments.aggregate([
        {
            "$group": {
                "_id": "$level",
                "count": {"$sum": 1}
            }
        },
        {"$sort": {"_id": 1}}
    ])
    
    print(f"📊 评论层级分布:")
    for stat in level_stats:
        print(f"  Level {stat['_id']}: {stat['count']} 条评论")
    
    print("\n" + "="*60)
    print("🔍 回复关系验证")
    print("="*60)
    
    # 查找所有回复评论（应该有parentId）
    reply_comments = list(db.comments.find({"parentId": {"$exists": True, "$ne": None}}).limit(5))
    print(f"📝 回复评论样本 (前5条):")
    
    for i, reply in enumerate(reply_comments, 1):
        print(f"\n  回复 {i}:")
        print(f"    _id: {reply['_id']}")
        print(f"    parentId: {reply.get('parentId', 'MISSING')}")
        print(f"    level: {reply.get('level', 'MISSING')}")
        print(f"    path: {reply.get('path', 'MISSING')}")
        print(f"    content: {reply['content'][:100]}...")
        
        # 验证父评论是否存在
        if 'parentId' in reply and reply['parentId']:
            parent_exists = db.comments.find_one({"_id": reply['parentId']})
            print(f"    父评论存在: {'✅' if parent_exists else '❌'}")
            if parent_exists:
                print(f"    父评论内容: {parent_exists['content'][:50]}...")
    
    print("\n" + "="*60)
    print("🔍 数据类型问题检查")
    print("="*60)
    
    # 检查parentId的数据类型问题
    print(f"📊 parentId数据类型分析:")
    
    # 查找parentId为字符串的情况
    string_parent_ids = list(db.comments.find({"parentId": {"$type": "string"}}).limit(3))
    if string_parent_ids:
        print(f"  发现 {len(string_parent_ids)} 条parentId为字符串类型的记录:")
        for comment in string_parent_ids:
            print(f"    _id: {comment['_id']}, parentId: '{comment['parentId']}' (string)")
    
    # 查找parentId为ObjectId的情况
    objectid_parent_ids = list(db.comments.find({"parentId": {"$type": "objectId"}}).limit(3))
    if objectid_parent_ids:
        print(f"  发现 {len(objectid_parent_ids)} 条parentId为ObjectId类型的记录:")
        for comment in objectid_parent_ids:
            print(f"    _id: {comment['_id']}, parentId: {comment['parentId']} (ObjectId)")
    
    print("\n" + "="*60)
    print("🔍 查询逻辑验证")
    print("="*60)
    
    # 模拟后端查询逻辑
    print(f"📊 模拟后端查询逻辑:")
    
    # 查询顶级评论（无父评论）
    top_level_query1 = {"parentId": {"$exists": False}}
    top_level_count1 = db.comments.count_documents(top_level_query1)
    print(f"  查询1 - parentId不存在: {top_level_count1} 条")
    
    top_level_query2 = {"parentId": None}
    top_level_count2 = db.comments.count_documents(top_level_query2)
    print(f"  查询2 - parentId为null: {top_level_count2} 条")
    
    top_level_query3 = {"$or": [{"parentId": {"$exists": False}}, {"parentId": None}]}
    top_level_count3 = db.comments.count_documents(top_level_query3)
    print(f"  查询3 - parentId不存在或为null: {top_level_count3} 条")
    
    # 检查Repository中的查询方法
    print(f"\n📊 Repository查询方法验证:")
    print(f"  findTopLevelComments查询应该使用: {{'parentId': {{'$exists': false}}}}")
    
    print("\n" + "="*60)
    print("💡 问题诊断建议")
    print("="*60)
    
    print("🔍 可能的问题原因:")
    print("1. parentId字段数据类型不一致（字符串 vs ObjectId）")
    print("2. 前端发送的parentId格式不正确")
    print("3. 后端保存时parentId处理逻辑有误")
    print("4. MongoDB查询条件不正确")
    print("5. 前端显示逻辑没有正确处理层级关系")
    
    print("\n🛠️ 建议检查:")
    print("1. 检查CommentServiceImpl.createComment()中parentId的处理")
    print("2. 检查前端发送的请求数据格式")
    print("3. 检查CommentRepository的查询方法")
    print("4. 检查前端评论显示逻辑是否支持层级结构")
    
    client.close()
    print(f"\n✅ 调试分析完成!")

if __name__ == "__main__":
    debug_comment_issue()
