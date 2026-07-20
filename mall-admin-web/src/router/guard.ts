import router from '@/router/index'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useUserStore } from '@/stores/user'
import usePermissionStore from '@/stores/permission'

// 无需登陆的白名单路径
const whiteList = ['/login', '/register']
// 配置路由前置守卫函数（每次路由跳转都会执行）
router.beforeEach((to, from, next) => {
  NProgress.start()
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()
  if (userStore.userInfo.token) {
    // 兼容版本升级前保存的旧登录缓存：它没有账号类型，继续跳转会生成空路由并循环。
    if (!userStore.userInfo.accountType) {
      userStore.fedLogout()
      next('/login')
      NProgress.done()
      return
    }
    if (userStore.userInfo.accountType === 'MEMBER') {
      if (to.path !== '/shop') next('/shop')
      else next()
      NProgress.done()
      return
    }
    if (to.path === '/login') {
      // 登录状态下访问login直接跳转到首页
      next({ path: '/' })
      NProgress.done()
    } else {
      if (permissionStore.addRouters.length === 0) {
        // 登录状态下无动态路由时根据menus生成动态路由
        try {
          permissionStore.generateRoutes({
            menus: userStore.userInfo.menus || [],
            username: userStore.userInfo.username,
          })
        } catch (error) {
          userStore.fedLogout()
          NProgress.done()
          next('/login')
          return
        }
        // 没有可访问的后台路由时不再 replace 当前地址，避免无限重定向。
        if (permissionStore.addRouters.length === 0) {
          userStore.fedLogout()
          NProgress.done()
          next('/login')
          return
        }
        permissionStore.addRouters.forEach(route => {
          router.addRoute(route)
        })
        next({ ...to, replace: true })
      } else {
        next()
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      // 未登录状态下白名单路径放行
      next()
    } else {
      // 未登录状态下非白名单路径跳转到登录页
      next('/login')
      NProgress.done()
    }
  }
})

// 配置路由后置函数守卫函数
router.afterEach(() => {
  NProgress.done()
})
