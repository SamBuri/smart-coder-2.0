// // Plugins
// import Components from 'unplugin-vue-components/vite'
// import Vue from '@vitejs/plugin-vue'
// import Vuetify, { transformAssetUrls } from 'vite-plugin-vuetify'
// import Fonts from 'unplugin-fonts/vite'
// import VueRouter from 'unplugin-vue-router/vite'

// // Utilities
// import { defineConfig } from 'vite'
// import { fileURLToPath, URL } from 'node:url'

// // https://vitejs.dev/config/
// export default defineConfig({
//   plugins: [
//     VueRouter({
//       dts: 'src/typed-router.d.ts',
//     }),
//     Vue({
//       template: { transformAssetUrls },
//     }),
//     // https://github.com/vuetifyjs/vuetify-loader/tree/master/packages/vite-plugin#readme
//     Vuetify({
//       autoImport: true,
//       styles: {
//         configFile: 'src/styles/settings.scss',
//       },
//     }),
//     Components({
//       dts: 'src/components.d.ts',
//     }),
//     Fonts({
//       fontsource: {
//         families: [
//           {
//             name: 'Roboto',
//             weights: [100, 300, 400, 500, 700, 900],
//             styles: ['normal', 'italic'],
//           },
//         ],
//       },
//     }),
//   ],
//   optimizeDeps: {
//     exclude: [
//       'vuetify',
//       'vue-router',
//       'unplugin-vue-router/runtime',
//       'unplugin-vue-router/data-loaders',
//       'unplugin-vue-router/data-loaders/basic',
//     ],
//   },
//   define: { 'process.env': {} },
//   resolve: {
//     alias: {
//       '@': fileURLToPath(new URL('src', import.meta.url)),
//     },
//     extensions: [
//       '.js',
//       '.json',
//       '.jsx',
//       '.mjs',
//       '.ts',
//       '.tsx',
//       '.vue',
//     ],
//   },
//   server: {
//     port: 3000,
//   },
//   css: {
//     preprocessorOptions: {
//       sass: {
//         api: 'modern-compiler',
//       },
//       scss: {
//         api: 'modern-compiler',
//       },
//     },
//   },
//   build: {
//     outDir: '../src/main/resources/static', // Path relative to the vue project
//     emptyOutDir: true, // Clear the static folder before each build
//   },

// })




// Plugins
import Components from 'unplugin-vue-components/vite'
import Vue from '@vitejs/plugin-vue'
import Vuetify, { transformAssetUrls } from 'vite-plugin-vuetify'
import Fonts from 'unplugin-fonts/vite'
import VueRouter from 'unplugin-vue-router/vite'

// Utilities
import { defineConfig, loadEnv } from 'vite'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // Load environment variables
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [
      VueRouter({
        dts: 'src/typed-router.d.ts',
      }),
      Vue({
        template: { transformAssetUrls },
      }),
      Vuetify({
        autoImport: true,
        styles: {
          configFile: 'src/styles/settings.scss',
        },
      }),
      Components({
        dts: 'src/components.d.ts',
      }),
      Fonts({
        fontsource: {
          families: [
            {
              name: 'Roboto',
              weights: [100, 300, 400, 500, 700, 900],
              styles: ['normal', 'italic'],
            },
          ],
        },
      }),
    ],
    optimizeDeps: {
      exclude: [
        'vuetify',
        'vue-router',
        'unplugin-vue-router/runtime',
        'unplugin-vue-router/data-loaders',
        'unplugin-vue-router/data-loaders/basic',
      ],
    },
    define: {
      'process.env': {},
      // Expose environment variables to your app
      __APP_ENV__: JSON.stringify(env),
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('src', import.meta.url)),
      },
      extensions: [
        '.js',
        '.json',
        '.jsx',
        '.mjs',
        '.ts',
        '.tsx',
        '.vue',
      ],
    },
    server: {
      port: 3000,
      // Proxy API requests in development
      proxy: {
        '/api': {
          target: env.VITE_API_BASE_URL || 'http://localhost:9797',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      },
    },
    css: {
      preprocessorOptions: {
        sass: {
          api: 'modern-compiler',
        },
        scss: {
          api: 'modern-compiler',
        },
      },
    },
    build: {
      outDir: '../src/main/resources/static',
      emptyOutDir: true,
      // Sourcemap generation based on environment
      sourcemap: mode === 'development',
    },
    // Environment-specific configurations
    ...(mode === 'production' && {
      esbuild: {
        drop: ['console', 'debugger'],
      },
    }),
  }
})
