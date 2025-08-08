import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    if (error.response?.status === 401) {
      // 清除token并跳转到登录页
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 认证相关API
export const authAPI = {
  // 用户注册
  register: (data) => api.post('/auth/register', data),
  
  // 用户登录
  login: (data) => api.post('/auth/login', data),
  
  // 检查用户名是否可用
  checkUsername: (username) => api.get(`/auth/check-username?username=${username}`),
};

// 用户相关API
export const userAPI = {
  // 获取当前用户信息
  getProfile: () => api.get('/users/profile'),
  
  // 根据ID获取用户信息
  getUserById: (userId) => api.get(`/users/${userId}`),
  
  // 更新用户信息
  updateProfile: (data) => api.put('/users/profile', data),
};

// 古文相关API
export const guwenAPI = {
  // 获取古文列表
  getList: (params) => api.get('/guwen', { params }),

  // 获取古文详情
  getById: (id) => api.get(`/guwen/${id}`),

  // 搜索古文
  search: (data) => api.post('/guwen/search', data),

  // 获取热门古文
  getHot: (params) => api.get('/guwen/hot', { params }),

  // 获取所有朝代列表
  getDynasties: () => api.get('/guwen/dynasties'),

  // 获取所有作者列表
  getWriters: () => api.get('/guwen/writers'),
};

// 作者相关API
export const writerAPI = {
  // 获取作者详情
  getById: (id) => api.get(`/writers/${id}`),

  // 获取作者列表
  getList: (params) => api.get('/writers', { params }),

  // 搜索作者
  search: (params) => api.get('/writers/search', { params }),

  // 获取所有朝代列表
  getDynasties: () => api.get('/writers/dynasties'),
};

// 名句相关API
export const sentenceAPI = {
  // 获取名句列表
  getList: (params) => api.get('/sentences', { params }),

  // 获取名句详情
  getById: (id) => api.get(`/sentences/${id}`),

  // 搜索名句
  search: (params) => api.get('/sentences/search', { params }),

  // 获取热门名句
  getHot: (params) => api.get('/sentences/hot', { params }),

  // 获取随机名句
  getRandom: (params) => api.get('/sentences/random', { params }),

  // 获取所有朝代列表
  getDynasties: () => api.get('/sentences/dynasties'),

  // 获取所有作者列表
  getAuthors: () => api.get('/sentences/authors'),

  // 获取所有出处列表
  getSources: () => api.get('/sentences/sources'),
};

// 评论相关API
export const commentAPI = {
  // 获取评论列表
  getList: (params) => api.get('/comments', { params }),

  // 创建评论
  create: (data) => api.post('/comments', data),

  // 删除评论
  delete: (id) => api.delete(`/comments/${id}`),

  // 点赞评论
  like: (id) => api.post(`/comments/${id}/like`),

  // 取消点赞
  unlike: (id) => api.delete(`/comments/${id}/like`),
};

// 用户行为相关API
export const userActionAPI = {
  // 记录用户行为
  recordAction: (data) => api.post('/actions', data),

  // 取消用户行为
  cancelAction: (params) => api.delete('/actions', { params }),

  // 检查用户是否已执行某行为
  hasAction: (params) => api.get('/actions/check', { params }),

  // 获取用户行为列表
  getUserActions: (params) => api.get('/actions/user', { params }),

  // 获取目标的行为统计
  getTargetStats: (params) => api.get('/actions/stats', { params }),

  // 获取热门内容
  getHotContent: (params) => api.get('/actions/hot', { params }),
};

export default api;
