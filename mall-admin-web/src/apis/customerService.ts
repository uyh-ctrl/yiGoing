import http from '@/utils/http'

export type CustomerChatResponse = { answer: string; sources?: string[] }

export function sendCustomerMessage(question: string) {
  return http<CustomerChatResponse>({ method: 'post', url: '/ai/customer-service/chat', data: { question } })
}
