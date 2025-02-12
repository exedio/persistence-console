/// <reference types="vitest" />
import { defineConfig } from "vite";
import { svelte } from "@sveltejs/vite-plugin-svelte";
import path from "path";

// https://vite.dev/config/
export default defineConfig({
  plugins: [svelte()],
  cacheDir: ".yarn/.vite",
  build: {
    rollupOptions: {
      input: {
        app: "./js/index.html",
      },
      output: {
        entryFileNames: `assets/[name].js`,
        assetFileNames: `assets/[name].[ext]`,
      },
    },
  },
  server: {
    open: "./js/index.html",
    port: 5193,
    strictPort: true,
    proxy: {
      "/cope-console/standard/api/": {
        target: "http://localhost:8080",
      },
      "/cope-console/reduced/api/": {
        target: "http://localhost:8080",
      },
    },
  },
  test: {
    environment: "jsdom",
    globals: true,
    setupFiles: ["js/tests/setupFilesAfterEnv.ts"],
    restoreMocks: true,
    reporters: ["default", "junit", "hanging-process"],
    outputFile: "build/vitestresults/junit.xml",
    coverage: {
      provider: "v8",
      reportsDirectory: "build/vitestcoverage",
      reporter: ["text", "html", "cobertura"],
      include: ["js/src/**/*.{ts,svelte}"],
    },
  },
  resolve: {
    alias: [
      { find: "@t", replacement: path.resolve("js/tests") },
      { find: "@", replacement: path.resolve("js/src") },
    ],
    // Tell Vitest to use the `browser` entry points in `package.json` files, even though it's running in Node
    conditions: process.env.VITEST ? ["browser"] : undefined,
  },
});
