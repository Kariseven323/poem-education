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
};

// 作者相关API
export const writerAPI = {
  // 获取作者详情
  getById: (id) => api.get(`/writers/${id}`),
  
  // 获取作者列表
  getList: (params) => api.get('/writers', { params }),
  
  // 搜索作者
  search: (data) => api.post('/writers/search', data),
};

// 名句相关API
export const sentenceAPI = {
  // 获取名句列表
  getList: (params) => api.get('/sentences', { params }),
  
  // 获取名句详情
  getById: (id) => api.get(`/sentences/${id}`),
  
  // 搜索名句
  search: (data) => api.post('/sentences/search', data),
  
  // 获取热门名句
  getHot: (params) => api.get('/sentences/hot', { params }),
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

export default api;
