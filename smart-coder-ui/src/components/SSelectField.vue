<script setup>
import { ref, watch } from "vue";
const emit = defineEmits(["update:modelValue", "ok", "clear"]);
const props = defineProps([
  "hint",
  "label",
  "modelValue",
  "rules",
  "items",
  "headers",
]);

const handleChange = (e) => {
  emit("update:modelValue", e);
};

const dialog = ref(false);
const open = () => (dialog.value = true);
const close = () => (dialog.value = false);
const dialogOk = (data) => {
  handleChange(data.id);
  emit("ok", data);
  dialog.value = false;
};

watch(
  () => props.modelValue,
  (v) => {
    let data = props.items.filter((it) => it.id === v);
    if (!data.isEmpty()) emit("ok", data[0]);
  },
  {immediate: true}
);
</script>
<template>
  <div>
    <v-text-field
      :label="label"
      :hint="hint"
      :modelValue="modelValue"
      :rules="rules"
      @update:modelValue="handleChange"
    >
      <template v-slot:append>
        <v-chip @click="open" cursor="pointer" color="primary">{{
          items.length
        }}</v-chip>
      </template>
    </v-text-field>
    <v-dialog v-model="dialog" :persistent="true" :max-with="600">
      <search-mini
        :mtdsProvided="true"
        @ok="dialogOk"
        @close="close"
        :items="items"
        :headers="headers"
      />
    </v-dialog>
  </div>
</template>
<!--
<script setup>
import { ref, watch, computed } from "vue";

const emit = defineEmits(['update:modelValue', 'ok', 'clear']);
const props = defineProps([
  "hint",
  "label",
  "modelValue",
  "rules",
  "items",
  "headers",
]);

const handleChange = (e) => {
  emit("update:modelValue", e);
};

const dialog = ref(false);
const open = () => dialog.propertyValue = true;
const close = () => dialog.propertyValue = false;

const dialogOk = (data) => {
  emit('ok', data)
  dialog.propertyValue = false;
};

// Computed property to get the number of items
const numberOfItems = computed(() => props.items ? props.items.length : 0);

watch(() => props.modelValue, (v) => {
  let data = props.items.filter(it => it.id === v);
  if (!data.isEmpty()) emit('ok', data[0])
});
</script>

<template>
  <div>
    <v-text-field
      :label="label"
      :persistent-hint="hint"
      :modelValue="modelValue"
      :rules="rules"
      @update:modelValue="handleChange"
    >

      <template v-slot:append>
        <span
          @click="open"
          style="cursor: pointer; color: blue;"
        >
          {{ numberOfItems }}
        </span>
      </template>
    </v-text-field>

    <v-dialog v-model="dialog" :persistent="true" :max-width="600">
      <search-mini
        :mtdsProvided="true"
        @ok="dialogOk"
        @close="close"
        :items="items"
        :headers="headers"
      />
    </v-dialog>
  </div>
</template> -->
