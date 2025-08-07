#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
数据验证脚本
验证MongoDB中的数据是否正确导入
"""

from pymongo import MongoClient
import json

def verify_mongodb_data():
    """验证MongoDB数据"""
    
    # 连接MongoDB
    try:
        client = MongoClient("mongodb://localhost:27017/")
        db = client["poem_education"]
        print("✅ 成功连接到MongoDB")
    except Exception as e:
        print(f"❌ 连接失败: {e}")
        return
    
    print("\n" + "="*50)
    print("📊 数据库统计信息")
    print("="*50)
    
    # 检查集合
    collections = db.list_collection_names()
    print(f"集合列表: {collections}")
    
    # 统计各集合数据量
    for collection_name in ['guwen', 'sentences', 'writers']:
        if collection_name in collections:
            count = db[collection_name].count_documents({})
            print(f"{collection_name}: {count} 条记录")
        else:
            print(f"❌ 集合 {collection_name} 不存在")
    
    print("\n" + "="*50)
    print("🔍 数据样本检查")
    print("="*50)
    
    # 检查古文数据样本
    print("\n📚 古文数据样本:")
    guwen_sample = db['guwen'].find_one()
    if guwen_sample:
        print(f"  标题: {guwen_sample.get('title', 'N/A')}")
        print(f"  作者: {guwen_sample.get('writer', 'N/A')}")
        print(f"  朝代: {guwen_sample.get('dynasty', 'N/A')}")
        print(f"  内容长度: {len(guwen_sample.get('content', ''))}")
    else:
        print("  ❌ 未找到古文数据")
    
    # 检查句子数据样本
    print("\n📝 句子数据样本:")
    sentence_sample = db['sentences'].find_one()
    if sentence_sample:
        print(f"  句子: {sentence_sample.get('name', 'N/A')}")
        print(f"  出处: {sentence_sample.get('from', 'N/A')}")
    else:
        print("  ❌ 未找到句子数据")
    
    # 检查作者数据样本
    print("\n👤 作者数据样本:")
    writer_sample = db['writers'].find_one()
    if writer_sample:
        print(f"  姓名: {writer_sample.get('name', 'N/A')}")
        print(f"  简介长度: {len(writer_sample.get('simpleIntro', ''))}")
    else:
        print("  ❌ 未找到作者数据")
    
    print("\n" + "="*50)
    print("🔍 索引检查")
    print("="*50)
    
    # 检查索引
    for collection_name in ['guwen', 'sentences', 'writers']:
        if collection_name in collections:
            indexes = list(db[collection_name].list_indexes())
            print(f"\n{collection_name} 集合索引:")
            for idx in indexes:
                print(f"  - {idx['name']}: {idx.get('key', {})}")
    
    print("\n" + "="*50)
    print("📈 数据质量检查")
    print("="*50)
    
    # 检查数据完整性
    print("\n古文数据质量:")
    total_guwen = db['guwen'].count_documents({})
    guwen_with_title = db['guwen'].count_documents({"title": {"$exists": True, "$ne": ""}})
    guwen_with_writer = db['guwen'].count_documents({"writer": {"$exists": True, "$ne": ""}})
    guwen_with_content = db['guwen'].count_documents({"content": {"$exists": True, "$ne": ""}})
    
    print(f"  总数: {total_guwen}")
    print(f"  有标题: {guwen_with_title} ({guwen_with_title/total_guwen*100:.1f}%)")
    print(f"  有作者: {guwen_with_writer} ({guwen_with_writer/total_guwen*100:.1f}%)")
    print(f"  有内容: {guwen_with_content} ({guwen_with_content/total_guwen*100:.1f}%)")
    
    print("\n句子数据质量:")
    total_sentences = db['sentences'].count_documents({})
    sentences_with_name = db['sentences'].count_documents({"name": {"$exists": True, "$ne": ""}})
    sentences_with_from = db['sentences'].count_documents({"from": {"$exists": True, "$ne": ""}})
    
    print(f"  总数: {total_sentences}")
    print(f"  有内容: {sentences_with_name} ({sentences_with_name/total_sentences*100:.1f}%)")
    print(f"  有出处: {sentences_with_from} ({sentences_with_from/total_sentences*100:.1f}%)")
    
    print("\n作者数据质量:")
    total_writers = db['writers'].count_documents({})
    writers_with_name = db['writers'].count_documents({"name": {"$exists": True, "$ne": ""}})
    writers_with_intro = db['writers'].count_documents({"simpleIntro": {"$exists": True, "$ne": ""}})
    
    print(f"  总数: {total_writers}")
    print(f"  有姓名: {writers_with_name} ({writers_with_name/total_writers*100:.1f}%)")
    print(f"  有简介: {writers_with_intro} ({writers_with_intro/total_writers*100:.1f}%)")
    
    print("\n" + "="*50)
    print("🔍 查询测试")
    print("="*50)
    
    # 测试查询
    print("\n查询测试:")
    
    # 查询李白的作品
    libai_count = db['guwen'].count_documents({"writer": "李白"})
    print(f"  李白的作品数量: {libai_count}")
    
    # 查询唐代作品
    tang_count = db['guwen'].count_documents({"dynasty": "唐代"})
    print(f"  唐代作品数量: {tang_count}")
    
    # 查询包含"月"的句子
    moon_sentences = db['sentences'].count_documents({"name": {"$regex": "月"}})
    print(f"  包含'月'字的句子: {moon_sentences}")
    
    client.close()
    print(f"\n✅ 验证完成!")

if __name__ == "__main__":
    verify_mongodb_data()
