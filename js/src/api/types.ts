export type ConnectRequest = {};

export type ConnectResponse = {};

export function isNotConnected(error: any) {
  return error.message.toString().includes("model not connected");
}

export type Schema = {
  readonly tables?: readonly SchemaTable[];
  readonly sequences?: readonly SchemaSequence[];
};

export type SchemaTable = {
  readonly name: string;
  readonly columns?: readonly SchemaColumn[];
  readonly constraints?: readonly SchemaConstraint[];
  readonly existence?: SchemaExistence;
  readonly remainder?: SchemaRemainder;
};

export type SchemaColumn = {
  readonly name: string;
  readonly type: string;
  readonly constraints?: readonly SchemaConstraint[];
  readonly existence?: SchemaExistence;
  readonly toleratesInsertIfUnused?: true;
  readonly errorType?: string;
  readonly remainder?: SchemaRemainder;
};

export type SchemaConstraint = {
  readonly name: string;
  readonly type: "PrimaryKey" | "ForeignKey" | "Unique" | "Check";
  readonly clause?: string;
  readonly existence?: SchemaExistence;
  readonly errorClause?: string;
  readonly errorClauseRaw?: string;
  readonly remainder?: SchemaRemainder;
};

export type SchemaSequence = {
  readonly name: string;
  readonly type: string;
  readonly start: number;
  readonly existence?: SchemaExistence;
  readonly errorType?: string;
  readonly errorStart?: number;
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
