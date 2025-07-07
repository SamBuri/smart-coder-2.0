import { defineStore } from "pinia";
import { defineRootStore } from "@/base/RootStore";

export const defineLookupStore = defineStore("lookup", {
  state: () => ({
    path: "enums/",
    projectTypes: [],
    projectTypesLoading: false,
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

  },
});