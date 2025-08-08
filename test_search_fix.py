#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试搜索修复的脚本
验证前端搜索功能是否正常工作
"""

import requests
import json

# 后端API基础URL
BASE_URL = "http://localhost:8080/api/v1"

def test_dynasties_api():
    """测试朝代列表API"""
    print("🔍 测试朝代列表API...")
    
    try:
        response = requests.get(f"{BASE_URL}/guwen/dynasties")
        if response.status_code == 200:
            data = response.json()
            if data.get('code') == 200:
                dynasties = data.get('data', [])
                print(f"✅ 获取朝代列表成功，共 {len(dynasties)} 个朝代:")
                for dynasty in dynasties[:10]:  # 只显示前10个
                    print(f"   - {dynasty}")
                if len(dynasties) > 10:
                    print(f"   ... 还有 {len(dynasties) - 10} 个朝代")
                return dynasties
            else:
                print(f"❌ API返回错误: {data.get('message', 'Unknown error')}")
        else:
            print(f"❌ HTTP错误: {response.status_code}")
    except Exception as e:
        print(f"❌ 请求失败: {e}")
    
    return []

def test_search_dufu_tang():
    """测试搜索杜甫的唐代作品"""
    print("\n🔍 测试搜索杜甫的唐代作品...")
    
    try:
        # 使用GET接口测试多条件搜索
        params = {
            'page': 1,
            'size': 5,
            'dynasty': '唐代',  # 使用正确的朝代名称
            'writer': '杜甫'
        }
        
        response = requests.get(f"{BASE_URL}/guwen", params=params)
        if response.status_code == 200:
            data = response.json()
            if data.get('code') == 200:
                result = data.get('data', {})
                poems = result.get('list', [])
                total = result.get('total', 0)
                
                print(f"✅ 搜索成功，找到 {total} 首杜甫的唐代作品:")
                for poem in poems:
                    print(f"   - {poem.get('title', 'N/A')} ({poem.get('writer', 'N/A')}, {poem.get('dynasty', 'N/A')})")
                
                return total > 0
            else:
                print(f"❌ API返回错误: {data.get('message', 'Unknown error')}")
        else:
            print(f"❌ HTTP错误: {response.status_code}")
    except Exception as e:
        print(f"❌ 请求失败: {e}")
    
    return False

def test_keyword_search():
    """测试关键词搜索"""
    print("\n🔍 测试关键词搜索...")

    try:
        # 使用POST接口测试关键词搜索
        search_data = {
            'keyword': '李白',
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

                print(f"✅ 关键词搜索成功，找到 {total} 首相关作品:")
                for poem in poems:
                    print(f"   - {poem.get('title', 'N/A')} ({poem.get('writer', 'N/A')}, {poem.get('dynasty', 'N/A')})")

                return total > 0
            else:
                print(f"❌ API返回错误: {data.get('message', 'Unknown error')}")
        else:
            print(f"❌ HTTP错误: {response.status_code}")
    except Exception as e:
        print(f"❌ 请求失败: {e}")

    return False

def test_enhanced_search_types():
    """测试增强的搜索类型"""
    print("\n🔍 测试增强的搜索类型...")

    search_types = [
        ('smart', '智能搜索'),
        ('fuzzy', '模糊搜索'),
        ('content', '内容搜索'),
        ('exact', '精确搜索')
    ]

    results = {}

    for search_type, type_name in search_types:
        print(f"\n  📝 测试{type_name}...")
        try:
            search_data = {
                'keyword': '明月',
                'searchType': search_type,
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

                    print(f"     ✅ {type_name}成功，找到 {total} 首作品")
                    for poem in poems[:2]:  # 只显示前2首
                        print(f"        - {poem.get('title', 'N/A')} ({poem.get('writer', 'N/A')})")

                    results[search_type] = total > 0
                else:
                    print(f"     ❌ API返回错误: {data.get('message', 'Unknown error')}")
                    results[search_type] = False
            else:
                print(f"     ❌ HTTP错误: {response.status_code}")
                results[search_type] = False
        except Exception as e:
            print(f"     ❌ 请求失败: {e}")
            results[search_type] = False

    return all(results.values())

def main():
    """主测试函数"""
    print("=" * 60)
    print("🧪 增强搜索功能验证测试")
    print("=" * 60)

    # 测试1: 朝代列表API
    dynasties = test_dynasties_api()

    # 测试2: 多条件搜索（杜甫+唐代）
    dufu_search_success = test_search_dufu_tang()

    # 测试3: 关键词搜索
    keyword_search_success = test_keyword_search()

    # 测试4: 增强的搜索类型
    enhanced_search_success = test_enhanced_search_types()

    # 总结
    print("\n" + "=" * 60)
    print("📊 测试结果总结")
    print("=" * 60)
    print(f"朝代列表API: {'✅ 通过' if dynasties else '❌ 失败'}")
    print(f"多条件搜索: {'✅ 通过' if dufu_search_success else '❌ 失败'}")
    print(f"关键词搜索: {'✅ 通过' if keyword_search_success else '❌ 失败'}")
    print(f"增强搜索类型: {'✅ 通过' if enhanced_search_success else '❌ 失败'}")

    if dynasties and dufu_search_success and keyword_search_success and enhanced_search_success:
        print("\n🎉 所有测试通过！增强搜索功能实现成功！")
        print("\n📋 新增功能:")
        print("   ✅ 智能搜索 - 综合多字段匹配")
        print("   ✅ 模糊搜索 - 支持部分匹配")
        print("   ✅ 内容搜索 - 专注正文内容")
        print("   ✅ 精确搜索 - 使用文本索引")
    else:
        print("\n⚠️  部分测试失败，请检查后端服务是否正常运行。")

if __name__ == "__main__":
    main()
