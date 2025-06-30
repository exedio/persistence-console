export type SchemaCheckbox = {
  readonly subject: "table" | "column" | "constraint" | "sequence";
  readonly tableName: string | undefined; // undefined for subject "table" and "sequence"
  readonly name: string;
  readonly method: "add" | "drop" | "modify";
};

export function workOnCheckboxes(source: SchemaCheckbox[]): SchemaCheckbox[] {
  let result: SchemaCheckbox[] = [];
  source.forEach((i) => {
    if (i.subject === "constraint" && i.method === "modify") {
      result.push({
        subject: i.subject,
        tableName: i.tableName,
        name: i.name,
        method: "drop",
      } satisfies SchemaCheckbox);
      result.push({
        subject: i.subject,
        tableName: i.tableName,
        name: i.name,
        method: "add",
      } satisfies SchemaCheckbox);
    } else result.push(i);
  });
  // TODO sort like old SchemaCop
  return result;
}
