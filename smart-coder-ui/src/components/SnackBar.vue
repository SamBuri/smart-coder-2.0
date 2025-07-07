<template>
  <div class="text-center">
    <v-snackbar v-model="snackbar" :color="color" rounded="pill" location="top" :timeout="timeout">
      {{ message }}

      <template v-slot:actions>
        <v-btn variant="text" @click="snackbar = false">
          <v-icon>mdi-close-circle</v-icon>
        </v-btn>
      </template>

    </v-snackbar>

  </div>
</template>

<script setup>
import { ref, computed, watch, watchEffect, defineEmits} from 'vue'
import { defineRootStore } from '@/root/RootStore'
// defineProps("buttonColor", "timeout")
const emit = defineEmits(["reset"])
const snackbar = ref(false);
const timeout = ref(5000);

const rootStore = defineRootStore();

const results = computed(() => rootStore.results);

const success = ref(false);
const message = ref("");

const color = computed(() => success.value ? "blue" : "red")


watchEffect(() => {

  if (results.value) {

    success.value = results.value.success;
    message.value = results.value.message;
    snackbar.value = results.value.show;
  }
});


watchEffect(() => {

  if (!snackbar.value) {
    emit("reset");
    rootStore.setResults(null);

  }
});



</script>
