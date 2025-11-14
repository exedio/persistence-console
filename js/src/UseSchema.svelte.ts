import type {
  SchemaColumn as ApiColumn,
  SchemaConstraint as ApiConstraint,
  SchemaRemainder as ApiRemainder,
  Schema as ApiSchema,
  SchemaSequence as ApiSequence,
  SchemaTable as ApiTable,
} from "@/api/types";
import type { Fix, Fixable, FixedFixable } from "@/SchemaFix";
import { useWithStore } from "@/utils";

export class Schema implements Bullet {
  private api: ApiSchema;
  private _tables: readonly Table[];
  private _sequences: readonly Sequence[];
  readonly bulletColor: Color;

  private readonly tablesStore = new Map<string, Table>();
  private readonly sequencesStore = new Map<string, Sequence>();

  constructor(apiParameterNotToBeUsedExceptForAssigment: ApiSchema) {
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

  tables(): readonly Table[] {
    return this._tables;
  }

  sequences(): readonly Sequence[] {
    return this._sequences;
  }

  update(api: ApiSchema) {
    this.api = api;
    this._tables = this.useTables(api.tables);
    this._sequences = this.useSequences(api.sequences);
  }

  private useTables(tables: readonly ApiTable[] | undefined): Table[] {
    return useWithStore(
      this.tablesStore,
      (source) => source.name,
      (source) => new Table(source),
      (target, source) => target.update(source),
      tables ?? [],
    );
  }

  private useSequences(
    sequences: readonly ApiSequence[] | undefined,
  ): Sequence[] {
    return useWithStore(
      this.sequencesStore,
      (source) => source.name,
      (source) => new Sequence(source),
      (target, source) => target.update(source),
      sequences ?? [],
    );
  }

  fixables(): FixedFixable[] {
    let result: FixedFixable[] = [];
    this._tables.forEach((i) => {
      i.addFixables(result);
    });
    this._sequences.forEach((i) => {
      pushFixed(result, i);
    });
    return result;
  }

  fixable(key: {
    subject: "table" | "column" | "constraint" | "sequence";
    tableName: string | undefined;
    name: string;
  }): Fixable {
    for (const tab of this._tables) {
      if (isFixable(tab, key)) return tab;
      for (const col of tab.columns()) {
        if (isFixable(col, key)) return col;
        for (const con of col.constraints())
          if (isFixable(con, key)) return con;
      }
      for (const con of tab.constraints()) {
        if (isFixable(con, key)) return con;
      }
    }
    for (const seq of this._sequences) {
      if (isFixable(seq, key)) return seq;
    }
    throw new Error(JSON.stringify(key));
  }

  dropRenames(key: {
    subject: "table" | "column" | "constraint" | "sequence";
    tableName: string | undefined;
    name: string;
  }): void {
    // constraints cannot be renamed
    if (key.subject === "constraint") throw new Error(JSON.stringify(key));

    for (const tab of this._tables) tab.dropRenames(key);
    for (const seq of this._sequences) dropRename(seq, key);
  }
}

export class Table implements ExpandableBullet, Fixable {
  private api: ApiTable;
  readonly subject = "table";
  readonly tableName = undefined;
  readonly name: string;
  readonly existence: Existence;
  private _columns: readonly Column[];
  private _constraints: readonly Constraint[];
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;

  private readonly columnsStore = new Map<string, Column>();
  private readonly constraintsStore = new Map<string, Constraint>();

  expanded: boolean = $state(false);
  fix: Fix | undefined = $state(undefined);

  constructor(apiParameterForAssigmentOnly: ApiTable) {
    this.api = $state(apiParameterForAssigmentOnly);
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
  }

  columns(): readonly Column[] {
    return this._columns;
  }

  constraints(): readonly Constraint[] {
    return this._constraints;
  }

  update(api: ApiTable) {
    if (this.name !== api.name) throw new Error(this.name);

    this.api = api;
    this._columns = this.useColumns(api.columns, this.constraintsStore);
    this._constraints = this.useConstraints(
      this.api.constraints,
      this.constraintsStore,
    );
  }

