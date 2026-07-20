<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const error = ref('')
const form = reactive({ username: userStore.userInfo.username || '', password: userStore.userInfo.password || '' })

async function handleLogin() {
  error.value = ''
  if (!form.username.trim() || !form.password) { error.value = '请输入用户名和密码'; return }
  loading.value = true
  try {
    await userStore.userLogin({ username: form.username.trim(), password: form.password })
    await router.push(userStore.userInfo.accountType === 'MEMBER' ? '/shop' : '/')
  } catch (e: any) {
    error.value = e?.message || '登录失败，请检查账号和密码'
  } finally { loading.value = false }
}
</script>

<template>
  <div class="login-page">
    <section class="intro"><div class="logo"><i>易</i><b>易going</b></div><div><span>SMART COMMERCE</span><h1>让每一次经营，<br>都有更好的发生。</h1><p>易going 为管理者提供运营工作台，也为会员提供轻快、安心的购物体验。</p></div></section>
    <main><form @submit.prevent="handleLogin"><span class="kicker">WELCOME BACK</span><h2>登录易going</h2><p>使用你的账号继续。</p><label>用户名<input v-model="form.username" autocomplete="username" placeholder="用户名"></label><label>密码<input v-model="form.password" type="password" autocomplete="current-password" placeholder="密码"></label><p v-if="error" class="error">{{ error }}</p><div class="extra"><span>会员用户登录后将进入商城</span><router-link to="/register">注册会员账号</router-link></div><button :disabled="loading" type="submit">{{ loading ? '正在登录…' : '进入易going' }}</button></form></main>
  </div>
</template>

<style scoped>
.login-page{min-height:100vh;display:grid;grid-template-columns:1.2fr .8fr;background:#fff;color:#1e2838}.intro{display:flex;flex-direction:column;justify-content:space-between;padding:54px clamp(36px,8vw,128px);background:linear-gradient(135deg,#17213b,#2e4779);color:#fff}.logo{display:flex;align-items:center;gap:10px;font-size:22px}.logo i{display:grid;place-items:center;width:36px;height:36px;border-radius:12px;background:linear-gradient(145deg,#ff8a50,#ff4f2d);font-style:normal;font-weight:800}.intro>div:last-child{max-width:560px;margin-bottom:15vh}.intro span,.kicker{font-size:12px;font-weight:700;letter-spacing:1.4px;color:#ffb28c}.intro h1{margin:18px 0;font-size:clamp(36px,4vw,64px);line-height:1.18;letter-spacing:-2px}.intro p{max-width:440px;color:#c1cce1;font-size:16px;line-height:1.8}main{display:grid;place-items:center;padding:30px}form{width:min(390px,100%)}h2{margin:10px 0 8px;font-size:30px;letter-spacing:-1px}form>p{margin:0 0 32px;color:#8993a5}label{display:grid;gap:8px;margin:0 0 16px;font-size:13px;color:#58647a}input{width:100%;padding:13px 14px;border:1px solid #e3e7ee;border-radius:10px;outline:0;font:inherit;color:#1e2838}input:focus{border-color:#ff6636;box-shadow:0 0 0 3px #ff66361f}.extra{display:flex;justify-content:space-between;gap:12px;margin:8px 0 24px;font-size:12px;color:#8993a5}.extra a{color:#f05b34}button{width:100%;height:46px;border:0;border-radius:10px;background:#ff5d2a;color:#fff;font-weight:700;cursor:pointer}button:disabled{opacity:.7;cursor:wait}.error{margin:-5px 0 14px;color:#d9472b!important;font-size:13px}@media(max-width:800px){.login-page{grid-template-columns:1fr}.intro{min-height:250px;padding:32px}.intro>div:last-child{margin:35px 0 0}.intro h1{font-size:34px}.intro p{display:none}main{padding:48px 28px}}
</style>
