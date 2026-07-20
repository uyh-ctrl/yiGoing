<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import CustomerService from '@/components/CustomerService/index.vue'
import { getShopProducts, recordProductView, type ShopProduct } from '@/apis/shop'

type Product = ShopProduct & { tag: string }
const router = useRouter()
const userStore = useUserStore()
const query = ref('')
const products = ref<Product[]>([])
const cart = ref<Product[]>([])
const selected = ref<Product | null>(null)
const cartOpen = ref(false)
const accountOpen = ref(false)
const loading = ref(true)
const message = ref('')
const error = ref('')
const favorites = ref<number[]>([])
// Enable after the backend containing /shop/products/{id}/view has been rebuilt.
const engagementEnabled = import.meta.env.VITE_ENGAGEMENT_ENABLED === 'true'
const visibleProducts = computed(() =>
  products.value.filter(item => item.name.includes(query.value.trim())),
)
const hotProducts = computed(() => {
  const featured = products.value.filter(item => item.newStatus === 1 || item.recommandStatus === 1)
  return (featured.length ? featured : products.value).slice(0, 8)
})
const total = computed(() =>
  cart.value.reduce((sum, item) => sum + Number(item.promotionPrice || item.price), 0),
)
const favoriteCount = computed(() => favorites.value.length)
const detailImages = computed(() => {
  if (!selected.value) return []
  return [selected.value.pic, ...(selected.value.albumPics || '').split(',')].filter(
    Boolean,
  ) as string[]
})