  private useColumns(
    columns: readonly ApiColumn[] | undefined,
    constraintsStore: Map<string, Constraint>,
  ): Column[] {
    return useWithStore(
      this.columnsStore,
      (source) => source.name,
      (source) => new Column(source, this.name, constraintsStore),
      (target, source) => target.update(source, constraintsStore),
      columns ?? [],
    );
  }

  private useConstraints(
    constraints: readonly ApiConstraint[] | undefined,
    constraintsStore: Map<string, Constraint>,
  ) {
    return useConstraints(constraintsStore, constraints, this.name, undefined);
  }

  renameFrom(schema: Schema): string[] {
    if (!this.existence || this.existence.text !== "missing") return [];
    return schema
      .tables()
      .filter((t) => t.existence && t.existence.text === "unused")
      .map((t) => t.name);
  }

  renameTo(schema: Schema): string[] {
    if (!this.existence || this.existence.text !== "unused") return [];
    return schema
      .tables()
      .filter((t) => t.existence && t.existence.text === "missing")
      .map((t) => t.name);
  }

  addFixables(result: FixedFixable[]) {
    pushFixed(result, this);
    this._columns.forEach((i) => {
      i.addFixables(result);
    });
    this._constraints.forEach((i) => {
      pushFixed(result, i);
    });
    return result;
  }

  dropRenames(key: {
    subject: "table" | "column" | "constraint" | "sequence";
    tableName: string | undefined;
    name: string;
  }): void {
    // constraints cannot be renamed
    if (key.subject === "constraint") throw new Error(JSON.stringify(key));

    dropRename(this, key);
    for (const col of this._columns) dropRename(col, key);
  }
}

export class Column implements ExpandableBullet, Fixable {
  private api: ApiColumn;
  readonly subject = "column";
  readonly tableName: string;
  readonly name: string;
  readonly existence: Existence;
  readonly type: Comparison;
  private _constraints: readonly Constraint[];
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;

  expanded: boolean = $state(false);
  fix: Fix | undefined = $state(undefined);

  constructor(
    apiParameterForAssigmentOnly: ApiColumn,
    tableName: string,
    constraintsStore: Map<string, Constraint>,
  ) {
    this.api = $state(apiParameterForAssigmentOnly);
    this.tableName = tableName;
    this.name = this.api.name;
    this.existence = $derived.by(() => {
      const error = this.api.error;
      if (!error || !error.existence) return undefined;

      if (error.existence === "unused" && error.toleratesInsertIfUnused)
        return { text: "unused", color: "yellow" };

      return { text: error.existence, color: "red" };
    });
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
  }

  constraints(): readonly Constraint[] {
    return this._constraints;
  }

  update(api: ApiColumn, constraintsStore: Map<string, Constraint>) {
    if (this.name !== api.name) throw new Error(this.name);

    this.api = api;
    this._constraints = this.useConstraints(
      this.api.constraints,
      constraintsStore,
    );
  }

  private useConstraints(
    constraints: readonly ApiConstraint[] | undefined,
    constraintsStore: Map<string, Constraint>,
  ) {
    return useConstraints(
      constraintsStore,
      constraints,
      this.tableName,
      this.name,
    );
  }

  renameFrom(table: Table): string[] {
    if (!this.existence || this.existence.text !== "missing") return [];
    return table
      .columns()
      .filter((c) => c.existence && c.existence.text === "unused")
      .map((c) => c.name);
  }

  renameTo(table: Table): string[] {
    if (!this.existence || this.existence.text !== "unused") return [];
    return table
      .columns()
      .filter((c) => c.existence && c.existence.text === "missing")
      .map((c) => c.name);
  }

  addFixables(result: FixedFixable[]) {
    pushFixed(result, this);
    this._constraints.forEach((i) => {
      pushFixed(result, i);
    });
    return result;
  }
}

function useConstraints(
  store: Map<string, Constraint>,
  constraints: readonly ApiConstraint[] | undefined,
  tableName: string,
  columnName: string | undefined,
): Constraint[] {
  return useWithStore(
    store,
    (source) => source.name,
    (source) => new Constraint(source, tableName, columnName),
    (target, source) => target.update(source),
    constraints ?? [],
  );
}

type ConstraintType = "pk" | "fk" | "unique" | "check";

export class Constraint implements Bullet, Fixable {
  private api: ApiConstraint;
  readonly subject = "constraint";
  readonly tableName: string;
  private readonly columnName: string | undefined;
  readonly name: string;
  readonly existence: Existence;
  readonly type: ConstraintType;
  readonly clause: Comparison | undefined;
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;

