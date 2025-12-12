export type ConnectRequest = {};

export type ConnectResponse = {};

export function isNotConnected(error: any) {
  return error.message.toString().includes("model not connected");
}

export type Schema = {
  readonly tables?: readonly SchemaTable[];
  readonly sequences: readonly SchemaSequence[] | undefined;
};

export type SchemaTable = {
  readonly name: string;
  readonly columns?: readonly SchemaColumn[];
  readonly constraints?: readonly SchemaConstraint[];
  readonly error?: SchemaTableError;
};

export type SchemaTableError = {
  readonly existence?: SchemaExistence;
  readonly remainder?: SchemaRemainder;
};

export type SchemaColumn = {
  readonly name: string;
  readonly type: string;
  readonly error?: SchemaColumnError;
  readonly constraints?: readonly SchemaConstraint[];
};

export type SchemaColumnError = {
  readonly existence?: SchemaExistence;
  readonly toleratesInsertIfUnused?: true;
  readonly type?: string;
  readonly remainder?: SchemaRemainder;
};

export type SchemaConstraint = {
  readonly name: string;
  readonly type: "PrimaryKey" | "ForeignKey" | "Unique" | "Check";
  readonly clause?: string;
  readonly error?: SchemaConstraintError;
};

export type SchemaConstraintError = {
  readonly existence?: SchemaExistence;
  readonly clause?: string;
  readonly clauseRaw?: string;
  readonly remainder?: SchemaRemainder;
};

export type SchemaSequence = {
  readonly name: string;
  readonly type: string;
  readonly start: number;
  readonly error?: SchemaSequenceError;
};

export type SchemaSequenceError = {
  readonly existence?: SchemaExistence;
  readonly type?: string;
  readonly start?: number;
  readonly remainder?: SchemaRemainder;
};

export type SchemaExistence = "missing" | "unused";

export type SchemaRemainder = readonly string[];

export type SchemaAlterResponse = {
  readonly sql: string;
};

export type SchemaMaintainOperation = "create" | "tearDown" | "drop" | "delete";

export type SchemaMaintainRequest = {
  readonly operation: SchemaMaintainOperation;
};

export type SchemaMaintainResponse = {
  readonly elapsedNanos: number;
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
