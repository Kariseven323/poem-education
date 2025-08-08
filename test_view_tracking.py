#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试访问记录功能的脚本
"""

import requests
import json
import time
import sys

def test_view_tracking():
    """测试访问记录功能"""
    base_url = "http://localhost:8080/api/v1"

    print("🧪 测试访问记录功能")
    print("=" * 60)

    try:
        # 0. 先注册测试用户，然后登录获取JWT token
        print("\n0. 注册测试用户")
        test_username = f"testuser_{int(time.time())}"  # 使用时间戳避免重复
        register_data = {
            "username": test_username,
            "password": "123456",
            "email": f"{test_username}@test.com"
        }

        register_response = requests.post(f"{base_url}/auth/register", json=register_data, timeout=10)

        if register_response.status_code == 200:
            register_result = register_response.json()
            if register_result.get('code') == 200:
                print(f"✅ 注册成功: {test_username}")
            else:
                print(f"⚠️  注册失败: {register_result.get('message', '未知错误')}")
                # 可能用户已存在，继续尝试登录
        else:
            print(f"⚠️  注册请求失败: {register_response.status_code}")

        print(f"\n1. 登录获取JWT token")
        login_data = {
            "username": test_username,
            "password": "123456"
        }

        login_response = requests.post(f"{base_url}/auth/login", json=login_data, timeout=10)

        if login_response.status_code != 200:
            print(f"❌ 登录失败: {login_response.status_code}")
            print(f"响应内容: {login_response.text}")
            return False

        login_result = login_response.json()
        if login_result.get('code') != 200:
            print(f"❌ 登录失败: {login_result.get('message', '未知错误')}")
            return False

        access_token = login_result.get('data', {}).get('accessToken')
        if not access_token:
            print(f"❌ 未获取到访问令牌")
            return False

        print(f"✅ 登录成功，获取到token: {access_token[:50]}...")

        # 设置认证头
        headers = {
            "Authorization": f"Bearer {access_token}",
            "Content-Type": "application/json"
        }
        # 2. 先获取一个真实的诗词ID
        print("\n2. 获取诗词列表以获取真实ID")
        poems_response = requests.get(f"{base_url}/guwen?page=1&size=1", timeout=10)
        
        if poems_response.status_code != 200:
            print(f"❌ 获取诗词列表失败: {poems_response.status_code}")
            return False
            
        poems_data = poems_response.json()
        if poems_data.get('code') != 200 or not poems_data.get('data', {}).get('list'):
            print(f"❌ 诗词列表为空或格式错误")
            return False
            
        poem_id = poems_data['data']['list'][0]['id']
        print(f"✅ 获取到诗词ID: {poem_id}")
        
        # 3. 记录访问行为
        print(f"\n3. 记录访问行为")
        view_data = {
            "targetId": poem_id,
            "targetType": "guwen", 
            "actionType": "view"
        }
        
        view_response = requests.post(f"{base_url}/actions", json=view_data, headers=headers, timeout=10)
        
        if view_response.status_code == 200:
            view_result = view_response.json()
            if view_result.get('code') == 200:
                print(f"✅ 成功记录访问行为")
                print(f"📊 返回数据: {json.dumps(view_result, indent=2, ensure_ascii=False)}")
            else:
                print(f"❌ 记录访问行为失败: {view_result.get('message', '未知错误')}")
                return False
        else:
            print(f"❌ HTTP请求失败: {view_response.status_code}")
            print(f"响应内容: {view_response.text}")
            return False
            
        # 4. 等待一秒，然后测试统计API
        print(f"\n4. 等待1秒后测试统计API...")
        time.sleep(1)
        
        stats_response = requests.get(f"{base_url}/stats/global", timeout=10)
        
        if stats_response.status_code == 200:
            stats_data = stats_response.json()
            if stats_data.get('code') == 200:
                stats = stats_data.get('data', {})
                print(f"✅ 全局统计API调用成功")
                print(f"📈 统计数据:")
                print(f"  诗词总数: {stats.get('poemCount', 0)} ({stats.get('poemCountDisplay', 'N/A')})")
                print(f"  作者数量: {stats.get('writerCount', 0)} ({stats.get('writerCountDisplay', 'N/A')})")
                print(f"  名句数量: {stats.get('sentenceCount', 0)} ({stats.get('sentenceCountDisplay', 'N/A')})")
                print(f"  今日访问: {stats.get('todayViews', 0)} ({stats.get('todayViewsDisplay', 'N/A')})")
                
                # 检查今日访问量是否大于0
                today_views = stats.get('todayViews', 0)
                if today_views > 0:
                    print(f"✅ 今日访问量正常: {today_views}")
                else:
                    print(f"⚠️  今日访问量为0，可能需要检查数据库或时区设置")
                    
            else:
                print(f"❌ 统计API返回错误: {stats_data.get('message', '未知错误')}")
                return False
        else:
            print(f"❌ 统计API HTTP请求失败: {stats_response.status_code}")
            return False
            
        # 5. 再记录几次访问，测试累计效果
        print(f"\n5. 记录更多访问行为测试累计效果")
        for i in range(3):
            # 使用不同的诗词ID（如果有的话）
            test_view_data = {
                "targetId": poem_id,
                "targetType": "guwen", 
                "actionType": "view"
            }
            
            test_response = requests.post(f"{base_url}/actions", json=test_view_data, headers=headers, timeout=10)
            if test_response.status_code == 200:
                result = test_response.json()
                if result.get('code') == 200:
                    print(f"  ✅ 第{i+1}次访问记录成功")
                else:
                    print(f"  ⚠️  第{i+1}次访问记录返回: {result.get('message', '未知')}")
            else:
                print(f"  ❌ 第{i+1}次访问记录失败: {test_response.status_code}")
                
            time.sleep(0.5)  # 短暂等待
            
        # 5. 最终统计检查
        print(f"\n5. 最终统计检查")
        final_stats_response = requests.get(f"{base_url}/stats/global", timeout=10)
        
        if final_stats_response.status_code == 200:
            final_stats_data = final_stats_response.json()
            if final_stats_data.get('code') == 200:
                final_stats = final_stats_data.get('data', {})
                final_today_views = final_stats.get('todayViews', 0)
                print(f"📊 最终今日访问量: {final_today_views}")
                
                if final_today_views >= 1:  # 至少应该有我们刚才记录的访问
                    print(f"✅ 访问记录功能正常工作!")
                    return True
                else:
                    print(f"❌ 访问记录功能可能有问题，今日访问量仍为0")
                    return False
            else:
                print(f"❌ 最终统计检查失败: {final_stats_data.get('message', '未知错误')}")
                return False
        else:
            print(f"❌ 最终统计检查HTTP失败: {final_stats_response.status_code}")
            return False
            
    except requests.exceptions.ConnectionError:
        print("❌ 连接失败: 请确保后端服务已启动 (http://localhost:8080)")
        return False
    except requests.exceptions.Timeout:
        print("❌ 请求超时: 后端服务响应太慢")
        return False
    except Exception as e:
        print(f"❌ 测试失败: {str(e)}")
        return False
    
    print("\n" + "=" * 60)

if __name__ == "__main__":
    success = test_view_tracking()
    if success:
        print("🎉 访问记录功能测试通过!")
    else:
        print("💥 访问记录功能测试失败!")
    sys.exit(0 if success else 1)
