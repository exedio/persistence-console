import type {
  SchemaColumnResponse,
  SchemaConstraintResponse,
  SchemaRemainder,
  SchemaResponse,
  SchemaSequenceResponse,
  SchemaTableResponse,
} from "@/api/types";
import type { SchemaFixable } from "@/SchemaFix";
import { useWithStore } from "@/utils";

export class UseSchema {
  private api: SchemaResponse;
  private _tables: readonly UseTable[];
  readonly sequences: readonly UseSequence[];
  readonly bulletColor: Color;

  private readonly tablesStore = new Map<string, UseTable>();

  constructor(apiParameterNotToBeUsedExceptForAssigment: SchemaResponse) {
    this.api = $state(apiParameterNotToBeUsedExceptForAssigment);
    this._tables = $state(this.useTables(this.api.tables));
    this.sequences = $derived(
      (this.api.sequences ?? []).map((i) => useSequence(i)),
    );
    this.bulletColor = $derived(
      worse(
        worst(this._tables.map((i) => i.bulletColor)),
        worst(this.sequences.map((i) => i.bulletColor)),
      ),
    );
  }

  tables(): readonly UseTable[] {
    return this._tables;
  }

  update(api: SchemaResponse) {
    this.api = api;
    this._tables = this.useTables(api.tables);
  }

  private useTables(
    tables: readonly SchemaTableResponse[] | undefined,
  ): UseTable[] {
    return useWithStore(
      this.tablesStore,
      (source) => source.name,
      (source) => new UseTable(source),
      (target, source) => target.update(source),
      tables ?? [],
    );
  }
}

export class UseTable {
  private api: SchemaTableResponse;
  readonly name: string;
  readonly existence: UseExistence;
  private _columns: readonly UseColumn[];
  readonly constraints: readonly UseConstraint[];
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;
  readonly renameFrom: (schema: UseSchema) => string[];
  readonly renameTo: (schema: UseSchema) => string[];
  readonly fixable: SchemaFixable;

  private readonly columnsStore = new Map<string, UseColumn>();

  expanded: boolean = $state(false);

  constructor(apiParameterForAssigmentOnly: SchemaTableResponse) {
    this.api = $state(apiParameterForAssigmentOnly);
    const name = this.api.name;
    this.name = this.api.name;
    this.existence = $derived(
      this.api.error && this.api.error.existence
        ? this.api.error.existence === "unused"
          ? { text: this.api.error.existence, color: "yellow" }
          : { text: this.api.error.existence, color: "red" }
        : undefined,
    );
    this._columns = $state(this.useColumns(this.api.columns));
    this.constraints = $derived(
      (this.api.constraints ?? []).map((i) =>
        useConstraint(i, name, undefined),
      ),
    );
    this.remainingErrors = $derived(useRemainder(this.api.error?.remainder));
    this.bulletColor = $derived(
      worst([
        this.existence?.color,
        remainderColor(this.api.error),
        worst(this._columns.map((i) => i.bulletColor)),
        worst(this.constraints.map((i) => i.bulletColor)),
      ]),
    );
    this.renameFrom = (schema) => {
      if (!this.existence || this.existence.text !== "missing") return [];
      return schema
        .tables()
        .filter((t) => t.existence && t.existence.text === "unused")
        .map((t) => t.name);
    };
    this.renameTo = (schema) => {
      if (!this.existence || this.existence.text !== "unused") return [];
      return schema
        .tables()
        .filter((t) => t.existence && t.existence.text === "missing")
        .map((t) => t.name);
    };
    this.fixable = {
      subject: "table",
      tableName: undefined,
      name,
    };
  }

  columns(): readonly UseColumn[] {
    return this._columns;
  }

  update(api: SchemaTableResponse) {
    if (this.name !== api.name) throw new Error(this.name);

    this.api = api;
    this._columns = this.useColumns(api.columns);
  }

  private useColumns(
    columns: readonly SchemaColumnResponse[] | undefined,
  ): UseColumn[] {
    return useWithStore(
      this.columnsStore,
      (source) => source.name,
      (source) => new UseColumn(source, this.name),
      (target, source) => target.update(source),
      columns ?? [],
    );
  }
}

export class UseColumn {
  private api: SchemaColumnResponse;
  readonly tableName: string;
  readonly name: string;
  readonly existence: UseExistence;
  readonly type: UseComparison;
  readonly constraints: readonly UseConstraint[];
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;
  readonly renameFrom: (table: UseTable) => string[];
  readonly renameTo: (table: UseTable) => string[];
  readonly fixable: SchemaFixable;

  expanded: boolean = $state(false);

  constructor(
    apiParameterForAssigmentOnly: SchemaColumnResponse,
    tableName: string,
  ) {
    this.api = $state(apiParameterForAssigmentOnly);
    this.tableName = tableName;
    const name = this.api.name;
    this.name = name;
    this.existence = $derived(columnExistence(this.api));
    this.type = $derived({
      name: "type",
      expected: this.api.type,
      actual: this.api.error?.type,
      actualRaw: undefined,
      shortener: (s) => s,
      color: this.api.error?.type ? "red" : undefined,
    });
    this.constraints = $derived(
      (this.api.constraints ?? []).map((i) =>
        useConstraint(i, tableName, name),
      ),
    );
    this.remainingErrors = $derived(useRemainder(this.api.error?.remainder));
    this.bulletColor = $derived(
      worst([
        this.existence?.color,
        this.type?.color,
        remainderColor(this.api.error),
        worst(this.constraints.map((i) => i.bulletColor)),
      ]),
    );
    this.renameFrom = (table) => {
      if (!this.existence || this.existence.text !== "missing") return [];
      return table
        .columns()
        .filter((c) => c.existence && c.existence.text === "unused")
        .map((c) => c.name);
    };
    this.renameTo = (table) => {
      if (!this.existence || this.existence.text !== "unused") return [];
      return table
        .columns()
        .filter((c) => c.existence && c.existence.text === "missing")
        .map((c) => c.name);
    };
    this.fixable = {
      subject: "column",
      tableName,
      name,
    };
  }

  update(api: SchemaColumnResponse) {
    if (this.name !== api.name) throw new Error(this.name);

    this.api = api;
  }
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
  readonly tableName: string;
  readonly name: string;
  readonly nameShort: () => string;
  readonly existence: UseExistence;
  readonly type: UseConstraintType;
  readonly clause: UseComparison | undefined;
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;
  readonly fixable: SchemaFixable;
};

type UseConstraintType = "pk" | "fk" | "unique" | "check";

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
    tableName,
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
    fixable: {
      subject: "constraint",
      tableName,
      name,
    },
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
      return "unique";
    case "Check":
      return "check";
  }
}

export type UseSequence = {
  readonly name: string;
  readonly existence: UseExistence;
  readonly type: UseComparison;
  readonly start: UseComparison;
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;
  readonly fixable: SchemaFixable;
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
    fixable: {
      subject: "sequence",
      tableName: undefined,
      name: api.name,
    },
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
