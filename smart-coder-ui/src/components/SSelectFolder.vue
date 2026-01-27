
<script setup lang="ts">
import { ref, watch } from 'vue'

// const props = withDefaults(
//   defineProps<{
//     modelValue: string
//     label?: string
//     hint?: string
//     placeholder?: string
//     disabled?: boolean
//     readonly?: boolean
//     clearable?: boolean
//     rules?: ((v: string) => boolean | string)[]
//   }>(),
//   {
//     readonly: true,
//     clearable: true,
//     placeholder: 'Click folder icon to select...',
//   }
// )

const props = withDefaults(defineProps<{
  modelValue?: string          // make it optional
  label?: string
  hint?: string
  placeholder?: string
  disabled?: boolean
  readonly?: boolean
  clearable?: boolean
  rules?: ((v: string | undefined) => boolean | string)[]
}>(), {
  readonly: true,
  clearable: true,
  placeholder: 'Click folder icon to select...',
  modelValue: ''               // provide default empty string
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const inputValue = ref(props.modelValue || '')

watch(() => props.modelValue, (val) => (inputValue.value = val || ''))

const selectFolder = async () => {
  try {
    const { open } = await import('@tauri-apps/plugin-dialog')
    const selected = await open({
      directory: true,
      multiple: false,
      title: props.label || 'Select Folder',
    })

    if (selected) {
      inputValue.value = selected as string
      emit('update:modelValue', selected as string)
    }
  } catch (err) {
    console.error('Folder selection failed:', err)
  }
}
</script>

<template>
  <v-text-field
    :model-value="inputValue"
    :label="label"
    :hint="hint"
    :placeholder="placeholder"
    :disabled="disabled"
    :readonly="readonly"
    :clearable="clearable"
    :rules="rules"
    prepend-inner-icon="mdi-folder-open"
    @click:prepend-inner="selectFolder"
    @update:model-value="emit('update:modelValue', $event as string)"
    persistent-hint
    variant="outlined"
  >
    <template #append-inner>
      <v-btn
        icon
        size="small"
        color="primary"
        variant="text"
        @click.stop="selectFolder"
        :disabled="disabled"
      >
        <v-icon>mdi-folder-search-outline</v-icon>
      </v-btn>
    </template>
  </v-text-field>
</template>
