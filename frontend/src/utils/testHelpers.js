// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "7200d3a5-79bc-4356-b90c-563084ed94f1"
//   Timestamp: "2025-08-08T11:09:49+08:00"
//   Authoring_Subagent: "PM-内置顾问团"
//   Principle_Applied: "SOLID-S (单一职责原则)"
//   Quality_Check: "测试辅助工具，便于调试和验证。"
// }}
// {{START_MODIFICATIONS}}

/**
 * 评论系统测试辅助工具
 * 提供便捷的测试数据生成和验证功能
 */

/**
 * 生成测试评论数据
 * @param {number} levels - 层级数量
 * @param {number} commentsPerLevel - 每层评论数量
 * @returns {Array} 测试评论数组
 */
export const generateTestComments = (levels = 5, commentsPerLevel = 2) => {
  const comments = [];
  let commentId = 1;
  
  // 生成顶级评论
  for (let i = 0; i < commentsPerLevel; i++) {
    const topComment = {
      id: `test-comment-${commentId}`,
      _id: `test-comment-${commentId}`,
      content: `这是第${i + 1}条顶级测试评论，内容长度测试：${'测试内容'.repeat(10)}`,
      userInfo: {
        nickname: `测试用户${commentId}`,
        avatar: i % 2 === 0 ? `avatar-${commentId}.jpg` : null
      },
      level: 1,
      parentId: null,
      likeCount: Math.floor(Math.random() * 10),
      createdAt: new Date(Date.now() - (commentId * 60000)).toISOString(),
      children: []
    };
    
    commentId++;
    
    // 生成子评论
    let currentParent = topComment;
    for (let level = 2; level <= levels; level++) {
      const childComment = {
        id: `test-comment-${commentId}`,
        _id: `test-comment-${commentId}`,
        content: `这是第${level}层回复评论，回复给${currentParent.userInfo.nickname}`,
        userInfo: {
          nickname: `测试用户${commentId}`,
          avatar: commentId % 3 === 0 ? `avatar-${commentId}.jpg` : null
        },
        level: level,
        parentId: currentParent.id,
        likeCount: Math.floor(Math.random() * 5),
        createdAt: new Date(Date.now() - ((commentId - level) * 60000)).toISOString(),
        children: []
      };
      
      currentParent.children.push(childComment);
      currentParent = childComment;
      commentId++;
    }
    
    comments.push(topComment);
  }
  
  return comments;
};

/**
 * 验证评论数据结构
 * @param {Array} comments - 评论数组
 * @returns {Object} 验证结果
 */
export const validateCommentStructure = (comments) => {
  const issues = [];
  const stats = {
    totalComments: 0,
    maxLevel: 0,
    missingIds: 0,
    missingUserInfo: 0,
    invalidTimes: 0
  };
  
  const validateComment = (comment, depth = 0) => {
    stats.totalComments++;
    stats.maxLevel = Math.max(stats.maxLevel, comment.level || 1);
    
    // 检查必需字段
    if (!comment.id && !comment._id) {
      stats.missingIds++;
      issues.push(`评论缺少ID: ${JSON.stringify(comment).substring(0, 100)}...`);
    }
    
    if (!comment.userInfo || !comment.userInfo.nickname) {
      stats.missingUserInfo++;
      issues.push(`评论缺少用户信息: ${comment.id || comment._id}`);
    }
    
    if (!comment.createdAt || isNaN(new Date(comment.createdAt).getTime())) {
      stats.invalidTimes++;
      issues.push(`评论时间无效: ${comment.id || comment._id}`);
    }
    
    // 递归检查子评论
    if (comment.children && comment.children.length > 0) {
      comment.children.forEach(child => validateComment(child, depth + 1));
    }
  };
  
  comments.forEach(comment => validateComment(comment));
  
  return {
    isValid: issues.length === 0,
    issues,
    stats
  };
};

/**
 * 性能测试工具
 * @param {Function} fn - 要测试的函数
 * @param {Array} args - 函数参数
 * @param {number} iterations - 迭代次数
 * @returns {Object} 性能测试结果
 */
export const performanceTest = (fn, args = [], iterations = 100) => {
  const times = [];
  
  for (let i = 0; i < iterations; i++) {
    const start = performance.now();
    fn(...args);
    const end = performance.now();
    times.push(end - start);
  }
  
  const avgTime = times.reduce((sum, time) => sum + time, 0) / times.length;
  const minTime = Math.min(...times);
  const maxTime = Math.max(...times);
  
  return {
    averageTime: avgTime.toFixed(3),
    minTime: minTime.toFixed(3),
    maxTime: maxTime.toFixed(3),
    iterations,
    totalTime: times.reduce((sum, time) => sum + time, 0).toFixed(3)
  };
};

/**
 * 模拟用户交互测试
 * @param {Object} options - 测试选项
 * @returns {Array} 交互测试步骤
 */
