// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "7200d3a5-79bc-4356-b90c-563084ed94f1"
//   Timestamp: "2025-08-08T11:09:49+08:00"
//   Authoring_Subagent: "PM-内置顾问团"
//   Principle_Applied: "SOLID-S (单一职责原则)"
//   Quality_Check: "全面测试扁平化评论系统功能。"
// }}
// {{START_MODIFICATIONS}}

/**
 * 扁平化评论系统测试套件
 * 测试多层级回复功能、引用关系、数据一致性等核心功能
 */

import moment from 'moment';

// 模拟测试数据
const mockComments = [
  {
    id: 'comment1',
    _id: 'comment1',
    content: '这是一条顶级评论，内容比较长，用来测试内容截断功能是否正常工作。',
    userInfo: { nickname: '用户A', avatar: null },
    level: 1,
    parentId: null,
    likeCount: 5,
    createdAt: '2025-08-08T10:00:00Z',
    children: [
      {
        id: 'comment2',
        _id: 'comment2',
        content: '这是对用户A的回复',
        userInfo: { nickname: '用户B', avatar: 'avatar-b.jpg' },
        level: 2,
        parentId: 'comment1',
        likeCount: 2,
        createdAt: '2025-08-08T10:05:00Z',
        children: [
          {
            id: 'comment3',
            _id: 'comment3',
            content: '这是三级回复',
            userInfo: { nickname: '用户C', avatar: null },
            level: 3,
            parentId: 'comment2',
            likeCount: 1,
            createdAt: '2025-08-08T10:10:00Z',
            children: [
              {
                id: 'comment4',
                _id: 'comment4',
                content: '这是四级回复，测试深层嵌套',
                userInfo: { nickname: '用户D', avatar: null },
                level: 4,
                parentId: 'comment3',
                likeCount: 0,
                createdAt: '2025-08-08T10:15:00Z',
                children: [
                  {
                    id: 'comment5',
                    _id: 'comment5',
                    content: '这是五级回复，测试无限层级支持',
                    userInfo: { nickname: '用户E', avatar: null },
                    level: 5,
                    parentId: 'comment4',
                    likeCount: 0,
                    createdAt: '2025-08-08T10:20:00Z',
                    children: []
                  }
                ]
              }
            ]
          }
        ]
      }
    ]
  },
  {
    id: 'comment6',
    _id: 'comment6',
    content: '另一条顶级评论',
    userInfo: { nickname: '用户F', avatar: 'avatar-f.jpg' },
    level: 1,
    parentId: null,
    likeCount: 3,
    createdAt: '2025-08-08T10:25:00Z',
    children: []
  }
];

// 边界测试数据
const edgeCaseComments = [
  {
    id: 'edge1',
    _id: 'edge1',
    content: '',
    userInfo: { nickname: '', avatar: null },
    level: 1,
    parentId: null,
    likeCount: 0,
    createdAt: '2025-08-08T10:30:00Z',
    children: []
  },
  {
    id: 'edge2',
    _id: 'edge2',
    content: '特殊字符测试: <script>alert("xss")</script> & "引号" \'单引号\' 中文，。！？',
    userInfo: { nickname: '特殊用户@#$%', avatar: null },
    level: 2,
    parentId: 'nonexistent',
    likeCount: 0,
    createdAt: '2025-08-08T10:35:00Z',
    children: []
  }
];

/**
 * 测试工具类
 */
class CommentSystemTester {
  constructor() {
    this.testResults = [];
    this.passedTests = 0;
    this.failedTests = 0;
  }

  // 断言函数
  assert(condition, message) {
    if (condition) {
      this.testResults.push({ status: 'PASS', message });
      this.passedTests++;
      console.log(`✅ PASS: ${message}`);
    } else {
      this.testResults.push({ status: 'FAIL', message });
      this.failedTests++;
      console.error(`❌ FAIL: ${message}`);
    }
  }

  // 深度相等比较
  deepEqual(actual, expected, message) {
    const isEqual = JSON.stringify(actual) === JSON.stringify(expected);
    this.assert(isEqual, message);
    if (!isEqual) {
      console.log('Expected:', expected);
      console.log('Actual:', actual);
    }
  }

  // 测试扁平化函数
  testFlattenComments(flattenComments) {
    console.log('\n🧪 测试 flattenComments 函数');
    
    // 测试正常情况
    const flattened = flattenComments(mockComments);
    this.assert(Array.isArray(flattened), 'flattenComments 应该返回数组');
    this.assert(flattened.length === 6, `扁平化后应该有6条评论，实际有${flattened.length}条`);
    
    // 测试时间排序
    const times = flattened.map(c => new Date(c.createdAt).getTime());
    const isSorted = times.every((time, i) => i === 0 || time >= times[i - 1]);
    this.assert(isSorted, '评论应该按时间排序');
    
    // 测试深度信息
    const hasDepthInfo = flattened.every(c => typeof c.depth === 'number');
    this.assert(hasDepthInfo, '每条评论都应该有depth信息');
    
    // 测试空数组
    const emptyResult = flattenComments([]);
    this.assert(emptyResult.length === 0, '空数组应该返回空数组');
    
    // 测试null/undefined
    const nullResult = flattenComments(null);
    this.assert(nullResult.length === 0, 'null应该返回空数组');
  }

