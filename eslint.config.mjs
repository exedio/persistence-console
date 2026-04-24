/**
 * @typedef {import('eslint').Linter.FlatConfig} FlatConfig
 * @typedef {import('eslint').ESLint.Plugin} Plugin
 */
import eslint from "@eslint/js";
import { configs } from "typescript-eslint";

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
  ...configs.recommended.map((conf) => ({
    ...conf,
    files: ["**/*.ts", "**/*.tsx", "**/*.mts", "**/*.cts"],
  })),
];
