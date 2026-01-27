<script setup>
import { onMounted, ref, shallowRef, toRef } from 'vue'
import Project from "@/pages/project.vue";
import {defineRootStore} from "@/base/RootStore.js";
import constants from "@/utils/constants.js";
import {useProjectController} from "@/project/ProjectController.js";

const currentYear = new Date().getFullYear()


const dialog = shallowRef(false)
const isEditing = toRef(() => !!formModel.value.id)

const headers = [
  { title: 'Project Name', key: 'projectName', align: 'start' },
  { title: 'Language', key: 'language' },
  { title: 'Project Type', key: 'projectType' },
  { title: 'Base Package', key: 'basePackage' },
  { title: 'Base Folder', key: 'baseFolder', align: 'start' },
  { title: 'Test Folder', key: 'testFolder', align: 'start' },
  { title: 'Resource Folder', key: 'resourceFolder', align: 'start' },
  { title: 'Parent', key: 'parent.projectName', align: 'start' },
  { title: 'Actions', key: 'actions', align: 'end', sortable: false },
]
const rootStore = defineRootStore();


onMounted(() => {
  rootStore.getData("projects")
  // reset()
})
const buttonLabel =ref("")
function add () {
  buttonLabel.value = constants.buttonTexts.save;
  dialog.value = true
}
const passedData = ref(null);
function edit (item) {
  passedData.value = item;
  buttonLabel.value = constants.buttonTexts.update;
  dialog.value = true
}

const deleteConfirm = ref(false)

const toDeleteId = ref(-1)


function deleteClicked(id){
 deleteConfirm.value=true;
  toDeleteId.value=id

}

async function remove() {
  try {
    const results = await rootStore.delete(`projects/${toDeleteId.value}`);

    if (results?.success) {
      const index = rootStore.data.findIndex(p => p.id === toDeleteId.value);
      if (index !== -1) {
        rootStore.data.splice(index, 1); // Faster than filter for large arrays
      }
    }
  } catch (err) {
    console.error("Error deleting project", err);
  } finally {
    toDeleteId.value = null;
    deleteConfirm.value = false;
  }
}


const cancel = ()=>{
  dialog.value = false
  rootStore.getData("projects");
}



</script>

<template>
  <v-sheet border rounded>
    <v-data-table
      :headers="headers"
      :hide-default-footer="rootStore.data.length < 11"
      :items="rootStore.data"
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>
            <v-icon color="medium-emphasis" icon="mdi-book-multiple" size="x-small" start></v-icon>

           Projects
          </v-toolbar-title>

          <v-btn
            class="me-2"
            prepend-icon="mdi-plus"
            rounded="lg"
            text="Add a Project"
            border
            @click="add"
          ></v-btn>
        </v-toolbar>
      </template>

      <template v-slot:item.projectName="{ value }">
        <v-chip :text="value" border="thin opacity-25" prepend-icon="mdi-book" label>
          <template v-slot:prepend>
            <v-icon color="medium-emphasis"></v-icon>
          </template>
        </v-chip>
      </template>

      <template v-slot:item.actions="{ item }">
        <div class="d-flex ga-2 justify-end">
          <v-icon color="medium-emphasis" icon="mdi-pencil" size="small" @click="edit(item)"></v-icon>

          <v-icon color="medium-emphasis" icon="mdi-delete" size="small" @click="deleteClicked(item.id)" :loading="rootStore.deleteLoading"></v-icon>
        </div>
      </template>

      <template v-slot:no-data>
        <v-btn
          class="me-2"
          prepend-icon="mdi-plus"
          rounded="lg"
          text="Add a Project"
          border
          @click="add"
        ></v-btn>
      </template>
    </v-data-table>
  </v-sheet>

  <v-dialog v-model="dialog" max-width="900" >
    <project :dialog="dialog" @cancel="cancel" :data="passedData" :buttonLabel="buttonLabel"/>
  </v-dialog>

  <s-confirm-dialog message="Are your sure you want to delete this project" v-model="deleteConfirm" @cancel="deleteConfirm=false" @ok="remove"/>
</template>
