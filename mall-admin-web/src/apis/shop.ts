import http from '@/utils/http'

export type ShopProduct = { id: number; name: string; pic?: string; albumPics?: string; subTitle?: string; description?: string; price: number; promotionPrice?: number | null; originalPrice?: number | null; stock?: number; newStatus?: number; recommandStatus?: number }

export function getShopProducts() {
  // The currently packaged backend exposes this public product endpoint.
  // Switch to /shop/products/hot after rebuilding the backend with Redis support.
  return http<ShopProduct[]>({ method: 'get', url: '/product/simpleList' })
}

export function recordProductView(id: number) {
  return http<void>({ method: 'post', url: `/shop/products/${id}/view` })
}

export function getProductRanking() {
  return http<ShopProduct[]>({ method: 'get', url: '/shop/products/ranking' })
}
