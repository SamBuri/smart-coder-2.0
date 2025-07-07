/// <reference types="vite/client" />
/// <reference types="unplugin-vue-router/client" />
 /// <reference types="vite/client" /> // or @vue/cli-service if using Vue CLI

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
  // add more environment variables here...
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
