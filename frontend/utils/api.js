import { request } from './request.js'

// 用户相关API
export const userApi = {
  login: (data) => request('/user/login', 'POST', data),
  register: (data) => request('/user/register', 'POST', data),
  getInfo: () => request('/user/info', 'POST', {})
}

// 应用相关API
export const appApi = {
  add: (data) => request('/app/add', 'POST', data),
  list: () => request('/app/list', 'POST', {}),
  desktop: () => request('/app/desktop', 'POST', {}),
  updatePosition: (data) => request('/app/updatePosition', 'POST', data),
  removeFromDesktop: (data) => request('/app/removeFromDesktop', 'POST', data),
  addToDesktop: (data) => request('/app/addToDesktop', 'POST', data),
  uninstall: (data) => request('/app/uninstall', 'POST', data),
  search: (data) => request('/app/search', 'POST', data),
  update: (data) => request('/app/update', 'POST', data),
  markAsRead: (data) => request('/app/markAsRead', 'POST', data)
}

// 设置相关API
export const settingsApi = {
  get: (data) => request('/settings/get', 'POST', data),
  update: (data) => request('/settings/update', 'POST', data)
}

// 回收站相关API
export const recycleBinApi = {
  list: () => request('/recycleBin/list', 'POST', {}),
  restore: (data) => request('/recycleBin/restore', 'POST', data),
  deletePermanently: (data) => request('/recycleBin/deletePermanently', 'POST', data)
}

// 网站信息相关API
export const websiteApi = {
  getInfo: (data) => request('/website/getInfo', 'POST', data)
}

// 应用文件夹相关API
export const folderApi = {
  create: (data) => request('/folder/create', 'POST', data),
  list: () => request('/folder/list', 'POST', {}),
  getApps: (data) => request('/folder/apps', 'POST', data),
  addApp: (data) => request('/folder/addApp', 'POST', data),
  removeApp: (data) => request('/folder/removeApp', 'POST', data),
  updatePosition: (data) => request('/folder/updatePosition', 'POST', data),
  delete: (data) => request('/folder/delete', 'POST', data),
  update: (data) => request('/folder/update', 'POST', data)
}

// 应用盒子相关API
export const boxApi = {
  create: (data) => request('/box/create', 'POST', data),
  list: () => request('/box/list', 'POST', {}),
  getApps: (data) => request('/box/apps', 'POST', data),
  addApp: (data) => request('/box/addApp', 'POST', data),
  removeApp: (data) => request('/box/removeApp', 'POST', data),
  updatePosition: (data) => request('/box/updatePosition', 'POST', data),
  updateSize: (data) => request('/box/updateSize', 'POST', data),
  toggle: (data) => request('/box/toggle', 'POST', data),
  delete: (data) => request('/box/delete', 'POST', data),
  update: (data) => request('/box/update', 'POST', data)
}

// 小组件相关API
export const widgetApi = {
  add: (data) => request('/widget/add', 'POST', data),
  list: () => request('/widget/list', 'POST', {}),
  updatePosition: (data) => request('/widget/updatePosition', 'POST', data),
  updateSize: (data) => request('/widget/updateSize', 'POST', data),
  updateConfig: (data) => request('/widget/updateConfig', 'POST', data),
  delete: (data) => request('/widget/delete', 'POST', data),
  update: (data) => request('/widget/update', 'POST', data)
}

// 文件上传相关API
export const fileApi = {
  upload: (filePath, type = 'icon') => {
    return new Promise((resolve, reject) => {
      uni.uploadFile({
        url: 'http://localhost:8428/miniHome/file/upload',
        filePath: filePath,
        name: 'file',
        formData: {
          type: type
        },
        header: {
          'satoken': uni.getStorageSync('token') || ''
        },
        success: (res) => {
          try {
            const data = JSON.parse(res.data)
            if (data.code === 200) {
              resolve(data.data)
            } else {
              uni.showToast({
                title: data.message || '上传失败',
                icon: 'none'
              })
              reject(data)
            }
          } catch (e) {
            reject(e)
          }
        },
        fail: (err) => {
          uni.showToast({
            title: '上传失败',
            icon: 'none'
          })
          reject(err)
        }
      })
    })
  }
}

