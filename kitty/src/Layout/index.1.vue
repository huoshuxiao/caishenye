<template>
  <v-app id="inspire">
    <v-navigation-drawer
      v-model="drawer"
      color="blue"
      absolute
      dark
      app
      src="https://cdn.vuetifyjs.com/images/backgrounds/bg-2.jpg"
    >
      <Sider />
    </v-navigation-drawer>
    <v-content>
       <!-- Provides the application the proper gutter -->
      <v-container fluid>
        <!-- If using vue-router -->
        <router-view/>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
import Sider from './components/Sider'
export default {
  name: 'Layout',
  components: {
    Sider
  },
  data: () => ({
    drawer: true
  })
}
</script>
