<template>
  <v-list
    dense
    nav
    class="menu-container"
  >
    <v-list-item two-line :class="'px-0'">
      <v-list-item-avatar>
        <img src="../../assets/img/avatar_1.jpg">
      </v-list-item-avatar>
      <v-list-item-content>
        <v-list-item-title>Howe's demo project</v-list-item-title>
        <v-list-item-subtitle>This is demo version</v-list-item-subtitle>
      </v-list-item-content>
    </v-list-item>
    <v-divider></v-divider>
    <template v-for="menu in menus">
      <template v-if="menu.children">
        <v-list-group :key="menu.title" active-class="active-menu">
          <template v-slot:activator>
            <v-list-item-icon>
              <v-icon dark>{{ menu.icon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-title>{{ menu.title }}</v-list-item-title>
          </template>
          <v-list-item v-for="child in menu.children" :key="child.title" link :to="child.to">
            <v-list-item-icon>
              <v-icon dark>{{ child.icon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>{{ child.title }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-group>
      </template>
      <template v-else>
        <v-list-item :key="menu.title" link :to="menu.to">
            <v-list-item-icon>
              <v-icon dark>{{ menu.icon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>{{ menu.title }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
      </template>
    </template>
    <v-footer absolute padless color="rgba(0,0,0,0)" class="sider-footer">
      <v-col
        class="text-center"
        cols="12"
      >
        V 0.0.1 -- Howe
      </v-col>
    </v-footer>
  </v-list>
</template>
<script>
export default {
  name: 'Menu',
  components: {
  },
  data: () => ({
    menus: [
      {
        title: 'Home',
        icon: 'mdi-home',
        to: 'home'
      }
    ]
  })
}
</script>
<style lang="scss" scoped>
.menu-container {
  .sider-footer {
    font-size: 12px;
  }
}
</style>
