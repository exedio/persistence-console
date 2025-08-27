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
  private _sequences: readonly UseSequence[];
  readonly bulletColor: Color;

  private readonly tablesStore = new Map<string, UseTable>();
  private readonly sequencesStore = new Map<string, UseSequence>();

  constructor(apiParameterNotToBeUsedExceptForAssigment: SchemaResponse) {
    this.api = $state(apiParameterNotToBeUsedExceptForAssigment);
    this._tables = $state(this.useTables(this.api.tables));
    this._sequences = $state(this.useSequences(this.api.sequences));
    this.bulletColor = $derived(
      worse(
        worst(this._tables.map((i) => i.bulletColor)),
        worst(this._sequences.map((i) => i.bulletColor)),
      ),
    );
  }

  tables(): readonly UseTable[] {
    return this._tables;
  }

  sequences(): readonly UseSequence[] {
    return this._sequences;
  }

  update(api: SchemaResponse) {
    this.api = api;
    this._tables = this.useTables(api.tables);
    this._sequences = this.useSequences(api.sequences);
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

  private useSequences(
    sequences: readonly SchemaSequenceResponse[] | undefined,
  ): UseSequence[] {
    return useWithStore(
      this.sequencesStore,
      (source) => source.name,
      (source) => new UseSequence(source),
      (target, source) => target.update(source),
      sequences ?? [],
    );
  }
}

export class UseTable {
  private api: SchemaTableResponse;
  readonly name: string;
  readonly existence: UseExistence;
  private _columns: readonly UseColumn[];
  private _constraints: readonly UseConstraint[];
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;
  readonly fixable: SchemaFixable;

  private readonly columnsStore = new Map<string, UseColumn>();
  private readonly constraintsStore = new Map<string, UseConstraint>();

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
    this._columns = $state(
      this.useColumns(this.api.columns, this.constraintsStore),
    );
    this._constraints = $state(
      this.useConstraints(this.api.constraints, this.constraintsStore),
    );
    this.remainingErrors = $derived(useRemainder(this.api.error?.remainder));
    this.bulletColor = $derived(
      worst([
        this.existence?.color,
        remainderColor(this.api.error),
        worst(this._columns.map((i) => i.bulletColor)),
        worst(this._constraints.map((i) => i.bulletColor)),
      ]),
    );
    this.fixable = {
      subject: "table",
      tableName: undefined,
      name,
    };
  }

  columns(): readonly UseColumn[] {
    return this._columns;
  }

  constraints(): readonly UseConstraint[] {
    return this._constraints;
  }

  update(api: SchemaTableResponse) {
    if (this.name !== api.name) throw new Error(this.name);

    this.api = api;
    this._columns = this.useColumns(api.columns, this.constraintsStore);
    this._constraints = this.useConstraints(
      this.api.constraints,
      this.constraintsStore,
    );
  }

  private useColumns(
    columns: readonly SchemaColumnResponse[] | undefined,
    constraintsStore: Map<string, UseConstraint>,
  ): UseColumn[] {
    return useWithStore(
      this.columnsStore,
      (source) => source.name,
      (source) => new UseColumn(source, this.name, constraintsStore),
      (target, source) => target.update(source, constraintsStore),
      columns ?? [],
    );
  }

  private useConstraints(
    constraints: readonly SchemaConstraintResponse[] | undefined,
    constraintsStore: Map<string, UseConstraint>,
  ) {
    return useConstraints(constraintsStore, constraints, this.name, undefined);
  }

  renameFrom(schema: UseSchema): string[] {
    if (!this.existence || this.existence.text !== "missing") return [];
    return schema
      .tables()
      .filter((t) => t.existence && t.existence.text === "unused")
      .map((t) => t.name);
  }

  renameTo(schema: UseSchema): string[] {
    if (!this.existence || this.existence.text !== "unused") return [];
    return schema
      .tables()
      .filter((t) => t.existence && t.existence.text === "missing")
      .map((t) => t.name);
  }
}

export class UseColumn {
  private api: SchemaColumnResponse;
  readonly tableName: string;
  readonly name: string;
  readonly existence: UseExistence;
  readonly type: UseComparison;
  private _constraints: readonly UseConstraint[];
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;
  readonly fixable: SchemaFixable;

  expanded: boolean = $state(false);

  constructor(
    apiParameterForAssigmentOnly: SchemaColumnResponse,
    tableName: string,
    constraintsStore: Map<string, UseConstraint>,
  ) {
    this.api = $state(apiParameterForAssigmentOnly);
    this.tableName = tableName;
    this.name = this.api.name;
    this.existence = $derived(columnExistence(this.api));
    this.type = $derived({
      name: "type",
      expected: this.api.type,
      actual: this.api.error?.type,
      actualRaw: undefined,
      shortener: (s) => s,
      color: this.api.error?.type ? "red" : undefined,
    });
    this._constraints = $state(
      this.useConstraints(this.api.constraints, constraintsStore),
    );
    this.remainingErrors = $derived(useRemainder(this.api.error?.remainder));
    this.bulletColor = $derived(
      worst([
        this.existence?.color,
        this.type?.color,
        remainderColor(this.api.error),
        worst(this._constraints.map((i) => i.bulletColor)),
      ]),
    );
    this.fixable = {
      subject: "column",
      tableName,
      name: this.name,
    };
  }

  constraints(): readonly UseConstraint[] {
    return this._constraints;
  }

