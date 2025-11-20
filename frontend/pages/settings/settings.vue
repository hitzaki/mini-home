<template>
  <view class="settings-container">
    <view class="settings-section">
      <text class="section-title">主题设置</text>
      <view class="theme-list">
        <view 
          v-for="theme in themes" 
          :key="theme.key"
          class="theme-item"
          :class="{ active: currentTheme === theme.key }"
          @click="selectTheme(theme.key)"
        >
          <view class="theme-preview" :style="{ background: theme.color }"></view>
          <text class="theme-name">{{ theme.name }}</text>
        </view>
      </view>
    </view>
    
    <view class="settings-section">
      <text class="section-title">壁纸设置</text>
      <view class="wallpaper-upload" @click="chooseWallpaper">
        <image v-if="wallpaper" class="wallpaper-preview" :src="wallpaper" mode="aspectFill"></image>
        <view v-else class="wallpaper-placeholder">点击选择壁纸</view>
      </view>
    </view>
    
    <view class="settings-section">
      <text class="section-title">默认打开方式</text>
      <picker :value="openTypeIndex" :range="openTypes" @change="handleOpenTypeChange">
        <view class="picker">{{ openTypes[openTypeIndex] }}</view>
      </picker>
    </view>
    
    <view class="settings-section">
      <text class="section-title">回收站自动清理</text>
      <view class="auto-clean">
        <input v-model.number="autoCleanDays" class="input" type="number" placeholder="天数" />
        <text class="unit">天后自动清理（0表示不自动清理）</text>
      </view>
    </view>
    
    <button class="btn-save" @click="handleSave">保存设置</button>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { settingsApi, fileApi } from '../../utils/api.js'

const currentTheme = ref('default')
const wallpaper = ref('')
const openTypeIndex = ref(1)
const autoCleanDays = ref(30)
const openTypes = ['外部浏览器', '内部窗口']

const themes = [
  { key: 'default', name: '默认', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', type: 'light' },
  { key: 'dark', name: '黑暗', color: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 100%)', type: 'dark' },
  { key: 'light', name: '洁白', color: 'linear-gradient(135deg, #ffffff 0%, #f5f5f5 100%)', type: 'light' },
  { key: 'sakura', name: '樱花粉', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', type: 'light' },
  { key: 'ocean', name: '海洋蓝', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)', type: 'light' },
  { key: 'forest', name: '森林绿', color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)', type: 'light' }
]

const loadSettings = async () => {
  try {
    const deviceType = uni.getSystemInfoSync().platform === 'android' || uni.getSystemInfoSync().platform === 'ios' ? 'MOBILE' : 'PC'
    const settings = await settingsApi.get({ deviceType })
    currentTheme.value = settings.theme || 'default'
    wallpaper.value = settings.wallpaper || ''
    openTypeIndex.value = settings.defaultOpenType === 'EXTERNAL' ? 0 : 1
    autoCleanDays.value = settings.recycleBinAutoCleanDays || 30
  } catch (error) {
    console.error('加载设置失败:', error)
  }
}

const selectTheme = (themeKey) => {
  currentTheme.value = themeKey
  // 应用主题到全局
  const theme = themes.find(t => t.key === themeKey)
  if (theme) {
    // 可以通过store或全局样式应用主题
    // 这里简化处理，实际可以通过CSS变量或动态类名实现
  }
}

const chooseWallpaper = async () => {
  uni.chooseImage({
    count: 1,
    success: async (res) => {
      try {
        uni.showLoading({
          title: '上传中...'
        })
        const url = await fileApi.upload(res.tempFilePaths[0], 'wallpaper')
        wallpaper.value = url
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

const handleSave = async () => {
  try {
    const deviceType = uni.getSystemInfoSync().platform === 'android' || uni.getSystemInfoSync().platform === 'ios' ? 'MOBILE' : 'PC'
    await settingsApi.update({
      deviceType: deviceType,
      theme: currentTheme.value,
      wallpaper: wallpaper.value,
      defaultOpenType: openTypeIndex.value === 0 ? 'EXTERNAL' : 'INTERNAL',
      recycleBinAutoCleanDays: autoCleanDays.value
    })
    
    uni.showToast({
      title: '保存成功',
      icon: 'success'
    })
    
    // 保存成功后，延迟返回主界面，让主界面重新加载设置
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('保存失败:', error)
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.settings-container {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}

.settings-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
  display: block;
}

.theme-list {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.theme-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

.theme-preview {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  margin-bottom: 8px;
  border: 3px solid transparent;
}

.theme-item.active .theme-preview {
  border-color: #667eea;
}

.theme-name {
  font-size: 12px;
  color: #666;
}

.wallpaper-upload {
  width: 100%;
  height: 200px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
}

.wallpaper-preview {
  width: 100%;
  height: 100%;
}

.wallpaper-placeholder {
  font-size: 14px;
  color: #999;
}

.picker {
  height: 50px;
  line-height: 50px;
  padding: 0 15px;
  background: #f5f5f5;
  border-radius: 8px;
}

.auto-clean {
  display: flex;
  align-items: center;
  gap: 10px;
}

.input {
  width: 100px;
  height: 50px;
  padding: 0 15px;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 16px;
}

.unit {
  font-size: 14px;
  color: #666;
}

.btn-save {
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

