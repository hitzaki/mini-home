// 请求工具
const baseURL = 'http://localhost:8428/miniHome'

export function request(url, method = 'POST', data = {}) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: baseURL + url,
      method: method,
      data: data,
      timeout: 30000, // 30秒超时
      header: {
        'Content-Type': 'application/json',
        'satoken': uni.getStorageSync('token') || ''
      },
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data && res.data.code === 200) {
            resolve(res.data.data)
          } else {
            const code = res.data?.code
            const message = res.data?.message || '请求失败'
            // 如果是401未授权或登录相关错误，跳转登录
            if (code === 401 || code === 403 || 
                message.includes('未登录') || 
                message.includes('token') || 
                message.includes('登录') ||
                message.includes('过期')) {
              uni.removeStorageSync('token')
              uni.showToast({
                title: message || '登录已过期，请重新登录',
                icon: 'none',
                duration: 2000
              })
              setTimeout(() => {
                uni.reLaunch({
                  url: '/pages/login/login'
                })
              }, 1500)
            } else {
              uni.showToast({
                title: message,
                icon: 'none'
              })
            }
            reject(res.data || res)
          }
        } else if (res.statusCode === 401 || res.statusCode === 403) {
          // HTTP状态码401/403，跳转登录
          uni.removeStorageSync('token')
          uni.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none',
            duration: 2000
          })
          setTimeout(() => {
            uni.reLaunch({
              url: '/pages/login/login'
            })
          }, 1500)
          reject(res)
        } else {
          uni.showToast({
            title: '请求失败: ' + res.statusCode,
            icon: 'none'
          })
          reject(res)
        }
      },
      fail: (err) => {
        console.error('请求失败:', err)
        let errorMessage = '网络请求失败'
        if (err.errMsg) {
          if (err.errMsg.includes('timeout') || err.errMsg.includes('超时')) {
            errorMessage = '连接服务器超时，请检查网络后重试'
          } else if (err.errMsg.includes('fail')) {
            errorMessage = '无法连接到服务器，请检查网络'
          } else {
            errorMessage = err.errMsg
          }
        }
        uni.showToast({
          title: errorMessage,
          icon: 'none',
          duration: 3000
        })
        reject(err)
      }
    })
  })
}

