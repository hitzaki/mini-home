<template>
  <view class="add-app-container">
    <view class="form">
      <view class="form-item">
        <text class="label">应用名称</text>
        <input v-model="form.name" class="input" placeholder="请输入应用名称" />
      </view>
      
      <view class="form-item">
        <text class="label">网站URL</text>
        <input v-model="form.url" class="input" placeholder="https://example.com" />
      </view>
      
      <view class="form-item">
        <text class="label">应用图标</text>
        <view class="icon-upload" @click="chooseIcon">
          <image v-if="form.icon" class="icon-preview" :src="form.icon" mode="aspectFit"></image>
          <view v-else class="icon-placeholder">点击上传图标</view>
        </view>
      </view>
      
      <view class="form-item">
        <text class="label">自动获取图标和名称</text>
        <switch v-model="autoFetch" @change="handleAutoFetch" />
      </view>
      
      <view class="form-item">
        <text class="label">打开方式</text>
        <picker :value="openTypeIndex" :range="openTypes" @change="handleOpenTypeChange">
          <view class="picker">{{ openTypes[openTypeIndex] }}</view>
        </picker>
      </view>
      
      <button class="btn-submit" @click="handleSubmit">添加应用</button>
    </view>
  </view>
</template>

<script setup>
import { ref, watch } from 'vue'
import { appApi, websiteApi, fileApi } from '../../utils/api.js'

const form = ref({
  name: '',
  url: '',
  icon: ''
})

const autoFetch = ref(true)
const openTypeIndex = ref(1) // 默认内部窗口
const openTypes = ['外部浏览器', '内部窗口']

// 监听URL变化，自动获取信息
watch(() => form.value.url, async (newUrl) => {
  if (autoFetch.value && newUrl && newUrl.trim()) {
    try {
      uni.showLoading({
        title: '正在获取...'
      })
      const info = await websiteApi.getInfo({ url: newUrl })
      if (info.title) {
        form.value.name = info.title
      }
      if (info.icon) {
        form.value.icon = info.icon
      }
      uni.hideLoading()
      uni.showToast({
        title: '获取成功',
        icon: 'success'
      })
    } catch (error) {
      uni.hideLoading()
      console.error('获取失败:', error)
    }
  }
})

const handleAutoFetch = async () => {
  if (autoFetch.value && form.value.url) {
    try {
      uni.showLoading({
        title: '正在获取...'
      })
      const info = await websiteApi.getInfo({ url: form.value.url })
      if (info.title) {
        form.value.name = info.title
      }
      if (info.icon) {
        form.value.icon = info.icon
      }
      uni.hideLoading()
      uni.showToast({
        title: '获取成功',
        icon: 'success'
      })
    } catch (error) {
      uni.hideLoading()
      console.error('获取失败:', error)
    }
  }
}

const chooseIcon = async () => {
  uni.chooseImage({
    count: 1,
    success: async (res) => {
      try {
        uni.showLoading({
          title: '上传中...'
        })
        const url = await fileApi.upload(res.tempFilePaths[0], 'icon')
        form.value.icon = url
        uni.hideLoading()
        uni.showToast({
          title: '上传成功',
          icon: 'success'
        })
      } catch (error) {
        uni.hideLoading()
        console.error('上传失败:', error)
      }
    }
  })
}

const handleOpenTypeChange = (e) => {
  openTypeIndex.value = e.detail.value
}

const handleSubmit = async () => {
  if (!form.value.name || !form.value.url) {
    uni.showToast({
      title: '请填写完整信息',
      icon: 'none'
    })
    return
  }
  
  try {
    await appApi.add({
      name: form.value.name,
      icon: form.value.icon,
      type: 'WEB',
      url: form.value.url,
      openType: openTypeIndex.value === 0 ? 'EXTERNAL' : 'INTERNAL'
    })
    
    uni.showToast({
      title: '添加成功',
      icon: 'success'
    })
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('添加失败:', error)
  }
}
</script>

<style scoped>
.add-app-container {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}

.form {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.form-item {
  margin-bottom: 30px;
}

.label {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 10px;
}

.input {
  width: 100%;
  height: 50px;
  padding: 0 15px;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 16px;
}

.icon-upload {
  width: 100px;
  height: 100px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.icon-preview {
  width: 100%;
  height: 100%;
  border-radius: 8px;
}

.icon-placeholder {
  font-size: 12px;
  color: #999;
}

.picker {
  height: 50px;
  line-height: 50px;
  padding: 0 15px;
  background: #f5f5f5;
  border-radius: 8px;
}

.btn-submit {
  width: 100%;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  border: none;
  margin-top: 20px;
}
</style>

