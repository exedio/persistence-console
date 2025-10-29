import { expect } from "vitest";
import { type Fix, workOnFixes } from "@/SchemaFix";

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
          fix: {
            method: "drop",
            value: "myValue",
          },
        },
      ]),
    ).toStrictEqual([
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        fix: {
          method: "drop",
          value: "myValue",
        },
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
          fix: {
            method: "modify",
            value: "myValue",
          },
        },
      ]),
    ).toStrictEqual([
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        fix: {
          method: "drop",
          value: undefined,
        },
      },
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        fix: {
          method: "add",
          value: undefined,
        },
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
          fix: {
            method: "add",
            value: "myValue",
          },
        },
        {
          subject: "constraint",
          tableName: "myTableName",
          name: "myName",
          fix: {
            method: "drop",
            value: "myValue",
          },
        },
        {
          subject: "column",
          tableName: "myTableName",
          name: "myName",
          fix: {
            method: "add",
            value: "myValue",
          },
        },
        {
          subject: "column",
          tableName: "myTableName",
          name: "myName",
          fix: {
            method: "rename",
            value: "myValue",
          },
        },
        {
          subject: "column",
          tableName: "myTableName",
          name: "myName",
          fix: {
            method: "modify",
            value: "myValue",
          },
        },
        {
          subject: "column",
          tableName: "myTableName",
          name: "myName",
          fix: {
            method: "drop",
            value: "myValue",
          },
        },
        {
          subject: "table",
          tableName: undefined,
          name: "myName",
          fix: {
            method: "add",
            value: "myValue",
          },
        },
        {
          subject: "table",
          tableName: undefined,
          name: "myName",
          fix: {
            method: "drop",
            value: "myValue",
          },
        },
        {
          subject: "table",
          tableName: undefined,
          name: "myName",
          fix: {
            method: "rename",
            value: "myValue",
          },
        },
        {
          subject: "sequence",
          tableName: undefined,
          name: "myName",
          fix: {
            method: "add",
            value: "myValue",
          },
        },
        {
          subject: "sequence",
          tableName: undefined,
          name: "myName",
          fix: {
            method: "drop",
            value: "myValue",
          },
        },
        {
          subject: "sequence",
          tableName: undefined,
          name: "myName2",
          fix: {
            method: "drop",
            value: "myValue",
          },
        },
      ]),
    ).toStrictEqual([
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        fix: {
          method: "drop",
          value: "myValue",
        },
      },
      {
        subject: "column",
        tableName: "myTableName",
        name: "myName",
        fix: {
          method: "drop",
          value: "myValue",
        },
      },
      {
        subject: "table",
        tableName: undefined,
        name: "myName",
        fix: {
          method: "drop",
          value: "myValue",
        },
      },
      {
        subject: "sequence",
        tableName: undefined,
        name: "myName",
        fix: { method: "drop", value: "myValue" },
      },
      {
        subject: "sequence",
        tableName: undefined,
        name: "myName2",
        fix: { method: "drop", value: "myValue" },
      },
      {
        subject: "table",
        tableName: undefined,
        name: "myName",
        fix: { method: "rename", value: "myValue" },
      },
      {
        subject: "column",
        tableName: "myTableName",
        name: "myName",
        fix: { method: "modify", value: "myValue" },
      },
      {
        subject: "column",
        tableName: "myTableName",
        name: "myName",
        fix: { method: "rename", value: "myValue" },
      },
      {
        subject: "sequence",
        tableName: undefined,
        name: "myName",
        fix: { method: "add", value: "myValue" },
      },
      {
        subject: "table",
        tableName: undefined,
        name: "myName",
        fix: { method: "add", value: "myValue" },
      },
      {
        subject: "column",
        tableName: "myTableName",
        name: "myName",
        fix: { method: "add", value: "myValue" },
      },
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        fix: { method: "add", value: "myValue" },
      },
    ]);
  });
});
