<script setup>
import { ref, computed, onMounted, watch } from 'vue';
// import type { Field } from '@/field/Field.ts';
import { createDefaultField } from '@/field/Field';
import { defineLookupStore } from '@/lookup/lookupStore';
import constants from '@/utils/constants';
import fieldHeaders from '@/field/FieldNav';
// import  { Field } from '@/field/Field';
import { FieldParser } from '@/field/FieldParser';



const computedHeaders = computed(() => {
  if (!model.value?.projectType) {
    return fieldHeaders.filter(h => !h.projectType); // If no projectType is set, return all headers
  }

  return fieldHeaders.filter(header => {
    // If header has no projectType restriction, include it
    if (!header.projectType) return true;

    // Otherwise, check if model.projectType is in the header's allowed projectTypes
    return header.projectType.includes(model.value.projectType);
  });
});

console.log(constants.projectTypes);


const cols = 12;
const md = 2;
const sm = 6;
const fields = ref([]);
const importedText = ref(''); // will hold the raw file contents
const model = ref({ projectType: '', projectName: '', objectName: '', objectCaption: '', entityType: '', serviceType: 'Base', model: '', file: '', fieldName: '' });


const addField = () => {
  fields.value.push(createDefaultField());
};

// Handle file input change
const handleImport = (file) => {
  const selectedFile = Array.isArray(file) ? file[0] : file;
  if (!selectedFile) return;

  const reader = new FileReader();
  reader.onload = (e) => {
    console.log("Imported Event", e)
    importedText.value = e.target?.result;
    console.log("Imported Text", importedText.value)
  };
  reader.readAsText(selectedFile);
};

// Parse and populate fields
const importFields = () => {
  if (!importedText.value) return;
  fields.value = FieldParser.parse(importedText.value);
  console.log('Parsed fields:', fields.value);
};

const exportFields = () => {
  const dataStr = JSON.stringify({ fields: fields.value }, null, 2);
  const blob = new Blob([dataStr], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'fields-config.json';
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
};

const lookupStore = defineLookupStore();

onMounted(() => {
  lookupStore.getProjectTypes();
  lookupStore.getSpringBootFiles();
  lookupStore.getEntityTypes();
  lookupStore.getServiceTypes();
  lookupStore.getVueFiles();
  lookupStore.getJavaFxFiles()
});

const files = ref([]);
const filesLoading = ref(false);

const isSpringBootProject = computed(() => model.value.projectType === constants.projectTypes.springBoot)
const isVueProject = computed(() => model.value.projectType === constants.projectTypes.vue)

watch(() => model.value.projectType, (newValue) => {
  files.value = [];
  filesLoading.value = false
  if (!newValue) return
  if (newValue === constants.projectTypes.springBoot) {
    files.value = lookupStore.springBootFiles;
    filesLoading.value = lookupStore.springBootFilesLoading
  }
  else if (newValue === constants.projectTypes.vue) {
    files.value = lookupStore.vueFiles;
    filesLoading.value = lookupStore.vueFilesLoading
  }
  else if (newValue === constants.projectTypes.javaFx) {
    files.value = lookupStore.javaFxFiles;
    filesLoading.value = lookupStore.javaFxFilesLoading
  }
});



const selectedFile = ref < File | null > (null);
const fileContent = ref('');

watch(fileContent, (newValue) => {
  console.log("File Content", newValue)
  fields.value = [];
  if (!newValue) return;
  fields.value = FieldParser.parse(newValue.trim());
  console.log(fields);
})









</script>

<template>
  <v-card class="pa-4 mx-2">
    <v-row>
      <!-- <v-col cols="4">
        <v-file-input label="Import Field Config" @change="handleImport" density="compact" hide-details />
      </v-col> -->

      <v-col cols="4">
        <text-file-reader v-model="selectedFile" v-model:content="fileContent" density="compact" hide-details label="Import Field" />
      </v-col>


      <v-col cols="1">
        <v-btn @click="importFields" color="primary">
          Import
        </v-btn>
      </v-col>

      <v-col cols="1">
        <v-btn @click="exportFields" color="primary">
          Export
        </v-btn>
      </v-col>

    </v-row>

    <v-row>
      <v-col :cols="cols" :md="md" :sm="sm">
        <v-autocomplete label="Project Type" density="compact" :items="lookupStore.projectTypes"
          v-model="model.projectType" :loading="lookupStore.projectTypesLoading" />
      </v-col>
      <v-col :cols="cols" :md="md" :sm="sm">
        <v-autocomplete v-model="model.projectName" label="Project" density="compact" hide-details />
      </v-col>
      <v-col :cols="cols" :md="md" :sm="sm">
        <v-text-field v-model="model.objectName" label="Object Name" density="compact" hide-details />
      </v-col>
      <v-col :cols="cols" :md="md" :sm="sm">
        <v-text-field v-model="model.objectCaption" label="Object Caption" density="compact" hide-details />
      </v-col>
      <v-col :cols="cols" :md="md" :sm="sm" v-if="isSpringBootProject">
        <v-autocomplete v-model="model.entityType" label="Entity Type" density="compact"
          :items="lookupStore.entityTypes" :loading="lookupStore.entityTypesLoading" />
      </v-col>
      <v-col :cols="cols" :md="md" :sm="sm" v-if="isSpringBootProject">
        <v-autocomplete v-model="model.serviceType" label="Service Type" density="compact"
          :items="lookupStore.serviceTypes" :loading="lookupStore.serviceTypesLoading" />
      </v-col>
      <v-col :cols="cols" :md="md" :sm="sm" v-if="isVueProject">
        <v-text-field v-model="model.model" label="Module" density="compact" hide-details />
      </v-col>
    </v-row>

    <v-row no-gutters>
      <v-col :cols="cols" :md="md" :sm="sm">
        <v-autocomplete v-model="model.file" label="File" density="compact" :items="files" :loading="filesLoading"
          multiple clearable />
      </v-col>
      <v-col :cols="cols" :md="md" :sm="sm">
        <v-autocomplete v-model="model.projectName" label="Project" density="compact" hide-details />
      </v-col>
      <v-col :cols="cols" :md="md" :sm="sm">
        <v-autocomplete v-model="model.fieldName" label="File Name" density="compact" hide-details />
      </v-col>
    </v-row>

    <v-data-table :items="fields" :items-per-page="-1" hide-default-footer :headers="computedHeaders" dense
      items-per-page="20" density="compact" class="mt-4">
      <!-- <template v-slot:top>
        <v-btn @click="addField">Add Field</v-btn>
      </template> -->

      <!-- Dynamically render item slots -->
      <template v-for="header in fieldHeaders" :key="header.key" v-slot:[`item.${header.key}`]="{ item }">
        <!-- Checkbox type -->
        <v-checkbox v-if="header.type === 'checkbox'" v-model="item[header.key]" density="compact" hide-details />

        <!-- Select type -->
        <v-autocomplete v-else-if="header.type === 'select'" :items="header.namedValues || []"
          v-model="item[header.key]" density="compact" hide-details clearable />

        <!-- Default fallback (if needed) -->
        <v-text-field v-else v-model="item[header.key]" density="compact" hide-details />
      </template>
    </v-data-table>

    <v-card-actions>
      <v-file-input label="Output Location" @change="handleImport" density="compact" hide-details />
      <v-spacer />
      <v-label>{{ fields.length }} row(s)</v-label>
      <v-btn @click="addField">Add Field</v-btn>
      <v-btn color="primary" @click="console.log(fields)">Generate Code</v-btn>
    </v-card-actions>
  </v-card>
</template>
