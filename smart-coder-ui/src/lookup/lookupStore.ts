import { defineStore } from "pinia";
import { defineRootStore } from "@/base/RootStore";

export const defineLookupStore = defineStore("lookup", {
  state: () => ({
    path: "enums/",
    projectTypes: [],
    projectTypesLoading: false,
    javaProjectTypes: [],
    javaProjectTypesLoading: false,
    javaScriptProjectTypes: [],
    javaScriptProjectTypesLoading: false,
    springBootFiles: [],
    springBootFilesLoading: false,
    mappings: [],
    mappingsLoading: false,
    entityTypes: [],
    entityTypesLoading: false,
    serviceTypes: [],
    serviceTypesLoading: false,
    keys: [],
    keysLoading: false,
    saburiKeys: [],
    saburiKeysLoading: false,
    vueFiles: [],
    vueFilesLoading: false,
    javaFxFiles: [],
    javaFxFilesLoading: false,
    languages: [],
    languagesLoading: false,
  }),

  actions: {
    async getProjectTypes() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}projecttypes`,
        () => {
          this.projectTypesLoading = true;
          this.projectTypes = [];
        },
        (res: any) => (this.projectTypes = res.data),
        () => (this.projectTypesLoading = false)
      );
      return data;
    },

    async getJavaProjectTypes() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}javaprojecttypes`,
        () => {
          this.javaProjectTypesLoading = true;
          this.javaProjectTypes = [];
        },
        (res: any) => (this.javaProjectTypes = res.data),
        () => (this.javaProjectTypesLoading = false)
      );
      return data;
    },

    async getJavaScriptProjectTypes() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}javascriptprojecttypes`,
        () => {
          this.javaScriptProjectTypesLoading = true;
          this.javaScriptProjectTypes = [];
        },
        (res: any) => (this.javaScriptProjectTypes = res.data),
        () => (this.javaScriptProjectTypesLoading = false)
      );
      return data;
    },

    async getSpringBootFiles() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}springbootfiles`,
        () => {
          this.springBootFilesLoading = true;
          this.springBootFiles = [];
        },
        (res: any) => (this.springBootFiles = res.data),
        () => (this.springBootFilesLoading = false)
      );
      return data;
    },

    async getMappings() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}mappings`,
        () => {
          this.mappingsLoading = true;
          this.mappings = [];
        },
        (res: any) => (this.mappings = res.data),
        () => (this.mappingsLoading = false)
      );
      return data;
    },

    async getEntityTypes() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}entitytypes`,
        () => {
          this.entityTypesLoading = true;
          this.entityTypes = [];
        },
        (res: any) => (this.entityTypes = res.data),
        () => (this.entityTypesLoading = false)
      );
      return data;
    },

    async getServiceTypes() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}servicetypes`,
        () => {
          this.serviceTypesLoading = true;
          this.serviceTypes = [];
        },
        (res: any) => (this.serviceTypes = res.data),
        () => (this.serviceTypesLoading = false)
      );
      return data;
    },

    async getKeys() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}keys`,
        () => {
          this.keysLoading = true;
          this.keys = [];
        },
        (res: any) => (this.keys = res.data),
        () => (this.keysLoading = false)
      );
      return data;
    },

    async getSaburiKeys() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}saburikeys`,
        () => {
          this.saburiKeysLoading = true;
          this.saburiKeys = [];
        },
        (res: any) => (this.saburiKeys = res.data),
        () => (this.saburiKeysLoading = false)
      );
      return data;
    },

    async getVueFiles() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}vuefiles`,
        () => {
          this.vueFilesLoading = true;
          this.vueFiles = [];
        },
        (res: any) => (this.vueFiles = res.data),
        () => (this.vueFilesLoading = false)
      );
      return data;
    },
      async getJavaFxFiles() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}javafxfiles`,
        () => {
          this.javaFxFilesLoading = true;
          this.javaFxFiles = [];
        },
        (res: any) => (this.javaFxFiles = res.data),
        () => (this.javaFxFilesLoading = false)
      );
      return data;
    },

    async getLanguages() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}languages`,
        () => {
          this.languagesLoading = true;
          this.languages = [];
        },
        (res: any) => (this.languages = res.data),
        () => (this.languagesLoading = false)
      );
      return data;
    },

  },
});
