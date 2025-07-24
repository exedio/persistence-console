export type SchemaFixable = {
  readonly subject: "table" | "column" | "constraint" | "sequence";
  readonly tableName: string | undefined; // undefined for subject "table" and "sequence"
  readonly name: string;
};

export function schemaFixableString(f: SchemaFixable): string {
  return f.subject + "/" + f.tableName + "/" + f.name;
}

export type SchemaFix = SchemaFixable & {
  readonly method: "add" | "drop" | "modify" | "rename";
  readonly value: string | undefined; // new name for method "rename"
};

export function workOnFixes(source: SchemaFix[]): SchemaFix[] {
  let result: SchemaFix[] = [];
  source.forEach((i) => {
    if (i.subject === "constraint" && i.method === "modify") {
      result.push({
        ...i,
        method: "drop",
      } satisfies SchemaFix);
      result.push({
        ...i,
        method: "add",
      } satisfies SchemaFix);
    } else result.push(i);
  });
  result.sort((a, b) => {
    return orderIndex(a) - orderIndex(b);
  });
  return result;
}

/**
 * Order taken from SchemaCop#writeApply
 */
function orderIndex(cb: SchemaFix): number {
  switch (cb.subject) {
    case "constraint": {
      switch (cb.method) {
        case "drop":
          return -19;
        case "add":
          return 19;
      }
      break;
    }
    case "column": {
      switch (cb.method) {
        case "drop":
          return -18;
        case "add":
          return 18;
        case "modify":
          return 1;
      }
      break;
    }
    case "table": {
      switch (cb.method) {
        case "drop":
          return -17;
        case "add":
          return 17;
        case "rename":
          return 0;
      }
      break;
    }
    case "sequence": {
      switch (cb.method) {
        case "drop":
          return -16;
        case "add":
          return 16;
      }
      break;
    }
  }
  throw new Error(JSON.stringify(cb));
}
