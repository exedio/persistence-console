import { expect } from "vitest";
import { workOnFixes } from "@/SchemaFix";

describe("Fix", () => {
  it("should pass through empty array", async () => {
    expect(workOnFixes([])).toStrictEqual([]);
  });
  it("should pass through normal fix", async () => {
    expect(
      workOnFixes([
        {
          subject: "constraint",
          tableName: "myTableName",
          name: "myName",
          method: "drop",
          value: "myValue",
        },
      ]),
    ).toStrictEqual([
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        method: "drop",
        value: "myValue",
      },
    ]);
  });
  it("should handle constraint modify", async () => {
    expect(
      workOnFixes([
        {
          subject: "constraint",
          tableName: "myTableName",
          name: "myName",
          method: "modify",
          value: "myValue",
        },
      ]),
    ).toStrictEqual([
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        method: "drop",
        value: "myValue",
      },
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        method: "add",
        value: "myValue",
      },
    ]);
  });
  it("should order", async () => {
    expect(
      workOnFixes([
        {
          subject: "constraint",
          tableName: "myTableName",
          name: "myName",
          method: "add",
          value: "myValue",
        },
        {
          subject: "constraint",
          tableName: "myTableName",
          name: "myName",
          method: "drop",
          value: "myValue",
        },
        {
          subject: "column",
          tableName: "myTableName",
          name: "myName",
          method: "add",
          value: "myValue",
        },
        {
          subject: "column",
          tableName: "myTableName",
          name: "myName",
          method: "modify",
          value: "myValue",
        },
        {
          subject: "column",
          tableName: "myTableName",
          name: "myName",
          method: "drop",
          value: "myValue",
        },
        {
          subject: "table",
          tableName: undefined,
          name: "myName",
          method: "add",
          value: "myValue",
        },
        {
          subject: "table",
          tableName: undefined,
          name: "myName",
          method: "drop",
          value: "myValue",
        },
        {
          subject: "table",
          tableName: undefined,
          name: "myName",
          method: "rename",
          value: "myValue",
        },
        {
          subject: "sequence",
          tableName: undefined,
          name: "myName",
          method: "add",
          value: "myValue",
        },
        {
          subject: "sequence",
          tableName: undefined,
          name: "myName",
          method: "drop",
          value: "myValue",
        },
        {
          subject: "sequence",
          tableName: undefined,
          name: "myName2",
          method: "drop",
          value: "myValue",
        },
      ]),
    ).toStrictEqual([
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        method: "drop",
        value: "myValue",
      },
      {
        subject: "column",
        tableName: "myTableName",
        name: "myName",
        method: "drop",
        value: "myValue",
      },
      {
        subject: "table",
        tableName: undefined,
        name: "myName",
        method: "drop",
        value: "myValue",
      },
      {
        subject: "sequence",
        tableName: undefined,
        name: "myName",
        method: "drop",
        value: "myValue",
      },
      {
        subject: "sequence",
        tableName: undefined,
        name: "myName2",
        method: "drop",
        value: "myValue",
      },
      {
        subject: "table",
        tableName: undefined,
        name: "myName",
        method: "rename",
        value: "myValue",
      },
      {
        subject: "column",
        tableName: "myTableName",
        name: "myName",
        method: "modify",
        value: "myValue",
      },
      {
        subject: "sequence",
        tableName: undefined,
        name: "myName",
        method: "add",
        value: "myValue",
      },
      {
        subject: "table",
        tableName: undefined,
        name: "myName",
        method: "add",
        value: "myValue",
      },
      {
        subject: "column",
        tableName: "myTableName",
        name: "myName",
        method: "add",
        value: "myValue",
      },
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        method: "add",
        value: "myValue",
      },
    ]);
  });
});