export const generateInteractionTests = (options = {}) => {
  const {
    maxLevels = 6,
    testSpecialChars = true,
    testLongContent = true,
    testEmptyContent = true
  } = options;
  
  const tests = [];
  
  // 基础多层级回复测试
  for (let level = 1; level <= maxLevels; level++) {
    tests.push({
      type: 'reply',
      level,
      content: `第${level}层回复测试`,
      expectedResult: `应该显示L${level}标签和正确的引用框`
    });
  }
  
  // 特殊字符测试
  if (testSpecialChars) {
    tests.push({
      type: 'special_chars',
      content: '<script>alert("XSS")</script> & "引号" \'单引号\' @用户 #标签',
      expectedResult: '特殊字符应该被正确转义和显示'
    });
  }
  
  // 长内容测试
  if (testLongContent) {
    tests.push({
      type: 'long_content',
      content: '这是一条很长的评论内容，'.repeat(20),
      expectedResult: '长内容应该正确显示，引用框应该截断到50字符'
    });
  }
  
  // 空内容测试
  if (testEmptyContent) {
    tests.push({
      type: 'empty_content',
      content: '',
      expectedResult: '应该显示错误提示，不允许发布空评论'
    });
  }
  
  return tests;
};

/**
 * 响应式测试辅助
 * @param {Array} breakpoints - 断点数组
 * @returns {Object} 响应式测试结果
 */
export const testResponsiveBreakpoints = (breakpoints = [480, 768, 1024, 1440]) => {
  const results = {};
  
  breakpoints.forEach(width => {
    // 模拟窗口宽度
    Object.defineProperty(window, 'innerWidth', {
      writable: true,
      configurable: true,
      value: width,
    });
    
    // 触发resize事件
    window.dispatchEvent(new Event('resize'));
    
    results[width] = {
      isMobile: width <= 768,
      isTablet: width > 768 && width <= 1024,
      isDesktop: width > 1024,
      expectedStyles: {
        cardMargin: width <= 768 ? '8px' : '12px',
        avatarSize: width <= 480 ? 28 : width <= 768 ? 32 : 36,
        fontSize: width <= 480 ? '12px' : width <= 768 ? '13px' : '14px'
      }
    };
  });
  
  return results;
};

/**
 * 调试工具：打印评论树结构
 * @param {Array} comments - 评论数组
 * @param {number} maxDepth - 最大显示深度
 */
export const printCommentTree = (comments, maxDepth = 10) => {
  const printComment = (comment, depth = 0, prefix = '') => {
    if (depth > maxDepth) return;
    
    const indent = '  '.repeat(depth);
    const connector = depth === 0 ? '' : '└─ ';
    const level = comment.level ? `[L${comment.level}]` : '';
    const user = comment.userInfo?.nickname || '匿名';
    const content = comment.content?.substring(0, 30) + '...' || '无内容';
    
    console.log(`${indent}${connector}${level} ${user}: ${content}`);
    
    if (comment.children && comment.children.length > 0) {
      comment.children.forEach((child, index) => {
        const isLast = index === comment.children.length - 1;
        printComment(child, depth + 1, isLast ? '└─ ' : '├─ ');
      });
    }
  };
  
  console.log('📊 评论树结构:');
  console.log('═'.repeat(50));
  comments.forEach(comment => printComment(comment));
  console.log('═'.repeat(50));
};

/**
 * 快速测试套件
 * @param {Object} commentFunctions - 评论相关函数
 * @returns {Object} 测试结果
 */
export const quickTestSuite = (commentFunctions) => {
  const { flattenComments, getReplyReference, buildReplyChain } = commentFunctions;
  const testData = generateTestComments(5, 2);
  
  console.log('🚀 开始快速测试套件');
  
  // 测试扁平化
  console.time('扁平化性能测试');
  const flattened = flattenComments(testData);
  console.timeEnd('扁平化性能测试');
  
  // 测试引用获取
  const testComment = flattened.find(c => c.level > 1);
  const reference = testComment ? getReplyReference(testComment, flattened) : null;
  
  // 测试回复链
  const deepComment = flattened.find(c => c.level >= 3);
  const chain = deepComment ? buildReplyChain(deepComment, flattened) : [];
  
  const results = {
    扁平化测试: {
      原始数量: testData.length,
      扁平化数量: flattened.length,
      状态: flattened.length > testData.length ? '✅ 通过' : '❌ 失败'
    },
    引用测试: {
      测试评论: testComment?.id || '无',
      引用结果: reference ? '✅ 找到引用' : '❌ 未找到引用',
      引用用户: reference?.userInfo?.nickname || '无'
    },
    回复链测试: {
      测试评论: deepComment?.id || '无',
      链长度: chain.length,
      状态: chain.length > 0 ? '✅ 通过' : '❌ 失败'
    }
  };
  
  console.table(results);
  return results;
};

// {{END_MODIFICATIONS}}
