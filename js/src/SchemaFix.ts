export type Fixable = {
  readonly subject: "table" | "column" | "constraint" | "sequence";
  readonly tableName: string | undefined; // undefined for subject "table" and "sequence"
  readonly name: string;
  fix: Fix | undefined;
};

export type Fix = {
  readonly method: "add" | "drop" | "modify" | "rename";
  readonly value: string | undefined; // new name for method "rename"
};

export type FixedFixable = {
  readonly subject: "table" | "column" | "constraint" | "sequence";
  readonly tableName: string | undefined; // undefined for subject "table" and "sequence"
  readonly name: string;
  fix: Fix;
};

export function workOnFixes(source: FixedFixable[]): FixedFixable[] {
  let result: FixedFixable[] = [];
  source.forEach((i) => {
    if (i.subject === "constraint" && i.fix.method === "modify") {
      result.push({
        ...i,
        fix: { method: "drop", value: undefined } satisfies Fix,
      } satisfies FixedFixable);
      result.push({
        ...i,
        fix: { method: "add", value: undefined } satisfies Fix,
      } satisfies FixedFixable);
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
function orderIndex(cb: FixedFixable): number {
  switch (cb.subject) {
    case "constraint": {
      switch (cb.fix.method) {
        case "drop":
          return -19;
        case "add":
          return 19;
      }
      break;
    }
    case "column": {
      switch (cb.fix.method) {
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
      switch (cb.fix.method) {
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
      switch (cb.fix.method) {
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

export function encodeJava(s: string): string {
  return '"' + s.replaceAll('"', '\\"') + '",';
}
