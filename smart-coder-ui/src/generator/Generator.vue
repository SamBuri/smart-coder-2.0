<script setup>
import {ref, computed, onMounted, watch, shallowRef} from 'vue';
import {defineSettingsStore} from "@/setting/SettingStore.js";
// import type { Field } from '@/field/Field.ts';
import {createDefaultField} from '@/field/Field.js';
import {defineLookupStore} from '@/lookup/lookupStore.js';
import constants from '@/utils/constants.js';
import fieldHeaders from '@/field/FieldNav.js';
import {FieldParser} from '@/field/FieldParser.js';
import {defineProjectStore} from "@/project/projectStore.ts";
import {toCaption} from "@/utils/funcs.ts";
import {defineRootStore} from "@/base/RootStore.ts";
import {useGeneratorController} from "@/generator/GeneratorController.ts";
import {useSettingsController} from "@/setting/SettingController.js";


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



const cols = 12;
const md = 2;
const sm = 6;
// const fields = ref([]);
const importedText = ref(''); // will hold the raw file contents
// const model = ref({ projectType: '', projectName: '', objectName: '', objectCaption: '', entityType: '', serviceType: 'Base', model: '', file: '', fieldName: '' });
const {model, save, rootState} = useGeneratorController()

const addField = () => {
  model.value.fields.push(createDefaultField());
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
  model.value.fields = FieldParser.parse(importedText.value);
  console.log('Parsed fields:', fields.value);
};

