import axios from "../node_modules/axios/index"

// 创建Axios实例
const Axios = axios.create({
  // baseURL:'http://localhost:8100',
  timeout:5000,
  // 是否在发送请求时携带Cookie
  withCredentials:true
})

// 添加请求拦截器
Axios.interceptors.request.use(function (config) {
  // 在发送请求之前做些什么
  return config;
}, function (error) {
  // 对请求错误做些什么
  return Promise.reject(error);
});

// 添加响应拦截器
Axios.interceptors.response.use(function (response) {
  // 2xx 范围内的状态码都会触发该函数。
  // 对响应数据做点什么
  return response;
}, function (error) {
  // 超出 2xx 范围的状态码都会触发该函数。
  // 对响应错误做点什么
  return Promise.reject(error);
});

export default Axios
