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
});
