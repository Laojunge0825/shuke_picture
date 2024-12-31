// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** health GET /api/main/health */
export async function healthUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseString_>('/api/main/health', {
    method: 'GET',
    ...(options || {}),
  })
}

/** health PUT /api/main/health */
export async function healthUsingPut(options?: { [key: string]: any }) {
  return request<API.BaseResponseString_>('/api/main/health', {
    method: 'PUT',
    ...(options || {}),
  })
}

/** health POST /api/main/health */
export async function healthUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseString_>('/api/main/health', {
    method: 'POST',
    ...(options || {}),
  })
}

/** health DELETE /api/main/health */
export async function healthUsingDelete(options?: { [key: string]: any }) {
  return request<API.BaseResponseString_>('/api/main/health', {
    method: 'DELETE',
    ...(options || {}),
  })
}

/** health PATCH /api/main/health */
export async function healthUsingPatch(options?: { [key: string]: any }) {
  return request<API.BaseResponseString_>('/api/main/health', {
    method: 'PATCH',
    ...(options || {}),
  })
}
