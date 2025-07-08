export type SchemaFix = {
  readonly subject: "table" | "column" | "constraint" | "sequence";
  readonly tableName: string | undefined; // undefined for subject "table" and "sequence"
  readonly name: string;
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
  if (cb.subject === "constraint") {
    if (cb.method === "drop") return -19;
    else if (cb.method === "add") return 19;
    else throw JSON.stringify(cb);
  } else if (cb.subject === "column") {
    if (cb.method === "drop") return -18;
    else if (cb.method === "add") return 18;
    else if (cb.method === "modify") return 1;
    else throw JSON.stringify(cb);
  } else if (cb.subject === "table") {
    if (cb.method === "drop") return -17;
    else if (cb.method === "add") return 17;
    else if (cb.method === "rename") return 0;
    else throw JSON.stringify(cb);
  } else if (cb.subject === "sequence") {
    if (cb.method === "drop") return -16;
    else if (cb.method === "add") return 16;
    else throw JSON.stringify(cb);
  }
  throw JSON.stringify(cb);
}
