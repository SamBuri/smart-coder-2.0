<!--<script setup>-->
<!--import {ref, computed, onMounted, watch, shallowRef} from 'vue';-->
<!--// import type { Field } from '@/field/Field.ts';-->
<!--import { createDefaultField } from '@/field/Field';-->
<!--import { defineLookupStore } from '@/lookup/lookupStore';-->
<!--import constants from '@/utils/constants';-->
<!--import fieldHeaders from '@/field/FieldNav';-->
<!--import { FieldParser } from '@/field/FieldParser';-->
<!--import {defineProjectStore} from "@/project/projectStore.js";-->
<!--import {toCaption} from "@/utils/funcs.js";-->
<!--import {defineRootStore} from "@/base/RootStore.js";-->
<!--// import type {Request} from "@/base/RootStore.js";-->


<!--const computedHeaders = computed(() => {-->
<!--  if (!model.propertyValue?.projectType) {-->
<!--    return fieldHeaders.filter(h => !h.projectType); // If no projectType is set, return all headers-->
<!--  }-->

<!--  return fieldHeaders.filter(header => {-->
<!--    // If header has no projectType restriction, include it-->
<!--    if (!header.projectType) return true;-->

<!--    // Otherwise, check if model.projectType is in the header's allowed projectTypes-->
<!--    return header.projectType.includes(model.propertyValue.projectType);-->
<!--  });-->
<!--});-->

<!--console.log(constants.projectTypes);-->


<!--const cols = 12;-->
<!--const md = 2;-->
<!--const sm = 6;-->
<!--const fields = ref([]);-->
<!--const importedText = ref(''); // will hold the raw file contents-->
<!--const model = ref({ projectType: '', projectName: '', objectName: '', objectCaption: '', entityType: '', serviceType: 'Base', model: '', file: '', fieldName: '' });-->


<!--const addField = () => {-->
<!--  fields.propertyValue.push(createDefaultField());-->
<!--};-->

<!--// Handle file input change-->
<!--const handleImport = (file) => {-->
<!--  const selectedFile = Array.isArray(file) ? file[0] : file;-->
<!--  if (!selectedFile) return;-->

<!--  const reader = new FileReader();-->
<!--  reader.onload = (e) => {-->
<!--    console.log("Imported Event", e)-->
<!--    importedText.propertyValue = e.target?.result;-->
<!--    console.log("Imported Text", importedText.propertyValue)-->
<!--  };-->
<!--  reader.readAsText(selectedFile);-->
<!--};-->

<!--// Parse and populate fields-->
<!--const importFields = () => {-->
<!--  if (!importedText.propertyValue) return;-->
<!--  fields.propertyValue = FieldParser.parse(importedText.propertyValue);-->
<!--  console.log('Parsed fields:', fields.propertyValue);-->
<!--};-->

<!--const exportFields = () => {-->
<!--  const dataStr = JSON.stringify({ fields: fields.propertyValue }, null, 2);-->
<!--  const blob = new Blob([dataStr], { type: 'application/json' });-->
<!--  const url = URL.createObjectURL(blob);-->
<!--  const a = document.createElement('a');-->
<!--  a.href = url;-->
<!--  a.download = 'fields-config.json';-->
<!--  document.body.appendChild(a);-->
<!--  a.click();-->
<!--  document.body.removeChild(a);-->
<!--  URL.revokeObjectURL(url);-->
<!--};-->

<!--const lookupStore = defineLookupStore();-->

<!--onMounted(() => {-->
<!--  lookupStore.getProjectTypes();-->
<!--  lookupStore.getSpringBootFiles();-->
<!--  lookupStore.getEntityTypes();-->
<!--  lookupStore.getServiceTypes();-->
<!--  lookupStore.getVueFiles();-->
<!--  lookupStore.getJavaFxFiles()-->
<!--});-->

<!--const files = ref([]);-->
<!--const filesLoading = ref(false);-->