function notify(text: string) {
  message.value = text
  window.setTimeout(() => {
    message.value = ''
  }, 2600)
}
function addToCart(product: Product) {
  cart.value.push(product)
  cartOpen.value = true
  notify('已加入购物车')
}
function openDetail(product: Product) {
  selected.value = product
  if (engagementEnabled) void recordProductView(product.id).catch(() => undefined)
}
function remove(index: number) {
  cart.value.splice(index, 1)
}
function toggleFavorite(product: Product) {
  const index = favorites.value.indexOf(product.id)
  if (index === -1) favorites.value.push(product.id)
  else favorites.value.splice(index, 1)
  localStorage.setItem('easygoing:favorites', JSON.stringify(favorites.value))
}
function logout() {
  userStore.fedLogout()
  router.replace('/login')
}
onMounted(async () => {
  try { favorites.value = JSON.parse(localStorage.getItem('easygoing:favorites') || '[]') } catch { favorites.value = [] }
  try {
    const response = await getShopProducts()
    products.value = response.data.map(item => ({
      ...item,
      tag:
        item.newStatus === 1
          ? '新品推荐'
          : item.recommandStatus === 1
            ? '精选好物'
            : '易going 优选',
    }))
  } catch {
    error.value = '商品加载失败，请刷新后重试。'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="shop">
    <div class="ambient ambient-one" />
    <div class="ambient ambient-two" />
    <header class="topbar">
      <button class="brand" @click="query = ''"><i>易</i><b>易going</b></button
      ><label class="search"
        ><span>⌕</span
        ><input v-model="query" placeholder="搜索商品、品牌或品类" aria-label="搜索商品"
      /></label>
      <div class="actions">
        <button class="cart-trigger" @click="cartOpen = !cartOpen">
          购物车 <em v-if="cart.length">{{ cart.length }}</em></button
        ><button class="account" @click="accountOpen = !accountOpen">
          <span>{{ userStore.userInfo.username.slice(0, 1) || '会' }}</span
          >{{ userStore.userInfo.username }}
        </button>
      </div>
    </header>
    <div v-if="accountOpen" class="account-menu">
      <b>会员中心</b><small>管理你的订单与账户</small><button @click="logout">退出登录</button>
    </div>
    <aside v-if="cartOpen" class="cart">
      <header><b>购物车</b><button @click="cartOpen = false">关闭</button></header>
      <p v-if="!cart.length" class="empty">购物车为空，去挑选喜欢的商品吧。</p>
      <ul v-else>
        <li v-for="(item, index) in cart" :key="`${item.id}-${index}`">
          <span
            >{{ item.name }}<small>¥ {{ item.promotionPrice || item.price }}</small></span
          ><button @click="remove(index)">移除</button>
        </li>
      </ul>
      <footer v-if="cart.length">
        <b>合计 ¥ {{ total.toFixed(2) }}</b
        ><button class="checkout" @click="notify('结算功能将在订单接口接入后开放')">去结算</button>
      </footer>
    </aside>
    <main>
      <section class="hero">
        <div>
          <p>GOOD THINGS, EASY GOING</p>
          <h1>把好生活，<br />轻松带回家。</h1>
          <span>严选商品 · 会员专享 · 安心售后</span>
        </div>
        <div class="hero-orb"><span>易</span></div>
      </section>
      <section class="benefits">
        <span>正品保障</span><span>满 99 元包邮</span><span>7 天无忧退换</span
        ><span>智能客服在线</span>
      </section>
      <section v-if="hotProducts.length" class="hot-strip">
        <div class="hot-label"><small>HOT PICKS</small><b>热点商品</b></div>
        <div class="hot-window">
          <div class="hot-track">
            <button
              v-for="item in [...hotProducts, ...hotProducts]"
              :key="`${item.id}-${item.name}`"
              class="hot-item"
              @click="openDetail(item)"
            >
              <img v-if="item.pic" :src="item.pic" :alt="item.name" loading="lazy" /><span>{{
                item.name
              }}</span
              ><strong>¥ {{ item.promotionPrice || item.price }}</strong>
            </button>
          </div>
        </div>
      </section>
      <div class="section-title">
        <div>
          <p>DISCOVER</p>
          <h2>为你精选</h2>
        </div>
        <span>{{ visibleProducts.length }} 件商品</span>
      </div>
      <p v-if="loading || error" class="state">{{ loading ? '正在加载商品…' : error }}</p>
      <section v-else-if="visibleProducts.length" class="products">
        <article
          v-for="(item, index) in visibleProducts"
          :key="item.id"
          :style="{ animationDelay: `${index * 55}ms` }"
        >
          <button class="favorite" :class="{ active: favorites.includes(item.id) }" :aria-label="favorites.includes(item.id) ? '取消收藏' : '收藏商品'" @click="toggleFavorite(item)">♥</button>
          <button class="product-main" @click="openDetail(item)">
            <div class="image">
              <img v-if="item.pic" :src="item.pic" :alt="item.name" loading="lazy" /><span v-else
                >易going</span
              >
            </div>
            <small>{{ item.tag }}</small
            ><b>{{ item.name }}</b
            ><strong>¥ {{ item.promotionPrice || item.price }}</strong></button
          ><button class="add" @click="addToCart(item)">加入购物车</button>
        </article>
      </section>
      <p v-else class="state">没有找到相关商品，换个关键词试试。</p>
    </main>
    <transition name="fade"
      ><div v-if="selected" class="mask" @click.self="selected = null">
        <section class="detail">
          <button class="close" @click="selected = null">×</button>
          <div class="detail-images">
            <img :src="detailImages[0]" :alt="selected.name" />
            <div v-if="detailImages.length > 1" class="thumbs">
              <img
                v-for="(image, index) in detailImages.slice(0, 3)"
                :key="image"
                :src="image"
                :alt="`${selected.name} ${index + 1}`"
              />
            </div>
          </div>
          <div class="detail-info">
            <small>{{ selected.tag }}</small>
            <h2>{{ selected.name }}</h2>
            <p>
              {{
                selected.subTitle ||
                selected.description ||
                '精选好物，更多商品信息请咨询易going智能客服。'
              }}
            </p>
            <div class="price">
              <strong>¥ {{ selected.promotionPrice || selected.price }}</strong
              ><del v-if="selected.originalPrice && selected.originalPrice !== selected.price"
                >¥ {{ selected.originalPrice }}</del
              >
            </div>
            <div class="stock">库存充足 · 正品保障 · 7 天无忧退换</div>
            <button class="detail-add" @click="addToCart(selected)">加入购物车</button>
          </div>
        </section>
      </div></transition
    >
    <transition name="toast"
      ><p v-if="message" class="toast">{{ message }}</p></transition
    ><CustomerService />
  </div>
</template>

<style scoped>
.shop {
  position: relative;
  min-height: 100vh;
  overflow-x: hidden;
  background: #f7f8fc;
  color: #20283a;
}
.ambient {
  position: absolute;
  z-index: 0;
  border-radius: 999px;
  filter: blur(2px);
  opacity: 0.5;
  pointer-events: none;
}
.ambient-one {
  top: 160px;
  right: -120px;
  width: 320px;
  height: 320px;
  background: #ffd0be;
  animation: drift 12s ease-in-out infinite;
}
.ambient-two {
  top: 640px;
  left: -140px;
  width: 260px;
  height: 260px;
  background: #d8dfff;
  animation: drift 15s ease-in-out -4s infinite;
}
.topbar,
main,
.account-menu,
.cart {
  position: relative;
  z-index: 2;
}
.topbar {
  height: 76px;
  display: flex;
  align-items: center;
  gap: 28px;
  padding: 0 5vw;
  background: #ffffffeb;
  backdrop-filter: blur(14px);
  box-shadow: 0 1px #e9edf4;
}
.shop button {
  border: 0;
  cursor: pointer;
  font: inherit;
}
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #20283a;
  background: transparent;
  font-size: 23px;
}
.brand i {
  display: grid;
  place-items: center;
  width: 33px;
  height: 33px;
  border-radius: 10px;
  color: #fff;
  background: linear-gradient(145deg, #ff8655, #ff4d2d);
  font-style: normal;
  font-weight: 800;
  box-shadow: 0 7px 15px #ff633844;
}
.search {
  display: flex;
  align-items: center;
  gap: 8px;
  width: min(520px, 48vw);
  padding: 0 14px;
  border: 1px solid #e1e6ef;
  border-radius: 12px;
  background: #fafbfe;
  transition: 0.2s;
}
.search:focus-within {
  border-color: #ff6338;
  box-shadow: 0 0 0 4px #ff633818;
  background: #fff;
}
.search span {
  font-size: 21px;
  color: #8c97aa;
}
.search input {
  width: 100%;
  padding: 12px 0;
  border: 0;
  outline: 0;
  background: transparent;
  font: inherit;
}
.actions {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-left: auto;
}
.cart-trigger {
  color: #344057;
  background: transparent;
}
.cart-trigger em {
  display: inline-grid;
  place-items: center;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  color: #fff;
  background: #ff6338;
  font-size: 11px;
  font-style: normal;
}
.account {
  display: flex;
  align-items: center;
  gap: 7px;
  color: #344057;
  background: transparent;
}
.account span {
  display: grid;
  place-items: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  color: #fff;
  background: #334a7c;
  font-size: 12px;
}
.account-menu,
.cart {
  position: fixed;
  top: 86px;
  right: 5vw;
  z-index: 20;
  width: min(360px, calc(100vw - 32px));
  overflow: hidden;
  border: 1px solid #e7ebf3;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 20px 50px #23314a26;
}
.account-menu {
  padding: 16px;
}
.account-menu b,
.account-menu small {
  display: block;
}
.account-menu small {
  margin: 5px 0 15px;
  color: #8490a5;
}
.account-menu button {
  width: 100%;
  padding: 10px;
  border-radius: 9px;
  color: #bd3c2b;
  background: #fff1ee;
}
.cart {
  top: 86px;
}
.cart header,
.cart footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid #edf0f5;
}
.cart footer {
  border: 0;
  border-top: 1px solid #edf0f5;
}
.cart ul {
  max-height: 280px;
  margin: 0;
  padding: 0;
  overflow: auto;
  list-style: none;
}
.cart li {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 13px 16px;
  border-bottom: 1px solid #f1f3f7;
  font-size: 13px;
}
.cart li small {
  display: block;
  margin-top: 5px;
  color: #ff6338;
}
.cart li button {
  color: #8b95a7;
  background: transparent;
}
.empty {
  padding: 27px 16px;
  color: #7c8799;
}
.checkout,
.add,
.detail-add {
  border-radius: 9px;
  color: #fff;
  background: #ff6338;
  box-shadow: 0 8px 18px #ff63382b;
}
.checkout {
  padding: 8px 12px;
}
main {
  max-width: 1180px;
  margin: auto;
  padding: 32px 28px 70px;
}
.hero {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 280px;
  overflow: hidden;
  padding: 50px 60px;
  border-radius: 26px;
  color: #fff;
  background: linear-gradient(115deg, #202a4d, #354a7b 55%, #d27b4f);
  box-shadow: 0 22px 40px #263b6c2e;
}
.hero:after {
  position: absolute;
  right: 14%;
  bottom: -130px;
  width: 350px;
  height: 350px;
  border: 1px solid #ffffff33;
  border-radius: 50%;
  content: '';
}
.hero p,
.section-title p {
  margin: 0;
  color: #ffcfb7;
  font-size: 12px;
  letter-spacing: 1.8px;
}
.hero h1 {
  margin: 14px 0;
  font-size: clamp(36px, 4vw, 56px);
  line-height: 1.1;
  letter-spacing: -2px;
}
.hero > div:first-child {
  position: relative;
  z-index: 1;
}
.hero > div:first-child span {
  color: #d9e1f3;
}
.hero-orb {
  position: relative;
  z-index: 1;
  display: grid;
  place-items: center;
  width: 128px;
  height: 128px;
  margin-right: 8%;
  border: 1px solid #ffffff4d;
  border-radius: 50%;
  background: #ffffff12;
  animation: float 4s ease-in-out infinite;
}
.hero-orb span {
  display: grid;
  place-items: center;
  width: 78px;
  height: 78px;
  border-radius: 26px;
  background: linear-gradient(145deg, #ff9365, #ff5939);
  font-size: 42px;
  font-weight: 800;
  transform: rotate(-8deg);
}
.benefits {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  margin: 18px 0 38px;
  border: 1px solid #e8ecf3;
  border-radius: 14px;
  background: #ffffffbf;
}
.benefits span {
  padding: 15px;
  text-align: center;
  color: #5f6b80;
  font-size: 13px;
}
.benefits span + span {
  border-left: 1px solid #edf0f5;
}
.hot-strip {
  display: flex;
  align-items: center;
  gap: 18px;
  overflow: hidden;
  margin: 0 0 38px;
  padding: 13px 16px;
  border-radius: 15px;
  background: #202b47;
  color: #fff;
}
.hot-label {
  flex: 0 0 76px;
}
.hot-label small,
.hot-label b {
  display: block;
}
.hot-label small {
  color: #ffbd9e;
  font-size: 9px;
  letter-spacing: 1px;
}
.hot-label b {
  margin-top: 3px;
}
.hot-window {
  min-width: 0;
  overflow: hidden;
}
.hot-track {
  display: flex;
  width: max-content;
  animation: hot-scroll 28s linear infinite;
}
.hot-window:hover .hot-track {
  animation-play-state: paused;
}
.hot-item {
  display: flex;
  align-items: center;
  gap: 9px;
  width: 205px;
  margin-right: 17px;
  padding: 0;
  color: #fff;
  background: transparent;
  text-align: left;
}
.hot-item img {
  width: 35px;
  height: 35px;
  flex: 0 0 35px;
  object-fit: contain;
  border-radius: 8px;
  background: #fff;
}
.hot-item span {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: 12px;
}
.hot-item strong {
  margin-left: auto;
  color: #ffbd9e;
  font-size: 12px;
}
.section-title {
  display: flex;
  align-items: end;
  justify-content: space-between;
  margin-bottom: 16px;
}
.section-title h2 {
  margin: 6px 0 0;
  font-size: 27px;
}
.section-title > span {
  color: #8c97aa;
  font-size: 13px;
}
.products {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 17px;
}
.products article {
  position: relative;
  padding: 13px;
  border: 1px solid transparent;
  border-radius: 17px;
  background: #fff;
  box-shadow: 0 6px 18px #22355d0b;
  animation: rise 0.45s both;
  transition: 0.22s;
}
.products article:hover {
  border-color: #ffcfbf;
  box-shadow: 0 16px 30px #22355d18;
  transform: translateY(-5px);
}
.favorite{position:absolute;z-index:2;right:22px;margin-top:9px;width:32px;height:32px;border-radius:50%;color:#8e99aa;background:#ffffffdd;font-size:18px;line-height:1;box-shadow:0 4px 12px #22355d1a;transition:.2s}.favorite:hover,.favorite.active{color:#f34f39;transform:scale(1.08)}
.product-main {
  width: 100%;
  text-align: left;
  background: transparent;
}
.image {
  height: 205px;
  display: grid;
  place-items: center;
  overflow: hidden;
  border-radius: 12px;
  background: linear-gradient(145deg, #f1f4fa, #e7ecf6);
}
.image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: #fff;
  transition: transform 0.35s;
}
.products article:hover .image img {
  transform: scale(1.055);
}
.products small,
.products b,
.products strong {
  display: block;
}
.products small {
  margin-top: 12px;
  color: #f26338;
  font-size: 12px;
}
.products b {
  display: -webkit-box;
  min-height: 45px;
  margin: 6px 0;
  overflow: hidden;
  line-height: 1.45;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}
.products strong {
  font-size: 20px;
}
.add {
  width: 100%;
  margin-top: 12px;
  padding: 10px;
  opacity: 0.92;
  transition: 0.2s;
}
.add:hover {
  opacity: 1;
  transform: translateY(-1px);
}
.state {
  padding: 56px;
  text-align: center;
  color: #8590a3;
  border-radius: 16px;
  background: #fff;
}
.mask {
  position: fixed;
  z-index: 1300;
  inset: 0;
  display: grid;
  place-items: center;
  padding: 24px;
  background: #18233a8c;
  backdrop-filter: blur(5px);
}
.detail {
  position: relative;
  display: grid;
  grid-template-columns: minmax(260px, 0.9fr) 1.1fr;
  gap: 30px;
  width: min(850px, 100%);
  max-height: 90vh;
  overflow: auto;
  padding: 30px;
  border-radius: 22px;
  background: #fff;
  box-shadow: 0 30px 80px #0d152866;
}
.close {
  position: absolute;
  top: 14px;
  right: 16px;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  color: #58647b;
  background: #f1f3f8;
  font-size: 24px;
}
.detail-images > img {
  width: 100%;
  height: 340px;
  object-fit: contain;
  border-radius: 14px;
  background: #f4f6fa;
}
.thumbs {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}
.thumbs img {
  width: 58px;
  height: 58px;
  object-fit: contain;
  border: 1px solid #e5e9f0;
  border-radius: 8px;
}
.detail-info {
  padding: 10px 8px;
}
.detail-info > small {
  color: #f26338;
}
.detail-info h2 {
  margin: 10px 0;
  font-size: 25px;
  line-height: 1.3;
}
.detail-info > p {
  color: #718097;
  line-height: 1.7;
}
.price {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin: 22px 0 12px;
}
.price strong {
  color: #ed4f2d;
  font-size: 31px;
}
.price del {
  color: #a3acba;
}
.stock {
  padding: 13px;
  border-radius: 9px;
  color: #5e6b81;
  background: #f6f8fb;
  font-size: 13px;
}
.detail-add {
  width: 100%;
  margin-top: 22px;
  padding: 13px;
  font-weight: 700;
}
.toast {
  position: fixed;
  z-index: 1500;
  top: 92px;
  left: 50%;
  margin: 0;
  padding: 11px 17px;
  border-radius: 10px;
  color: #fff;
  background: #202b47;
  box-shadow: 0 8px 24px #202b4733;
  transform: translateX(-50%);
}
.fade-enter-active,
.fade-leave-active,
.toast-enter-active,
.toast-leave-active {
  transition: 0.2s;
}
.fade-enter-from,
.fade-leave-to,
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
}
@keyframes float {
  50% {
    transform: translateY(-10px);
  }
}
@keyframes drift {
  50% {
    transform: translate(20px, -24px) scale(1.08);
  }
}
@keyframes rise {
  from {
    opacity: 0;
    transform: translateY(14px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
@keyframes hot-scroll {
  to {
    transform: translateX(-50%);
  }
}
@media (max-width: 760px) {
  .topbar {
    gap: 12px;
    padding: 0 16px;
  }
  .search {
    width: 100%;
  }
  .account {
    font-size: 0;
  }
  .account span {
    font-size: 12px;
  }
  .hero {
    min-height: 230px;
    padding: 34px;
  }
  .hero-orb {
    display: none;
  }
  .benefits {
    grid-template-columns: repeat(2, 1fr);
  }
  .benefits span:nth-child(3) {
    border-left: 0;
    border-top: 1px solid #edf0f5;
  }
  .benefits span:nth-child(4) {
    border-top: 1px solid #edf0f5;
  }
  .products {
    grid-template-columns: repeat(2, 1fr);
    gap: 11px;
  }
  .products article {
    padding: 10px;
  }
  .image {
    height: 150px;
  }
  main {
    padding: 20px 16px 60px;
  }
  .detail {
    grid-template-columns: 1fr;
    gap: 10px;
    padding: 24px 18px;
  }
  .detail-images > img {
    height: 230px;
  }
  .detail-info {
    padding: 0;
  }
  .hero h1 {
    font-size: 36px;
  }
}
</style>