  update(
    api: SchemaColumnResponse,
    constraintsStore: Map<string, UseConstraint>,
  ) {
    if (this.name !== api.name) throw new Error(this.name);

    this.api = api;
    this._constraints = this.useConstraints(
      this.api.constraints,
      constraintsStore,
    );
  }

  private useConstraints(
    constraints: readonly SchemaConstraintResponse[] | undefined,
    constraintsStore: Map<string, UseConstraint>,
  ) {
    return useConstraints(
      constraintsStore,
      constraints,
      this.tableName,
      this.name,
    );
  }

  renameFrom(table: UseTable): string[] {
    if (!this.existence || this.existence.text !== "missing") return [];
    return table
      .columns()
      .filter((c) => c.existence && c.existence.text === "unused")
      .map((c) => c.name);
  }

  renameTo(table: UseTable): string[] {
    if (!this.existence || this.existence.text !== "unused") return [];
    return table
      .columns()
      .filter((c) => c.existence && c.existence.text === "missing")
      .map((c) => c.name);
  }
}

function useConstraints(
  store: Map<string, UseConstraint>,
  constraints: readonly SchemaConstraintResponse[] | undefined,
  tableName: string,
  columnName: string | undefined,
): UseConstraint[] {
  return useWithStore(
    store,
    (source) => source.name,
    (source) => new UseConstraint(source, tableName, columnName),
    (target, source) => target.update(source),
    constraints ?? [],
  );
}

function columnExistence(api: SchemaColumnResponse): UseExistence {
  const error = api.error;
  if (!error || !error.existence) return undefined;

  // TODO "not null" should be replaced by explicit API
  if (error.existence === "unused" && !api.type.endsWith(" not null"))
    return { text: "unused", color: "yellow" };

  return { text: error.existence, color: "red" };
}

type UseConstraintType = "pk" | "fk" | "unique" | "check";

export class UseConstraint {
  private api: SchemaConstraintResponse;
  readonly tableName: string;
  readonly name: string;
  readonly nameShort: () => string;
  readonly existence: UseExistence;
  readonly type: UseConstraintType;
  readonly clause: UseComparison | undefined;
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;
  readonly fixable: SchemaFixable;

  constructor(
    apiParameterForAssigmentOnly: SchemaConstraintResponse,
    tableName: string,
    columnName: string | undefined,
  ) {
    this.api = $state(apiParameterForAssigmentOnly);
    this.tableName = tableName;
    this.name = this.api.name;
    this.existence = $derived(constraintExistence(this.api));
    this.clause = $derived(
      this.api.clause
        ? {
            name: "clause",
            expected: this.api.clause,
            actual: this.api.error?.clause,
            actualRaw: this.api.error?.clauseRaw,
            shortener: (s) => {
              if (!columnName) return s;
              return s
                .replace('"' + columnName + '"', "⬁") // hsqldb, PostgreSQL
                .replace("`" + columnName + "`", "⬁"); // MySQL
            },
            color: this.api.error?.clause ? "red" : undefined,
          }
        : undefined,
    );

    this.nameShort = () => {
      if (columnName) {
        const prefix = tableName + "_" + columnName + "_";
        if (this.name.startsWith(prefix))
          return "~" + this.name.substring(prefix.length);
      }
      const prefix = tableName + "_";
      if (this.name.startsWith(prefix))
        return "~" + this.name.substring(prefix.length);

      return this.name;
    };
    this.type = $derived(useConstraintType(this.api));
    this.remainingErrors = $derived(useRemainder(this.api.error?.remainder));
    this.bulletColor = $derived(
      worst([
        this.existence?.color,
        this.clause?.color,
        remainderColor(this.api.error),
      ]),
    );
    this.fixable = {
      subject: "constraint",
      tableName,
      name: this.name,
    };
  }

  update(api: SchemaConstraintResponse) {
    if (this.name !== api.name) throw new Error(this.name);

    this.api = api;
  }
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

export class UseSequence {
  private api: SchemaSequenceResponse;
  readonly name: string;
  readonly existence: UseExistence;
  readonly type: UseComparison;
  readonly start: UseComparison;
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;
  readonly fixable: SchemaFixable;

  constructor(apiParameterForAssigmentOnly: SchemaSequenceResponse) {
    this.api = $state(apiParameterForAssigmentOnly);
    this.name = this.api.name;
    this.existence = $derived(sequenceExistence(this.api));
    this.type = $derived({
      name: "type",
      expected: this.api.type,
      actual: this.api.error?.type,
      actualRaw: undefined,
      shortener: (s) => s,
      color: this.api.error?.type ? "red" : undefined,
    });
    this.start = $derived({
      name: "start",
      expected: this.api.start.toString(),
      actual: this.api.error?.start?.toString(),
      actualRaw: undefined,
      shortener: (s) => s,
      color: this.api.error?.start ? "red" : undefined,
    });
    this.remainingErrors = $derived(useRemainder(this.api.error?.remainder));
    this.bulletColor = $derived(
      worst([
        this.existence?.color,
        this.type.color,
        this.start.color,
        remainderColor(this.api.error),
      ]),
    );
    this.fixable = {
      subject: "sequence",
      tableName: undefined,
      name: this.api.name,
    };
  }

  update(api: SchemaSequenceResponse) {
    if (this.name !== api.name) throw new Error(this.name);

    this.api = api;
  }
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
