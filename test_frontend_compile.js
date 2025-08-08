#!/usr/bin/env node
/**
 * 测试前端编译的简单脚本
 */

const fs = require('fs');
const path = require('path');

console.log('🔍 检查前端文件语法...');

// 检查PoemDetailModal.js文件
const filePath = path.join(__dirname, 'frontend/src/components/PoemDetailModal.js');

try {
  const content = fs.readFileSync(filePath, 'utf8');
  
  console.log('✅ 文件读取成功');
  console.log(`📊 文件大小: ${content.length} 字符`);
  console.log(`📊 文件行数: ${content.split('\n').length} 行`);
  
  // 简单的语法检查
  const openTags = content.match(/<[^/][^>]*>/g) || [];
  const closeTags = content.match(/<\/[^>]*>/g) || [];
  
  console.log(`📊 开始标签数量: ${openTags.length}`);
  console.log(`📊 结束标签数量: ${closeTags.length}`);
  
  // 检查特定的标签对
  const modalOpen = (content.match(/<Modal/g) || []).length;
  const modalClose = (content.match(/<\/Modal>/g) || []).length;
  const rowOpen = (content.match(/<Row/g) || []).length;
  const rowClose = (content.match(/<\/Row>/g) || []).length;
  const colOpen = (content.match(/<Col/g) || []).length;
  const colClose = (content.match(/<\/Col>/g) || []).length;
  
  console.log('\n🔍 标签配对检查:');
  console.log(`Modal: ${modalOpen} 开始, ${modalClose} 结束 ${modalOpen === modalClose ? '✅' : '❌'}`);
  console.log(`Row: ${rowOpen} 开始, ${rowClose} 结束 ${rowOpen === rowClose ? '✅' : '❌'}`);
  console.log(`Col: ${colOpen} 开始, ${colClose} 结束 ${colOpen === colClose ? '✅' : '❌'}`);
  
  // 检查Fragment
  const fragmentOpen = (content.match(/<>/g) || []).length;
  const fragmentClose = (content.match(/<\/>/g) || []).length;
  console.log(`Fragment: ${fragmentOpen} 开始, ${fragmentClose} 结束 ${fragmentOpen === fragmentClose ? '✅' : '❌'}`);
  
  // 检查return语句
  const returnStatements = content.match(/return\s*\(/g) || [];
  console.log(`\n📊 Return语句数量: ${returnStatements.length}`);
  
  // 检查import语句
  const imports = content.match(/import.*from/g) || [];
  console.log(`📊 Import语句数量: ${imports.length}`);
  
  console.log('\n✅ 语法检查完成!');
  
} catch (error) {
  console.error('❌ 文件检查失败:', error.message);
  process.exit(1);
}
