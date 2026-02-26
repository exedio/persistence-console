import { expect } from "vitest";
import { type FixedFixable, workOnFixes } from "@/SchemaFix";

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
          fix: { method: "drop", value: "myValue" },
        },
      ]),
    ).toStrictEqual([
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        fix: { method: "drop", value: "myValue" },
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
          fix: { method: "modify", value: "myValue" },
        },
      ]),
    ).toStrictEqual([
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        fix: { method: "drop" },
      },
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        fix: { method: "add" },
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
          fix: { method: "add", value: "myValue" },
        },
        {
          subject: "constraint",
          tableName: "myTableName",
          name: "myName",
          fix: { method: "drop", value: "myValue" },
        },
        {
          subject: "column",
          tableName: "myTableName",
          name: "myName",
          fix: { method: "add", value: "myValue" },
        },
        {
          subject: "column",
          tableName: "myTableName",
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
          fix: { method: "drop", value: "myValue" },
        },
        {
          subject: "table",
          name: "myName",
          fix: { method: "add", value: "myValue" },
        },
        {
          subject: "table",
          name: "myName",
          fix: { method: "drop", value: "myValue" },
        },
        {
          subject: "table",
          name: "myName",
          fix: { method: "rename", value: "myValue" },
        },
        {
          subject: "sequence",
          name: "myName",
          fix: { method: "add", value: "myValue" },
        },
        {
          subject: "sequence",
          name: "myName",
          fix: { method: "drop", value: "myValue" },
        },
        {
          subject: "sequence",
          name: "myName",
          fix: { method: "rename", value: "myValue" },
        },
        {
          subject: "sequence",
          name: "myName2",
          fix: { method: "drop", value: "myValue" },
        },
      ]),
    ).toStrictEqual([
      {
        subject: "constraint",
        tableName: "myTableName",
        name: "myName",
        fix: { method: "drop", value: "myValue" },
      },
      {
        subject: "column",
        tableName: "myTableName",
        name: "myName",
        fix: { method: "drop", value: "myValue" },
      },
      {
        subject: "table",
        name: "myName",
        fix: { method: "drop", value: "myValue" },
      },
      {
        subject: "sequence",
        name: "myName",
        fix: { method: "drop", value: "myValue" },
      },
      {
        subject: "sequence",
        name: "myName2",
        fix: { method: "drop", value: "myValue" },
      },
      {
        subject: "table",
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
        name: "myName",
        fix: { method: "rename", value: "myValue" },
      },
      {
        subject: "sequence",
        name: "myName",
        fix: { method: "add", value: "myValue" },
      },
      {
        subject: "table",
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
  it("should join columns", async () => {
    expect(
      workOnFixes(
        [
          columnDrop("myTableName", "myName1"),
          columnDrop("myTableName", "myName2"),
          columnDrop("myTableName", "myName3"),
          columnDrop("myTableName", "myName4"),
        ],
        true,
      ),
    ).toStrictEqual([
      columnDrop("myTableName", "myName1", "head"),
      columnDrop("myTableName", "myName2", "middle"),
      columnDrop("myTableName", "myName3", "middle"),
      columnDrop("myTableName", "myName4", "tail"),
    ]);
  });
  it("should join columns with padding", async () => {
    expect(
      workOnFixes(
        [
          columnDrop("myTableName1", "myName1", "middle"), // check whether joinable is deleted
          columnDrop("myTableNameJ", "myName2"),
          columnDrop("myTableNameJ", "myName3"),
          columnDrop("myTableName2", "myName4", "middle"), // check whether joinable is deleted
          columnDrop("myTableNameJ", "myName5"),
          columnDrop("myTableNameJ", "myName6"),
          columnDrop("myTableName3", "myName7", "middle"), // check whether joinable is deleted
        ],
        true,
      ),
    ).toStrictEqual([
      columnDrop("myTableName1", "myName1"),
      columnDrop("myTableNameJ", "myName2", "head"),
      columnDrop("myTableNameJ", "myName3", "tail"),
      columnDrop("myTableName2", "myName4"),
      columnDrop("myTableNameJ", "myName5", "head"),
      columnDrop("myTableNameJ", "myName6", "tail"),
      columnDrop("myTableName3", "myName7"),
    ]);
  });
  it("should not join columns when disabled", async () => {
    expect(
      workOnFixes(
        [
          columnDrop("myTableName", "myName1"),
          columnDrop("myTableName", "myName2", "middle"), // check whether joinable is deleted
          columnDrop("myTableName", "myName3"),
          columnDrop("myTableName", "myName4"),
        ],
        false,
      ),
    ).toStrictEqual([
      columnDrop("myTableName", "myName1"),
      columnDrop("myTableName", "myName2"),
      columnDrop("myTableName", "myName3"),
      columnDrop("myTableName", "myName4"),
    ]);
  });
  it("should join columns with different methods", async () => {
    expect(
      workOnFixes(
        [
          columnDrop("myTableName", "myName1"),
          columnAdd("myTableName", "myName2"),
          columnDrop("myTableName", "myName3"),
          columnAdd("myTableName", "myName4"),
        ],
        true,
      ),
    ).toStrictEqual([
      columnDrop("myTableName", "myName1", "head"),
      columnDrop("myTableName", "myName3", "middle"),
      columnAdd("myTableName", "myName2", "middle"),
      columnAdd("myTableName", "myName4", "tail"),
    ]);
  });
  it("should join constraints", async () => {
    expect(
      workOnFixes(
        [
          constraintDrop("myTableName", "myName1"),
          constraintDrop("myTableName", "myName2"),
          constraintDrop("myTableName", "myName3"),
        ],
        true,
      ),
    ).toStrictEqual([
      constraintDrop("myTableName", "myName1", "head"),
      constraintDrop("myTableName", "myName2", "middle"),
      constraintDrop("myTableName", "myName3", "tail"),
    ]);
  });
  it("should join columns mixed with constraints", async () => {
    expect(
      workOnFixes(
        [
          columnDrop("myTableName", "myName1"),
          constraintDrop("myTableName", "myName2"),
          columnDrop("myTableName", "myName3"),
          constraintDrop("myTableName", "myName4"),
        ],
        true,
      ),
    ).toStrictEqual([
      constraintDrop("myTableName", "myName2", "head"),
      constraintDrop("myTableName", "myName4", "middle"),
      columnDrop("myTableName", "myName1", "middle"),
      columnDrop("myTableName", "myName3", "tail"),
    ]);
  });
  it("should break join by table change", async () => {
    expect(
      workOnFixes(
        [
          columnDrop("myTableName1", "myName1"),
          columnDrop("myTableName1", "myName2"),
          columnDrop("myTableName2", "myName3"),
          columnDrop("myTableName2", "myName4"),
        ],
        true,
      ),
    ).toStrictEqual([
      columnDrop("myTableName1", "myName1", "head"),
      columnDrop("myTableName1", "myName2", "tail"),
      columnDrop("myTableName2", "myName3", "head"),
      columnDrop("myTableName2", "myName4", "tail"),
    ]);
  });
  it("should break join by other statement", async () => {
    expect(
      workOnFixes(
        [
          columnDrop("myTableName", "myName1"),
          columnAdd("myTableName", "myName2"),
          {
            subject: "table",
            name: "myTableName",
            fix: { method: "drop" },
            joinable: "middle", // check whether joinable is deleted
          },
          columnDrop("myTableName", "myName3"),
          columnAdd("myTableName", "myName4"),
        ],
        true,
      ),
    ).toStrictEqual([
      columnDrop("myTableName", "myName1", "head"),
      columnDrop("myTableName", "myName3", "tail"),
      {
        subject: "table",
        name: "myTableName",
        fix: { method: "drop" },
      },
      columnAdd("myTableName", "myName2", "head"),
      columnAdd("myTableName", "myName4", "tail"),
    ]);
  });
});

function columnAdd(
  tableName: string,
  name: string,
  joinable: "head" | "middle" | "tail" | undefined = undefined,
): FixedFixable {
  return {
    subject: "column",
    tableName,
    name,
    fix: { method: "add" },
    joinable,
  };
}
function columnDrop(
  tableName: string,
  name: string,
  joinable: "head" | "middle" | "tail" | undefined = undefined,
): FixedFixable {
  if (joinable)
    return {
      subject: "column",
      tableName,
      name,
      fix: { method: "drop" },
      joinable,
    };

  return {
    subject: "column",
    tableName,
    name,
    fix: { method: "drop" },
  };
}
function constraintDrop(
  tableName: string,
  name: string,
  joinable: "head" | "middle" | "tail" | undefined = undefined,
): FixedFixable {
  return {
    subject: "constraint",
    tableName,
    name,
    fix: { method: "drop" },
    joinable,
  };
}