<!--const isSpringBootProject = computed(() => model.propertyValue.projectType === constants.projectTypes.springBoot)-->
<!--const isVueProject = computed(() => model.propertyValue.projectType === constants.projectTypes.vue)-->
<!--const projectStore = defineProjectStore();-->
<!--watch(() => model.propertyValue.projectType, (newValue) => {-->
<!--  files.propertyValue = [];-->
<!--  filesLoading.propertyValue = false-->
<!--  if (!newValue) return-->
<!--  if (newValue === constants.projectTypes.springBoot) {-->
<!--    files.propertyValue = lookupStore.springBootFiles;-->
<!--    filesLoading.propertyValue = lookupStore.springBootFilesLoading-->
<!--  }-->
<!--  else if (newValue === constants.projectTypes.vue) {-->
<!--    files.propertyValue = lookupStore.vueFiles;-->
<!--    filesLoading.propertyValue = lookupStore.vueFilesLoading-->
<!--  }-->
<!--  else if (newValue === constants.projectTypes.javaFx) {-->
<!--    files.propertyValue = lookupStore.javaFxFiles;-->
<!--    filesLoading.propertyValue = lookupStore.javaFxFilesLoading-->
<!--  }-->
<!--  projectStore.getMiniProjectsByType(newValue)-->
<!--});-->



<!--const selectedFile = ref (null);-->
<!--// const selectedFile = shallowRef<File | null>(null)-->
<!--const fileContent = ref('');-->

<!--watch(fileContent, (newValue) => {-->
<!--  console.log("File Content", newValue)-->
<!--  fields.propertyValue = [];-->
<!--  if (!newValue) return;-->
<!--  fields.propertyValue = FieldParser.parse(newValue.trim());-->
<!--  console.log(fields);-->
<!--})-->

<!--watch(selectedFile, (newVal) => {-->
<!--  // Reset when no file-->
<!--  if (!newVal) {-->
<!--    model.propertyValue.objectName = '';-->
<!--    model.propertyValue.objectCaption = '';-->
<!--    return;-->
<!--  }-->

<!--  // Get filename without extension-->
<!--  const filenameWithoutExt = newVal.name.replace(/\.[^/.]+$/, '');-->

<!--  // Update the model-->
<!--  model.propertyValue.objectName = filenameWithoutExt;-->

<!--  // Convert to human-readable caption: "my-file_name" → "My File Name"-->
<!--  model.propertyValue.objectCaption = toCaption(filenameWithoutExt);-->
<!--});-->


<!--const rootStore = defineRootStore();-->
<!--const generate = ()=>{-->

<!--  rootStore.post({request: model.propertyValue, path: 'generate'})-->
<!--}-->



<!--</script>-->

<!--<template>-->
<!--  <v-card class="pa-4 mx-2">-->
<!--    <v-row>-->
<!--      &lt;!&ndash; <v-col cols="4">-->
<!--        <v-file-input label="Import Field Config" @change="handleImport" density="compact" hide-details />-->
<!--      </v-col> &ndash;&gt;-->

<!--      <v-col cols="4">-->
<!--        <text-file-reader v-model="selectedFile" v-model:content="fileContent" density="compact" hide-details label="Import Field" />-->
<!--      </v-col>-->


<!--      <v-col cols="1">-->
<!--        <v-btn @click="importFields" color="primary">-->
<!--          Import-->
<!--        </v-btn>-->
<!--      </v-col>-->

<!--      <v-col cols="1">-->
<!--        <v-btn @click="exportFields" color="primary">-->
<!--          Export-->
<!--        </v-btn>-->
<!--      </v-col>-->



<!--    </v-row>-->

<!--    <v-row>-->
<!--      <v-col :cols="cols" :md="md" :sm="sm">-->
<!--        <v-autocomplete label="Project Type" density="compact" :items="lookupStore.projectTypes"-->
<!--          v-model="model.projectType" :loading="lookupStore.projectTypesLoading" />-->
<!--      </v-col>-->
<!--      <v-col :cols="cols" :md="md" :sm="sm">-->
<!--        <v-autocomplete v-model="model.projectName"-->
<!--                        :items="projectStore.projectsByType"-->
<!--                        :loading="projectStore.projectsByTypeLoading"-->
<!--                        item-title="projectName"-->
<!--                        item-propertyValue="id"-->
<!--                        label="Project" density="compact" hide-details />-->
<!--      </v-col>-->
<!--      <v-col :cols="cols" :md="md" :sm="sm">-->
<!--        <v-text-field v-model="model.objectName" label="Object Name" density="compact" hide-details />-->
<!--      </v-col>-->
<!--      <v-col :cols="cols" :md="md" :sm="sm">-->
<!--        <v-text-field v-model="model.objectCaption" label="Object Caption" density="compact" hide-details />-->
<!--      </v-col>-->
<!--      <v-col :cols="cols" :md="md" :sm="sm" v-if="isSpringBootProject">-->
<!--        <v-autocomplete v-model="model.entityType" label="Entity Type" density="compact"-->
<!--          :items="lookupStore.entityTypes" :loading="lookupStore.entityTypesLoading" />-->
<!--      </v-col>-->
<!--      <v-col :cols="cols" :md="md" :sm="sm" v-if="isSpringBootProject">-->
<!--        <v-autocomplete v-model="model.serviceType" label="Service Type" density="compact"-->
<!--          :items="lookupStore.serviceTypes" :loading="lookupStore.serviceTypesLoading" />-->
<!--      </v-col>-->
<!--      <v-col :cols="cols" :md="md" :sm="sm" v-if="isVueProject">-->
<!--        <v-text-field v-model="model.model" label="Module" density="compact" hide-details />-->
<!--      </v-col>-->
<!--    </v-row>-->

