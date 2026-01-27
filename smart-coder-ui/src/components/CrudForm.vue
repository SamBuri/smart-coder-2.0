<script setup>
import { ref, onMounted, computed } from "vue";
import { defineRootStore } from "@/base/RootStore";
import constants from "@/utils/constants";
const props = defineProps([
  "inner",
  "dialog",
  "retain",
  "data",
  "buttonLabel",
  "controller",

]);
const emit = defineEmits(["add"]);
const rootState = props.controller.rootState;
const options = props.controller.options;
const isUpdate = props.controller.isUpdate;

const form = ref(null);
const idForm = ref(null);
// props.controller.setProps(props);


onMounted(() => {
  try {


    form.value.validate();
    if (!props.dialog || props.buttonLabel === constants.buttonTexts.save) {

      if (!props.retain) props.controller.clear();

    }

    if (props.buttonLabel) {
      props.controller.setButtonText(props.buttonLabel);
      props.controller.rootState.value.showSearch = false;
    }
    if (props.data) {
      props.controller.setData(props.data);
    }
  } catch (e) {
    console.log("Error", e)

  }


});

const edit = () => {
  if (props.buttonLabel === constants.buttonTexts.done) {
    emit('add', props.controller.model.value, () => props.controller.clear());
    // props.controller.clear()
  }
  else props.controller.editClicked()
}

const rootStore = defineRootStore();

const changed = ()=>{


    if(!rootState.id) props.controller.clear()
    else{
      props.controller.clear()
      props.controller.setSearchedData();
  }
  }
</script>

<template>
  <v-card flat :max-width="options.maxWidth" class="mx-auto mt-0 pa-1 scrollable-card">
    <slot name="before-card"></slot>
    <v-toolbar flat v-if="!props.inner">
      <v-card-title>
        <!-- <span class="headline"> -->
        <slot name="heading"> </slot>
        <!-- </span> -->
      </v-card-title>
      <v-spacer></v-spacer>
      <slot name="right"> </slot>
      <v-btn color="primary" v-if="props.dialog" text @click="$emit('cancel')">
        <v-icon>mdi-close</v-icon>
      </v-btn>
    </v-toolbar>
    <v-card-text>
      <!-- ID Form for searching -->

      <template v-if="rootState.showSearch">
        <v-form ref="idForm" v-model="rootState.idValid" @submit="controller.search">
          <v-container>


            <v-row v-if="!controller.currentStore">

              <v-col cols="10">
                <v-text-field label="Id" v-model="rootState.id" hint="Enter the id. and press enter to load data"
                  v-on:keyup.enter="controller.search" :rules="rootState.idRules" required @change="controller.clear"
                 ></v-text-field>
              </v-col>

              <v-col cols="2">
                <v-btn color="primary" text type="submit" :disabled="!rootState.idValid" :loading="rootStore.objLoading"
                  loading-text="Please Wait...">
                  search
                </v-btn>
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12" v-if="controller.currentStore">
                <s-select-field id="Id" label="Id" hint="Enter the id. and press enter to load data"
                  v-model="rootState.id" v-on:keyup.enter="controller.search"
                   :rules="rootState.idRules" required
                  @ok="controller.setSearchedData" @change="changed"
                   :items="controller?.currentStore?.mini || []"
                  :headers="props.controller.currentNav.menu.miniHeaders || []"></s-select-field>

              </v-col>


            </v-row>
          </v-container>


        </v-form>
      </template>
      <v-form ref="form" v-model="rootState.valid" @submit="(e) => e.preventDefault">
        <v-container>
          <v-row>
            <slot name="form-header" :isUpdate="controller.isUpdate" :isPreview="controller.isPreview"></slot>
          </v-row>
          <v-row>
            <slot name="form-data" :isUpdate="controller.isUpdate" :isPreview="controller.isPreview"></slot>
          </v-row>
        </v-container>
        <!-- <v-dialog v-model="$store.state.search.miniDialog" :max-width="formWidth" persistent>
            <search-mini />
          </v-dialog> -->

          <s-confirm-dialog :message="options.warningMsg" @ok="controller.editConfirmOk"  v-model="rootState.confirmEdit" :max-width="500"
            @cancel="controller.cancelEdit" />


<!--          <s-confirm-dialog :message="options.warningMsg" @ok="controller.deleteOk"  v-model="rootState.confirmEdit" :max-width="500"-->
<!--            @cancel="controller.cancelDelete" />-->

      </v-form>
    </v-card-text>

    <v-card-actions>
      <slot name="left-actions"></slot>
      <v-spacer></v-spacer>
      <slot name="card-actions" :valid="rootState.valid">
        <v-btn color="primary" v-if="props.dialog" text @click="$emit('cancel')">
          Cancel
        </v-btn>
        <span><v-checkbox label="Print" v-model="rootState.printData" v-if="options.showPrintPrompt">
          </v-checkbox></span>
        <v-btn color="primary" text @click="edit" :loading="rootStore.loading" :disabled="!rootState.valid">
          {{ rootState.buttonText }}
        </v-btn>

        <v-btn v-if="(isUpdate && options.showDelete)" color="primary" text @click="controller.deleteData"
          :disabled="!rootState.valid" :loading="rootStore.deleteLoading">
          Delete
        </v-btn>
      </slot>
    </v-card-actions>
    <snack-bar />
    <slot name="after-card"></slot>
  </v-card>
</template>

<style scoped>
.scrollable-card {
  overflow-y: auto;
}
</style>
