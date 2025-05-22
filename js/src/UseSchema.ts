import type {
  SchemaColumnResponse,
  SchemaConstraintResponse,
  SchemaRemainder,
  SchemaResponse,
  SchemaSequenceResponse,
  SchemaTableResponse,
} from "@/api/types";

export type UseSchema = {
  readonly tables: readonly UseTable[];
  readonly sequences: readonly UseSequence[];
  readonly bulletColor: Color;
};

export function useSchema(api: SchemaResponse): UseSchema {
  const tables = (api.tables ?? []).map((i) => useTable(i));
  const sequences = (api.sequences ?? []).map((i) => useSequence(i));
  return {
    tables,
    sequences,
    bulletColor: worse(
      worst(tables.map((i) => i.bulletColor)),
      worst(sequences.map((i) => i.bulletColor)),
    ),
  } satisfies UseSchema;
}

export type UseTable = {
  readonly name: string;
  readonly existence: UseExistence;
  readonly columns: readonly UseColumn[];
  readonly constraints: readonly UseConstraint[];
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;
};

export function useTable(api: SchemaTableResponse): UseTable {
  const name = api.name;
  const existence: UseExistence =
    api.error && api.error.existence
      ? api.error.existence === "unused"
        ? { text: api.error.existence, color: "yellow" }
        : { text: api.error.existence, color: "red" }
      : undefined;
  const columns = (api.columns ?? []).map((i) => useColumn(i, name));
  const constraints = (api.constraints ?? []).map((i) =>
    useConstraint(i, name, undefined),
  );
  return {
    name,
    existence,
    columns,
    constraints,
    remainingErrors: useRemainder(api.error?.remainder),
    bulletColor: worst([
      existence?.color,
      remainderColor(api.error),
      worst(columns.map((i) => i.bulletColor)),
      worst(constraints.map((i) => i.bulletColor)),
    ]),
  } satisfies UseTable;
}

export type UseColumn = {
  readonly name: string;
  readonly existence: UseExistence;
  readonly type: UseComparison;
  readonly constraints: readonly UseConstraint[];
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;
};

export function useColumn(
  api: SchemaColumnResponse,
  tableName: string,
): UseColumn {
  const name = api.name;
  const existence = columnExistence(api);
  const type: UseComparison = {
    name: "type",
    expected: api.type,
    actual: api.error?.type,
    actualRaw: undefined,
    shortener: (s) => s,
    color: api.error?.type ? "red" : undefined,
  };
  const constraints = (api.constraints ?? []).map((i) =>
    useConstraint(i, tableName, name),
  );
  return {
    name,
    existence,
    type,
    constraints,
    remainingErrors: useRemainder(api.error?.remainder),
    bulletColor: worst([
      existence?.color,
      type?.color,
      remainderColor(api.error),
      worst(constraints.map((i) => i.bulletColor)),
    ]),
  } satisfies UseColumn;
}

function columnExistence(api: SchemaColumnResponse): UseExistence {
  const error = api.error;
  if (!error || !error.existence) return undefined;

  // TODO "not null" should be replaced by explicit API
  if (error.existence === "unused" && !api.type.endsWith(" not null"))
    return { text: "unused", color: "yellow" };

  return { text: error.existence, color: "red" };
}

export type UseConstraint = {
  readonly name: string;
  readonly nameShort: () => string;
  readonly existence: UseExistence;
  readonly type: UseConstraintType;
  readonly clause: UseComparison | undefined;
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;
};

type UseConstraintType = "pk" | "fk" | "unq" | "chk";

export function useConstraint(
  api: SchemaConstraintResponse,
  tableName: string,
  columnName: string | undefined,
): UseConstraint {
  const name = api.name;
  const existence: UseExistence = constraintExistence(api);
  const clause: UseComparison | undefined = api.clause
    ? {
        name: "clause",
        expected: api.clause,
        actual: api.error?.clause,
        actualRaw: api.error?.clauseRaw,
        shortener: (s) => {
          if (!columnName) return s;
          return s
            .replace('"' + columnName + '"', "⬁") // hsqldb, PostgreSQL
            .replace("`" + columnName + "`", "⬁"); // MySQL
        },
        color: api.error?.clause ? "red" : undefined,
      }
    : undefined;

  return {
    name,
    nameShort: () => {
      if (columnName) {
        const prefix = tableName + "_" + columnName + "_";
        if (name.startsWith(prefix)) return "~" + name.substring(prefix.length);
      }
      const prefix = tableName + "_";
      if (name.startsWith(prefix)) return "~" + name.substring(prefix.length);

      return name;
    },
    existence,
    type: useConstraintType(api),
    clause,
    remainingErrors: useRemainder(api.error?.remainder),
    bulletColor: worst([
      existence?.color,
      clause?.color,
      remainderColor(api.error),
    ]),
  } satisfies UseConstraint;
}

function constraintExistence(api: SchemaConstraintResponse): UseExistence {
  const error = api.error;
  if (!error || !error.existence) return undefined;
  return { text: error.existence, color: "red" };
}

function useConstraintType(api: SchemaConstraintResponse): UseConstraintType {
  switch (api.type) {
    case "PrimaryKey":
      return "pk";
    case "ForeignKey":
      return "fk";
    case "Unique":
      return "unq";
    case "Check":
      return "chk";
  }
}

export type UseSequence = {
  readonly name: string;
  readonly existence: UseExistence;
  readonly type: UseComparison;
  readonly start: UseComparison;
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;
};

export function useSequence(api: SchemaSequenceResponse): UseSequence {
  const existence = sequenceExistence(api);
  const type: UseComparison = {
    name: "type",
    expected: api.type,
    actual: api.error?.type,
    actualRaw: undefined,
    shortener: (s) => s,
    color: api.error?.type ? "red" : undefined,
  };
  const start: UseComparison = {
    name: "start",
    expected: api.start.toString(),
    actual: api.error?.start?.toString(),
    actualRaw: undefined,
    shortener: (s) => s,
    color: api.error?.start ? "red" : undefined,
  };
  return {
    name: api.name,
    existence,
    type,
    start,
    remainingErrors: useRemainder(api.error?.remainder),
    bulletColor: worst([
      existence?.color,
      type.color,
      start.color,
      remainderColor(api.error),
    ]),
  } satisfies UseSequence;
}

function sequenceExistence(api: SchemaSequenceResponse): UseExistence {
  const error = api.error;
  if (!error || !error.existence) return undefined;
  return error.existence === "missing"
    ? { text: error.existence, color: "red" }
    : { text: error.existence, color: "yellow" };
}

export function useRemainder(api: SchemaRemainder): readonly string[] {
  return api ? api : [];
}

export type UseExistence =
  | {
      text: "missing" | "unused";
      color: "red" | "yellow";
    }
  | undefined;

export type UseComparison = {
  name: string;
  expected: string;
  actual: string | undefined;
  actualRaw: string | undefined;
  shortener: (s: string) => string;
  color: Color;
};

export type Color = "red" | "yellow" | undefined;

function worst(colors: Color[]): Color {
  return colors.reduce((accu, current) => worse(accu, current), undefined);
}

function worse(a: Color, b: Color): Color {
  return a === "red"
    ? "red"
    : b === "red"
      ? "red"
      : a == "yellow"
        ? "yellow"
        : b === "yellow"
          ? "yellow"
          : undefined;
}

function remainderColor(
  error:
    | {
        readonly remainder: SchemaRemainder;
      }
    | undefined,
): Color {
  return error?.remainder ? "red" : undefined;
}
