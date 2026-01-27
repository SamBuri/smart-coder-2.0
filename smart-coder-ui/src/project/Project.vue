<script setup lang="ts">
import {useProjectController} from './ProjectController.ts'
import rootOptions from '@/base/RootOptions.ts'


const controller = useProjectController()
const {model, isUpdate} = controller

rootOptions.maxWidth = 1100

const cols = 12
const sm = 6
const md = 6
</script>

<template>
  <crud-form :controller="controller">
    <template #heading>
      {{ isUpdate ? 'Edit Project' : 'Add New Project' }}
    </template>

    <template #form-data>

      <v-col :cols="cols" :sm="sm" :md="md">
        <s-autocomplete
          label="Language *"
          v-model="model.language"
          :items="controller.lookupStore.languages"
          :loading="controller.lookupStore.languagesLoading"
          :rules="[(v) => !!v || 'Required']"
        />
      </v-col>
      <v-col :cols="cols" :sm="sm" :md="md">
        <s-autocomplete
          label="Project Type *"
          v-model="model.projectType"
          :items="controller.lookupStore.projectTypes"
          :rules="[(v) => !!v || 'Required']"
        />
      </v-col>
      <v-col :cols="cols" :sm="sm" :md="md">
        <s-text-field
          id="Project Name"
          label="Project Name *"
          v-model="model.projectName"
          :rules="[(v: string) => !!v || 'Required']"
          counter="100"
        />
      </v-col>


      <v-col :cols="cols" :sm="sm" :md="md">
        <s-autocomplete
          id="parent"
          label="Parent"
          :items="controller.projectStore.mini"
          :loading="controller.projectStore.miniLoading"
          item-title="projectName"
          item-value="id"
          hint="parent/common project"
          v-model="model.parent"
          return-object
        />
      </v-col>


      <v-col :cols="cols" :sm="sm" :md="md">
        <s-select-folder
          v-model="model.baseFolder"
          label="Project src Folder *"
          hint="Click the folder icon to browse"
          :rules="[(v) => !!v || 'Project folder is required']"
          :readonly="false"
        />
      </v-col>
      <v-col :cols="cols" :sm="sm" :md="md"  v-if="model.projectType==='Springboot'">
        <s-select-folder
          v-model="model.resourceFolder"
          label="Project Resource Folder *"
          hint="Click the folder icon to browse"
          :rules="[(v) => !!v || 'Project Resource is required']"
          :readonly="false"

        />
      </v-col>

      <v-col :cols="cols" :sm="sm" :md="md">
        <s-select-folder
          v-model="model.testFolder"
          label="Project Test Folder *"
          hint="Click the folder icon to browse"
          :rules="[(v) => !!v || 'Project Test is required']"
          :readonly="false"
        />
      </v-col>

      <v-col :cols="cols" :sm="sm" :md="md" v-if="model.projectType==='Springboot'">
        <s-text-field
          id="basePackage"
          label="Base Package *"
          hint="e.g. com.company.domain"
          v-model="model.basePackage"
          :rules="[(v: string) => !!v || 'Required']"
        />
      </v-col>


      <v-col :cols="cols" :sm="sm" :md="md">
        <s-textarea
          label="Notes"
          v-model="model.notes"
          rows="2"
          auto-grow
        />
      </v-col>
    </template>
  </crud-form>
</template>
