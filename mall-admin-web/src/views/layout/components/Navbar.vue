<script lang="ts" setup>
import { computed } from 'vue'
import Breadcrumb from '@/components/Breadcrumb/index.vue'
import Hamburger from '@/components/Hamburger/index.vue'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { ArrowDown, Bell, Search, Plus } from '@element-plus/icons-vue'

// 定义组件名称
defineOptions({
  name: 'Navbar'
})

const appStore = useAppStore()
const userStore = useUserStore()

const sidebar = computed(() => appStore.sidebar)
const avatar = computed(() => userStore.userInfo.avatar)

// 处理开关侧边栏操作
const handleToggleSideBar = () => {
  appStore.toggleSideBar()
}

// 处理用户登出
const handleLogout = async () => {
  await userStore.userLogout()
  // 为了重新实例化vue-router对象 避免bug
  location.reload()
}
</script>

<template>
  <header class="navbar">
    <div class="nav-left">
      <hamburger class="hamburger-container" :toggle-click="handleToggleSideBar" :is-active="sidebar.opened" />
      <breadcrumb />
    </div>
    <div class="nav-actions">
      <el-input class="global-search" placeholder="搜索订单、商品或会员" :prefix-icon="Search" />
      <el-button class="quick-create" type="primary" :icon="Plus">新建商品</el-button>
      <el-badge is-dot class="notice"><el-button circle :icon="Bell" /></el-badge>
    <el-dropdown class="avatar-container" trigger="click">
      <div class="avatar-wrapper">
        <img class="user-avatar" :src="avatar">
        <el-icon class="el-icon-caret-bottom">
          <arrow-down />
        </el-icon>
      </div>
      <template #dropdown>
        <el-dropdown-menu class="user-dropdown">
          <router-link class="inlineBlock" to="/">
            <el-dropdown-item>
              首页
            </el-dropdown-item>
          </router-link>
          <el-dropdown-item divided>
            <span @click="handleLogout" style="display:block;">退出</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    </div>
  </header>
</template>

<style lang="scss" scoped>
.navbar {
  height: 68px; display: flex; align-items: center; justify-content: space-between; padding: 0 28px;
  background: #fff; border-bottom: 1px solid #edf0f6;
  .nav-left, .nav-actions { display: flex; align-items: center; gap: 14px; }

  .hamburger-container {
    line-height: 1; height: auto; padding: 8px;
  }

  .global-search { width: 220px; }
  .quick-create { border-radius: 9px; }
  .notice :deep(.el-button) { border: 0; color: #526078; background: #f4f6fb; }

  .avatar-container {
    height: 44px; display: inline-block;

    .avatar-wrapper {
      cursor: pointer;
      margin-top: 2px;
      position: relative;

      .user-avatar {
        width: 36px; height: 36px; border-radius: 50%; border: 2px solid #f0f2f7;
      }

      .el-icon-caret-bottom {
        position: absolute;
        right: -18px; top: 14px;
        font-size: 12px;
      }
    }
  }
}
@media (max-width: 900px) { .global-search, .quick-create { display: none; } .navbar { padding: 0 14px; } }
</style>
