#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试评论层级显示功能
验证前后端是否正确处理评论的层级结构
"""

import requests
import json
import time
from pymongo import MongoClient

# 配置
API_BASE = "http://localhost:8080/api/v1"
VALID_OBJECT_ID = "5b9a0254367d5caccce1aa13"

def test_comment_hierarchy():
    """测试评论层级显示功能"""
    
    print("=" * 60)
    print("🧪 评论层级显示功能测试")
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
    delete_result = db.comments.delete_many({"content": {"$regex": "层级测试.*"}})
    print(f"删除了 {delete_result.deleted_count} 条测试评论")
    
    print("\n" + "=" * 60)
    print("📝 步骤1: 创建顶级评论")
    print("=" * 60)
    
    # 创建顶级评论
    comment_data = {
        "targetId": VALID_OBJECT_ID,
        "targetType": "guwen",
        "content": "层级测试 - 这是顶级评论"
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
        
        if response.status_code == 200:
            parent_comment = response.json()
            if parent_comment.get("code") == 200:
                parent_comment_data = parent_comment["data"]
                parent_comment_id = parent_comment_data["id"]
                print(f"✅ 顶级评论创建成功: ID = {parent_comment_id}")
                
                time.sleep(1)  # 等待1秒
                
                print("\n" + "=" * 60)
                print("📝 步骤2: 创建一级回复")
                print("=" * 60)
                
                # 创建一级回复
                reply1_data = {
                    "targetId": VALID_OBJECT_ID,
                    "targetType": "guwen", 
                    "content": "层级测试 - 这是一级回复",
                    "parentId": parent_comment_id
                }
                
                print(f"📤 发送一级回复请求: {json.dumps(reply1_data, ensure_ascii=False)}")
                
                reply1_response = requests.post(
                    f"{API_BASE}/comments",
                    json=reply1_data,
                    headers={"Content-Type": "application/json"},
                    timeout=10
                )
                
                print(f"📥 一级回复响应状态码: {reply1_response.status_code}")
                
                if reply1_response.status_code == 200:
                    reply1_comment = reply1_response.json()
                    if reply1_comment.get("code") == 200:
                        reply1_comment_data = reply1_comment["data"]
                        reply1_comment_id = reply1_comment_data["id"]
                        print(f"✅ 一级回复创建成功: ID = {reply1_comment_id}")
                        
                        time.sleep(1)  # 等待1秒
                        
                        print("\n" + "=" * 60)
                        print("📝 步骤3: 创建二级回复")
                        print("=" * 60)
                        
                        # 创建二级回复
                        reply2_data = {
                            "targetId": VALID_OBJECT_ID,
                            "targetType": "guwen", 
                            "content": "层级测试 - 这是二级回复",
                            "parentId": reply1_comment_id
                        }
                        
                        print(f"📤 发送二级回复请求: {json.dumps(reply2_data, ensure_ascii=False)}")
                        
                        reply2_response = requests.post(
                            f"{API_BASE}/comments",
                            json=reply2_data,
                            headers={"Content-Type": "application/json"},
                            timeout=10
                        )
                        
                        print(f"📥 二级回复响应状态码: {reply2_response.status_code}")
                        
                        if reply2_response.status_code == 200:
                            reply2_comment = reply2_response.json()
                            if reply2_comment.get("code") == 200:
                                reply2_comment_data = reply2_comment["data"]
                                reply2_comment_id = reply2_comment_data["id"]
                                print(f"✅ 二级回复创建成功: ID = {reply2_comment_id}")
                                
                                print("\n" + "=" * 60)
                                print("📝 步骤4: 验证数据库层级结构")
                                print("=" * 60)
                                
                                # 验证数据库中的层级结构
                                test_comments = list(db.comments.find({"content": {"$regex": "层级测试.*"}}).sort("createdAt", 1))
                                print(f"📊 测试评论总数: {len(test_comments)}")
                                
                                for i, comment in enumerate(test_comments, 1):
                                    print(f"\n评论 {i}:")
                                    print(f"  _id: {comment['_id']}")
                                    print(f"  parentId: {comment.get('parentId', 'None')}")
                                    print(f"  level: {comment.get('level', 'None')}")
                                    print(f"  path: {comment.get('path', 'None')}")
                                    print(f"  content: {comment['content']}")
                                
                                print("\n" + "=" * 60)
                                print("📝 步骤5: 测试API返回的层级结构")
                                print("=" * 60)
                                
                                # 测试API返回的评论列表
                                api_response = requests.get(
                                    f"{API_BASE}/comments",
                                    params={
                                        "targetId": VALID_OBJECT_ID,
                                        "targetType": "guwen",
                                        "page": 1,
                                        "size": 50
                                    },
                                    timeout=10
                                )
                                
                                if api_response.status_code == 200:
                                    api_data = api_response.json()
                                    if api_data.get("code") == 200:
                                        comment_list = api_data["data"]["list"]
                                        print(f"📊 API返回评论数: {len(comment_list)}")
                                        
                                        # 过滤测试评论
                                        test_api_comments = [c for c in comment_list if "层级测试" in c.get("content", "")]
                                        print(f"📊 API返回测试评论数: {len(test_api_comments)}")
                                        
                                        for i, comment in enumerate(test_api_comments, 1):
                                            print(f"\nAPI评论 {i}:")
                                            print(f"  id: {comment.get('id', comment.get('_id', 'None'))}")
                                            print(f"  parentId: {comment.get('parentId', 'None')}")
                                            print(f"  level: {comment.get('level', 'None')}")
                                            print(f"  children: {len(comment.get('children', []))} 个子评论")
                                            print(f"  content: {comment['content']}")
                                            
                                            # 显示子评论
                                            if comment.get('children'):
                                                for j, child in enumerate(comment['children'], 1):
                                                    print(f"    子评论 {j}:")
                                                    print(f"      id: {child.get('id', child.get('_id', 'None'))}")
                                                    print(f"      parentId: {child.get('parentId', 'None')}")
                                                    print(f"      level: {child.get('level', 'None')}")
                                                    print(f"      content: {child['content']}")
                                        
                                        print("\n" + "=" * 60)
                                        print("🎯 层级结构验证")
                                        print("=" * 60)
                                        
                                        # 验证层级结构是否正确
                                        top_level_comments = [c for c in test_api_comments if c.get('level') == 1]
                                        print(f"✅ 顶级评论数量: {len(top_level_comments)}")
                                        
                                        total_children = sum(len(c.get('children', [])) for c in test_api_comments)
                                        print(f"✅ 子评论总数: {total_children}")
                                        
                                        if len(test_api_comments) == 1 and total_children == 2:
                                            print("🎉 层级结构测试成功！")
                                            print("   - 1个顶级评论")
                                            print("   - 2个子评论（一级和二级回复）")
                                        else:
                                            print("⚠️ 层级结构可能有问题")
                                            print(f"   - 期望: 1个顶级评论，2个子评论")
                                            print(f"   - 实际: {len(top_level_comments)}个顶级评论，{total_children}个子评论")
                                    else:
                                        print(f"❌ API返回错误: {api_data}")
                                else:
                                    print(f"❌ API请求失败: 状态码 {api_response.status_code}")
                            else:
                                print(f"❌ 二级回复创建失败: {reply2_comment}")
                        else:
                            print(f"❌ 二级回复请求失败: 状态码 {reply2_response.status_code}")
                    else:
                        print(f"❌ 一级回复创建失败: {reply1_comment}")
                else:
                    print(f"❌ 一级回复请求失败: 状态码 {reply1_response.status_code}")
            else:
                print(f"❌ 顶级评论创建失败: {parent_comment}")
        else:
            print(f"❌ 顶级评论请求失败: 状态码 {response.status_code}")
            print(f"响应内容: {response.text}")
            
    except requests.exceptions.RequestException as e:
        print(f"❌ 请求异常: {e}")
    except Exception as e:
        print(f"❌ 测试异常: {e}")
    
    client.close()
    print(f"\n✅ 测试完成!")

if __name__ == "__main__":
    test_comment_hierarchy()
