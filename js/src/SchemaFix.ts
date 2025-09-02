export type Fixable = {
  readonly subject: "table" | "column" | "constraint" | "sequence";
  readonly tableName: string | undefined; // undefined for subject "table" and "sequence"
  readonly name: string;
};

export function fixableString(f: Fixable): string {
  return f.subject + "/" + f.tableName + "/" + f.name;
}

export type Fix = Fixable & {
  readonly method: "add" | "drop" | "modify" | "rename";
  readonly value: string | undefined; // new name for method "rename"
};

export function workOnFixes(source: Fix[]): Fix[] {
  let result: Fix[] = [];
  source.forEach((i) => {
    if (i.subject === "constraint" && i.method === "modify") {
      result.push({
        ...i,
        method: "drop",
      } satisfies Fix);
      result.push({
        ...i,
        method: "add",
      } satisfies Fix);
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
function orderIndex(cb: Fix): number {
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
        case "rename":
          return 2;
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
