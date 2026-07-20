<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { memberRegisterAPI } from '@/apis/admin'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
const router = useRouter(); const formRef = ref<FormInstance>(); const loading = ref(false)
const form = reactive({ username: '', password: '', phone: '' })
const rules: FormRules = { username:[{required:true,message:'请输入用户名',trigger:'blur'}], password:[{required:true,min:6,message:'密码至少 6 位',trigger:'blur'}], phone:[{required:true,pattern:/^1\d{10}$/,message:'请输入有效手机号',trigger:'blur'}] }
const submit = () => formRef.value?.validate(async valid => { if (!valid) return; loading.value=true; try { await memberRegisterAPI(form); ElMessage.success('注册成功，请登录'); router.push('/login') } finally { loading.value=false } })
</script>
<template><div class="register"><section><b><i>易</i> 易going</b><h1>创建你的会员账号</h1><p>注册后即可浏览商品、管理订单并使用智能客服。</p><el-form ref="formRef" :model="form" :rules="rules"><el-form-item prop="username"><el-input v-model="form.username" placeholder="用户名" /></el-form-item><el-form-item prop="phone"><el-input v-model="form.phone" placeholder="手机号" /></el-form-item><el-form-item prop="password"><el-input v-model="form.password" type="password" show-password placeholder="设置密码（至少 6 位）" /></el-form-item><el-button type="primary" :loading="loading" @click="submit">注册并登录</el-button><router-link to="/login">已有账号？去登录</router-link></el-form></section></div></template>
<style scoped>.register{min-height:100vh;display:grid;place-items:center;background:linear-gradient(135deg,#f7f9fe,#fff0ea)}section{width:min(420px,calc(100% - 40px));padding:38px;border-radius:20px;background:#fff;box-shadow:0 20px 50px #34456b18}b{font-size:22px}b i{display:inline-grid;place-items:center;width:30px;height:30px;color:#fff;border-radius:9px;background:#ff6338;font-style:normal}h1{margin:28px 0 8px}p{margin:0 0 25px;color:#7d8799}.el-button{width:100%;margin-bottom:18px}.el-form>a{display:block;text-align:center;color:#f15e38;font-size:13px}</style>
