<template>
  <view class="login-container">
    <view class="login-box">
      <view class="title">Mini Home</view>
      <view class="subtitle">桌面环境系统</view>
      
      <view class="form">
        <input 
          v-model="form.username" 
          class="input" 
          placeholder="用户名" 
          placeholder-class="input-placeholder"
        />
        <input 
          v-model="form.password" 
          class="input" 
          type="password" 
          placeholder="密码" 
          placeholder-class="input-placeholder"
        />
        
        <button class="btn-login" @click="handleLogin">登录</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { userApi } from '../../utils/api.js'
import { useStore } from 'vuex'

const store = useStore()

const form = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    uni.showToast({
      title: '请输入用户名和密码',
      icon: 'none'
    })
    return
  }
  
  try {
    const res = await userApi.login({
      username: form.value.username,
      password: form.value.password
    })
    
    store.dispatch('login', {
      user: res.user,
      token: res.token
    })
    
    uni.reLaunch({
      url: '/pages/index/index'
    })
  } catch (error) {
    console.error('登录失败:', error)
  }
}

</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 90%;
  max-width: 400px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 32px;
  font-weight: bold;
  text-align: center;
  color: #333;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 14px;
  text-align: center;
  color: #666;
  margin-bottom: 40px;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input {
  width: 100%;
  height: 50px;
  padding: 0 15px;
  background: #f5f5f5;
  border-radius: 10px;
  font-size: 16px;
}

.input-placeholder {
  color: #999;
}

.btn-login {
  width: 100%;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px;
  font-size: 16px;
  font-weight: bold;
  border: none;
  margin-top: 10px;
}

</style>