const exportFields = () => {
  const dataStr = JSON.stringify({fields: model.value.fields}, null, 2);
  const blob = new Blob([dataStr], {type: 'application/json'});
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
const settingStore = defineSettingsStore();

onMounted(() => {
  lookupStore.getProjectTypes();
  lookupStore.getSpringBootFiles();
  lookupStore.getEntityTypes();
  lookupStore.getServiceTypes();
  lookupStore.getVueFiles();
  lookupStore.getJavaFxFiles()
  settingStore.getOutputDefaultLocation();
});

watch(settingStore.defaultOutputLocation, (newValue)=>{
  if(newValue) model.value.outputDirectory = newValue;
});

const files = ref([]);
const filesLoading = ref(false);

const isSpringBootProject = computed(() => model.value.projectType === constants.projectTypes.springBoot)
const isVueProject = computed(() => model.value.projectType === constants.projectTypes.vue)
const projectStore = defineProjectStore();
watch(() => model.value.projectType, (newValue) => {
  files.value = [];
  filesLoading.value = false
  if (!newValue) return
  if (newValue === constants.projectTypes.springBoot) {
    files.value = lookupStore.springBootFiles;
    filesLoading.value = lookupStore.springBootFilesLoading
  } else if (newValue === constants.projectTypes.vue) {
    files.value = lookupStore.vueFiles;
    filesLoading.value = lookupStore.vueFilesLoading
  } else if (newValue === constants.projectTypes.javaFx) {
    files.value = lookupStore.javaFxFiles;
    filesLoading.value = lookupStore.javaFxFilesLoading
  }
  projectStore.getMiniProjectsByType(newValue)
});


const selectedFile = ref(null);
// const selectedFile = shallowRef<File | null>(null)
const fileContent = ref('');

watch(fileContent, (newValue) => {
  console.log("File Content", newValue)
  model.value.fields = [];
  if (!newValue) return;
  model.value.fields = FieldParser.parse(newValue.trim());
  console.log(model.value.fields);
})

watch(selectedFile, (newVal) => {
  // Reset when no file
  if (!newVal) {
    model.value.objectName = '';
    model.value.objectCaption = '';
    return;
  }

  // Get filename without extension
  const filenameWithoutExt = newVal.name.replace(/\.[^/.]+$/, '');

  // Update the model
  model.value.objectName = filenameWithoutExt;

  // Convert to human-readable caption: "my-file_name" → "My File Name"
  model.value.objectCaption = toCaption(filenameWithoutExt);
});


//just to save the output location;
// const {model : settingsModel, saveDefaultLocationSettings, } = useSettingsController()

const settings = useSettingsController();
const saveOutputDir = ()=>{
  settings.saveDefaultLocationSettings(settings.model.value, model.value.outputDirectory)
}




</script>

<template>
  <v-card class="pa-4 mx-2">
    <v-form v-model="rootState.valid">
      {{ settingsModel }}
      <v-row>
        <!-- <v-col cols="4">
          <v-file-input label="Import Field Config" @change="handleImport" density="compact" hide-details />
        </v-col> -->

        <v-col cols="4">
          <text-file-reader v-model="selectedFile" v-model:content="fileContent" density="compact" hide-details
                            label="Import Field"/>
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
                          v-model="model.projectType" :loading="lookupStore.projectTypesLoading"/>
        </v-col>
        <v-col :cols="cols" :md="md" :sm="sm">
          <v-autocomplete v-model="model.projectId"
                          :items="projectStore.projectsByType"
                          :loading="projectStore.projectsByTypeLoading"
                          item-title="projectName"
                          item-value="id"
                          label="Project" density="compact" hide-details/>
        </v-col>
        <v-col :cols="cols" :md="md" :sm="sm">
          <v-text-field v-model="model.objectName" label="Object Name" density="compact" hide-details/>
        </v-col>
        <v-col :cols="cols" :md="md" :sm="sm">
          <v-text-field v-model="model.objectCaption" label="Object Caption" density="compact" hide-details/>
        </v-col>
        <v-col :cols="cols" :md="md" :sm="sm" v-if="isSpringBootProject">
          <v-autocomplete v-model="model.entityType" label="Entity Type" density="compact"
                          :items="lookupStore.entityTypes" :loading="lookupStore.entityTypesLoading"/>
        </v-col>
        <v-col :cols="cols" :md="md" :sm="sm" v-if="isSpringBootProject">
          <v-autocomplete v-model="model.serviceType" label="Service Type" density="compact"
                          :items="lookupStore.serviceTypes" :loading="lookupStore.serviceTypesLoading"/>
        </v-col>
        <v-col :cols="cols" :md="md" :sm="sm" v-if="isVueProject">
          <v-text-field v-model="model.moduleName" label="Module" density="compact" hide-details/>
        </v-col>


      </v-row>

      <v-row no-gutters>
        <v-col :cols="cols" :md="md" :sm="sm">
          <v-autocomplete v-model="model.files" label="File" density="compact" :items="files" :loading="filesLoading"
                          multiple clearable/>
        </v-col>
        <v-col :cols="cols" :md="md" :sm="sm" >
          <v-checkbox v-model="model.saveToProject" label="Save To Project" density="compact" hide-details/>
        </v-col>

        <v-col :cols="cols" :md="md" :sm="sm" >
          <v-checkbox v-model="model.openFile" label="Open" density="compact" hide-details/>
        </v-col>
        <!--      <v-col :cols="cols" :md="md" :sm="sm">-->
        <!--        <v-autocomplete v-model="model.projectName" label="Project" density="compact" hide-details />-->
        <!--      </v-col>-->
        <!--      <v-col :cols="cols" :md="md" :sm="sm">-->
        <!--        <v-autocomplete v-model="model.fieldName" label="File Name" density="compact" hide-details />-->
        <!--      </v-col>-->
      </v-row>

      <v-data-table :items="model.fields" :items-per-page="-1" hide-default-footer :headers="computedHeaders" dense
                    items-per-page="20" density="compact" class="mt-4 fixed-table" v-model="model.fields">
        <!-- <template v-slot:top>
          <v-btn @click="addField">Add Field</v-btn>
        </template> -->

        <!-- Dynamically render item slots -->
        <template v-for="header in fieldHeaders" :key="header.key" v-slot:[`item.${header.key}`]="{ item }">
          <!-- Checkbox type -->
          <v-checkbox v-if="header.type === 'checkbox'" v-model="item[header.key]" density="compact" hide-details/>

          <!-- Select type -->
          <v-autocomplete v-else-if="header.type === 'select'" :items="header.namedValues || []"
                          v-model="item[header.key]" density="compact" hide-details clearable/>

          <v-autocomplete v-else-if="header.key === 'projectName'" :items="projectStore.projectsByType"
                          item-title="projectName" item-value="id"
                          v-model="item[header.key]" density="compact" hide-details clearable/>

          <!-- Default fallback (if needed) -->
          <v-text-field v-else v-model="item[header.key]" density="compact" hide-details/>
        </template>
      </v-data-table>

      <v-card-actions>
        <s-select-folder label="Output Location" density="compact" :readonly="false" v-model="model.outputDirectory" hide-details directory/> <v-btn color="primary" @click="saveOutputDir" :disabled="!model.outputDirectory">Save</v-btn>
        <v-spacer/>
        <v-label>{{ model.fields.length }} row(s)</v-label>
        <v-btn @click="addField" color="primary">Add Field</v-btn>
        <v-btn color="primary" @click="save">Generate Code</v-btn>
      </v-card-actions>
    </v-form>
    <snack-bar/>
  </v-card>

</template>
