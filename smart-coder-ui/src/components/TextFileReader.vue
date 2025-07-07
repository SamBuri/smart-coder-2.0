<template>
  <v-card>
    

    <v-card-text>
      <v-file-input
        :model-value="modelValue ? [modelValue] : []"
        @update:modelValue="handleFileChange"
        :accept="accept"
        :label="label"
        prepend-icon="mdi-paperclip"
        :clearable="clearable"
        :outlined="outlined"
        :loading="isLoading"
        :rules="computedRules"
        :multiple="false"
        density="compact"
      ></v-file-input>

      <v-alert v-if="error" type="error" class="my-4">
        {{ error }}
      </v-alert>


      <slot name="content" :content="content" v-if="showTextField">
        <v-textarea
          :model-value="content"
          @update:modelValue="$emit('update:content', $event)"
          :label="contentLabel"
          rows="8"
          auto-grow
          outlined
          :readonly="contentReadonly"
          class="mt-4"
          hide-details
        ></v-textarea>
      </slot>
    </v-card-text>

    <v-card-actions v-if="showTextField">
      <slot name="actions" :copy="copyToClipboard" :clear="clearAll">
        <v-btn
          color="primary"
          :disabled="!content"
          @click="copyToClipboard"
          :loading="isCopying"
        >
          <v-icon left>mdi-content-copy</v-icon>
          Copy to Clipboard
        </v-btn>
        <v-spacer />
        <v-btn
          color="error"
          :disabled="!content"
          @click="clearAll"
          variant="outlined"
        >
          <v-icon left>mdi-delete</v-icon>
          Clear All
        </v-btn>
      </slot>
    </v-card-actions>
  </v-card>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useClipboard } from '@vueuse/core';
import type { PropType } from 'vue';

const props = defineProps({
  modelValue: {
    type: Object as PropType<File | null>,
    default: null
  },
  content: {
    type: String,
    default: ''
  },
  accept: {
    type: String,
    default: '.txt,.csv,.json,text/plain'
  },
  label: {
    type: String,
    default: 'Select a text file'
  },
  contentLabel: {
    type: String,
    default: 'File Contents'
  },
  contentReadonly: {
    type: Boolean,
    default: true
  },
  clearable: {
    type: Boolean,
    default: true
  },
  outlined: {
    type: Boolean,
    default: true
  },
  maxSizeMB: {
    type: Number,
    default: 5
  },
  rules: {
    type: Array as () => Array<(v: File) => boolean | string>,
    default: () => []
  },
  showTextField: {
    type: Boolean,
    default: false,
  }
});

const emit = defineEmits([
  'update:modelValue',
  'update:content',
  'file-changed',
  'error'
]);

const { copy, isSupported } = useClipboard();
const isLoading = ref(false);
const error = ref<string | null>(null);
const isCopying = ref(false);

const computedRules = computed(() => [
  ...props.rules,
  (file: File) => {
    if (!file) return true;
    const validTypes = ['text/plain', 'application/json', 'text/csv'];
    return validTypes.includes(file.type) || !file.type || file.name.match(/\.(txt|csv|json)$/i) 
      ? true 
      : 'Please select a valid text file';
  },
  (file: File) => {
    if (!file) return true;
    return file.size < 1024 * 1024 * props.maxSizeMB
      ? true 
      : `File size should be less than ${props.maxSizeMB}MB`;
  }
]);

const handleFileChange = (files: File | File[]) => {
  const file = Array.isArray(files) ? files[0] || null : files || null;
  emit('update:modelValue', file);
  error.value = null;
  
  if (!file) {
    emit('update:content', '');
    return;
  }


  isLoading.value = true;
  const reader = new FileReader();

  reader.onload = (e) => {
    try {
      const result = e.target?.result;
      if (typeof result === 'string') {
        emit('update:content', result);
        emit('file-changed', { file, content: result });
      }
    } catch (err) {
      error.value = 'Failed to parse file content';
      emit('error', { file, error: error.value });
    } finally {
      isLoading.value = false;
    }
  };

  reader.onerror = () => {
    error.value = 'Error reading file';
    emit('error', { file, error: error.value });
    isLoading.value = false;
  };

  reader.readAsText(file);
};

const copyToClipboard = async () => {
  if (!isSupported.value) {
    error.value = 'Clipboard not supported in your browser';
    return;
  }

  try {
    isCopying.value = true;
    await copy(props.content);
  } catch (err) {
    error.value = 'Failed to copy to clipboard';
  } finally {
    isCopying.value = false;
  }
};

const clearAll = () => {
  emit('update:modelValue', null);
  emit('update:content', '');
  error.value = null;
};
</script>