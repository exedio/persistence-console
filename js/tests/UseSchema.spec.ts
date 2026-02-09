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
          additionalErrors: ["someAdditionalErrors"],
        },
        {
          name: "tableMissing1",
          existence: "missing",
        },
        {
          name: "tableMissing2",
          existence: "missing",
        },
        {
          name: "tableUnused1",
          existence: "unused",
        },
        {
          name: "tableUnused2",
          existence: "unused",
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
              existence: "missing",
            },
            {
              name: "columnMissing2",
              type: "myType",
              existence: "missing",
            },
            {
              name: "columnUnused1",
              type: "myType",
              existence: "unused",
            },
            {
              name: "columnUnused2",
              type: "myType",
              existence: "unused",
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

  it("sequence renameTo/From", async () => {
    const schema = new Schema({
      sequences: [
        {
          name: "sequenceOk",
          type: "someType",
          start: 55,
        },
        {
          name: "sequenceExisting",
          type: "someType",
          start: 55,
          additionalErrors: ["someAdditionalErrors"],
        },
        {
          name: "sequenceMissing1",
          type: "someType",
          start: 55,
          existence: "missing",
        },
        {
          name: "sequenceMissing2",
          type: "someType",
          start: 55,
          existence: "missing",
        },
        {
          name: "sequenceUnused1",
          type: "someType",
          start: 55,
          existence: "unused",
        },
        {
          name: "sequenceUnused2",
          type: "someType",
          start: 55,
          existence: "unused",
        },
      ],
    });

    const missing1 = schema
      .sequences()
      .find((t) => t.name === "sequenceMissing1");
    const missing2 = schema
      .sequences()
      .find((t) => t.name === "sequenceMissing2");
    const unused1 = schema
      .sequences()
      .find((t) => t.name === "sequenceUnused1");
    const unused2 = schema
      .sequences()
      .find((t) => t.name === "sequenceUnused2");
    expect(missing1!.renameTo(schema)).toStrictEqual([]);
    expect(missing2!.renameTo(schema)).toStrictEqual([]);
    expect(unused1!.renameFrom(schema)).toStrictEqual([]);
    expect(unused2!.renameFrom(schema)).toStrictEqual([]);
    expect(unused1!.renameTo(schema)).toStrictEqual([
      "sequenceMissing1",
      "sequenceMissing2",
    ]);
    expect(unused2!.renameTo(schema)).toStrictEqual([
      "sequenceMissing1",
      "sequenceMissing2",
    ]);
    expect(missing1!.renameFrom(schema)).toStrictEqual([
      "sequenceUnused1",
      "sequenceUnused2",
    ]);
    expect(missing2!.renameFrom(schema)).toStrictEqual([
      "sequenceUnused1",
      "sequenceUnused2",
    ]);
  });
});
