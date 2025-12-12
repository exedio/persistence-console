import { expect } from "vitest";
import { Schema } from "@/UseSchema.svelte";

describe("UseSchema", () => {
  it("table renameTo/From", async () => {
    const schema = new Schema({
      tables: [
        {
          name: "tableOk",
        },
        {
          name: "tableExisting",
          error: {
            remainder: ["someRemainder"],
          },
        },
        {
          name: "tableMissing1",
          error: {
            existence: "missing",
          },
        },
        {
          name: "tableMissing2",
          error: {
            existence: "missing",
          },
        },
        {
          name: "tableUnused1",
          error: {
            existence: "unused",
          },
        },
        {
          name: "tableUnused2",
          error: {
            existence: "unused",
          },
        },
      ],
    });

    const missing1 = schema.tables().find((t) => t.name === "tableMissing1");
    const missing2 = schema.tables().find((t) => t.name === "tableMissing2");
    const unused1 = schema.tables().find((t) => t.name === "tableUnused1");
    const unused2 = schema.tables().find((t) => t.name === "tableUnused2");
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
    const schema = new Schema({
      tables: [
        {
          name: "myTable",
          columns: [
            {
              name: "columnExisting",
              type: "myType",
            },
            {
              name: "columnMissing1",
              type: "myType",
              error: {
                existence: "missing",
              },
            },
            {
              name: "columnMissing2",
              type: "myType",
              error: {
                existence: "missing",
              },
            },
            {
              name: "columnUnused1",
              type: "myType",
              error: {
                existence: "unused",
              },
            },
            {
              name: "columnUnused2",
              type: "myType",
              error: {
                existence: "unused",
              },
            },
          ],
        },
      ],
    });

    const table = schema.tables().find((t) => t.name === "myTable");
    const missing1 = table?.columns().find((t) => t.name === "columnMissing1");
    const missing2 = table?.columns().find((t) => t.name === "columnMissing2");
    const unused1 = table?.columns().find((t) => t.name === "columnUnused1");
    const unused2 = table?.columns().find((t) => t.name === "columnUnused2");
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
