<script setup>
	// #ifdef VUE3
	import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
	
	onLaunch(() => {
		console.log('App Launch')
		// 检查登录状态
		const token = uni.getStorageSync('token')
		if (!token) {
			uni.reLaunch({
				url: '/pages/login/login'
			})
		}
	})
	
	onShow(() => {
		console.log('App Show')
	})
	
	onHide(() => {
		console.log('App Hide')
	})
	// #endif
	
	// #ifndef VUE3
	export default {
		onLaunch: function() {
			console.log('App Launch')
			const token = uni.getStorageSync('token')
			if (!token) {
				uni.reLaunch({
					url: '/pages/login/login'
				})
			}
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
	// #endif
</script>

<style>
	/*每个页面公共css */
	page {
		background-color: #f5f5f5;
	}
</style>
