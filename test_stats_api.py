#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试统计数据API的脚本
"""

import requests
import json
import sys

def test_stats_api():
    """测试统计数据API"""
    base_url = "http://localhost:8080/api/v1"
    
    print("🧪 测试统计数据API")
    print("=" * 50)
    
    try:
        # 测试全局统计API
        print("\n1. 测试全局统计API")
        response = requests.get(f"{base_url}/stats/global", timeout=10)
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ 全局统计API调用成功")
            print(f"📊 响应数据: {json.dumps(data, indent=2, ensure_ascii=False)}")
            
            if data.get('code') == 200:
                stats = data.get('data', {})
                print(f"\n📈 统计数据解析:")
                print(f"  诗词总数: {stats.get('poemCount', 0)} ({stats.get('poemCountDisplay', 'N/A')})")
                print(f"  作者数量: {stats.get('writerCount', 0)} ({stats.get('writerCountDisplay', 'N/A')})")
                print(f"  名句数量: {stats.get('sentenceCount', 0)} ({stats.get('sentenceCountDisplay', 'N/A')})")
                print(f"  今日访问: {stats.get('todayViews', 0)} ({stats.get('todayViewsDisplay', 'N/A')})")
            else:
                print(f"❌ API返回错误: {data.get('message', '未知错误')}")
        else:
            print(f"❌ HTTP请求失败: {response.status_code}")
            print(f"响应内容: {response.text}")
            
    except requests.exceptions.ConnectionError:
        print("❌ 连接失败: 请确保后端服务已启动 (http://localhost:8080)")
        return False
    except requests.exceptions.Timeout:
        print("❌ 请求超时: 后端服务响应太慢")
        return False
    except Exception as e:
        print(f"❌ 测试失败: {str(e)}")
        return False
    
    print("\n" + "=" * 50)
    
    try:
        # 测试各模块的统计API
        print("\n2. 测试各模块统计API")
        
        modules = [
            ("古文统计", "/guwen/stats"),
            ("作者统计", "/writers/stats"),
            ("名句统计", "/sentences/stats")
        ]
        
        for name, endpoint in modules:
            print(f"\n测试 {name}: {endpoint}")
            response = requests.get(f"{base_url}{endpoint}", timeout=10)
            
            if response.status_code == 200:
                data = response.json()
                if data.get('code') == 200:
                    total = data.get('data', {}).get('total', 0)
                    print(f"✅ {name}: {total} 条记录")
                else:
                    print(f"❌ {name} API返回错误: {data.get('message', '未知错误')}")
            else:
                print(f"❌ {name} HTTP请求失败: {response.status_code}")
                
    except Exception as e:
        print(f"❌ 模块统计测试失败: {str(e)}")
        return False
    
    print("\n✅ 所有测试完成!")
    return True

if __name__ == "__main__":
    success = test_stats_api()
    sys.exit(0 if success else 1)
