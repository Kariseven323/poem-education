#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
简单搜索测试脚本
"""

import requests
import json

# 后端API基础URL
BASE_URL = "http://localhost:8080/api/v1"

def test_simple_search():
    """测试简单搜索"""
    print("🔍 测试简单搜索...")
    
    try:
        # 最简单的搜索请求
        search_data = {
            'keyword': '李白'
        }
        
        print(f"发送请求: {json.dumps(search_data, ensure_ascii=False)}")
        
        response = requests.post(f"{BASE_URL}/guwen/search", 
                               json=search_data,
                               headers={'Content-Type': 'application/json'})
        
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
        if response.status_code == 200:
            data = response.json()
            if data.get('code') == 200:
                result = data.get('data', {})
                poems = result.get('list', [])
                total = result.get('total', 0)
                
                print(f"✅ 搜索成功，找到 {total} 首相关作品:")
                for poem in poems[:3]:
                    print(f"   - {poem.get('title', 'N/A')} ({poem.get('writer', 'N/A')}, {poem.get('dynasty', 'N/A')})")
                
                return True
            else:
                print(f"❌ API返回错误: {data.get('message', 'Unknown error')}")
        else:
            print(f"❌ HTTP错误: {response.status_code}")
            
    except Exception as e:
        print(f"❌ 请求失败: {e}")
    
    return False

def test_with_all_fields():
    """测试包含所有字段的搜索"""
    print("\n🔍 测试包含所有字段的搜索...")
    
    try:
        search_data = {
            'keyword': '李白',
            'writer': None,
            'dynasty': None,
            'type': None,
            'page': 1,
            'size': 5,
            'sortBy': 'createdAt',
            'sortDir': 'desc',
            'searchType': 'smart'
        }
        
        print(f"发送请求: {json.dumps(search_data, ensure_ascii=False)}")
        
        response = requests.post(f"{BASE_URL}/guwen/search", 
                               json=search_data,
                               headers={'Content-Type': 'application/json'})
        
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
        if response.status_code == 200:
            data = response.json()
            if data.get('code') == 200:
                result = data.get('data', {})
                poems = result.get('list', [])
                total = result.get('total', 0)
                
                print(f"✅ 搜索成功，找到 {total} 首相关作品:")
                for poem in poems[:3]:
                    print(f"   - {poem.get('title', 'N/A')} ({poem.get('writer', 'N/A')}, {poem.get('dynasty', 'N/A')})")
                
                return True
            else:
                print(f"❌ API返回错误: {data.get('message', 'Unknown error')}")
        else:
            print(f"❌ HTTP错误: {response.status_code}")
            
    except Exception as e:
        print(f"❌ 请求失败: {e}")
    
    return False

if __name__ == "__main__":
    print("=" * 60)
    print("🧪 简单搜索测试")
    print("=" * 60)
    
    # 测试1: 最简单的搜索
    simple_success = test_simple_search()
    
    # 测试2: 包含所有字段的搜索
    full_success = test_with_all_fields()
    
    print("\n" + "=" * 60)
    print("📊 测试结果总结")
    print("=" * 60)
    print(f"简单搜索: {'✅ 通过' if simple_success else '❌ 失败'}")
    print(f"完整搜索: {'✅ 通过' if full_success else '❌ 失败'}")
