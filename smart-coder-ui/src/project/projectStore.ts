import { defineStore } from "pinia";
import { defineRootStore } from "@/base/RootStore.ts";

export const defineProjectStore = defineStore("project", {
  state: () => ({
    path: "projects/",
    mini: [] as Array<any>,
    miniLoading: false,
    projectsByType: [] as Array<any>,
    projectsByTypeLoading: false,
  }),

  actions: {
    async getMini() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}mini`,
        () => {
          this.miniLoading = true;
          this.mini = [];
        },
        (res: any) => (this.mini = res.data),
        () => (this.miniLoading = false)
      );
      return data;
    },

    async getMiniProjectsByType(projectType: string) {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}mini/projecttypes/${projectType}`,
        () => {
          this.projectsByTypeLoading = true;
          this.projectsByType = [];
        },
        (res: any) => (this.projectsByType = res.data),
        () => (this.projectsByTypeLoading = false)
      );
      return data;
    },




  },


});
