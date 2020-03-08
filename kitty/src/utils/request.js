import axios from 'axios'
import NProgress from 'nprogress'
import Router from '@/router'
// import { Message } from 'element-ui'
// import { removeToken } from '@/utils/auth'
// import settings from '@/settings'
import vue from 'vue'

const service = axios.create({
  timeout: 30 * 1000 * 2,
  withCredentials: false,
  baseURL: './'
})

service.interceptors.request.use(async config => {
  config.url = '/api' + config.url
  NProgress.start()
  if (config.method === 'get') {
    config.params = config.params || {}
    if (typeof config.params === 'string') {
      config.url += '?t=' + new Date().getTime()
    } else {
      config.params.t = new Date().getTime()
    }
  }
  config.headers.common = {
    Pragma: 'no-cache',
    'Cache-Control': 'no-cache'
  }
  return config
}, error => {
  NProgress.done()
  // 请求错误处理
  Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(
  response => {
    NProgress.done()
    return response
  },
  error => {
    if (!error.response) {
      NProgress.done()
      if (error.code === 'ECONNABORTED') {
        // Message.warning({
        //   message: '当前网络状况不佳...',
        //   duration: 5 * 1000
        // })
      }
      return Promise.reject(error)
    }
    switch (error.response.status) {
      case 401:
        // removeToken()
        Router.push('/login')
        break
      case 500:
        // Message.error(error)
        break
      case 503: case 504:
        break
      default:
        break
    }
    return Promise.reject(error)
  })

export function doRequest (options) {
  return service(options).then(res => {
    if (res.data.code !== 200) {
      const err = new Error(res.message)
      err.res = res
      throw err
    } else {
      return res.data.data
    }
  }).catch(err => {
    // Message.error(err.message)
    throw err
  })
}
vue.prototype.$doRequest = doRequest

export default service
