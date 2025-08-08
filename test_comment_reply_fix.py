#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
评论回复功能修复测试脚本
测试parentId是否能正确保存和显示
"""

import requests
import json
import time
from pymongo import MongoClient

# 配置
API_BASE = "http://localhost:8080/api/v1"
VALID_OBJECT_ID = "5b9a0254367d5caccce1aa13"

def test_comment_reply_fix():
    """测试评论回复功能修复"""
    
    print("=" * 60)
    print("🧪 评论回复功能修复测试")
    print("=" * 60)
    
    # 连接MongoDB检查数据
    try:
        client = MongoClient("mongodb://localhost:27017/")
        db = client["poem_education"]
        print("✅ 成功连接到MongoDB")
    except Exception as e:
        print(f"❌ MongoDB连接失败: {e}")
        return
    
    # 清理测试数据
    print("\n🧹 清理旧的测试数据...")
    delete_result = db.comments.delete_many({"content": {"$regex": "测试.*"}})
    print(f"删除了 {delete_result.deleted_count} 条测试评论")
    
    print("\n" + "=" * 60)
    print("📝 步骤1: 创建顶级评论")
    print("=" * 60)
    
    # 创建顶级评论
    comment_data = {
        "targetId": VALID_OBJECT_ID,
        "targetType": "guwen",
        "content": "测试顶级评论 - 用于回复测试"
    }
    
    print(f"📤 发送顶级评论请求: {json.dumps(comment_data, ensure_ascii=False)}")
    
    try:
        response = requests.post(
            f"{API_BASE}/comments",
            json=comment_data,
            headers={"Content-Type": "application/json"},
            timeout=10
        )
        
        print(f"📥 响应状态码: {response.status_code}")
        print(f"📥 响应内容: {response.text}")
        
        if response.status_code == 200:
            parent_comment = response.json()
            if parent_comment.get("code") == 200:
                parent_comment_data = parent_comment["data"]
                parent_comment_id = parent_comment_data["id"]
                print(f"✅ 顶级评论创建成功: ID = {parent_comment_id}")
                
                # 验证数据库中的顶级评论
                db_comment = db.comments.find_one({"_id": parent_comment_id})
                if db_comment:
                    print(f"✅ 数据库验证: 顶级评论存在")
                    print(f"   - _id: {db_comment['_id']}")
                    print(f"   - parentId: {db_comment.get('parentId', 'MISSING')}")
                    print(f"   - level: {db_comment.get('level', 'MISSING')}")
                    print(f"   - content: {db_comment['content']}")
                else:
                    print(f"❌ 数据库验证失败: 找不到顶级评论")
                    return
                
                print("\n" + "=" * 60)
                print("📝 步骤2: 创建回复评论")
                print("=" * 60)
                
                # 创建回复评论
                reply_data = {
                    "targetId": VALID_OBJECT_ID,
                    "targetType": "guwen", 
                    "content": "测试回复评论 - 这是对顶级评论的回复",
                    "parentId": parent_comment_id
                }
                
                print(f"📤 发送回复评论请求: {json.dumps(reply_data, ensure_ascii=False)}")
                print(f"🔍 关键信息: parentId = '{parent_comment_id}'")
                
                time.sleep(1)  # 等待1秒确保时间戳不同
                
                reply_response = requests.post(
                    f"{API_BASE}/comments",
                    json=reply_data,
                    headers={"Content-Type": "application/json"},
                    timeout=10
                )
                
                print(f"📥 回复响应状态码: {reply_response.status_code}")
                print(f"📥 回复响应内容: {reply_response.text}")
                
                if reply_response.status_code == 200:
                    reply_comment = reply_response.json()
                    if reply_comment.get("code") == 200:
                        reply_comment_data = reply_comment["data"]
                        reply_comment_id = reply_comment_data["id"]
                        print(f"✅ 回复评论创建成功: ID = {reply_comment_id}")
                        
                        # 验证数据库中的回复评论
                        db_reply = db.comments.find_one({"_id": reply_comment_id})
                        if db_reply:
                            print(f"✅ 数据库验证: 回复评论存在")
                            print(f"   - _id: {db_reply['_id']}")
                            print(f"   - parentId: {db_reply.get('parentId', 'MISSING')}")
                            print(f"   - level: {db_reply.get('level', 'MISSING')}")
                            print(f"   - content: {db_reply['content']}")
                            
                            # 关键验证点
                            print(f"\n🔍 关键验证:")
                            parent_id_in_db = db_reply.get('parentId')
                            level_in_db = db_reply.get('level')
                            
                            if parent_id_in_db is not None:
                                print(f"✅ parentId已保存: {parent_id_in_db}")
                                if str(parent_id_in_db) == parent_comment_id:
                                    print(f"✅ parentId正确: 指向父评论")
                                else:
                                    print(f"❌ parentId错误: 期望={parent_comment_id}, 实际={parent_id_in_db}")
                            else:
                                print(f"❌ parentId未保存: 值为null")
                            
                            if level_in_db == 2:
                                print(f"✅ level正确: {level_in_db} (回复评论)")
                            else:
                                print(f"❌ level错误: 期望=2, 实际={level_in_db}")
                                
                        else:
                            print(f"❌ 数据库验证失败: 找不到回复评论")
                    else:
                        print(f"❌ 回复评论创建失败: {reply_comment}")
                else:
                    print(f"❌ 回复请求失败: 状态码 {reply_response.status_code}")
            else:
                print(f"❌ 顶级评论创建失败: {parent_comment}")
        else:
            print(f"❌ 顶级评论请求失败: 状态码 {response.status_code}")
            
    except requests.exceptions.RequestException as e:
        print(f"❌ 请求异常: {e}")
    except Exception as e:
        print(f"❌ 测试异常: {e}")
    
    print("\n" + "=" * 60)
    print("📊 最终数据库状态检查")
    print("=" * 60)
    
    # 检查所有测试评论
    test_comments = list(db.comments.find({"content": {"$regex": "测试.*"}}).sort("createdAt", 1))
    print(f"📊 测试评论总数: {len(test_comments)}")
    
    for i, comment in enumerate(test_comments, 1):
        print(f"\n评论 {i}:")
        print(f"  _id: {comment['_id']}")
        print(f"  parentId: {comment.get('parentId', 'MISSING')}")
        print(f"  level: {comment.get('level', 'MISSING')}")
        print(f"  content: {comment['content'][:50]}...")
    
    # 统计层级分布
    level_stats = {}
    for comment in test_comments:
        level = comment.get('level', 'MISSING')
        level_stats[level] = level_stats.get(level, 0) + 1
    
    print(f"\n📊 测试评论层级分布:")
    for level, count in sorted(level_stats.items()):
        print(f"  Level {level}: {count} 条")
    
    # 检查回复关系
    parent_child_pairs = 0
    for comment in test_comments:
        if comment.get('parentId'):
            parent_exists = any(c['_id'] == comment['parentId'] for c in test_comments)
            if parent_exists:
                parent_child_pairs += 1
                print(f"✅ 发现有效的父子关系: {comment['_id']} -> {comment['parentId']}")
    
    print(f"\n📊 有效的父子关系数量: {parent_child_pairs}")
    
    client.close()
    print(f"\n✅ 测试完成!")

if __name__ == "__main__":
    test_comment_reply_fix()
