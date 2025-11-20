<template>
  <view 
    class="desktop-container" 
    :style="wallpaper ? { backgroundImage: `url(${wallpaper})` } : {}"
    @longpress="showDesktopContextMenu"
    @contextmenu.prevent="showDesktopContextMenu"
    @touchstart="handleDesktopTouchStart"
  >
    <!-- 加载错误提示 -->
    <view v-if="loadError && desktopApps.length === 0" class="error-container" @click="retryLoad">
      <text class="error-text">连接服务器失败</text>
      <text class="error-hint">点击屏幕重试</text>
    </view>
    
    <!-- 桌面应用图标和文件夹 -->
    <view class="desktop-icons">
      <!-- 文件夹图标 -->
      <view 
        v-for="folder in folders" 
        :key="'folder-' + folder.id"
        class="app-icon folder-icon"
        :style="{ left: (folder.positionX || 0) + 'px', top: (folder.positionY || 0) + 'px' }"
        @click="openFolder(folder)"
        @longpress="showFolderContextMenu(folder, $event)"
        @contextmenu.prevent="showFolderContextMenu(folder, $event)"
      >
        <view class="folder-icon-container">
          <view class="folder-icon-grid">
            <view v-for="i in 4" :key="i" class="folder-icon-item"></view>
          </view>
        </view>
        <text class="icon-name">{{ folder.name }}</text>
      </view>
      
      <!-- 应用图标 -->
      <view 
        v-for="app in desktopApps" 
        :key="app.id"
        class="app-icon"
        :class="{ 
          'dragging': (iconDragState && iconDragState.appId === app.id) || (iconDragStatePC && iconDragStatePC.appId === app.id),
          'drag-to-folder': dragToFolderState && dragToFolderState.appId === app.id
        }"
        :style="{ left: (app.positionX || 0) + 'px', top: (app.positionY || 0) + 'px' }"
        @click="handleAppClick(app, $event)"
        @longpress="showAppContextMenu(app, $event)"
        @contextmenu.prevent="showAppContextMenu(app, $event)"
        @touchstart="handleIconDragStart(app, $event)"
        @touchmove="handleIconDrag"
        @touchend="handleIconDragEnd(app)"
        @mousedown="handleIconDragStartPC(app, $event)"
        @mousemove="handleIconDragPC"
        @mouseup="handleIconDragEndPC(app)"
        @mouseleave="handleIconDragEndPC(app)"
      >
        <image class="icon-img" :src="app.icon || '/static/default-app.png'" mode="aspectFit"></image>
        <text class="icon-name">{{ app.name }}</text>
        <view v-if="app.isNew" class="new-badge">新</view>
      </view>
      
      <!-- 拖拽到文件夹的提示区域 -->
      <view 
        v-if="dragToFolderState"
        v-for="folder in folders"
        :key="'drop-' + folder.id"
        class="folder-drop-zone"
        :style="{ 
          left: (folder.positionX || 0) + 'px', 
          top: (folder.positionY || 0) + 'px',
          opacity: isOverFolder(folder) ? 1 : 0.3
        }"
        @mouseenter="dragToFolderState.targetFolderId = folder.id"
        @mouseleave="dragToFolderState.targetFolderId = null"
        @mouseup="handleDropToFolder"
      >
        <text class="drop-hint">拖到此处</text>
      </view>
    </view>
    
    <!-- 应用盒子 -->
    <view class="app-boxes">
      <view 
        v-for="box in boxes" 
        :key="'box-' + box.id"
        class="app-box"
        :class="{ 'expanded': box.isExpanded === 1, 'dragging': boxDragState && boxDragState.boxId === box.id }"
        :style="{ 
          left: (box.positionX || 0) + 'px', 
          top: (box.positionY || 0) + 'px',
          width: box.width + 'px',
          height: box.isExpanded === 1 ? box.height + 'px' : '40px'
        }"
        @mousedown="handleBoxDragStart(box, $event)"
        @touchstart="handleBoxDragStart(box, $event)"
      >
        <view class="box-header" @click="toggleBox(box)">
          <text class="box-title">{{ box.name }}</text>
          <view class="box-controls">
            <text class="box-toggle">{{ box.isExpanded === 1 ? '−' : '+' }}</text>
            <text class="box-close" @click.stop="deleteBox(box)">×</text>
          </view>
        </view>
        <view v-if="box.isExpanded === 1" class="box-content">
          <view 
            v-for="app in getBoxApps(box.id)" 
            :key="app.id"
            class="box-app-item"
            @click="handleAppClick(app)"
          >
            <image class="box-app-icon" :src="app.icon || '/static/default-app.png'"></image>
            <text class="box-app-name">{{ app.name }}</text>
          </view>
          <view v-if="getBoxApps(box.id).length === 0" class="empty-box">
            <text class="empty-text">拖拽应用到这里</text>
          </view>
        </view>
        <!-- 调整大小手柄 -->
        <view 
          v-if="box.isExpanded === 1"
          class="box-resize-handle"
          @mousedown="handleBoxResizeStart(box, $event)"
          @touchstart="handleBoxResizeStart(box, $event)"
        ></view>
      </view>
    </view>
    
    <!-- 桌面小组件 -->
    <view class="desktop-widgets">
      <view 
        v-for="widget in desktopWidgets" 
        :key="widget.id"
        class="widget"
        :class="{
          'clock-widget': widget.type === 'CLOCK',
          'calendar-widget': widget.type === 'CALENDAR',
          'weather-widget': widget.type === 'WEATHER',
          'performance-widget': widget.type === 'PERFORMANCE',
          'dragging': widgetDragState && widgetDragState.widgetId === widget.id
        }"
        :style="{ 
          left: (widget.positionX || 0) + 'px', 
          top: (widget.positionY || 0) + 'px',
          width: widget.width + 'px',
          height: widget.height + 'px'
        }"
        @mousedown="handleWidgetDragStart(widget, $event)"
        @touchstart="handleWidgetDragStart(widget, $event)"
      >
        <!-- 时钟组件 -->
        <view v-if="widget.type === 'CLOCK'" class="widget-content">
          <text class="time">{{ currentTime }}</text>
          <text class="date">{{ currentDate }}</text>
        </view>
        
        <!-- 日历组件 -->
        <view v-if="widget.type === 'CALENDAR'" class="widget-content calendar-content">
          <view class="calendar-header">
            <text class="calendar-month">{{ calendarMonth }}</text>
          </view>
          <view class="calendar-grid">
            <view v-for="day in calendarDays" :key="day" class="calendar-day" :class="{ 'today': day === currentDay }">
              {{ day }}
            </view>
          </view>
        </view>
        
        <!-- 天气组件 -->
        <view v-if="widget.type === 'WEATHER'" class="widget-content weather-content">
          <text class="weather-icon">☀️</text>
          <text class="weather-temp">{{ weatherTemp }}°C</text>
          <text class="weather-desc">{{ weatherDesc }}</text>
        </view>
        
        <!-- 性能监控组件 -->
        <view v-if="widget.type === 'PERFORMANCE'" class="widget-content performance-content">
          <view class="performance-item">
            <text class="performance-label">CPU</text>
            <view class="performance-bar">
              <view class="performance-fill" :style="{ width: performanceData.cpu + '%' }"></view>
            </view>
            <text class="performance-value">{{ performanceData.cpu }}%</text>
          </view>
          <view class="performance-item">
            <text class="performance-label">内存</text>
            <view class="performance-bar">
              <view class="performance-fill" :style="{ width: performanceData.memory + '%' }"></view>
            </view>
            <text class="performance-value">{{ performanceData.memory }}%</text>
          </view>
        </view>
        
        <!-- 小组件控制按钮 -->
        <view class="widget-controls">
          <text class="widget-resize" @mousedown.stop="handleWidgetResizeStart(widget, $event)" @touchstart.stop="handleWidgetResizeStart(widget, $event)">⚙</text>
          <text class="widget-close" @click.stop="deleteWidget(widget)">×</text>
        </view>
      </view>
    </view>
    
    <!-- 右键菜单 -->
    <view 
      v-if="contextMenu.show" 
      class="context-menu"
      :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }"
      @click.stop
      @contextmenu.prevent.stop
    >
      <view 
        v-for="item in contextMenu.items" 
        :key="item.key"
        class="menu-item"
        @click="handleMenuClick(item.key)"
      >
        {{ item.label }}
      </view>
    </view>
    
    <!-- 点击空白处关闭菜单 -->
    <view 
      v-if="contextMenu.show" 
      class="context-menu-overlay"
      @click="closeContextMenu"
      @contextmenu.prevent="closeContextMenu"
    ></view>
    
    <!-- 底部任务栏 -->
    <view class="taskbar">
      <view class="taskbar-left">
        <view class="taskbar-icon" @click="showAppList">
          <text class="iconfont">☰</text>
        </view>
        <view class="taskbar-icon" @click="showSearch">
          <text class="iconfont">🔍</text>
        </view>
      </view>
      <view class="taskbar-center">
        <!-- 已打开的应用窗口 -->
        <view 
          v-for="window in openedWindows" 
          :key="window.id"
          class="taskbar-window"
          @click="bringToFront(window.id)"
        >
          <text class="taskbar-window-title">{{ window.title }}</text>
        </view>
        <!-- 最小化的窗口 -->
        <view 
          v-for="window in minimizedWindows" 
          :key="'min-' + window.id"
          class="taskbar-window minimized"
          @click="restoreWindow(window.id)"
        >
          <text class="taskbar-window-title">{{ window.title }}</text>
        </view>
      </view>
      <view class="taskbar-right">
        <view class="taskbar-icon" @click="showSettings">
          <text class="iconfont">⚙</text>
        </view>
      </view>
    </view>
    
    <!-- 应用窗口 -->
    <view v-if="openedWindows.length > 0" class="windows-container">
      <view 
        v-for="window in openedWindows" 
        :key="window.id"
        class="app-window"
        :style="{ 
          left: window.x + 'px', 
          top: window.y + 'px',
          width: window.width + 'px',
          height: window.height + 'px',
          zIndex: window.zIndex
        }"
      >
        <view class="window-header" @touchstart="handleWindowDragStart(window, $event)" @touchmove="handleWindowDrag" @touchend="handleWindowDragEnd" @mousedown="handleWindowDragStartPC(window, $event)">
          <text class="window-title">{{ window.title }}</text>
          <view class="window-controls">
            <view class="window-btn" @click.stop="minimizeWindow(window.id)">−</view>
            <view class="window-btn" @click.stop="maximizeWindow(window.id)">□</view>
            <view class="window-btn close" @click.stop="closeWindow(window.id)">×</view>
          </view>
        </view>
        <view class="window-content">
          <!-- 调整大小手柄 -->
          <view class="window-resize-handle" @mousedown="handleWindowResizeStart(window, $event)" @touchstart="handleWindowResizeStart(window, $event)"></view>
        <!-- #ifdef H5 -->
        <iframe v-if="window.type === 'web'" :src="window.url" style="width: 100%; height: 100%; border: none;"></iframe>
        <!-- #endif -->
        <!-- #ifndef H5 -->
        <web-view v-if="window.type === 'web'" :src="window.url"></web-view>
        <!-- #endif -->
        <view v-if="window.type !== 'web'" class="native-app-content">
          {{ window.title }} 内容
        </view>
        </view>
      </view>
    </view>
    
    <!-- 应用列表弹窗 -->
    <view v-if="showAppListModal" class="modal-overlay" @click="showAppListModal = false">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">应用列表</text>
          <text class="modal-close" @click="showAppListModal = false">×</text>
        </view>
        <view class="app-list-content">
          <view 
            v-for="app in allApps" 
            :key="app.id"
            class="app-list-item"
            @click="addAppToDesktop(app)"
          >
            <image class="app-list-icon" :src="app.icon || '/static/default-app.png'"></image>
            <text class="app-list-name">{{ app.name }}</text>
            <view v-if="app.isNew" class="new-badge-small">新</view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 搜索弹窗 -->
    <view v-if="showSearchModal" class="modal-overlay" @click="showSearchModal = false">
      <view class="modal-content search-modal" @click.stop>
        <input 
          v-model="searchKeyword" 
          class="search-input" 
          placeholder="搜索应用..." 
          @input="handleSearch"
          @confirm="handleSearchConfirm"
        />
        <view class="search-results">
          <view 
            v-for="app in searchResults" 
            :key="app.id"
            class="search-result-item"
            @click="handleAppClick(app)"
          >
            <image class="search-icon" :src="app.icon || '/static/default-app.png'"></image>
            <text class="search-name">{{ app.name }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 文件夹弹窗 -->
    <view v-if="showFolderModal" class="modal-overlay" @click="closeFolder">
      <view class="modal-content folder-modal" @click.stop>
        <view class="modal-header">
          <text class="modal-title">文件夹</text>
          <text class="modal-close" @click="closeFolder">×</text>
        </view>
        <view class="folder-content">
          <view 
            v-for="app in currentFolderApps" 
            :key="app.id"
            class="folder-app-item"
            @click="handleAppClick(app)"
            @longpress="showAppContextMenu(app, $event)"
            @contextmenu.prevent="showAppContextMenu(app, $event)"
          >
            <image class="folder-app-icon" :src="app.icon || '/static/default-app.png'"></image>
            <text class="folder-app-name">{{ app.name }}</text>
          </view>
          <view v-if="currentFolderApps.length === 0" class="empty-folder">
            <text class="empty-text">文件夹为空</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 添加小组件对话框 -->
    <view v-if="showAddWidgetModal" class="modal-overlay" @click="showAddWidgetModal = false">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">添加小组件</text>
          <text class="modal-close" @click="showAddWidgetModal = false">×</text>
        </view>
        <view class="widget-list-content">
          <view 
            v-for="widgetType in availableWidgetTypes" 
            :key="widgetType.type"
            class="widget-type-item"
            @click="addWidgetToDesktop(widgetType)"
          >
            <text class="widget-type-icon">{{ widgetType.icon }}</text>
            <text class="widget-type-name">{{ widgetType.name }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 属性对话框 -->
    <view v-if="showPropertiesModal" class="modal-overlay" @click="showPropertiesModal = false">
      <view class="modal-content properties-modal" @click.stop>
        <view class="modal-header">
          <text class="modal-title">应用属性</text>
          <text class="modal-close" @click="showPropertiesModal = false">×</text>
        </view>
        <view v-if="currentPropertiesApp" class="properties-content">
          <view class="property-item">
            <text class="property-label">应用名称：</text>
            <input 
              v-model="currentPropertiesApp.name" 
              class="property-input"
              placeholder="应用名称"
            />
          </view>
          <view class="property-item">
            <text class="property-label">应用图标：</text>
            <view class="property-icon">
              <image 
                class="icon-preview-small" 
                :src="currentPropertiesApp.icon || '/static/default-app.png'"
                mode="aspectFit"
              ></image>
              <button class="btn-change-icon" @click="changeAppIcon">更换图标</button>
            </view>
          </view>
          <view class="property-item">
            <text class="property-label">应用类型：</text>
            <text class="property-value">{{ currentPropertiesApp.type === 'WEB' ? 'Web应用' : '原生应用' }}</text>
          </view>
          <view v-if="currentPropertiesApp.type === 'WEB'" class="property-item">
            <text class="property-label">URL：</text>
            <input 
              v-model="currentPropertiesApp.url" 
              class="property-input"
              placeholder="应用URL"
            />
          </view>
          <view class="property-item">
            <text class="property-label">打开方式：</text>
            <picker 
              :value="currentPropertiesApp.openType === 'EXTERNAL' ? 0 : 1" 
              :range="['外部浏览器', '内部窗口']" 
              @change="handlePropertiesOpenTypeChange"
            >
              <view class="property-picker">
                {{ currentPropertiesApp.openType === 'EXTERNAL' ? '外部浏览器' : '内部窗口' }}
              </view>
            </picker>
          </view>
          <view class="property-item">
            <text class="property-label">创建时间：</text>
            <text class="property-value">{{ formatDate(currentPropertiesApp.createTime) }}</text>
          </view>
          <view class="property-actions">
            <button class="btn-cancel" @click="showPropertiesModal = false">取消</button>
            <button class="btn-save" @click="saveProperties">保存</button>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { appApi, settingsApi, fileApi, folderApi, boxApi, widgetApi } from '../../utils/api.js'
import { useStore } from 'vuex'

const store = useStore()

const desktopApps = ref([])
const allApps = ref([])
const folders = ref([])
const boxes = ref([])
const wallpaper = ref('')
const currentTime = ref('')
const currentDate = ref('')
const calendarMonth = ref('')
const calendarDays = ref([])
const currentDay = ref(0)
const weatherTemp = ref('25')
const weatherDesc = ref('晴天')
const performanceData = ref({ cpu: 45, memory: 60 })
const desktopWidgets = ref([])
const widgetDragState = ref(null)
const widgetResizeState = ref(null)
const showAddWidgetModal = ref(false)
const showClock = ref(true)
const showAppListModal = ref(false)
const showSearchModal = ref(false)
const showFolderModal = ref(false)
const currentFolderApps = ref([])
const currentFolderId = ref(null)
const searchKeyword = ref('')
const searchResults = ref([])
const openedWindows = ref([])
const minimizedWindows = ref([]) // 最小化的窗口
const showPropertiesModal = ref(false)
const currentPropertiesApp = ref(null)
const isLoading = ref(false)
const dragToFolderState = ref(null) // 拖拽到文件夹的状态
const boxDragState = ref(null) // 盒子拖拽状态
const boxResizeState = ref(null) // 盒子调整大小状态
const loadError = ref(false)
let windowZIndex = 1000

const contextMenu = ref({
  show: false,
  x: 0,
  y: 0,
  items: [],
  target: null,
  targetType: 'app' // 'app' or 'folder'
})

// 重命名文件夹
const renameFolder = async (folder) => {
  uni.showModal({
    title: '重命名文件夹',
    editable: true,
    placeholderText: '请输入新名称',
    content: folder.name,
    success: async (res) => {
      if (res.confirm && res.content && res.content.trim()) {
        try {
          await folderApi.update({
            id: folder.id,
            name: res.content.trim()
          })
          loadFolders()
          uni.showToast({
            title: '重命名成功',
            icon: 'success'
          })
        } catch (error) {
          console.error('重命名失败:', error)
        }
      }
    }
  })
}

// 获取桌面应用
const loadDesktopApps = async () => {
  try {
    loadError.value = false
    isLoading.value = true
    const apps = await appApi.desktop()
    desktopApps.value = apps || []
    store.commit('setDesktopApps', apps || [])
    loadError.value = false
  } catch (error) {
    console.error('加载桌面应用失败:', error)
    // 如果加载失败，设置为空数组，避免界面异常
    desktopApps.value = []
    loadError.value = true
  } finally {
    isLoading.value = false
  }
}

// 获取所有应用
const loadAllApps = async () => {
  try {
    const apps = await appApi.list()
    allApps.value = apps || []
    store.commit('setAllApps', apps || [])
  } catch (error) {
    console.error('加载应用列表失败:', error)
    // 如果加载失败，设置为空数组，避免界面异常
    allApps.value = []
  }
}

// 加载文件夹
const loadFolders = async () => {
  try {
    const folderList = await folderApi.list()
    folders.value = folderList || []
  } catch (error) {
    console.error('加载文件夹失败:', error)
    folders.value = []
  }
}

// 打开文件夹
const openFolder = async (folder) => {
  try {
    currentFolderId.value = folder.id
    const apps = await folderApi.getApps({ folderId: folder.id })
    currentFolderApps.value = apps || []
    showFolderModal.value = true
  } catch (error) {
    console.error('打开文件夹失败:', error)
    uni.showToast({
      title: '打开文件夹失败',
      icon: 'none'
    })
  }
}

// 关闭文件夹
const closeFolder = () => {
  showFolderModal.value = false
  currentFolderId.value = null
  currentFolderApps.value = []
}

// 加载应用盒子
const loadBoxes = async () => {
  try {
    const boxList = await boxApi.list()
    boxes.value = boxList || []
    // 加载所有展开盒子的应用
    for (const box of boxes.value) {
      if (box.isExpanded === 1) {
        await loadBoxApps(box.id)
      }
    }
  } catch (error) {
    console.error('加载应用盒子失败:', error)
    boxes.value = []
  }
}

// 获取盒子内的应用（缓存）
const boxAppsCache = ref({})
const getBoxApps = (boxId) => {
  return boxAppsCache.value[boxId] || []
}

// 加载盒子内的应用
const loadBoxApps = async (boxId) => {
  try {
    const apps = await boxApi.getApps({ boxId })
    boxAppsCache.value[boxId] = apps || []
  } catch (error) {
    console.error('加载盒子应用失败:', error)
    boxAppsCache.value[boxId] = []
  }
}

// 切换盒子展开/收起
const toggleBox = async (box) => {
  try {
    await boxApi.toggle({ boxId: box.id })
    box.isExpanded = box.isExpanded === 1 ? 0 : 1
    if (box.isExpanded === 1) {
      await loadBoxApps(box.id)
    }
  } catch (error) {
    console.error('切换盒子状态失败:', error)
  }
}

// 删除盒子
const deleteBox = async (box) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除应用盒子 ${box.name} 吗？`,
    success: async (res) => {
      if (res.confirm) {
        try {
          await boxApi.delete({ boxId: box.id })
          loadBoxes()
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          })
        } catch (error) {
          console.error('删除盒子失败:', error)
        }
      }
    }
  })
}

// 创建新应用盒子
const createNewBox = async () => {
  uni.showModal({
    title: '新建应用盒子',
    editable: true,
    placeholderText: '请输入盒子名称',
    success: async (res) => {
      if (res.confirm && res.content) {
        try {
          const systemInfo = uni.getSystemInfoSync()
          const x = Math.random() * (systemInfo.windowWidth - 300)
          const y = Math.random() * (systemInfo.windowHeight - 400)
          
          await boxApi.create({
            name: res.content,
            x: Math.floor(x),
            y: Math.floor(y),
            width: 300,
            height: 400
          })
          
          loadBoxes()
          uni.showToast({
            title: '创建成功',
            icon: 'success'
          })
        } catch (error) {
          console.error('创建应用盒子失败:', error)
          uni.showToast({
            title: '创建失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

// 盒子拖拽
const handleBoxDragStart = (box, event) => {
  if (event.button !== 0 && event.touches === undefined) return
  event.preventDefault()
  event.stopPropagation()
  
  let startX, startY
  if (event.touches && event.touches.length > 0) {
    startX = event.touches[0].clientX
    startY = event.touches[0].clientY
  } else {
    startX = event.clientX
    startY = event.clientY
  }
  
  boxDragState.value = {
    boxId: box.id,
    startX: startX,
    startY: startY,
    boxX: box.positionX || 0,
    boxY: box.positionY || 0
  }
  
  // #ifdef H5
  if (typeof document !== 'undefined') {
    document.addEventListener('mousemove', handleBoxDragGlobal)
    document.addEventListener('mouseup', handleBoxDragEndGlobal)
    document.addEventListener('touchmove', handleBoxDragGlobal)
    document.addEventListener('touchend', handleBoxDragEndGlobal)
  }
  // #endif
}

const handleBoxDragGlobal = (event) => {
  if (!boxDragState.value) return
  event.preventDefault()
  
  const box = boxes.value.find(b => b.id === boxDragState.value.boxId)
  if (!box) return
  
  let currentX, currentY
  if (event.touches && event.touches.length > 0) {
    currentX = event.touches[0].clientX
    currentY = event.touches[0].clientY
  } else {
    currentX = event.clientX
    currentY = event.clientY
  }
  
  const deltaX = currentX - boxDragState.value.startX
  const deltaY = currentY - boxDragState.value.startY
  box.positionX = Math.max(0, boxDragState.value.boxX + deltaX)
  box.positionY = Math.max(0, boxDragState.value.boxY + deltaY)
}

const handleBoxDragEndGlobal = async () => {
  if (!boxDragState.value) return
  
  const box = boxes.value.find(b => b.id === boxDragState.value.boxId)
  if (box) {
    try {
      await boxApi.updatePosition({
        boxId: box.id,
        x: box.positionX,
        y: box.positionY
      })
    } catch (error) {
      console.error('更新盒子位置失败:', error)
    }
  }
  
  boxDragState.value = null
  
  // #ifdef H5
  if (typeof document !== 'undefined') {
    document.removeEventListener('mousemove', handleBoxDragGlobal)
    document.removeEventListener('mouseup', handleBoxDragEndGlobal)
    document.removeEventListener('touchmove', handleBoxDragGlobal)
    document.removeEventListener('touchend', handleBoxDragEndGlobal)
  }
  // #endif
}

// 盒子调整大小
const handleBoxResizeStart = (box, event) => {
  if (event.button !== 0 && event.touches === undefined) return
  event.preventDefault()
  event.stopPropagation()
  
  let startX, startY
  if (event.touches && event.touches.length > 0) {
    startX = event.touches[0].clientX
    startY = event.touches[0].clientY
  } else {
    startX = event.clientX
    startY = event.clientY
  }
  
  boxResizeState.value = {
    boxId: box.id,
    startX: startX,
    startY: startY,
    boxWidth: box.width,
    boxHeight: box.height
  }
  
  // #ifdef H5
  if (typeof document !== 'undefined') {
    document.addEventListener('mousemove', handleBoxResizeGlobal)
    document.addEventListener('mouseup', handleBoxResizeEndGlobal)
    document.addEventListener('touchmove', handleBoxResizeGlobal)
    document.addEventListener('touchend', handleBoxResizeEndGlobal)
  }
  // #endif
}

const handleBoxResizeGlobal = (event) => {
  if (!boxResizeState.value) return
  event.preventDefault()
  
  const box = boxes.value.find(b => b.id === boxResizeState.value.boxId)
  if (!box) return
  
  let currentX, currentY
  if (event.touches && event.touches.length > 0) {
    currentX = event.touches[0].clientX
    currentY = event.touches[0].clientY
  } else {
    currentX = event.clientX
    currentY = event.clientY
  }
  
  const deltaX = currentX - boxResizeState.value.startX
  const deltaY = currentY - boxResizeState.value.startY
  box.width = Math.max(200, boxResizeState.value.boxWidth + deltaX)
  box.height = Math.max(150, boxResizeState.value.boxHeight + deltaY)
}

const handleBoxResizeEndGlobal = async () => {
  if (!boxResizeState.value) return
  
  const box = boxes.value.find(b => b.id === boxResizeState.value.boxId)
  if (box) {
    try {
      await boxApi.updateSize({
        boxId: box.id,
        width: box.width,
        height: box.height
      })
    } catch (error) {
      console.error('更新盒子大小失败:', error)
    }
  }
  
  boxResizeState.value = null
  
  // #ifdef H5
  if (typeof document !== 'undefined') {
    document.removeEventListener('mousemove', handleBoxResizeGlobal)
    document.removeEventListener('mouseup', handleBoxResizeEndGlobal)
    document.removeEventListener('touchmove', handleBoxResizeGlobal)
    document.removeEventListener('touchend', handleBoxResizeEndGlobal)
  }
  // #endif
}

// 获取用户设置
const loadSettings = async () => {
  try {
    const deviceType = uni.getSystemInfoSync().platform === 'android' || uni.getSystemInfoSync().platform === 'ios' ? 'MOBILE' : 'PC'
    const settings = await settingsApi.get({ deviceType })
    if (settings) {
      wallpaper.value = settings.wallpaper || ''
      store.commit('setTheme', settings.theme || 'default')
    }
  } catch (error) {
    console.error('加载设置失败:', error)
    // 如果加载失败，使用默认值
    wallpaper.value = ''
    store.commit('setTheme', 'default')
  }
}

// 更新时钟
const updateClock = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 应用点击
const handleAppClick = (app, event) => {
  // 如果正在拖拽，不触发点击
  if (iconDragState && iconDragState.appId === app.id) {
    return
  }
  if (iconDragStatePC && iconDragStatePC.appId === app.id) {
    return
  }
  if (app.type === 'WEB') {
    if (app.openType === 'EXTERNAL') {
      // 外部打开
      // #ifdef H5
      window.open(app.url, '_blank')
      // #endif
      // #ifndef H5
      plus.runtime.openURL(app.url)
      // #endif
    } else {
      // 内部窗口
      openWindow({
        id: app.id,
        title: app.name,
        url: app.url,
        type: 'web',
        x: 100,
        y: 100,
        width: 800,
        height: 600
      })
    }
  } else {
    // 原生应用
    openWindow({
      id: app.id,
      title: app.name,
      type: 'native',
      x: 100,
      y: 100,
      width: 800,
      height: 600
    })
  }
  
  // 标记为已读
  if (app.isNew) {
    appApi.markAsRead({ appId: app.id })
    app.isNew = 0
  }
}

// 打开窗口
const openWindow = (windowConfig) => {
  windowZIndex++
  const window = {
    ...windowConfig,
    zIndex: windowZIndex
  }
  openedWindows.value.push(window)
}

// 关闭窗口
const closeWindow = (windowId) => {
  const index = openedWindows.value.findIndex(w => w.id === windowId)
  if (index > -1) {
    openedWindows.value.splice(index, 1)
  }
  // 同时从最小化列表中移除
  const minIndex = minimizedWindows.value.findIndex(w => w.id === windowId)
  if (minIndex > -1) {
    minimizedWindows.value.splice(minIndex, 1)
  }
}

// 将窗口置于最前
const bringToFront = (windowId) => {
  const window = openedWindows.value.find(w => w.id === windowId)
  if (window) {
    windowZIndex++
    window.zIndex = windowZIndex
  }
}

// 最小化窗口
const minimizeWindow = (windowId) => {
  const index = openedWindows.value.findIndex(w => w.id === windowId)
  if (index > -1) {
    const window = openedWindows.value[index]
    minimizedWindows.value.push(window)
    openedWindows.value.splice(index, 1)
  }
}

// 恢复窗口（从最小化状态恢复）
const restoreWindow = (windowId) => {
  const index = minimizedWindows.value.findIndex(w => w.id === windowId)
  if (index > -1) {
    const window = minimizedWindows.value[index]
    windowZIndex++
    window.zIndex = windowZIndex
    window.minimized = false
    openedWindows.value.push(window)
    minimizedWindows.value.splice(index, 1)
    // 将窗口置于最前
    bringToFront(windowId)
  }
}

// 最大化窗口
const maximizeWindow = (windowId) => {
  const window = openedWindows.value.find(w => w.id === windowId)
  if (window) {
    const systemInfo = uni.getSystemInfoSync()
    window.x = 0
    window.y = 0
    window.width = systemInfo.windowWidth
    window.height = systemInfo.windowHeight - 50 // 减去任务栏高度
  }
}

// 窗口拖拽（移动端）
let dragState = null
const handleWindowDragStart = (window, event) => {
  if (event.touches && event.touches.length > 0) {
    dragState = {
      windowId: window.id,
      startX: event.touches[0].clientX,
      startY: event.touches[0].clientY,
      windowX: window.x,
      windowY: window.y
    }
  }
}

const handleWindowDrag = (event) => {
  if (!dragState) return
  const window = openedWindows.value.find(w => w.id === dragState.windowId)
  if (window && event.touches && event.touches.length > 0) {
    window.x = Math.max(0, dragState.windowX + (event.touches[0].clientX - dragState.startX))
    window.y = Math.max(0, dragState.windowY + (event.touches[0].clientY - dragState.startY))
  }
}

const handleWindowDragEnd = () => {
  dragState = null
}

// 窗口拖拽（PC端）
let windowDragStatePC = null
const handleWindowDragStartPC = (window, event) => {
  if (event.button !== 0) return
  event.preventDefault()
  event.stopPropagation()
  
  windowDragStatePC = {
    windowId: window.id,
    startX: event.clientX,
    startY: event.clientY,
    windowX: window.x,
    windowY: window.y
  }
  
  // #ifdef H5
  if (typeof document !== 'undefined') {
    document.addEventListener('mousemove', handleWindowDragPCGlobal)
    document.addEventListener('mouseup', handleWindowDragEndPCGlobal)
  }
  // #endif
}

const handleWindowDragPCGlobal = (event) => {
  if (!windowDragStatePC) return
  event.preventDefault()
  
  const window = openedWindows.value.find(w => w.id === windowDragStatePC.windowId)
  if (!window) return
  
  const deltaX = event.clientX - windowDragStatePC.startX
  const deltaY = event.clientY - windowDragStatePC.startY
  window.x = Math.max(0, windowDragStatePC.windowX + deltaX)
  window.y = Math.max(0, windowDragStatePC.windowY + deltaY)
}

const handleWindowDragEndPCGlobal = () => {
  windowDragStatePC = null
  
  // #ifdef H5
  if (typeof document !== 'undefined') {
    document.removeEventListener('mousemove', handleWindowDragPCGlobal)
    document.removeEventListener('mouseup', handleWindowDragEndPCGlobal)
  }
  // #endif
}

// 窗口调整大小
let windowResizeState = null
const handleWindowResizeStart = (window, event) => {
  if (event.button !== 0 && event.touches === undefined) return
  event.preventDefault()
  event.stopPropagation()
  
  let startX, startY
  if (event.touches && event.touches.length > 0) {
    startX = event.touches[0].clientX
    startY = event.touches[0].clientY
  } else {
    startX = event.clientX
    startY = event.clientY
  }
  
  windowResizeState = {
    windowId: window.id,
    startX: startX,
    startY: startY,
    windowWidth: window.width,
    windowHeight: window.height
  }
  
  // #ifdef H5
  if (typeof document !== 'undefined') {
    document.addEventListener('mousemove', handleWindowResizeGlobal)
    document.addEventListener('mouseup', handleWindowResizeEndGlobal)
    document.addEventListener('touchmove', handleWindowResizeGlobal)
    document.addEventListener('touchend', handleWindowResizeEndGlobal)
  }
  // #endif
}

const handleWindowResizeGlobal = (event) => {
  if (!windowResizeState) return
  event.preventDefault()
  
  const window = openedWindows.value.find(w => w.id === windowResizeState.windowId)
  if (!window) return
  
  let currentX, currentY
  if (event.touches && event.touches.length > 0) {
    currentX = event.touches[0].clientX
    currentY = event.touches[0].clientY
  } else {
    currentX = event.clientX
    currentY = event.clientY
  }
  
  const deltaX = currentX - windowResizeState.startX
  const deltaY = currentY - windowResizeState.startY
  window.width = Math.max(300, windowResizeState.windowWidth + deltaX)
  window.height = Math.max(200, windowResizeState.windowHeight + deltaY)
}

const handleWindowResizeEndGlobal = () => {
  windowResizeState = null
  
  // #ifdef H5
  if (typeof document !== 'undefined') {
    document.removeEventListener('mousemove', handleWindowResizeGlobal)
    document.removeEventListener('mouseup', handleWindowResizeEndGlobal)
    document.removeEventListener('touchmove', handleWindowResizeGlobal)
    document.removeEventListener('touchend', handleWindowResizeEndGlobal)
  }
  // #endif
}

// 显示应用右键菜单
const showAppContextMenu = (app, event) => {
  // 阻止默认右键菜单
  if (event && event.preventDefault) {
    event.preventDefault()
  }
  
  // 获取鼠标/触摸位置
  let x, y
  if (event) {
    if (event.touches && event.touches.length > 0) {
      // 移动端
      x = event.touches[0].clientX
      y = event.touches[0].clientY
    } else if (event.clientX !== undefined) {
      // PC端
      x = event.clientX
      y = event.clientY
    } else if (event.pageX !== undefined) {
      // 备用方案
      x = event.pageX
      y = event.pageY
    } else {
      // 默认位置
      x = 100
      y = 100
    }
  } else {
    x = 100
    y = 100
  }
  
  contextMenu.value = {
    show: true,
    x: x,
    y: y,
    items: [
      { key: 'open', label: '打开' },
      { key: 'remove', label: '从桌面移除' },
      { key: 'uninstall', label: '卸载' },
      { key: 'properties', label: '属性' }
    ],
    target: app
  }
  
  return false
}

const closeContextMenu = () => {
  contextMenu.value.show = false
}

// 菜单点击
const handleMenuClick = async (key) => {
  const target = contextMenu.value.target
  const targetType = contextMenu.value.targetType || 'app'
  closeContextMenu()
  
  // 文件夹菜单
  if (targetType === 'folder') {
    const folder = target
    switch (key) {
      case 'openFolder':
        openFolder(folder)
        break
      case 'renameFolder':
        renameFolder(folder)
        break
      case 'deleteFolder':
        uni.showModal({
          title: '确认删除',
          content: `确定要删除文件夹 ${folder.name} 吗？文件夹内的应用将移回桌面。`,
          success: async (res) => {
            if (res.confirm) {
              try {
                await folderApi.delete({ folderId: folder.id })
                loadFolders()
                loadDesktopApps()
                uni.showToast({
                  title: '删除成功',
                  icon: 'success'
                })
              } catch (error) {
                console.error('删除文件夹失败:', error)
              }
            }
          }
        })
        break
    }
    return
  }
  
  // 应用菜单
  const app = target
  switch (key) {
    case 'open':
      handleAppClick(app)
      break
    case 'remove':
      await appApi.removeFromDesktop({ appId: app.id })
      loadDesktopApps()
      break
    case 'uninstall':
      uni.showModal({
        title: '确认卸载',
        content: `确定要卸载 ${app.name} 吗？`,
        success: async (res) => {
          if (res.confirm) {
            await appApi.uninstall({ appId: app.id })
            loadDesktopApps()
            loadAllApps()
          }
        }
      })
      break
    case 'properties':
      showPropertiesDialog(app)
      break
    case 'addApp':
      uni.navigateTo({
        url: '/pages/add-app/add-app'
      })
      break
    case 'refresh':
      loadDesktopApps()
      loadAllApps()
      loadFolders()
      loadBoxes()
      loadWidgets()
      uni.showToast({
        title: '刷新成功',
        icon: 'success'
      })
      break
    case 'newFolder':
      createNewFolder()
      break
    case 'newBox':
      createNewBox()
      break
    case 'addWidget':
      showAddWidgetDialog()
      break
    case 'view':
      // TODO: 查看选项
      break
  }
}

// 显示应用列表
const showAppList = () => {
  showAppListModal.value = true
  loadAllApps()
}

// 添加到桌面
const addAppToDesktop = async (app) => {
  const systemInfo = uni.getSystemInfoSync()
  const x = Math.random() * (systemInfo.windowWidth - 100)
  const y = Math.random() * (systemInfo.windowHeight - 200)
  
  await appApi.addToDesktop({
    appId: app.id,
    x: Math.floor(x),
    y: Math.floor(y)
  })
  
  loadDesktopApps()
  showAppListModal.value = false
}

// 显示搜索
const showSearch = () => {
  showSearchModal.value = true
  searchKeyword.value = ''
  searchResults.value = []
}

// 搜索
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    searchResults.value = []
    return
  }
  
  try {
    const results = await appApi.search({ keyword: searchKeyword.value })
    searchResults.value = results
  } catch (error) {
    console.error('搜索失败:', error)
  }
}

const handleSearchConfirm = () => {
  if (searchResults.value.length > 0) {
    handleAppClick(searchResults.value[0])
    showSearchModal.value = false
  }
}

// 显示设置
const showSettings = () => {
  uni.navigateTo({
    url: '/pages/settings/settings'
  })
}

// 显示属性对话框
const showPropertiesDialog = (app) => {
  currentPropertiesApp.value = JSON.parse(JSON.stringify(app)) // 深拷贝
  showPropertiesModal.value = true
}

// 更换应用图标
const changeAppIcon = async () => {
  uni.chooseImage({
    count: 1,
    success: async (res) => {
      try {
        uni.showLoading({
          title: '上传中...'
        })
        const url = await fileApi.upload(res.tempFilePaths[0], 'icon')
        if (currentPropertiesApp.value) {
          currentPropertiesApp.value.icon = url
        }
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

// 属性对话框打开方式改变
const handlePropertiesOpenTypeChange = (e) => {
  if (currentPropertiesApp.value) {
    currentPropertiesApp.value.openType = e.detail.value === 0 ? 'EXTERNAL' : 'INTERNAL'
  }
}

// 保存属性
const saveProperties = async () => {
  if (!currentPropertiesApp.value) return
  
  try {
    await appApi.update({
      id: currentPropertiesApp.value.id,
      name: currentPropertiesApp.value.name,
      icon: currentPropertiesApp.value.icon,
      url: currentPropertiesApp.value.url,
      openType: currentPropertiesApp.value.openType
    })
    
    // 更新本地数据
    const desktopIndex = desktopApps.value.findIndex(a => a.id === currentPropertiesApp.value.id)
    if (desktopIndex > -1) {
      Object.assign(desktopApps.value[desktopIndex], currentPropertiesApp.value)
    }
    
    const allIndex = allApps.value.findIndex(a => a.id === currentPropertiesApp.value.id)
    if (allIndex > -1) {
      Object.assign(allApps.value[allIndex], currentPropertiesApp.value)
    }
    
    uni.showToast({
      title: '保存成功',
      icon: 'success'
    })
    
    showPropertiesModal.value = false
  } catch (error) {
    console.error('保存失败:', error)
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 桌面空白处右键菜单
const showDesktopContextMenu = (event) => {
  // 阻止默认右键菜单
  if (event.preventDefault) {
    event.preventDefault()
  }
  
  // 检查是否点击在应用图标上
  if (event.target && event.target.closest && event.target.closest('.app-icon')) {
    return false
  }
  
  // 获取鼠标/触摸位置
  let x, y
  if (event.touches && event.touches.length > 0) {
    // 移动端
    x = event.touches[0].clientX
    y = event.touches[0].clientY
  } else if (event.clientX !== undefined) {
    // PC端
    x = event.clientX
    y = event.clientY
  } else {
    // 备用方案
    x = event.pageX || 0
    y = event.pageY || 0
  }
  
  contextMenu.value = {
    show: true,
    x: x,
    y: y,
    items: [
      { key: 'addApp', label: '添加应用' },
      { key: 'refresh', label: '刷新' },
      { key: 'newFolder', label: '新建文件夹' },
      { key: 'view', label: '查看' }
    ],
    target: null
  }
  
  return false
}

// 桌面触摸开始（用于区分点击和长按）
const handleDesktopTouchStart = (event) => {
  // 用于长按检测
}

// 图标拖拽（移动端）
let iconDragState = null
const handleIconDragStart = (app, event) => {
  // 阻止点击事件
  event.stopPropagation()
  iconDragState = {
    appId: app.id,
    startX: event.touches[0].clientX,
    startY: event.touches[0].clientY,
    appX: app.positionX || 0,
    appY: app.positionY || 0
  }
  // 初始化拖拽到文件夹状态
  dragToFolderState.value = {
    appId: app.id,
    targetFolderId: null
  }
}

const handleIconDrag = (event) => {
  if (!iconDragState) return
  event.preventDefault()
  event.stopPropagation()
  const app = desktopApps.value.find(a => a.id === iconDragState.appId)
  if (app && event.touches && event.touches.length > 0) {
    const deltaX = event.touches[0].clientX - iconDragState.startX
    const deltaY = event.touches[0].clientY - iconDragState.startY
    app.positionX = Math.max(0, iconDragState.appX + deltaX)
    app.positionY = Math.max(0, iconDragState.appY + deltaY)
  }
}

const handleIconDragEnd = async (app) => {
  if (!iconDragState || iconDragState.appId !== app.id) return
  // 保存新位置
  try {
    await appApi.updatePosition({
      appId: app.id,
      x: app.positionX,
      y: app.positionY
    })
  } catch (error) {
    console.error('更新位置失败:', error)
    // 恢复原位置
    app.positionX = iconDragState.appX
    app.positionY = iconDragState.appY
  }
  iconDragState = null
}

// 图标拖拽（PC端）
let iconDragStatePC = null
const handleIconDragStartPC = (app, event) => {
  // 只有鼠标左键才能拖拽
  if (event.button !== 0) return
  event.preventDefault()
  event.stopPropagation()
  iconDragStatePC = {
    appId: app.id,
    startX: event.clientX,
    startY: event.clientY,
    appX: app.positionX || 0,
    appY: app.positionY || 0
  }
  // 初始化拖拽到文件夹状态
  dragToFolderState.value = {
    appId: app.id,
    targetFolderId: null
  }
  
  // 添加全局鼠标事件监听
  // #ifdef H5
  if (typeof document !== 'undefined') {
    document.addEventListener('mousemove', handleIconDragPCGlobal)
    document.addEventListener('mouseup', handleIconDragEndPCGlobal)
  }
  // #endif
}

const handleIconDragPC = (event) => {
  // 这个函数在图标元素上，但实际使用全局监听器
}

const handleIconDragPCGlobal = (event) => {
  if (!iconDragStatePC) return
  event.preventDefault()
  const app = desktopApps.value.find(a => a.id === iconDragStatePC.appId)
  if (app) {
    const deltaX = event.clientX - iconDragStatePC.startX
    const deltaY = event.clientY - iconDragStatePC.startY
    app.positionX = Math.max(0, iconDragStatePC.appX + deltaX)
    app.positionY = Math.max(0, iconDragStatePC.appY + deltaY)
  }
}

const handleIconDragEndPC = async (app) => {
  if (!iconDragStatePC || iconDragStatePC.appId !== app.id) return
  await saveIconPosition(iconDragStatePC.appId)
  iconDragStatePC = null
  
  // 移除全局事件监听
  // #ifdef H5
  if (typeof document !== 'undefined') {
    document.removeEventListener('mousemove', handleIconDragPCGlobal)
    document.removeEventListener('mouseup', handleIconDragEndPCGlobal)
  }
  // #endif
}

const handleIconDragEndPCGlobal = async () => {
  if (!iconDragStatePC) return
  
  // 检查是否拖到文件夹
  if (dragToFolderState && dragToFolderState.targetFolderId) {
    await handleDropToFolder()
    iconDragStatePC = null
    dragToFolderState.value = null
    
    // 移除全局事件监听
    // #ifdef H5
    if (typeof document !== 'undefined') {
      document.removeEventListener('mousemove', handleIconDragPCGlobal)
      document.removeEventListener('mouseup', handleIconDragEndPCGlobal)
    }
    // #endif
    return
  }
  
  await saveIconPosition(iconDragStatePC.appId)
  iconDragStatePC = null
  dragToFolderState.value = null
  
  // 移除全局事件监听
  // #ifdef H5
  if (typeof document !== 'undefined') {
    document.removeEventListener('mousemove', handleIconDragPCGlobal)
    document.removeEventListener('mouseup', handleIconDragEndPCGlobal)
  }
  // #endif
}

// 保存图标位置
const saveIconPosition = async (appId) => {
  const app = desktopApps.value.find(a => a.id === appId)
  if (!app) return
  
  try {
    await appApi.updatePosition({
      appId: app.id,
      x: app.positionX,
      y: app.positionY
    })
  } catch (error) {
    console.error('更新位置失败:', error)
    // 恢复原位置
    if (iconDragStatePC && iconDragStatePC.appId === appId) {
      app.positionX = iconDragStatePC.appX
      app.positionY = iconDragStatePC.appY
    } else if (iconDragState && iconDragState.appId === appId) {
      app.positionX = iconDragState.appX
      app.positionY = iconDragState.appY
    }
  }
}

// 重试加载
const retryLoad = () => {
  loadError.value = false
  loadDesktopApps()
  loadAllApps()
  loadSettings()
}

onMounted(() => {
  loadDesktopApps()
  loadAllApps()
  loadFolders()
  loadBoxes()
  loadWidgets()
  loadSettings()
  updateClock()
  setInterval(updateClock, 1000)
  
  // 更新性能数据（模拟）
  setInterval(() => {
    performanceData.value = {
      cpu: Math.floor(Math.random() * 50) + 20,
      memory: Math.floor(Math.random() * 40) + 40
    }
  }, 2000)
  
  // PC端：全局阻止默认右键菜单（仅在桌面容器内）
  // #ifdef H5
  if (typeof document !== 'undefined') {
    const preventContextMenu = (e) => {
      // 如果点击在桌面容器内，阻止默认行为
      const desktopContainer = document.querySelector('.desktop-container')
      if (desktopContainer && desktopContainer.contains(e.target)) {
        e.preventDefault()
      }
    }
    document.addEventListener('contextmenu', preventContextMenu)
    
    // 保存清理函数
    window._preventContextMenu = preventContextMenu
  }
  // #endif
})

// 页面显示时重新加载设置（从设置页面返回时会触发）
onShow(() => {
  loadSettings()
})

onUnmounted(() => {
  // #ifdef H5
  // 移除事件监听器
  if (typeof document !== 'undefined' && window._preventContextMenu) {
    document.removeEventListener('contextmenu', window._preventContextMenu)
    delete window._preventContextMenu
  }
  // #endif
})
</script>

<style scoped>
.desktop-container {
  width: 100vw;
  height: 100vh;
  position: relative;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  overflow: hidden;
  background-color: #1a1a2e; /* 默认背景色，当没有壁纸时显示 */
}

.desktop-icons {
  position: relative;
  width: 100%;
  height: calc(100% - 60px);
}

.app-icon {
  position: absolute;
  width: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
  cursor: pointer;
  transition: transform 0.2s;
  user-select: none;
  -webkit-user-select: none;
}

.app-icon:active:not(.dragging) {
  transform: scale(0.95);
}

.app-icon.dragging {
  cursor: grabbing;
  cursor: -webkit-grabbing;
  z-index: 1000;
  opacity: 0.8;
  transition: none;
}

.app-icon.drag-to-folder {
  opacity: 0.6;
}

/* 文件夹图标样式 */
.folder-icon-container {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 5px;
  backdrop-filter: blur(10px);
}

.folder-icon-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 4px;
  width: 40px;
  height: 40px;
}

.folder-icon-item {
  background: rgba(0, 0, 0, 0.3);
  border-radius: 4px;
}

/* 拖拽到文件夹的提示区域 */
.folder-drop-zone {
  position: absolute;
  width: 80px;
  height: 80px;
  border: 2px dashed #667eea;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(102, 126, 234, 0.2);
  z-index: 999;
  pointer-events: all;
}

.drop-hint {
  font-size: 10px;
  color: #667eea;
  font-weight: bold;
}

.icon-img {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  margin-bottom: 5px;
}

.icon-name {
  font-size: 12px;
  color: #fff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
  text-align: center;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.new-badge {
  position: absolute;
  top: 5px;
  right: 5px;
  background: #ff4757;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
}

.taskbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  z-index: 10000;
}

.taskbar-left,
.taskbar-right {
  display: flex;
  gap: 10px;
}

.taskbar-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.taskbar-icon:active {
  background: rgba(255, 255, 255, 0.2);
}

.taskbar-center {
  flex: 1;
  display: flex;
  gap: 5px;
  overflow-x: auto;
  padding: 0 10px;
}

.taskbar-window {
  padding: 5px 15px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.2s;
  white-space: nowrap;
  min-width: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.taskbar-window.minimized {
  opacity: 0.6;
}

.taskbar-window.active {
  background: rgba(255, 255, 255, 0.2);
  border-bottom: 2px solid rgba(255, 255, 255, 0.5);
}

.taskbar-window.active {
  background: rgba(255, 255, 255, 0.2);
  border-bottom: 2px solid rgba(255, 255, 255, 0.5);
}

.taskbar-window:active {
  background: rgba(255, 255, 255, 0.2);
}

.taskbar-window-title {
  font-size: 12px;
  color: white;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100px;
}

.context-menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 10000;
}

.context-menu {
  position: fixed;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  padding: 5px 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 10001;
  min-width: 150px;
}

.menu-item {
  padding: 10px 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:active {
  background: rgba(0, 0, 0, 0.1);
}

.app-window {
  position: fixed;
  background: white;
  border-radius: 8px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.window-header {
  height: 40px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
  cursor: move;
}

.window-title {
  font-size: 14px;
  color: #333;
}

.window-controls {
  display: flex;
  gap: 10px;
}

.window-btn {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  cursor: pointer;
  font-size: 18px;
  color: #666;
}

.window-btn.close {
  color: #ff4757;
}

.window-btn:active {
  background: rgba(0, 0, 0, 0.1);
}

.window-content {
  flex: 1;
  overflow: hidden;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 20000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.modal-title {
  font-size: 18px;
  font-weight: bold;
}

.modal-close {
  font-size: 24px;
  color: #999;
  cursor: pointer;
}

.app-list-content {
  padding: 20px;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 20px;
}

.app-list-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;
}

.app-list-item:active {
  background: #f5f5f5;
}

.app-list-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  margin-bottom: 8px;
}

.app-list-name {
  font-size: 12px;
  text-align: center;
}

.new-badge-small {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #ff4757;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
}

.search-modal {
  max-width: 500px;
}

.search-input {
  width: 100%;
  height: 50px;
  padding: 0 20px;
  font-size: 16px;
  border: none;
  border-bottom: 1px solid #eee;
}

.search-results {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
}

.search-result-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.search-result-item:active {
  background: #f5f5f5;
}

.search-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  margin-right: 15px;
}

.search-name {
  font-size: 16px;
}

.clock-widget {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.time {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.properties-modal {
  max-width: 500px;
}

.properties-content {
  padding: 20px;
}

.property-item {
  margin-bottom: 20px;
}

.property-label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.property-input {
  width: 100%;
  height: 40px;
  padding: 0 12px;
  background: #f5f5f5;
  border-radius: 6px;
  font-size: 14px;
}

.property-value {
  font-size: 14px;
  color: #333;
}

.property-icon {
  display: flex;
  align-items: center;
  gap: 15px;
}

.icon-preview-small {
  width: 50px;
  height: 50px;
  border-radius: 8px;
}

.btn-change-icon {
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border-radius: 6px;
  font-size: 14px;
  border: none;
}

.property-picker {
  height: 40px;
  line-height: 40px;
  padding: 0 12px;
  background: #f5f5f5;
  border-radius: 6px;
  font-size: 14px;
}

.property-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.btn-cancel,
.btn-save {
  flex: 1;
  height: 40px;
  border-radius: 6px;
  font-size: 14px;
  border: none;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-save {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}
.error-container {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  background: rgba(0, 0, 0, 0.8);
  padding: 30px 50px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.error-text {
  font-size: 16px;
  color: #fff;
  margin-bottom: 10px;
}

.error-hint {
  font-size: 14px;
  color: #999;
}

/* 应用盒子样式 */
.app-boxes {
  position: relative;
  width: 100%;
  height: calc(100% - 60px);
  pointer-events: none;
}

.app-box {
  position: absolute;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  pointer-events: all;
  transition: height 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.app-box.dragging {
  opacity: 0.8;
  z-index: 2000;
}

.box-header {
  height: 40px;
  background: rgba(102, 126, 234, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
  cursor: move;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.box-title {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  flex: 1;
}

.box-controls {
  display: flex;
  gap: 15px;
  align-items: center;
}

.box-toggle,
.box-close {
  font-size: 18px;
  color: #666;
  cursor: pointer;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background 0.2s;
}

.box-toggle:active,
.box-close:active {
  background: rgba(0, 0, 0, 0.1);
}

.box-close {
  color: #ff4757;
}

.box-content {
  padding: 15px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 15px;
  overflow-y: auto;
  max-height: calc(100% - 40px);
}

.box-app-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.box-app-item:active {
  background: rgba(0, 0, 0, 0.05);
}

.box-app-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  margin-bottom: 5px;
}

.box-app-name {
  font-size: 11px;
  color: #333;
  text-align: center;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-box {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
}

.box-resize-handle {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 20px;
  height: 20px;
  cursor: nwse-resize;
  background: linear-gradient(-45deg, transparent 40%, rgba(0, 0, 0, 0.2) 40%, rgba(0, 0, 0, 0.2) 60%, transparent 60%);
}

.box-resize-handle:active {
  background: linear-gradient(-45deg, transparent 40%, rgba(0, 0, 0, 0.4) 40%, rgba(0, 0, 0, 0.4) 60%, transparent 60%);
}
</style>
