import { defineStore } from "pinia";
import { defineRootStore } from "@/base/RootStore";
import constants from "@/utils/constants.ts";

export const defineSettingsStore = defineStore("settings", {
  state: () => ({
    path: "settings/",
    defaultOutputLocation: '',
    defaultOutputLocationLoading: false,

  }),

  actions: {
    async getOutputDefaultLocation() {
      const rootStore = defineRootStore();
      let data = await rootStore.fetch(
        `${this.path}property/${constants.settingProperties.outputFolder}`,
        () => {
          this.defaultOutputLocationLoading = true;
          this.defaultOutputLocation = '';
        },
        (res: any) => (this.defaultOutputLocation = res.data),
        () => (this.defaultOutputLocationLoading = false)
      );
      return data;
    },

  },
});
