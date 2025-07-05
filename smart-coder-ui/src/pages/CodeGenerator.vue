<script  setup>
import { ref, computed } from 'vue';
// import type { Field } from '@/field/Field.ts';
import { createDefaultField } from '@/field/Field';

const headers = [
  { key: "fieldName", title: "Field Name", type: "text" },
  { key: "caption", title: "Caption", type: "text" },
  {
    key: "dataType", title: "Data Type", type: "select",
    namedValues: ["String", "List", "Set", "Image", "LocalDate", "LocalDateTime",
      "LocalTime", "boolean", "int", "long", "float", "double", "BigDecimal", "File"]
  },
  { key: "references", title: "References", type: "text" },
  {
    key: "mapping", title: "Mapping", type: "select",
    namedValues: ['OneToOne', 'ManyToOne', 'OneToMany', 'ManyToMany']
  },
  { key: "key", title: "Key", type: "select", namedValues: ['Primary', 'Primary_Auto', 'Foreign', 'Unique', 'Unique_Group'] },
  {
    key: "saburiKey", title: "Saburi Key", type: "select",
    namedValues: ['ID_Helper', 'ID_Generator', 'Display', 'UI_Only', 'Query_Only', 'Read_Only']
  },
  { key: "size", title: "Size", type: "text" },
  { key: "nullable", title: "Nullable", type: "checkbox" },
  { key: "enumerated", title: "Enum", type: "checkbox" },
  { key: "projectName", title: "Project Name", type: "text" },
  { key: "expose", title: "Expose", type: "checkbox" },
  { key: "moduleName", title: "Module Name", type: "text" },
  { key: "select", title: "Select", type: "checkbox" },
  { key: "subFields", title: "Sub Fields", type: "text" },

];

const cols = 12;
const md = 3;
const sm = 6;


const fields = ref([
  {
    fieldName: 'field1',
    caption: 'Field 1',
    dataType: 'String',
    references: '',
    mapping: '',
    key: '',
    saburiKey: '',
    size: 100,
    nullable: false,
    enumerated: false,
    subFields: '',
    projectName: '',
    expose: false,
    moduleName: '',
    select: false,
  },
  {
    fieldName: 'field2',
    caption: 'Field 2',
    dataType: 'int',
    references: '',
    mapping: '',
    key: '',
    saburiKey: '',
    size: 100,
    nullable: true,
    enumerated: false,
    subFields: '',
    projectName: '',
    expose: false,
    moduleName: '',
    select: false,
  }
]);
const model = ref({ projectType: '', projectName: '', objectName: '', objectCaption: '', entityType: '', model: '', file: '', fieldName: '' });


const addField = () => {
  fields.value.push(createDefaultField());
};

const handleImport = (file) => {
  const reader = new FileReader();
  reader.onload = (e) => {
    try {
      const data = JSON.parse(e.target?.result);
      fields.value = data.fields || [];
    } catch (error) {
      console.error('Error parsing JSON:', error);
    }
  };
  reader.readAsText(file);
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
</script>

<template>
  <v-card class="pa-4 mx-2">
    <v-row no-gutters>
      <v-col cols="10">
        <v-file-input label="Import Field Config" @change="handleImport" density="compact" hide-details />
      </v-col>

      <v-col cols="1">
        <v-btn @click="exportFields" color="primary">
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
      <v-row no-gutters>
        <v-col :cols="cols" :md="md" :sm="sm">
          <v-autocomplete v-model="model.projectType" label="Project Type" density="compact" hide-details />
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
        <v-col :cols="cols" :md="md" :sm="sm" v-if="model.projectType === 'SpringBoot'">
          <v-autocomplete v-model="model.entityType" label="Entity Type" density="compact" hide-details />
        </v-col>
        <v-col :cols="cols" :md="md" :sm="sm" v-if="model.projectType === 'Vue 3' || model.projectType === 'Vue'">
          <v-text-field v-model="model.model" label="Module" density="compact" hide-details />
        </v-col>
      </v-row>

      <v-row no-gutters>
        <v-col :cols="cols" :md="md" :sm="sm">
          <v-autocomplete v-model="model.file" label="File" density="compact" hide-details />
        </v-col>
        <v-col :cols="cols" :md="md" :sm="sm">
          <v-autocomplete v-model="model.projectName" label="Project" density="compact" hide-details />
        </v-col>
        <v-col :cols="cols" :md="md" :sm="sm">
          <v-autocomplete v-model="model.fieldName" label="File Name" density="compact" hide-details />
        </v-col>
      </v-row>

    </v-row>
    <v-data-table :items="fields" :headers="headers" dense items-per-page="20" density="compact">
      <template v-slot:top>
        <v-btn @click="addField">Add Field</v-btn>
      </template>

      <!-- Dynamically render item slots -->
      <template v-for="header in headers" :key="header.key" v-slot:[`item.${header.key}`]="{ item }">
        <!-- Checkbox type -->
        <v-checkbox v-if="header.type === 'checkbox'" v-model="item[header.key]" density="compact"
          hide-details />

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
      <v-btn @click="addField">Add Field</v-btn>
      <v-btn color="primary" @click="console.log(fields)">Generate Code</v-btn>
    </v-card-actions>
  </v-card>
</template>
