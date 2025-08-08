#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试MongoDB查询修复的脚本
验证$elemMatch语法是否正确工作
"""

import requests
import json

# 后端API基础URL
BASE_URL = "http://localhost:8080/api/v1"

def test_type_search():
    """测试type字段搜索"""
    print("🔍 测试type字段搜索...")
    
    # 测试不同的type关键词
    type_keywords = ['诗', '词', '乐府', '唐诗', '宋词']
    
    for keyword in type_keywords:
        print(f"\n  📝 测试关键词: '{keyword}'")
        try:
            search_data = {
                'keyword': keyword,
                'searchType': 'smart',
                'page': 1,
                'size': 3
            }
            
            response = requests.post(f"{BASE_URL}/guwen/search", json=search_data)
            if response.status_code == 200:
                data = response.json()
                if data.get('code') == 200:
                    result = data.get('data', {})
                    poems = result.get('list', [])
                    total = result.get('total', 0)
                    
                    print(f"     ✅ 找到 {total} 首作品")
                    for poem in poems[:2]:  # 只显示前2首
                        types = poem.get('type', [])
                        if isinstance(types, list):
                            types_str = ', '.join(types[:3])  # 只显示前3个类型
                            if len(types) > 3:
                                types_str += f" (+{len(types)-3}个)"
                        else:
                            types_str = str(types)
                        print(f"        - {poem.get('title', 'N/A')} ({poem.get('writer', 'N/A')})")
                        print(f"          类型: {types_str}")
                else:
                    print(f"     ❌ API返回错误: {data.get('message', 'Unknown error')}")
            else:
                print(f"     ❌ HTTP错误: {response.status_code}")
                print(f"     响应内容: {response.text}")
        except Exception as e:
            print(f"     ❌ 请求失败: {e}")

def test_smart_search_comprehensive():
    """测试智能搜索的综合功能"""
    print("\n🔍 测试智能搜索综合功能...")
    
    # 测试不同类型的关键词
    test_cases = [
        ('李白', '作者搜索'),
        ('明月', '内容搜索'),
        ('送别', 'type搜索'),
        ('唐诗', 'type搜索'),
        ('静夜思', '标题搜索')
    ]
    
    for keyword, search_type in test_cases:
        print(f"\n  📝 测试{search_type}: '{keyword}'")
        try:
            search_data = {
                'keyword': keyword,
                'searchType': 'smart',
                'page': 1,
                'size': 5
            }
            
            response = requests.post(f"{BASE_URL}/guwen/search", json=search_data)
            if response.status_code == 200:
                data = response.json()
                if data.get('code') == 200:
                    result = data.get('data', {})
                    poems = result.get('list', [])
                    total = result.get('total', 0)
                    
                    print(f"     ✅ 找到 {total} 首作品")
                    
                    # 分析匹配字段
                    match_fields = set()
                    for poem in poems[:3]:
                        title = poem.get('title', '')
                        writer = poem.get('writer', '')
                        content = poem.get('content', '')
                        types = poem.get('type', [])
                        
                        if keyword.lower() in title.lower():
                            match_fields.add('标题')
                        if keyword.lower() in writer.lower():
                            match_fields.add('作者')
                        if keyword.lower() in content.lower():
                            match_fields.add('内容')
                        if isinstance(types, list):
                            for t in types:
                                if keyword.lower() in t.lower():
                                    match_fields.add('类型')
                                    break
                    
                    if match_fields:
                        print(f"     匹配字段: {', '.join(match_fields)}")
                    
                    # 显示示例结果
                    for poem in poems[:2]:
                        print(f"        - {poem.get('title', 'N/A')} ({poem.get('writer', 'N/A')})")
                        
                else:
                    print(f"     ❌ API返回错误: {data.get('message', 'Unknown error')}")
            else:
                print(f"     ❌ HTTP错误: {response.status_code}")
                if response.status_code == 500:
                    print(f"     这可能是MongoDB查询语法错误")
        except Exception as e:
            print(f"     ❌ 请求失败: {e}")

def test_mongodb_elemMatch():
    """专门测试$elemMatch语法"""
    print("\n🔍 测试MongoDB $elemMatch语法...")
    
    # 测试包含在type数组中的关键词
    elemMatch_tests = [
        '送别',  # 应该在某些诗的type数组中
        '抒情',  # 常见的诗歌类型
        '咏物',  # 另一个常见类型
        '唐诗三百首'  # 经典分类
    ]
    
    for keyword in elemMatch_tests:
        print(f"\n  📝 测试$elemMatch: '{keyword}'")
        try:
            search_data = {
                'keyword': keyword,
                'searchType': 'smart',
                'page': 1,
                'size': 3
            }
            
            response = requests.post(f"{BASE_URL}/guwen/search", json=search_data)
            if response.status_code == 200:
                data = response.json()
                if data.get('code') == 200:
                    result = data.get('data', {})
                    poems = result.get('list', [])
                    total = result.get('total', 0)
                    
                    print(f"     ✅ 找到 {total} 首作品")
                    
                    # 验证type字段确实包含关键词
                    for poem in poems[:2]:
                        types = poem.get('type', [])
                        matching_types = []
                        if isinstance(types, list):
                            matching_types = [t for t in types if keyword.lower() in t.lower()]
                        
                        print(f"        - {poem.get('title', 'N/A')} ({poem.get('writer', 'N/A')})")
                        if matching_types:
                            print(f"          匹配类型: {', '.join(matching_types)}")
                        else:
                            print(f"          所有类型: {', '.join(types[:3]) if isinstance(types, list) else types}")
                        
                else:
                    print(f"     ❌ API返回错误: {data.get('message', 'Unknown error')}")
            else:
                print(f"     ❌ HTTP错误: {response.status_code}")
                if response.status_code == 500:
                    print(f"     ⚠️  500错误可能表示MongoDB查询语法仍有问题")
                    print(f"     响应内容: {response.text}")
        except Exception as e:
            print(f"     ❌ 请求失败: {e}")

def main():
    """主测试函数"""
    print("=" * 60)
    print("🧪 MongoDB查询修复验证测试")
    print("=" * 60)
    
    # 测试1: type字段搜索
    test_type_search()
    
    # 测试2: 智能搜索综合功能
    test_smart_search_comprehensive()
    
    # 测试3: 专门测试$elemMatch语法
    test_mongodb_elemMatch()
    
    print("\n" + "=" * 60)
    print("📊 测试完成")
    print("=" * 60)
    print("如果所有测试都通过，说明MongoDB查询修复成功！")
    print("如果出现500错误，说明$elemMatch语法可能仍有问题。")

if __name__ == "__main__":
    main()
