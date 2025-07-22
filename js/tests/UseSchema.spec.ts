import { expect } from "vitest";
import { workOnFixes } from "@/SchemaFix";
import { useSchema } from "@/UseSchema";
import type {
  SchemaColumnResponse,
  SchemaConstraintResponse,
  SchemaExistence,
  SchemaRemainder,
  SchemaTableError,
} from "@/api/types";

describe("UseSchema", () => {
  it("table renameTo/From", async () => {
    const schema = useSchema({
      tables: [
        {
          name: "tableOk",
          columns: undefined,
          constraints: undefined,
          error: undefined,
        },
        {
          name: "tableExisting",
          columns: undefined,
          constraints: undefined,
          error: {
            existence: undefined,
            remainder: ["someRemainder"],
          },
        },
        {
          name: "tableMissing1",
          columns: undefined,
          constraints: undefined,
          error: {
            existence: "missing",
            remainder: [],
          },
        },
        {
          name: "tableMissing2",
          columns: undefined,
          constraints: undefined,
          error: {
            existence: "missing",
            remainder: [],
          },
        },
        {
          name: "tableUnused1",
          columns: undefined,
          constraints: undefined,
          error: {
            existence: "unused",
            remainder: [],
          },
        },
        {
          name: "tableUnused2",
          columns: undefined,
          constraints: undefined,
          error: {
            existence: "unused",
            remainder: [],
          },
        },
      ],
      sequences: [],
    });

    const missing1 = schema.tables.find((t) => t.name === "tableMissing1");
    const missing2 = schema.tables.find((t) => t.name === "tableMissing2");
    const unused1 = schema.tables.find((t) => t.name === "tableUnused1");
    const unused2 = schema.tables.find((t) => t.name === "tableUnused2");
    expect(missing1!.renameTo(schema)).toStrictEqual([]);
    expect(missing2!.renameTo(schema)).toStrictEqual([]);
    expect(unused1!.renameFrom(schema)).toStrictEqual([]);
    expect(unused2!.renameFrom(schema)).toStrictEqual([]);
    expect(unused1!.renameTo(schema)).toStrictEqual([
      "tableMissing1",
      "tableMissing2",
    ]);
    expect(unused2!.renameTo(schema)).toStrictEqual([
      "tableMissing1",
      "tableMissing2",
    ]);
    expect(missing1!.renameFrom(schema)).toStrictEqual([
      "tableUnused1",
      "tableUnused2",
    ]);
    expect(missing2!.renameFrom(schema)).toStrictEqual([
      "tableUnused1",
      "tableUnused2",
    ]);
  });
});