  fix: Fix | undefined = $state(undefined);

  constructor(
    apiParameterForAssigmentOnly: ApiConstraint,
    tableName: string,
    columnName: string | undefined,
  ) {
    this.api = $state(apiParameterForAssigmentOnly);
    this.tableName = tableName;
    this.columnName = columnName;
    this.name = this.api.name;
    this.existence = $derived.by(() => {
      const error = this.api.error;
      if (!error || !error.existence) return undefined;
      return { text: error.existence, color: "red" };
    });
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

    this.type = $derived(useConstraintType(this.api));
    this.remainingErrors = $derived(useRemainder(this.api.error?.remainder));
    this.bulletColor = $derived(
      worst([
        this.existence?.color,
        this.clause?.color,
        remainderColor(this.api.error),
      ]),
    );
  }

  update(api: ApiConstraint) {
    if (this.name !== api.name) throw new Error(this.name);

    this.api = api;
  }

  nameShort(): string {
    if (this.columnName) {
      const prefix = this.tableName + "_" + this.columnName + "_";
      if (this.name.startsWith(prefix))
        return "~" + this.name.substring(prefix.length);
    }
    const prefix = this.tableName + "_";
    if (this.name.startsWith(prefix))
      return "~" + this.name.substring(prefix.length);

    return this.name;
  }
}

function useConstraintType(api: ApiConstraint): ConstraintType {
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

export class Sequence implements Bullet, Fixable {
  private api: ApiSequence;
  readonly subject = "sequence";
  readonly tableName = undefined;
  readonly name: string;
  readonly existence: Existence;
  readonly type: Comparison;
  readonly start: Comparison;
  readonly remainingErrors: readonly string[];
  readonly bulletColor: Color;

  fix: Fix | undefined = $state(undefined);

  constructor(apiParameterForAssigmentOnly: ApiSequence) {
    this.api = $state(apiParameterForAssigmentOnly);
    this.name = this.api.name;
    this.existence = $derived.by(() => {
      const error = this.api.error;
      if (!error || !error.existence) return undefined;
      return error.existence === "missing"
        ? { text: error.existence, color: "red" }
        : { text: error.existence, color: "yellow" };
    });
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
  }

  update(api: ApiSequence) {
    if (this.name !== api.name) throw new Error(this.name);

    this.api = api;
  }
}

export function useRemainder(api: ApiRemainder): readonly string[] {
  return api ? api : [];
}

export type Existence =
  | {
      text: "missing" | "unused";
      color: "red" | "yellow";
    }
  | undefined;

export type Comparison = {
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
        readonly remainder: ApiRemainder;
      }
    | undefined,
): Color {
  return error?.remainder ? "red" : undefined;
}

export type Bullet = {
  readonly bulletColor: Color;
};

export type ExpandableBullet = Bullet & {
  expanded: boolean;
};

function pushFixed(result: FixedFixable[], fixable: Fixable) {
  if (fixable.fix) result.push(fixable as FixedFixable);
}

function isFixable(
  fixable: Fixable,
  key: {
    subject: "table" | "column" | "constraint" | "sequence";
    tableName: string | undefined;
    name: string;
  },
): boolean {
  return (
    fixable.subject === key.subject &&
    fixable.tableName === key.tableName &&
    fixable.name === key.name
  );
}

function dropRename(
  fixable: Fixable,
  key: {
    subject: "table" | "column" | "constraint" | "sequence";
    tableName: string | undefined;
    name: string;
  },
): void {
  if (
    fixable.subject === key.subject &&
    fixable.tableName === key.tableName &&
    fixable.fix?.method === "rename" &&
    fixable.fix?.value === key.name
  )
    fixable.fix = undefined;
}
