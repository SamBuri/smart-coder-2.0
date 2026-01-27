<!-- src/components/SConfirmDialog.vue -->
<template>
  <v-dialog
    v-model="internalModel"
    max-width="400"
    persistent
  >
    <v-card>
      <v-card-title class="text-h6 text-center py-6">
        {{ message }}
      </v-card-title>

      <v-card-actions class="justify-center pb-6 gap-4">
        <v-btn
          variant="text"
          color="grey-darken-2"
          @click="handleCancel"
        >
          Cancel
        </v-btn>

        <v-btn
          variant="tonal"
          color="error"
          @click="handleOk"
        >
          OK
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    modelValue: boolean
    message?: string
  }>(),
  {
    message: 'Are you sure?'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'ok': []
  'cancel': []
}>()

// Two-way v-model binding
const internalModel = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const handleOk = () => {
  emit('ok')
  emit('update:modelValue', false)  // close dialog
}

const handleCancel = () => {
  emit('cancel')
  emit('update:modelValue', false)  // close dialog
}
</script>

<style scoped>
.gap-4 > :not(:last-child) {
  margin-right: 16px;
}
</style>
