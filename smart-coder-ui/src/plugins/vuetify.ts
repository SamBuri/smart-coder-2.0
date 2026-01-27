/**
 * plugins/vuetify.ts
 *
 * Framework documentation: https://vuetifyjs.com`
 */

// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// Composables
import { createVuetify } from 'vuetify'
import {VTreeview} from "vuetify/labs/components";
import { VTextField } from 'vuetify/components/VTextField'
import { VTextarea } from 'vuetify/components/VTextarea'
import { VAutocomplete } from 'vuetify/components/VAutocomplete'

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
export default createVuetify({
  components: {
    VTreeview
  },
  aliases: {
    STextField: VTextField,
    STextarea: VTextarea,
    SAutocomplete: VAutocomplete,
  },
  defaults: {
    STextField: {
      density: "compact"
    },

    VTextField: {
      density: "compact"
    },

    VSelect: {
      density: "compact"
    },
    VAutocomplete: {
      density: "compact",
      clearable:true
    },

    SAutocomplete: {
      density: "compact",
      clearable: true,
    },
    STextarea: {
      density: "compact",
      rows: 1,
      autoGrow: true
    },

    VTextarea: {
      density: "compact",
      rows: 1,
      autoGrow: true
    },

    VFileInput: {
      density: "compact"
    },

    VCheckbox: {
      density: "compact"
    },

  },
  theme: {
    defaultTheme: 'dark',
  },
})
