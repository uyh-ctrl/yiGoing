<script lang="ts" setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import SidebarItem from './SidebarItem.vue'
import ScrollBar from '@/components/ScrollBar/index.vue'
import { useAppStore } from '@/stores/app'
import usePermissionStore from '@/stores/permission'

// 定义组件名称
defineOptions({
  name: 'Sidebar'
})

// 使用 Pinia stores
const appStore = useAppStore()
const permissionStore = usePermissionStore()

const route = useRoute()
// 所有路由
const routes = computed(() => permissionStore.routers)
// 侧边栏打开状态
const isCollapse = computed(() => !appStore.sidebar.opened)
</script>

<template>
  <scroll-bar>
    <div class="brand" :class="{ compact: isCollapse }">
      <span class="brand-mark">易</span>
      <span class="brand-name">易going</span>
    </div>
    <div class="workspace" v-if="!isCollapse">商家运营中心</div>
    <el-menu class="going-menu" mode="vertical" :show-timeout="200" :default-active="route.path" :collapse="isCollapse">
      <sidebar-item :routes="routes" />
    </el-menu>
  </scroll-bar>
</template>

<style lang="scss" scoped>
.brand { height: 76px; display: flex; align-items: center; gap: 11px; padding: 0 22px; color: #fff; white-space: nowrap; }
.brand.compact { padding: 0 8px; justify-content: center; }
.brand-mark { width: 34px; height: 34px; display: grid; place-items: center; border-radius: 11px; background: linear-gradient(145deg, #ff8a3d, #ff4f28); font-weight: 800; font-size: 18px; box-shadow: 0 8px 18px #ff5d303d; }
.brand-name { font-size: 20px; font-weight: 750; letter-spacing: -.7px; }
.workspace { margin: 2px 16px 12px; padding: 9px 12px; border-radius: 9px; color: #95a1bf; background: #252c43; font-size: 12px; }
.going-menu { border: 0; background: transparent; }
:deep(.el-menu-item), :deep(.el-sub-menu__title) { height: 48px; margin: 3px 12px; border-radius: 9px; color: #b7c0d5; }
:deep(.el-menu-item:hover), :deep(.el-sub-menu__title:hover) { color: #fff; background: #252d43; }
:deep(.el-menu-item.is-active) { color: #fff; background: linear-gradient(90deg, #ff6c38, #f05031); box-shadow: 0 7px 15px #e6573530; }
:deep(.el-menu-item .svg-icon), :deep(.el-sub-menu__title .svg-icon) { margin-right: 12px; }
</style>
