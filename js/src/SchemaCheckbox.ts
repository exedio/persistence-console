export type SchemaCheckbox = {
  readonly subject: "table" | "column" | "constraint" | "sequence";
  readonly tableName: string | undefined; // undefined for subject "table" and "sequence"
  readonly name: string;
  readonly method: "add" | "drop" | "modify";
};
