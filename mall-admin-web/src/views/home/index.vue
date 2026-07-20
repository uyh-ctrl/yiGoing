<script setup lang="ts">
import { ArrowRight, Bell, Box, Goods, Promotion, TrendCharts, UserFilled } from '@element-plus/icons-vue'

const metrics = [
  { label: '今日成交额', value: '¥ 28,560', change: '+18.6%', icon: TrendCharts, tone: 'orange' },
  { label: '待处理订单', value: '86', change: '12 单待发货', icon: Goods, tone: 'blue' },
  { label: '商品在售', value: '1,284', change: '本周新增 36', icon: Box, tone: 'purple' },
  { label: '新增会员', value: '248', change: '+12.4%', icon: UserFilled, tone: 'green' },
]

const tasks = [
  ['待付款订单', '24', '请在 30 分钟内关注支付转化'],
  ['退款申请', '8', '有 3 笔即将超时'],
  ['库存预警', '16', '建议优先补货'],
  ['营销待发布', '3', '今晚 20:00 档期'],
]

const orders = [
  ['EG20260716001', '林小姐', '¥ 299.00', '待发货'],
  ['EG20260716002', '周先生', '¥ 1,069.00', '待付款'],
  ['EG20260716003', '陈小姐', '¥ 458.00', '售后中'],
  ['EG20260716004', '赵先生', '¥ 169.00', '已完成'],
]
</script>

<template>
  <main class="going-dashboard">
    <section class="welcome-panel">
      <div>
        <span class="eyebrow">易going · 智能经营</span>
        <h1>下午好，今天也要成交满满。</h1>
        <p>7 月 16 日 · 店铺经营保持向上趋势，重点关注待发货与库存预警。</p>
      </div>
      <div class="campaign"><span>大促筹备进度</span><strong>72%</strong><div class="progress"><i /></div><small>距夏日好物节还有 4 天</small></div>
    </section>

    <section class="metric-grid">
      <article v-for="metric in metrics" :key="metric.label" class="metric-card">
        <div class="metric-icon" :class="metric.tone"><el-icon><component :is="metric.icon" /></el-icon></div>
        <div><span>{{ metric.label }}</span><strong>{{ metric.value }}</strong><small>{{ metric.change }}</small></div>
      </article>
    </section>

    <section class="dashboard-grid">
      <article class="panel sales-panel">
        <header><div><span class="eyebrow">销售概览</span><h2>近 7 日经营走势</h2></div><el-button text>经营报表 <el-icon><ArrowRight /></el-icon></el-button></header>
        <div class="chart-labels"><span>成交额</span><b>¥ 126,840</b><em>较上周 +16.2%</em></div>
        <div class="bar-chart">
          <div v-for="height in [38, 52, 44, 68, 58, 82, 94]" :key="height"><i :style="{ height: `${height}%` }" /><span>周{{ ['一','二','三','四','五','六','日'][[38,52,44,68,58,82,94].indexOf(height)] }}</span></div>
        </div>
      </article>

      <article class="panel task-panel">
        <header><div><span class="eyebrow">待办中心</span><h2>需要优先处理</h2></div><el-icon class="bell"><Bell /></el-icon></header>
        <div v-for="task in tasks" :key="task[0]" class="task"><div><b>{{ task[0] }}</b><small>{{ task[2] }}</small></div><strong>{{ task[1] }}</strong></div>
      </article>
    </section>

    <section class="dashboard-grid bottom-grid">
      <article class="panel order-panel"><header><div><span class="eyebrow">订单动态</span><h2>最新订单</h2></div><el-button text>全部订单 <el-icon><ArrowRight /></el-icon></el-button></header>
        <div class="orders"><div v-for="order in orders" :key="order[0]" class="order-row"><div><b>{{ order[0] }}</b><small>{{ order[1] }}</small></div><strong>{{ order[2] }}</strong><span :class="order[3]">{{ order[3] }}</span></div></div>
      </article>
      <article class="panel promotion-panel"><span class="eyebrow">运营建议</span><h2>让流量更有效地转化</h2><p>「夏日轻盈季」已有 16 件商品库存偏低，现在补货可避免流量损失。</p><el-button type="primary" :icon="Promotion">查看运营建议</el-button></article>
    </section>
  </main>
</template>

