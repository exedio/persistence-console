export type ConnectRequest = {};

export type ConnectResponse = {};

export function isNotConnected(error: any) {
  return error.message.toString().includes("model not connected");
}

export type SchemaResponse = {
  readonly tables: readonly SchemaTableResponse[] | undefined;
  readonly sequences: readonly SchemaSequenceResponse[] | undefined;
};

export type SchemaTableResponse = {
  readonly name: string;
  readonly columns: readonly SchemaColumnResponse[] | undefined;
  readonly constraints: readonly SchemaConstraintResponse[] | undefined;
  readonly error: SchemaTableError | undefined;
};

export type SchemaTableError = {
  readonly existence: SchemaExistence;
  readonly remainder: SchemaRemainder;
};

export type SchemaColumnResponse = {
  readonly name: string;
  readonly type: string;
  readonly error: SchemaColumnError | undefined;
  readonly constraints: readonly SchemaConstraintResponse[] | undefined;
};

export type SchemaColumnError = {
  readonly existence: SchemaExistence;
  readonly type: string | undefined;
  readonly remainder: SchemaRemainder;
};

export type SchemaConstraintResponse = {
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

export type SchemaSequenceResponse = {
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

export type AlterSchemaResponse = {
  readonly sql: string;
};

export type HashID = {
  readonly type: string;
  readonly name: string;
};

export type HashesResponse = HashID & {
  readonly plainTextLimit: number;
  readonly plainTextValidator: string | undefined;
  readonly algorithmID: string;
  readonly algorithmDescription: string;
};

export type DoHashRequest = HashID & {
  readonly plainText: string;
};

export type DoHashResponse = {
  readonly hash: string;
  readonly elapsedNanos: number;
};

export type SuspicionsResponse = {
  readonly type: string;
  readonly name: string;
  readonly suspicions: readonly string[];
};
