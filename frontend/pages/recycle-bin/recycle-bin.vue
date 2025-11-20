<template>
  <view class="recycle-bin-container">
    <view v-if="items.length === 0" class="empty">
      <text class="empty-text">回收站为空</text>
    </view>
    
    <view v-else class="item-list">
      <view v-for="item in items" :key="item.id" class="item">
        <view class="item-info">
          <text class="item-type">{{ getItemTypeName(item.itemType) }}</text>
          <text class="item-time">{{ formatTime(item.deleteTime) }}</text>
        </view>
        <view class="item-actions">
          <button class="btn-restore" @click="handleRestore(item.id)">恢复</button>
          <button class="btn-delete" @click="handleDelete(item.id)">彻底删除</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { recycleBinApi } from '../../utils/api.js'

const items = ref([])

const loadItems = async () => {
  try {
    const list = await recycleBinApi.list()
    items.value = list
  } catch (error) {
    console.error('加载回收站失败:', error)
  }
}

const getItemTypeName = (type) => {
  const map = {
    'APP': '应用',
    'FOLDER': '文件夹',
    'WIDGET': '小组件'
  }
  return map[type] || type
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

const handleRestore = async (id) => {
  uni.showModal({
    title: '确认恢复',
    content: '确定要恢复该项目吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await recycleBinApi.restore({ recycleBinId: id })
          uni.showToast({
            title: '恢复成功',
            icon: 'success'
          })
          loadItems()
        } catch (error) {
          console.error('恢复失败:', error)
        }
      }
    }
  })
}

const handleDelete = async (id) => {
  uni.showModal({
    title: '确认删除',
    content: '确定要彻底删除该项目吗？此操作不可恢复！',
    success: async (res) => {
      if (res.confirm) {
        try {
          await recycleBinApi.deletePermanently({ recycleBinId: id })
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          })
          loadItems()
        } catch (error) {
          console.error('删除失败:', error)
        }
      }
    }
  })
}

onMounted(() => {
  loadItems()
})
</script>

<style scoped>
.recycle-bin-container {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}

.empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
}

.empty-text {
  font-size: 16px;
  color: #999;
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.item-type {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.item-time {
  font-size: 12px;
  color: #999;
}

.item-actions {
  display: flex;
  gap: 10px;
}

.btn-restore,
.btn-delete {
  padding: 8px 20px;
  border-radius: 6px;
  font-size: 14px;
  border: none;
}

.btn-restore {
  background: #667eea;
  color: white;
}

.btn-delete {
  background: #ff4757;
  color: white;
}
</style>

