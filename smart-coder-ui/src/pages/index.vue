<template>
  <!-- <HelloWorld /> -->
   <!-- <CodeGenerator /> -->
    <v-app-bar :elevation="2">
  <template v-slot:prepend>
    <v-app-bar-nav-icon></v-app-bar-nav-icon>
  </template>

  <v-app-bar-title>Smart Coder</v-app-bar-title>
</v-app-bar>
<CodeGenerator />
  <!-- <v-container>
    <v-row>
      <v-col cols="12">
        <CodeGenerator />
      </v-col>
    </v-row>
  </v-container> -->
</template>

<script lang="ts" setup>
  import CodeGenerator from './CodeGenerator.vue';
</script>
