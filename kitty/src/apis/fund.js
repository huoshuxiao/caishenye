import request from '@/utils/request'

export function getFundDataList() {
  return request({
    url: '/search/funddata',
    method: 'get'
  })
}
