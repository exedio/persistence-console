export type ConnectRequest = {};

export type ConnectResponse = {};

export function isNotConnected(error: any) {
  return error.message.toString().includes("model not connected");
}

export type Schema = {
  readonly tables: readonly SchemaTable[] | undefined;
  readonly sequences: readonly SchemaSequence[] | undefined;
};

export type SchemaTable = {
  readonly name: string;
  readonly columns: readonly SchemaColumn[] | undefined;
  readonly constraints: readonly SchemaConstraint[] | undefined;
  readonly error: SchemaTableError | undefined;
};

export type SchemaTableError = {
  readonly existence: SchemaExistence;
  readonly remainder: SchemaRemainder;
};

export type SchemaColumn = {
  readonly name: string;
  readonly type: string;
  readonly error: SchemaColumnError | undefined;
  readonly constraints: readonly SchemaConstraint[] | undefined;
};

export type SchemaColumnError = {
  readonly existence: SchemaExistence;
  readonly toleratesInsertIfUnused: true | undefined;
  readonly type: string | undefined;
  readonly remainder: SchemaRemainder;
};

export type SchemaConstraint = {
  readonly name: string;
  readonly type: "PrimaryKey" | "ForeignKey" | "Unique" | "Check";
  readonly clause: string | undefined;
  readonly error: SchemaConstraintError | undefined;
};

export type SchemaConstraintError = {
  readonly existence: SchemaExistence;
  readonly clause: string | undefined;
  readonly clauseRaw: string | undefined;
  readonly remainder: SchemaRemainder;
};

export type SchemaSequence = {
  readonly name: string;
  readonly type: string;
  readonly start: number;
  readonly error: SchemaSequenceError | undefined;
};

export type SchemaSequenceError = {
  readonly existence: SchemaExistence;
  readonly type: string | undefined;
  readonly start: number | undefined;
  readonly remainder: SchemaRemainder;
};

export type SchemaExistence = "missing" | "unused" | undefined;

export type SchemaRemainder = readonly string[] | undefined;

export type SchemaAlterResponse = {
  readonly sql: string;
};

export type HashID = {
  readonly type: string;
  readonly name: string;
};

export type Hash = HashID & {
  readonly plainTextLimit: number;
  readonly plainTextValidator: string | undefined;
  readonly algorithmID: string;
  readonly algorithmDescription: string;
};

export type HashRequest = HashID & {
  readonly plainText: string;
};

export type HashResponse = {
  readonly hash: string;
  readonly elapsedNanos: number;
};

export type Suspicion = {
  readonly type: string;
  readonly name: string;
  readonly suspicions: readonly string[];
};
