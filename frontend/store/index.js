import { createStore } from 'vuex'

export default createStore({
  state: {
    user: null,
    token: '',
    theme: 'default',
    desktopApps: [],
    allApps: []
  },
  mutations: {
    setUser(state, user) {
      state.user = user
    },
    setToken(state, token) {
      state.token = token
      uni.setStorageSync('token', token)
    },
    setTheme(state, theme) {
      state.theme = theme
    },
    setDesktopApps(state, apps) {
      state.desktopApps = apps
    },
    setAllApps(state, apps) {
      state.allApps = apps
    }
  },
  actions: {
    login({ commit }, { user, token }) {
      commit('setUser', user)
      commit('setToken', token)
    },
    logout({ commit }) {
      commit('setUser', null)
      commit('setToken', '')
      uni.removeStorageSync('token')
    }
  }
})

