<template>
  <v-data-table
    :headers="headers"
    :items="dataList"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>
</template>
<script>
import { getFundDataList } from '@/apis/fund'
export default {
  data () {
    return {
      headers: [
        // {
        //   text: 'Dessert (100g serving)',
        //   align: 'start',
        //   sortable: false,
        //   value: 'name'
        // },
        // { text: 'Calories' },
        // { text: 'Fat (g)' },
        // { text: 'Carbs (g)', value: 'carbs' },
        // { text: 'Protein (g)', value: 'protein' },
        // { text: 'Iron (%)', value: 'iron' }
        { text: '基金代码', value: 'fund_code', width: '100' },
        { text: '基金名称', value: 'fund_name', width: '200' },
        { text: '净值日期', value: 'net_value_date', width: '100' },
        { text: '单位净值', value: 'unit_net', width: '100' },
        { text: '风险', value: 'risk', width: '80' },
        { text: '成立日期', value: 'set_up_date' },
        { text: '年平均回报', value: 'avg_year_profit', width: '110' },
        { text: '1天回报', value: 'day_profit', width: '110' },
        { text: '1周回报', value: 'week_profit', width: '110' },
        { text: '1个月回报', value: 'month_profit', width: '110' },
        { text: '3个月回报', value: 'quarter_profit', width: '110' },
        { text: '6个月回报', value: 'half_a_year', width: '110' },
        { text: '1年回报', value: 'year_profit', width: '100' },
        { text: '2年回报', value: 'two_year_profit', width: '100' },
        { text: '3年回报', value: 'three_year_profit', width: '100' },
        { text: '5年回报', value: 'five_year_profit', width: '100' },
        { text: '10年回报', value: 'ten_year_profit', width: '100' },
        { text: '设立以来', value: 'total_profit', width: '100' }
      ],
      dataList: []
    }
  },
  async mounted () {
    await this.getTabData()
  },
  methods: {
    async getTabData () {
      console.log(this.$store.getters)
      const res = await getFundDataList()
      debugger
      this.dataList = res.data
    }
  }

}
</script>
<style lang="scss" scoped>
.home-container {

}
</style>