<!--    <v-row no-gutters>-->
<!--      <v-col :cols="cols" :md="md" :sm="sm">-->
<!--        <v-autocomplete v-model="model.file" label="File" density="compact" :items="files" :loading="filesLoading"-->
<!--          multiple clearable />-->
<!--      </v-col>-->
<!--&lt;!&ndash;      <v-col :cols="cols" :md="md" :sm="sm">&ndash;&gt;-->
<!--&lt;!&ndash;        <v-autocomplete v-model="model.projectName" label="Project" density="compact" hide-details />&ndash;&gt;-->
<!--&lt;!&ndash;      </v-col>&ndash;&gt;-->
<!--&lt;!&ndash;      <v-col :cols="cols" :md="md" :sm="sm">&ndash;&gt;-->
<!--&lt;!&ndash;        <v-autocomplete v-model="model.fieldName" label="File Name" density="compact" hide-details />&ndash;&gt;-->
<!--&lt;!&ndash;      </v-col>&ndash;&gt;-->
<!--    </v-row>-->

<!--    <v-data-table :items="fields" :items-per-page="-1" hide-default-footer :headers="computedHeaders" dense-->
<!--      items-per-page="20" density="compact" class="mt-4 fixed-table">-->
<!--      &lt;!&ndash; <template v-slot:top>-->
<!--        <v-btn @click="addField">Add Field</v-btn>-->
<!--      </template> &ndash;&gt;-->

<!--      &lt;!&ndash; Dynamically render item slots &ndash;&gt;-->
<!--      <template v-for="header in fieldHeaders" :property="header.property" v-slot:[`item.${header.property}`]="{ item }">-->
<!--        &lt;!&ndash; Checkbox type &ndash;&gt;-->
<!--        <v-checkbox v-if="header.type === 'checkbox'" v-model="item[header.property]" density="compact" hide-details />-->

<!--        &lt;!&ndash; Select type &ndash;&gt;-->
<!--        <v-autocomplete v-else-if="header.type === 'select'" :items="header.namedValues || []"-->
<!--          v-model="item[header.property]" density="compact" hide-details clearable />-->

<!--        <v-autocomplete v-else-if="header.property === 'projectName'" :items="projectStore.projectsByType"-->
<!--                        item-title="projectName" item-propertyValue="id"-->
<!--                        v-model="item[header.property]" density="compact" hide-details clearable />-->

<!--        &lt;!&ndash; Default fallback (if needed) &ndash;&gt;-->
<!--        <v-text-field v-else v-model="item[header.property]" density="compact" hide-details />-->
<!--      </template>-->
<!--    </v-data-table>-->

<!--    <v-card-actions>-->
<!--      <s-select-folder label="Output Location"  density="compact" hide-details directory />-->
<!--      <v-spacer />-->
<!--      <v-label>{{ fields.length }} row(s)</v-label>-->
<!--      <v-btn @click="addField" color="primary">Add Field</v-btn>-->
<!--      <v-btn color="primary" @click="generate">Generate Code</v-btn>-->
<!--    </v-card-actions>-->
<!--  </v-card>-->
<!--</template>-->
<script>
import Generator from "@/generator/Generator.vue";
import {defineComponent} from "vue";

export default defineComponent({
  components: {Generator}
})

</script>
<template>
  <generator/>
</template>
