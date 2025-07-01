import { expect } from "vitest";
import { workOnCheckboxes } from "@/SchemaCheckbox";

describe("Checkbox", () => {
  it("should pass through empty array", async () => {
    expect(workOnCheckboxes([])).toStrictEqual([]);
  });
  it("should pass through normal checkbox", async () => {
    expect(
      workOnCheckboxes([
        {
          subject: "constraint",
          tableName: "myTableName",
          name: "myName",
          method: "drop",
        },
      ]),
    ).toStrictEqual([
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        method: "drop",
      },
    ]);
  });
  it("should handle constraint modify", async () => {
    expect(
      workOnCheckboxes([
        {
          subject: "constraint",
          tableName: "myTableName",
          name: "myName",
          method: "modify",
        },
      ]),
    ).toStrictEqual([
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        method: "drop",
      },
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        method: "add",
      },
    ]);
  });
  it("should order", async () => {
    expect(
      workOnCheckboxes([
        {
          subject: "constraint",
          tableName: "myTableName",
          name: "myName",
          method: "add",
        },
        {
          subject: "constraint",
          tableName: "myTableName",
          name: "myName",
          method: "drop",
        },
        {
          subject: "column",
          tableName: "myTableName",
          name: "myName",
          method: "add",
        },
        {
          subject: "column",
          tableName: "myTableName",
          name: "myName",
          method: "modify",
        },
        {
          subject: "column",
          tableName: "myTableName",
          name: "myName",
          method: "drop",
        },
        {
          subject: "table",
          tableName: undefined,
          name: "myName",
          method: "add",
        },
        {
          subject: "table",
          tableName: undefined,
          name: "myName",
          method: "drop",
        },
        {
          subject: "sequence",
          tableName: undefined,
          name: "myName",
          method: "add",
        },
        {
          subject: "sequence",
          tableName: undefined,
          name: "myName",
          method: "drop",
        },
        {
          subject: "sequence",
          tableName: undefined,
          name: "myName2",
          method: "drop",
        },
      ]),
    ).toStrictEqual([
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        method: "drop",
      },
      {
        subject: "column",
        tableName: "myTableName",
        name: "myName",
        method: "drop",
      },
      {
        subject: "table",
        tableName: undefined,
        name: "myName",
        method: "drop",
      },
      {
        subject: "sequence",
        tableName: undefined,
        name: "myName",
        method: "drop",
      },
      {
        subject: "sequence",
        tableName: undefined,
        name: "myName2",
        method: "drop",
      },
      {
        subject: "column",
        tableName: "myTableName",
        name: "myName",
        method: "modify",
      },
      {
        subject: "sequence",
        tableName: undefined,
        name: "myName",
        method: "add",
      },
      {
        subject: "table",
        tableName: undefined,
        name: "myName",
        method: "add",
      },
      {
        subject: "column",
        tableName: "myTableName",
        name: "myName",
        method: "add",
      },
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        method: "add",
      },
    ]);
  });
});
