import { defineStore } from 'pinia'
import { getAdminInfoAPI, unifiedLoginAPI, adminLogoutAPI } from '@/apis/admin'
import { ref } from 'vue'
import type { LoginParam, UserInfo } from '@/types/admin'

export const useUserStore = defineStore(
  'user',
  () => {
    // 用户信息
    const userInfo = ref<UserInfo>({
      username: '',
      password: '',
      avatar: '',
      roles: [],
      token: '',
      menus: [],
      accountType: '' as '' | 'ADMIN' | 'MEMBER',
    })

    // 用户登录
    const userLogin = async (loginParam: LoginParam) => {
      const res = await unifiedLoginAPI(loginParam)
      const tokenStr = res.data.tokenHead + res.data.token
      userInfo.value.token = tokenStr
      userInfo.value.username = loginParam.username
      userInfo.value.password = loginParam.password
      userInfo.value.accountType = res.data.accountType
      userInfo.value.avatar = res.data.avatar || ''
      // Keep the account name for route and permission checks. The API displayName
      // may be a nickname (for example "系统管理员") rather than "admin".
      if (res.data.accountType === 'ADMIN') await getUserInfo()
    }

    // 获取用户信息
    const getUserInfo = async () => {
      const res = await getAdminInfoAPI()
      if (res.data.roles && res.data.roles.length > 0) {
        // 验证返回的roles是否是一个非空数组
        userInfo.value.roles = res.data.roles
      } else {
        throw new Error('该用户暂未分配角色，请先分配角色！')
      }
      userInfo.value.menus = res.data.menus
      userInfo.value.avatar = res.data.icon
    }

    // 用户登出
    const userLogout = async () => {
      await adminLogoutAPI()
      userInfo.value.token = ''
      userInfo.value.roles = []
      userInfo.value.accountType = ''
    }

    // 前端登出
    const fedLogout = () => {
      userInfo.value.token = ''
      userInfo.value.accountType = ''
    }

    return {
      userInfo,
      userLogin,
      getUserInfo,
      userLogout,
      fedLogout,
    }
  },
  {
    // 持久化配置
    persist: true,
  },
)
