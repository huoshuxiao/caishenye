import Vue from 'vue'
import Vuex from 'vuex'
import { autoLoadModule } from './util'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: autoLoadModule()
})
