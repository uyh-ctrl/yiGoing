<script setup lang="ts">
import { nextTick, ref } from 'vue'
import { ChatDotRound, Close, Promotion } from '@element-plus/icons-vue'
import { sendCustomerMessage } from '@/apis/customerService'

type Message = { role: 'assistant' | 'user'; text: string }
const open = ref(false)
const sending = ref(false)
const question = ref('')
const messages = ref<Message[]>([{ role: 'assistant', text: '你好，我是易going智能客服。关于商品、订单、售后或活动的问题都可以问我。' }])
const messageList = ref<HTMLElement>()

const scrollToBottom = async () => { await nextTick(); messageList.value?.scrollTo({ top: messageList.value.scrollHeight, behavior: 'smooth' }) }
const ask = async () => {
  const content = question.value.trim()
  if (!content || sending.value) return
  messages.value.push({ role: 'user', text: content }); question.value = ''; sending.value = true; await scrollToBottom()
  try {
    const result = await sendCustomerMessage(content)
    messages.value.push({ role: 'assistant', text: result.data.answer })
  } catch {
    messages.value.push({ role: 'assistant', text: '智能客服暂时不可用，请稍后再试。' })
  } finally { sending.value = false; await scrollToBottom() }
}
</script>

<template>
  <div class="customer-service">
    <transition name="slide-up"><section v-if="open" class="chat-panel"><header><div><span class="online-dot" /><div><b>易going 智能客服</b><small>RAG 知识检索 · 在线</small></div></div><el-button text :icon="Close" @click="open = false" /></header>
      <div ref="messageList" class="messages"><div v-for="(message, index) in messages" :key="index" class="message" :class="message.role"><span>{{ message.role === 'assistant' ? '易' : '我' }}</span><p>{{ message.text }}</p></div><div v-if="sending" class="typing">正在检索知识库并组织回答…</div></div>
      <div class="quick-questions"><button v-for="item in ['订单如何发货？', '商品库存在哪里改？', '如何创建优惠券？']" :key="item" @click="question = item">{{ item }}</button></div>
      <footer><el-input v-model="question" maxlength="500" placeholder="输入你的问题…" @keyup.enter="ask" /><el-button type="primary" circle :icon="Promotion" :loading="sending" @click="ask" /></footer>
    </section></transition>
    <button class="float-button" @click="open = !open"><el-icon><ChatDotRound /></el-icon><span>智能客服</span></button>
  </div>
</template>

<style scoped>
.customer-service{position:fixed;right:28px;bottom:28px;z-index:1500}.float-button{display:flex;align-items:center;gap:8px;padding:13px 18px;border:0;border-radius:14px;color:#fff;background:linear-gradient(135deg,#ff7548,#ff4e32);box-shadow:0 12px 28px #e7563b55;cursor:pointer;font:600 14px inherit}.float-button .el-icon{font-size:19px}.chat-panel{position:absolute;right:0;bottom:58px;width:min(380px,calc(100vw - 32px));overflow:hidden;border:1px solid #e9edf4;border-radius:18px;background:#fff;box-shadow:0 20px 50px #1b284433}.chat-panel header{display:flex;align-items:center;justify-content:space-between;padding:16px 18px;color:#fff;background:linear-gradient(135deg,#202b47,#344c7e)}.chat-panel header>div{display:flex;align-items:center;gap:10px}.chat-panel b,.chat-panel small{display:block}.chat-panel small{margin-top:3px;color:#bac6dd;font-size:11px}.online-dot{width:9px;height:9px;border-radius:50%;background:#55d7a7;box-shadow:0 0 0 4px #55d7a72e}.messages{height:300px;overflow-y:auto;padding:16px;background:#f6f8fc}.message{display:flex;gap:8px;margin-bottom:14px}.message.user{flex-direction:row-reverse}.message span{flex:0 0 28px;height:28px;display:grid;place-items:center;border-radius:9px;color:#fff;background:#344c7e;font-size:12px;font-weight:700}.message.user span{background:#ff6338}.message p{max-width:76%;margin:0;padding:9px 11px;border-radius:4px 12px 12px;color:#344057;background:#fff;font-size:13px;line-height:1.55;white-space:pre-wrap}.message.user p{border-radius:12px 4px 12px 12px;color:#fff;background:#ff6338}.typing{padding:0 36px;color:#8792a8;font-size:12px}.quick-questions{display:flex;gap:6px;overflow:auto;padding:10px 14px;border-top:1px solid #edf0f5}.quick-questions button{flex:0 0 auto;border:1px solid #e6eaf1;border-radius:99px;color:#667187;background:#fff;padding:5px 8px;font-size:11px;cursor:pointer}.chat-panel footer{display:flex;gap:8px;padding:10px 14px 14px}.chat-panel footer :deep(.el-input__wrapper){border-radius:10px;background:#f6f8fc;box-shadow:0 0 0 1px #e6eaf1 inset}.slide-up-enter-active,.slide-up-leave-active{transition:.2s ease}.slide-up-enter-from,.slide-up-leave-to{opacity:0;transform:translateY(10px)}@media(max-width:600px){.customer-service{right:16px;bottom:16px}.float-button span{display:none}.float-button{padding:15px;border-radius:50%}}
</style>
