/**
 * @typedef {import('eslint').Linter.FlatConfig} FlatConfig
 * @typedef {import('eslint').ESLint.Plugin} Plugin
 */
import eslint from "@eslint/js";
import { configs } from "typescript-eslint";
import svelte from "eslint-plugin-svelte";
import svelteConfig from "./svelte.config.js";
import ts from "typescript-eslint";
import comments from "@eslint-community/eslint-plugin-eslint-comments/configs";

function globalIgnore(ignores) {
  return {
    name: "global ignores",
    ignores,
  };
}

export default [
  globalIgnore([
    "*",
    "!js", // ignore all except /js
    ".yarn/**",
    "!*.mjs", // check javascript in root
    "!*.ts", // check typescript in root
  ]),
  {
    ...eslint.configs.recommended,
    name: "JS files",
    files: ["**/*.mjs", "**/*.cjs", "**/*.js"],
  },
  ...svelte.configs.recommended,
  {
    files: ["**/*.svelte", "**/*.svelte.ts", "**/*.svelte.js"],
    languageOptions: {
      parserOptions: {
        projectService: true,
        extraFileExtensions: [".svelte"],
        parser: ts.parser,
        svelteConfig,
      },
    },
  },
  ...configs.recommended.map((conf) => ({
    ...conf,
    files: ["**/*.ts", "**/*.tsx", "**/*.mts", "**/*.cts"],
  })),
  comments.recommended,
];
