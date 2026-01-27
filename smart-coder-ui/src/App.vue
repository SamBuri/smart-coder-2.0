<template>
  <v-app>
    <v-navigation-drawer
      v-model="drawer"
      :location="$vuetify.display.mobile ? 'bottom' : undefined"
      temporary
    >

      <!--      image="https://cdn.vuetifyjs.com/images/backgrounds/bg-2.jpg"-->
      <side-bar-nav/>
    </v-navigation-drawer>
    <v-app-bar :elevation="2">

      <template v-slot:prepend>
        <v-app-bar-nav-icon @click="drawer=!drawer"></v-app-bar-nav-icon>
      </template>

      <v-app-bar-title>Smart Coder


      </v-app-bar-title>
      <v-btn to="/" v-if="$route.path!=='/'"><v-icon>mdi-home</v-icon> Home</v-btn>
    </v-app-bar>


    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import SideBarNav from "@/nav/SideBarNav.vue";


const drawer = ref(false)
</script>