<style scoped lang="scss">
.going-dashboard { max-width: 1440px; margin: 0 auto; }
.welcome-panel { display: flex; justify-content: space-between; gap: 32px; padding: 32px 36px; color: #fff; border-radius: 18px; background: radial-gradient(circle at 82% 20%, #ffad70 0, transparent 23%), linear-gradient(110deg, #18243e, #334875); }
.eyebrow { display: block; color: #8a96ae; font-size: 12px; font-weight: 700; letter-spacing: .7px; }
.welcome-panel .eyebrow { color: #ffd6ba; } h1 { margin: 10px 0; font-size: 28px; } h2 { margin: 6px 0 0; font-size: 18px; } p { color: #bdc8da; margin: 0; }
.campaign { min-width: 230px; padding: 14px 0; } .campaign span, .campaign small { display: block; color: #d4daea; font-size: 13px; } .campaign strong { display: block; margin: 7px 0; font-size: 28px; } .progress { height: 7px; overflow: hidden; border-radius: 8px; background: #ffffff33; } .progress i { display:block; width:72%; height:100%; border-radius:inherit; background:#ff8a50; }
.metric-grid { display:grid; grid-template-columns:repeat(4,1fr); gap:16px; margin:20px 0; }.metric-card,.panel { background:#fff; border:1px solid #edf0f5; box-shadow:0 7px 24px #283e6810; border-radius:14px; }.metric-card { display:flex; align-items:center; gap:14px; padding:20px; }.metric-card span,.metric-card small { display:block; color:#7d879c; font-size:13px; }.metric-card strong { display:block; margin:5px 0; color:#252d3e; font-size:23px; }.metric-card small { color:#25a277; }.metric-icon { width:44px; height:44px; display:grid; place-items:center; border-radius:13px; font-size:21px; }.orange{color:#f05a32;background:#fff0e9}.blue{color:#3977e8;background:#eef4ff}.purple{color:#8557d8;background:#f4efff}.green{color:#20a27a;background:#eaf9f4}
.dashboard-grid { display:grid; grid-template-columns:1.65fr 1fr; gap:20px; }.panel { padding:24px; } header { display:flex; align-items:center; justify-content:space-between; } header :deep(.el-button) { color:#53637e; }.chart-labels { display:flex; align-items:baseline; gap:12px; margin:26px 0 12px; color:#8892a5; font-size:13px; }.chart-labels b { color:#20293b; font-size:25px; }.chart-labels em { color:#1ea477; font-style:normal; }.bar-chart { height:205px; display:flex; align-items:end; justify-content:space-between; gap:14px; padding:12px 8px 0; border-bottom:1px solid #edf0f5; background:repeating-linear-gradient(to bottom,transparent 0,transparent 50px,#eef1f6 51px); }.bar-chart div { height:100%; flex:1; display:flex; flex-direction:column; justify-content:end; align-items:center; gap:8px; }.bar-chart i { width:min(34px,75%); border-radius:9px 9px 3px 3px; background:linear-gradient(#ff875a,#ff5733); }.bar-chart span { color:#8f99aa; font-size:12px; }
.bell { padding:9px; border-radius:10px; color:#ff663d; background:#fff0e9; }.task { display:flex; justify-content:space-between; align-items:center; padding:15px 0; border-bottom:1px solid #f0f2f6; }.task:last-child { border:0; }.task b,.task small { display:block; }.task b { font-size:14px; }.task small { margin-top:4px; color:#9099aa; font-size:12px; }.task strong { color:#f05d38; font-size:20px; }
.bottom-grid { margin-top:20px; grid-template-columns:1.65fr 1fr; }.orders { margin-top:12px; }.order-row { display:grid; grid-template-columns:1.5fr 1fr .7fr; align-items:center; padding:12px 0; border-bottom:1px solid #f0f2f6; }.order-row b,.order-row small { display:block; }.order-row b { font-size:13px; }.order-row small { margin-top:3px; color:#8e98aa; font-size:12px; }.order-row strong { font-size:14px; }.order-row span { justify-self:end; padding:5px 9px; border-radius:8px; font-size:12px; }.待发货{color:#dd6d21;background:#fff4e8}.待付款{color:#4a7bd7;background:#eff5ff}.售后中{color:#9a6bce;background:#f6f0ff}.已完成{color:#25946f;background:#eaf8f2}.promotion-panel { background:linear-gradient(145deg,#fff4ec,#fff); }.promotion-panel h2 { margin:10px 0; font-size:22px; }.promotion-panel p { margin:14px 0 22px; color:#667188; line-height:1.7; }
@media(max-width:1000px){.metric-grid{grid-template-columns:repeat(2,1fr)}.dashboard-grid,.bottom-grid{grid-template-columns:1fr}.welcome-panel{display:block}.campaign{margin-top:18px}}@media(max-width:600px){.metric-grid{grid-template-columns:1fr}.app-container{padding:14px}.welcome-panel{padding:24px}.order-row{grid-template-columns:1.2fr .8fr .7fr}}
</style>
