import { expect } from "vitest";
import { useSchema } from "@/UseSchema";

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
  it("column renameTo/From", async () => {
    const schema = useSchema({
      tables: [
        {
          name: "myTable",
          columns: [
            {
              name: "columnExisting",
              type: "myType",
              constraints: undefined,
              error: undefined,
            },
            {
              name: "columnMissing1",
              type: "myType",
              constraints: undefined,
              error: {
                existence: "missing",
                type: undefined,
                remainder: undefined,
              },
            },
            {
              name: "columnMissing2",
              type: "myType",
              constraints: undefined,
              error: {
                existence: "missing",
                type: undefined,
                remainder: undefined,
              },
            },
            {
              name: "columnUnused1",
              type: "myType",
              constraints: undefined,
              error: {
                existence: "unused",
                type: undefined,
                remainder: undefined,
              },
            },
            {
              name: "columnUnused2",
              type: "myType",
              constraints: undefined,
              error: {
                existence: "unused",
                type: undefined,
                remainder: undefined,
              },
            },
          ],
          constraints: undefined,
          error: undefined,
        },
      ],
      sequences: [],
    });

    const table = schema.tables.find((t) => t.name === "myTable");
    const missing1 = table?.columns.find((t) => t.name === "columnMissing1");
    const missing2 = table?.columns.find((t) => t.name === "columnMissing2");
    const unused1 = table?.columns.find((t) => t.name === "columnUnused1");
    const unused2 = table?.columns.find((t) => t.name === "columnUnused2");
    expect(missing1!.renameTo(table!)).toStrictEqual([]);
    expect(missing2!.renameTo(table!)).toStrictEqual([]);
    expect(unused1!.renameFrom(table!)).toStrictEqual([]);
    expect(unused2!.renameFrom(table!)).toStrictEqual([]);
    expect(unused1!.renameTo(table!)).toStrictEqual([
      "columnMissing1",
      "columnMissing2",
    ]);
    expect(unused2!.renameTo(table!)).toStrictEqual([
      "columnMissing1",
      "columnMissing2",
    ]);
    expect(missing1!.renameFrom(table!)).toStrictEqual([
      "columnUnused1",
      "columnUnused2",
    ]);
    expect(missing2!.renameFrom(table!)).toStrictEqual([
      "columnUnused1",
      "columnUnused2",
    ]);
  });
});
