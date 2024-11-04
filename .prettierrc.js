//
// To configure IntelliJ IDEA to use Prettier see
// https://www.jetbrains.com/help/idea/prettier.html
//
// Would be nicer to use a .prettierrc.yaml, but js is needed because of:
// https://github.com/prettier/prettier/issues/15085

export default {
  plugins: [
    import.meta.resolve("prettier-plugin-java"),
    import.meta.resolve("prettier-plugin-svelte"),
  ],
  overrides: [
    {
      files: ["*.java"],
      options: {
        parser: "java",
      },
    },
    {
      files: ["*.svelte"],
      options: {
        parser: "svelte",
      },
    },
  ],
};
