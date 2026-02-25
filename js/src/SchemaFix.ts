export type Fixable = {
  readonly subject: "table" | "column" | "constraint" | "sequence";
  readonly tableName: string | undefined; // undefined for subject "table" and "sequence"
  readonly name: string;
  fix: Fix | undefined;
};

export function setFix(
  set: boolean,
  fixable: Fixable,
  method: "add" | "drop" | "modify" | "rename",
  value: string | undefined,
) {
  fixable.fix = set ? { method, value } : undefined;
}

export type Fix = {
  readonly method: "add" | "drop" | "modify" | "rename";
  readonly value?: string; // initial value for method "add" and subject "column", new name for method "rename"
};

export type FixedFixable = {
  readonly subject: "table" | "column" | "constraint" | "sequence";
  readonly tableName?: string; // undefined for subject "table" and "sequence"
  readonly name: string;
  fix: Fix;
  joinable?: Joinable;
};

export type Joinable = "head" | "middle" | "tail";

export function workOnFixes(
  source: FixedFixable[],
  alterTablesJoined: boolean = false,
): FixedFixable[] {
  let result: FixedFixable[] = [];
  source.forEach((i) => {
    if (i.subject === "constraint" && i.fix.method === "modify") {
      result.push({
        ...i,
        fix: { method: "drop" } satisfies Fix,
      } satisfies FixedFixable);
      result.push({
        ...i,
        fix: { method: "add" } satisfies Fix,
      } satisfies FixedFixable);
    } else result.push(i);
  });
  result.sort((a, b) => {
    return orderIndex(a) - orderIndex(b);
  });
  if (alterTablesJoined) {
    let beforeWithSameTable: FixedFixable | undefined;
    let beforeWithSameTableHead: boolean = false;
    result.forEach((i) => {
      // condition below matches ALTER TABLE statements
      if (i.subject === "column" || i.subject === "constraint") {
        if (i.tableName === beforeWithSameTable?.tableName) {
          (beforeWithSameTable as FixedFixable).joinable =
            beforeWithSameTableHead ? "head" : "middle";
          i.joinable = "tail";
          beforeWithSameTable = i;
          beforeWithSameTableHead = false;
        } else {
          delete i.joinable;
          beforeWithSameTable = i;
          beforeWithSameTableHead = true;
        }
      } else {
        delete i.joinable;
        beforeWithSameTable = undefined;
        beforeWithSameTableHead = false;
      }
    });
  } else {
    result.forEach((i) => {
      delete i.joinable;
    });
  }
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

export function encodePatch(
  joinable: Joinable | undefined,
  java: boolean,
  s: string,
): string {
  // another part of the same statement came before me
  if (joinable === "middle" || joinable === "tail")
    s = s.replace(/^ALTER +TABLE *([`"])[0-9A-Za-z_]+\1 */, "");

  // another part of the same statement comes after me
  const moreToCome = joinable === "head" || joinable === "middle";
  if (moreToCome) s = s + ",";

  if (java) s = '"' + s.replaceAll('"', '\\"') + '"';

  if (moreToCome) {
    if (java) s = s + " +";
  } else {
    s = s + (java ? "," : ";");
  }

  return s;
}