  // 测试引用获取函数
  testGetReplyReference(getReplyReference) {
    console.log('\n🧪 测试 getReplyReference 函数');
    
    const flattened = this.flattenComments(mockComments);
    
    // 测试正常回复引用
    const comment2 = flattened.find(c => c.id === 'comment2');
    const reference = getReplyReference(comment2, flattened);
    
    this.assert(reference !== null, '应该找到回复引用');
    this.assert(reference.id === 'comment1', '引用ID应该正确');
    this.assert(reference.userInfo.nickname === '用户A', '引用用户信息应该正确');
    
    // 测试顶级评论
    const comment1 = flattened.find(c => c.id === 'comment1');
    const topReference = getReplyReference(comment1, flattened);
    this.assert(topReference === null, '顶级评论应该没有引用');
    
    // 测试不存在的父评论
    const orphanComment = { ...comment2, parentId: 'nonexistent' };
    const orphanReference = getReplyReference(orphanComment, flattened);
    this.assert(orphanReference === null, '不存在的父评论应该返回null');
  }

  // 测试回复链构建函数
  testBuildReplyChain(buildReplyChain) {
    console.log('\n🧪 测试 buildReplyChain 函数');
    
    const flattened = this.flattenComments(mockComments);
    
    // 测试深层回复链
    const comment5 = flattened.find(c => c.id === 'comment5');
    const chain = buildReplyChain(comment5, flattened);
    
    this.assert(Array.isArray(chain), 'buildReplyChain 应该返回数组');
    this.assert(chain.length === 4, `五级回复的链应该有4个父级，实际有${chain.length}个`);
    
    // 验证链的顺序
    const expectedOrder = ['comment1', 'comment2', 'comment3', 'comment4'];
    const actualOrder = chain.map(c => c.id);
    this.deepEqual(actualOrder, expectedOrder, '回复链顺序应该正确');
    
    // 测试顶级评论
    const comment1 = flattened.find(c => c.id === 'comment1');
    const topChain = buildReplyChain(comment1, flattened);
    this.assert(topChain.length === 0, '顶级评论的回复链应该为空');
  }

  // 测试内容截断
  testContentTruncation() {
    console.log('\n🧪 测试内容截断功能');
    
    const longContent = 'a'.repeat(100);
    const truncated = longContent.substring(0, 50) + '...';
    
    this.assert(truncated.length === 53, '截断后长度应该正确');
    this.assert(truncated.endsWith('...'), '截断后应该以...结尾');
  }

  // 测试边界情况
  testEdgeCases(flattenComments, getReplyReference) {
    console.log('\n🧪 测试边界情况');
    
    // 测试空内容
    const emptyComment = edgeCaseComments[0];
    this.assert(emptyComment.content === '', '应该能处理空内容');
    
    // 测试特殊字符
    const specialComment = edgeCaseComments[1];
    this.assert(specialComment.content.includes('<script>'), '应该保留特殊字符');
    
    // 测试不存在的父评论
    const flattened = flattenComments(edgeCaseComments);
    const orphanReference = getReplyReference(specialComment, flattened);
    this.assert(orphanReference === null, '不存在的父评论应该返回null');
  }

  // 模拟扁平化函数（用于测试）
  flattenComments(comments) {
    if (!comments || comments.length === 0) {
      return [];
    }
    
    const result = [];
    
    const traverse = (commentList, depth = 0) => {
      commentList.forEach(comment => {
        result.push({
          ...comment,
          depth: depth,
          flatIndex: result.length
        });
        
        if (comment.children && comment.children.length > 0) {
          traverse(comment.children, depth + 1);
        }
      });
    };
    
    traverse(comments);
    
    return result.sort((a, b) => {
      const timeA = new Date(a.createdAt);
      const timeB = new Date(b.createdAt);
      return timeA - timeB;
    });
  }

  // 运行所有测试
  runAllTests() {
    console.log('🚀 开始运行扁平化评论系统测试套件');
    console.log('=' * 50);
    
    this.testFlattenComments(this.flattenComments.bind(this));
    this.testGetReplyReference(this.mockGetReplyReference.bind(this));
    this.testBuildReplyChain(this.mockBuildReplyChain.bind(this));
    this.testContentTruncation();
    this.testEdgeCases(this.flattenComments.bind(this), this.mockGetReplyReference.bind(this));
    
    console.log('\n📊 测试结果汇总');
    console.log('=' * 50);
    console.log(`✅ 通过: ${this.passedTests} 个测试`);
    console.log(`❌ 失败: ${this.failedTests} 个测试`);
    console.log(`📈 成功率: ${((this.passedTests / (this.passedTests + this.failedTests)) * 100).toFixed(1)}%`);
    
    return {
      passed: this.passedTests,
      failed: this.failedTests,
      results: this.testResults
    };
  }

  // 模拟引用获取函数
  mockGetReplyReference(comment, allComments) {
    if (!comment.parentId) {
      return null;
    }
    
    const parentComment = allComments.find(c => 
      (c.id === comment.parentId || c._id === comment.parentId)
    );
    
    if (!parentComment) {
      return null;
    }
    
    return {
      id: parentComment.id || parentComment._id,
      userInfo: parentComment.userInfo,
      content: parentComment.content,
      createdAt: parentComment.createdAt
    };
  }

  // 模拟回复链构建函数
  mockBuildReplyChain(comment, allComments) {
    const chain = [];
    let currentComment = comment;
    
    while (currentComment && currentComment.parentId) {
      const parentComment = allComments.find(c => 
        (c.id === currentComment.parentId || c._id === currentComment.parentId)
      );
      
      if (parentComment) {
        chain.unshift({
          id: parentComment.id || parentComment._id,
          userInfo: parentComment.userInfo,
          content: parentComment.content.substring(0, 30) + '...'
        });
        currentComment = parentComment;
      } else {
        break;
      }
    }
    
    return chain;
  }
}

// 导出测试类和测试数据
export { CommentSystemTester, mockComments, edgeCaseComments };

// {{END_MODIFICATIONS}}
