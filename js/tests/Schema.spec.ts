// noinspection SqlNoDataSourceInspection

import { mount } from "svelte";
import { default as Schema } from "@/Schema.svelte";
import {
  mockClipboardWriteText,
  mockFetch,
  request,
  responseFailure,
  responseSuccess,
} from "@t/mockApi";
import { flushPromises, formatHtml } from "@t/utils";
import { expect } from "vitest";
import type {
  SchemaAlterResponse,
  ConnectRequest,
  ConnectResponse,
  Schema as ApiSchema,
  SchemaPatchResponse,
  SchemaPatchRequest,
} from "@/api/types";

describe("Schema", () => {
  it("should render an empty schema", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccess({} satisfies ApiSchema));
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a minimal table", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [{ name: "myOnlyName" }],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [{ name: "myOnlyName" }],
        } satisfies ApiSchema),
      );
      (document.querySelectorAll(".reload").item(0) as HTMLElement).click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [{ name: "myOnlyNameReloaded" }],
        } satisfies ApiSchema),
      );
      (document.querySelectorAll(".reload").item(0) as HTMLElement).click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a normal table without constraints", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              columns: [{ name: "myColumn1Name", type: "myColumn1Type" }],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a normal table with constraints", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              columns: [
                {
                  name: "myColumn1Name",
                  type: "myColumn1Type",
                  constraints: [
                    {
                      name: "myColumn1Constraint1Name",
                      type: "PrimaryKey",
                    },
                    {
                      name: "myColumn1Constraint2Name",
                      type: "Check",
                      clause: "myColumn1Constraint2Clause",
                    },
                    {
                      name: "myTable1Name_myColumn1Name_ConstraintTildeNameColumn",
                      type: "PrimaryKey",
                    },
                    {
                      name: "myColumn1ConstraintArrowName",
                      type: "Check",
                      clause:
                        'myColumn1ConstraintArrowClause hsqldb:"myColumn1Name" mysql:`myColumn1Name` second hsqldb:"myColumn1Name" mysql:`myColumn1Name`',
                    },
                  ],
                },
              ],
              constraints: [
                {
                  name: "myTable1Constraint1Name",
                  type: "PrimaryKey",
                },
                {
                  name: "myTable1Name_constraintTildeNameTable",
                  type: "PrimaryKey",
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a missing table", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [{ name: "myTable1Name", existence: "missing" }],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const create = () => checkbox("create", 0);
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter("CREATE TABLE myTable1Name"),
      );
      create().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=table&name=myTable1Name&method=add",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    create().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a missing table and patch fails", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [{ name: "myTable1Name", existence: "missing" }],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();

    const create = () => checkbox("create", 0);
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseFailure("myError"));
      create().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=table&name=myTable1Name&method=add",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    create().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused table", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [{ name: "myTable1Name", existence: "unused" }],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const drop = () => checkbox("drop", 0);
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter("DROP TABLE myTable1Name"),
      );
      drop().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=table&name=myTable1Name&method=drop",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    drop().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused table to be renamed to a missing", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            { name: "myUnusedTableName", existence: "unused" },
            { name: "myMissingTableName", existence: "missing" },
            { name: "myMissingTableName2", existence: "missing" },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    const unused = () => select("rename to ...", 0);
    const missing1 = () => select("rename from ...", 0);
    const missing2 = () => select("rename from ...", 1);
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();
    expect(unused().value).toBe("<NONE>");
    expect(missing1().value).toBe("<NONE>");
    expect(missing2().value).toBe("<NONE>");

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          'ALTER TABLE "myUnusedTableName" RENAME TO "myMissingTableName"',
        ),
      );
      unused().value = "myMissingTableName";
      unused().dispatchEvent(new Event("input", { bubbles: true }));
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=table&name=myUnusedTableName&method=rename&value=myMissingTableName",
      );
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(await formatHtml(sql())).toMatchSnapshot();
    expect(unused().value).toBe("myMissingTableName");
    expect(missing1().value).toBe("myUnusedTableName");
    expect(missing2().value).toBe("<NONE>");

    unused().value = "<NONE>";
    unused().dispatchEvent(new Event("input", { bubbles: true }));
    await flushPromises();
    expect(sql()).toBeNull();
    expect(unused().value).toBe("<NONE>");
    expect(missing1().value).toBe("<NONE>");
    expect(missing2().value).toBe("<NONE>");
  });

  it("should render a missing table to be renamed to an unused", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            { name: "myMissingTableName", existence: "missing" },
            { name: "myUnusedTableName", existence: "unused" },
            { name: "myUnusedTableName2", existence: "unused" },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    const missing = () => select("rename from ...", 0);
    const unused1 = () => select("rename to ...", 0);
    const unused2 = () => select("rename to ...", 1);
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();
    expect(missing().value).toBe("<NONE>");
    expect(unused1().value).toBe("<NONE>");
    expect(unused2().value).toBe("<NONE>");

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          'ALTER TABLE "myUnusedTableName" RENAME TO "myMissingTableName"',
        ),
      );
      missing().value = "myUnusedTableName";
      missing().dispatchEvent(new Event("input", { bubbles: true }));
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=table&name=myUnusedTableName&method=rename&value=myMissingTableName",
      );
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(await formatHtml(sql())).toMatchSnapshot();
    expect(missing().value).toBe("myUnusedTableName");
    expect(unused1().value).toBe("myMissingTableName");
    expect(unused2().value).toBe("<NONE>");

    missing().value = "<NONE>";
    missing().dispatchEvent(new Event("input", { bubbles: true }));
    await flushPromises();
    expect(sql()).toBeNull();
    expect(missing().value).toBe("<NONE>");
    expect(unused1().value).toBe("<NONE>");
    expect(unused2().value).toBe("<NONE>");
  });

  it("should render a table with an additional error", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              additionalErrors: ["additionalErrors1", "additionalErrors2"],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a missing column", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              columns: [
                {
                  name: "myColumn1Name",
                  type: "myColumn1Type",
                  existence: "missing",
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const create = () => checkbox("add", 0);
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          "ALTER TABLE myTable1Name ADD COLUMN myColumn1Name",
        ),
      );
      create().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=column&table=myTable1Name&name=myColumn1Name&method=add",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    create().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused column", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              columns: [
                {
                  name: "myColumn1Name",
                  type: "myColumn1Type",
                  existence: "unused",
                  toleratesInsertIfUnused: true,
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const drop = () => checkbox("drop", 0);
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          "ALTER TABLE myTable1Name DROP COLUMN myColumn1Name",
        ),
      );
      drop().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=column&table=myTable1Name&name=myColumn1Name&method=drop",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    drop().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused NOT NULL column", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              columns: [
                {
                  name: "myColumn1Name",
                  type: "myColumn1Type",
                  existence: "unused",
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a unused column to be renamed to a missing", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTableName",
              columns: [
                {
                  name: "myUnusedColumnName",
                  type: "myType",
                  existence: "unused",
                },
                {
                  name: "myMissingColumnName",
                  type: "myType",
                  existence: "missing",
                },
                {
                  name: "myMissingColumnName2",
                  type: "myType",
                  existence: "missing",
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    const unused = () => select("rename to ...", 0);
    const missing1 = () => select("rename from ...", 0);
    const missing2 = () => select("rename from ...", 1);
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();
    expect(unused().value).toBe("<NONE>");
    expect(missing1().value).toBe("<NONE>");
    expect(missing2().value).toBe("<NONE>");

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          'ALTER TABLE "myTableName" ALTER COLUMN "myUnusedColumnName" RENAME TO "myMissingColumnName"',
        ),
      );
      unused().value = "myMissingColumnName";
      unused().dispatchEvent(new Event("input", { bubbles: true }));
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=column&table=myTableName&name=myUnusedColumnName&method=rename&value=myMissingColumnName",
      );
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(await formatHtml(sql())).toMatchSnapshot();
    expect(unused().value).toBe("myMissingColumnName");
    expect(missing1().value).toBe("myUnusedColumnName");
    expect(missing2().value).toBe("<NONE>");

    unused().value = "<NONE>";
    unused().dispatchEvent(new Event("input", { bubbles: true }));
    await flushPromises();
    expect(sql()).toBeNull();
    expect(unused().value).toBe("<NONE>");
    expect(missing1().value).toBe("<NONE>");
    expect(missing2().value).toBe("<NONE>");
  });

  it("should render a missing column to be renamed to a unused", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTableName",
              columns: [
                {
                  name: "myMissingColumnName",
                  type: "myType",
                  existence: "missing",
                },
                {
                  name: "myUnusedColumnName",
                  type: "myType",
                  existence: "unused",
                },
                {
                  name: "myUnusedColumnName2",
                  type: "myType",
                  existence: "unused",
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    const missing = () => select("rename from ...", 0);
    const unused1 = () => select("rename to ...", 0);
    const unused2 = () => select("rename to ...", 1);
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();
    expect(missing().value).toBe("<NONE>");
    expect(unused1().value).toBe("<NONE>");
    expect(unused2().value).toBe("<NONE>");

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          'ALTER TABLE "myTableName" ALTER COLUMN "myUnusedColumnName" RENAME TO "myMissingColumnName"',
        ),
      );
      missing().value = "myUnusedColumnName";
      missing().dispatchEvent(new Event("input", { bubbles: true }));
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=column&table=myTableName&name=myUnusedColumnName&method=rename&value=myMissingColumnName",
      );
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(await formatHtml(sql())).toMatchSnapshot();
    expect(missing().value).toBe("myUnusedColumnName");
    expect(unused1().value).toBe("myMissingColumnName");
    expect(unused2().value).toBe("<NONE>");

    missing().value = "<NONE>";
    missing().dispatchEvent(new Event("input", { bubbles: true }));
    await flushPromises();
    expect(sql()).toBeNull();
    expect(missing().value).toBe("<NONE>");
    expect(unused1().value).toBe("<NONE>");
    expect(unused2().value).toBe("<NONE>");
  });

  it("should render a column with a wrong type", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              columns: [
                {
                  name: "myColumn1Name",
                  type: "myColumn1Type",
                  mismatchingType: "myColumn1TypeX",
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const modify = () => checkbox("adjust", 0);
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          "ALTER TABLE myTable1Name ALTER myColumn1Name TYPE myColumn1Type",
        ),
      );
      modify().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=column&table=myTable1Name&name=myColumn1Name&method=modify",
      );
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(await formatHtml(sql())).toMatchSnapshot();

    modify().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a multiple columns with a wrong type", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              columns: [
                {
                  name: "myColumn1Name",
                  type: "myColumn1Type",
                  mismatchingType: "myColumn1TypeX",
                },
                {
                  name: "myColumn2Name",
                  type: "myColumn2Type",
                  mismatchingType: "myColumn2TypeX",
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }

    const modifyA = () => checkboxStart("adjust columns with type mismatch");
    const modify1 = () => checkbox("adjust", 0);
    const modify2 = () => checkbox("adjust", 1);
    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(false);
    expect(modifyA().indeterminate).toBe(false);
    expect(modify1().checked).toBe(false);
    expect(modify2().checked).toBe(false);

    {
      const mock = mockFetch();
      mock.mockResolvedValue(responseSuccessAlter("WHATEVER"));
      modify1().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledTimes(1);
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(true);
    expect(modifyA().indeterminate).toBe(true);
    expect(modify1().checked).toBe(true);
    expect(modify2().checked).toBe(false);

    {
      const mock = mockFetch();
      mock.mockResolvedValue(responseSuccessAlter("WHATEVER"));
      modify2().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledTimes(1);
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(true);
    expect(modifyA().indeterminate).toBe(false);
    expect(modify1().checked).toBe(true);
    expect(modify2().checked).toBe(true);

    {
      modifyA().click();
      await flushPromises();
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(false);
    expect(modifyA().indeterminate).toBe(false);
    expect(modify1().checked).toBe(false);
    expect(modify2().checked).toBe(false);

    {
      modifyA().click();
      await flushPromises();
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(true);
    expect(modifyA().indeterminate).toBe(false);
    expect(modify1().checked).toBe(true);
    expect(modify2().checked).toBe(true);

    {
      modify1().click();
      await flushPromises();
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(true);
    expect(modifyA().indeterminate).toBe(true);
    expect(modify1().checked).toBe(false);
    expect(modify2().checked).toBe(true);

    {
      modifyA().click();
      await flushPromises();
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(false);
    expect(modifyA().indeterminate).toBe(false);
    expect(modify1().checked).toBe(false);
    expect(modify2().checked).toBe(false);
  });

  it("should render a column with an additional error", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              columns: [
                {
                  name: "myColumn1Name",
                  type: "myColumn1Type",
                  additionalErrors: ["additionalErrors1", "additionalErrors2"],
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a missing constraint", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              constraints: [
                {
                  name: "myConstraint1Name",
                  type: "Check",
                  clause: "myConstraint1Clause",
                  existence: "missing",
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const create = () => checkbox("add", 0);
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          "ALTER TABLE myTable1Name ADD CONSTRAINT myConstraint1Name",
        ),
      );
      create().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=constraint&table=myTable1Name&name=myConstraint1Name&method=add",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    create().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused constraint", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              constraints: [
                {
                  name: "myConstraint1Name",
                  type: "Check",
                  clause: "myConstraint1Clause",
                  existence: "unused",
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const drop = () => checkbox("drop", 0);
    {
      const mockFix = mockFetch();
      mockFix.mockResolvedValueOnce(
        responseSuccessAlter(
          "ALTER TABLE myTable1Name DROP CONSTRAINT myConstraint1Name",
        ),
      );
      drop().click();
      await flushPromises();
      expect(mockFix).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=constraint&table=myTable1Name&name=myConstraint1Name&method=drop",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    drop().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a constraint with a wrong clause", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              constraints: [
                {
                  name: "myConstraint1Name",
                  type: "Check",
                  clause: "myConstraint1Clause",
                  mismatchingClause: "myConstraint1ClauseX",
                  mismatchingClauseRaw: "myConstraint1ClauseX Raw",
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const modify = () => checkbox("recreate", 0);
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          "ALTER TABLE myTable1Name DROP CONSTRAINT myConstraint1Name",
        ),
      );
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          "ALTER TABLE myTable1Name ADD CONSTRAINT myConstraint1Name",
        ),
      );
      modify().click();
      await flushPromises();
      expect(mock).toHaveBeenNthCalledWith(
        1,
        "/myApiPath/schema/alter?subject=constraint&table=myTable1Name&name=myConstraint1Name&method=drop",
      );
      expect(mock).toHaveBeenNthCalledWith(
        2,
        "/myApiPath/schema/alter?subject=constraint&table=myTable1Name&name=myConstraint1Name&method=add",
      );
    }
    checkbox("join ALTER TABLE statements on the same table", 0).click();
    await flushPromises();

    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(await formatHtml(sql())).toMatchSnapshot();

    modify().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a multiple constraints with a wrong clause", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              constraints: [
                {
                  name: "myConstraint1Name",
                  type: "Check",
                  clause: "myConstraint1Clause",
                  mismatchingClause: "myConstraint1ClauseX",
                },
                {
                  name: "myConstraint2Name",
                  type: "Check",
                  clause: "myConstraint2Clause",
                  mismatchingClause: "myConstraint2ClauseX",
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }

    const modifyA = () =>
      checkboxStart("recreate constraints with clause mismatch", 0);
    const modify1 = () => checkbox("recreate", 0);
    const modify2 = () => checkbox("recreate", 1);
    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(false);
    expect(modifyA().indeterminate).toBe(false);
    expect(modify1().checked).toBe(false);
    expect(modify2().checked).toBe(false);

    {
      const mock = mockFetch();
      mock.mockResolvedValue(responseSuccessAlter("WHATEVER"));
      modify1().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledTimes(2);
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(true);
    expect(modifyA().indeterminate).toBe(true);
    expect(modify1().checked).toBe(true);
    expect(modify2().checked).toBe(false);

    {
      const mock = mockFetch();
      mock.mockResolvedValue(responseSuccessAlter("WHATEVER"));
      modify2().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledTimes(2);
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(true);
    expect(modifyA().indeterminate).toBe(false);
    expect(modify1().checked).toBe(true);
    expect(modify2().checked).toBe(true);

    {
      modifyA().click();
      await flushPromises();
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(false);
    expect(modifyA().indeterminate).toBe(false);
    expect(modify1().checked).toBe(false);
    expect(modify2().checked).toBe(false);

    {
      modifyA().click();
      await flushPromises();
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(true);
    expect(modifyA().indeterminate).toBe(false);
    expect(modify1().checked).toBe(true);
    expect(modify2().checked).toBe(true);

    {
      modify1().click();
      await flushPromises();
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(true);
    expect(modifyA().indeterminate).toBe(true);
    expect(modify1().checked).toBe(false);
    expect(modify2().checked).toBe(true);

    {
      modifyA().click();
      await flushPromises();
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(modifyA().checked).toBe(false);
    expect(modifyA().indeterminate).toBe(false);
    expect(modify1().checked).toBe(false);
    expect(modify2().checked).toBe(false);
  });

  it("should render a constraint with shortener", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              columns: [
                {
                  name: "myColumn1Name",
                  type: "myColumn1Type",
                  constraints: [
                    {
                      name: "myConstraint1Name",
                      type: "Check",
                      clause: "(`myColumn1Name`) myConstraint1Clause", // MySQL
                      mismatchingClause:
                        '("myColumn1Name") myConstraint1ClauseX', // hsqldb, PostgreSQL
                      mismatchingClauseRaw:
                        '("myColumn1Name") myConstraint1ClauseX Raw',
                    },
                  ],
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a constraint with an additional error", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              constraints: [
                {
                  name: "myConstraint1Name",
                  type: "Check",
                  clause: "myConstraint1Clause",
                  additionalErrors: ["additionalErrors1", "additionalErrors2"],
                },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a minimal sequence", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          sequences: [{ name: "myOnlyName", type: "myType", start: 55 }],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a missing sequence", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          sequences: [
            {
              name: "myOnlyName",
              type: "myType",
              start: 55,
              existence: "missing",
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const create = () => checkbox("create", 0);
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter("CREATE SEQUENCE myOnlyName"),
      );
      create().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=sequence&name=myOnlyName&method=add",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    create().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused sequence", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          sequences: [
            {
              name: "myOnlyName",
              type: "myType",
              start: 55,
              existence: "unused",
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const drop = () => checkbox("drop", 0);
    {
      const mockFix = mockFetch();
      mockFix.mockResolvedValueOnce(
        responseSuccessAlter("DROP SEQUENCE myOnlyName"),
      );
      drop().click();
      await flushPromises();
      expect(mockFix).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=sequence&name=myOnlyName&method=drop",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    drop().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused sequence to be renamed to a missing", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          sequences: [
            {
              name: "myUnusedSequenceName",
              type: "someType",
              start: 55,
              existence: "unused",
            },
            {
              name: "myMissingSequenceName",
              type: "someType",
              start: 55,
              existence: "missing",
            },
            {
              name: "myMissingSequenceName2",
              type: "someType",
              start: 55,
              existence: "missing",
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    const unused = () => select("rename to ...", 0);
    const missing1 = () => select("rename from ...", 0);
    const missing2 = () => select("rename from ...", 1);
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();
    expect(unused().value).toBe("<NONE>");
    expect(missing1().value).toBe("<NONE>");
    expect(missing2().value).toBe("<NONE>");

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          'ALTER TABLE "myUnusedSequenceName" RENAME TO "myMissingSequenceName"',
        ),
      );
      unused().value = "myMissingSequenceName";
      unused().dispatchEvent(new Event("input", { bubbles: true }));
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=sequence&name=myUnusedSequenceName&method=rename&value=myMissingSequenceName",
      );
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(await formatHtml(sql())).toMatchSnapshot();
    expect(unused().value).toBe("myMissingSequenceName");
    expect(missing1().value).toBe("myUnusedSequenceName");
    expect(missing2().value).toBe("<NONE>");

    unused().value = "<NONE>";
    unused().dispatchEvent(new Event("input", { bubbles: true }));
    await flushPromises();
    expect(sql()).toBeNull();
    expect(unused().value).toBe("<NONE>");
    expect(missing1().value).toBe("<NONE>");
    expect(missing2().value).toBe("<NONE>");
  });

  it("should render a missing sequence to be renamed to an unused", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          sequences: [
            {
              name: "myMissingSequenceName",
              type: "someType",
              start: 55,
              existence: "missing",
            },
            {
              name: "myUnusedSequenceName",
              type: "someType",
              start: 55,
              existence: "unused",
            },
            {
              name: "myUnusedSequenceName2",
              type: "someType",
              start: 55,
              existence: "unused",
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    const missing = () => select("rename from ...", 0);
    const unused1 = () => select("rename to ...", 0);
    const unused2 = () => select("rename to ...", 1);
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();
    expect(missing().value).toBe("<NONE>");
    expect(unused1().value).toBe("<NONE>");
    expect(unused2().value).toBe("<NONE>");

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          'ALTER TABLE "myUnusedSequenceName" RENAME TO "myMissingSequenceName"',
        ),
      );
      missing().value = "myUnusedSequenceName";
      missing().dispatchEvent(new Event("input", { bubbles: true }));
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=sequence&name=myUnusedSequenceName&method=rename&value=myMissingSequenceName",
      );
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(await formatHtml(sql())).toMatchSnapshot();
    expect(missing().value).toBe("myUnusedSequenceName");
    expect(unused1().value).toBe("myMissingSequenceName");
    expect(unused2().value).toBe("<NONE>");

    missing().value = "<NONE>";
    missing().dispatchEvent(new Event("input", { bubbles: true }));
    await flushPromises();
    expect(sql()).toBeNull();
    expect(missing().value).toBe("<NONE>");
    expect(unused1().value).toBe("<NONE>");
    expect(unused2().value).toBe("<NONE>");
  });

  it("should render a sequence with wrong type", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          sequences: [
            {
              name: "myOnlyName",
              type: "myType",
              start: 55,
              mismatchingType: "myTypeX",
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a sequence with wrong start", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          sequences: [
            {
              name: "myOnlyName",
              type: "myType",
              start: 55,
              mismatchingStart: 66,
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a sequence with an additional error", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          sequences: [
            {
              name: "myOnlyName",
              type: "myType",
              start: 55,
              additionalErrors: ["additionalErrors1", "additionalErrors2"],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should hide table / sequences without problems", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            { name: "tab1ok" },
            { name: "tab2ok" },
            { name: "tab3missing", existence: "missing" },
            { name: "tab4ok" },
            { name: "tab5ok" },
          ],
          sequences: [
            { name: "seq1ok", type: "someType", start: 55 },
            { name: "seq2ok", type: "someType", start: 55 },
            {
              name: "seq3missing",
              type: "someType",
              start: 55,
              existence: "unused",
            },
            { name: "seq4ok", type: "someType", start: 55 },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    const show = () => checkbox("show all tables / sequences", 0);
    show().click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render multiple missing nodes", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "tableExisting",
              columns: [
                {
                  name: "columnMissing",
                  type: "myType",
                  existence: "missing",
                  constraints: [
                    {
                      name: "columnConstraintMissing",
                      type: "Check",
                      clause: "myClause",
                      existence: "missing",
                    },
                  ],
                },
              ],
              constraints: [
                {
                  name: "tableConstraintMissing",
                  type: "Check",
                  clause: "myClause",
                  existence: "missing",
                },
              ],
            },
            { name: "tableMissing1", existence: "missing" },
          ],
          sequences: [
            {
              name: "sequenceMissing",
              type: "someType",
              start: 55,
              existence: "missing",
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }

    const all = () =>
      checkboxStart("add missing nodes without rename-from option", 0);
    const column = () => checkbox("add", 0);
    const columnConstraint = () => checkbox("add", 1);
    const tableConstraint = () => checkbox("add", 2);
    const table = () => checkbox("create", 0);
    const sequence = () => checkbox("create", 1);
    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(all().checked).toBe(false);
    expect(all().indeterminate).toBe(false);
    expect(column().checked).toBe(false);
    expect(columnConstraint().checked).toBe(false);
    expect(tableConstraint().checked).toBe(false);
    expect(table().checked).toBe(false);
    expect(sequence().checked).toBe(false);

    {
      const mock = mockFetch();
      mock.mockResolvedValue(responseSuccessAlter("WHATEVER"));
      column().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledTimes(1);
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(all().checked).toBe(true);
    expect(all().indeterminate).toBe(true);
    expect(column().checked).toBe(true);
    expect(columnConstraint().checked).toBe(false);
    expect(tableConstraint().checked).toBe(false);
    expect(table().checked).toBe(false);
    expect(sequence().checked).toBe(false);

    {
      const mock = mockFetch();
      mock.mockResolvedValue(responseSuccessAlter("WHATEVER"));
      columnConstraint().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledTimes(1);
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(all().checked).toBe(true);
    expect(all().indeterminate).toBe(true);
    expect(column().checked).toBe(true);
    expect(columnConstraint().checked).toBe(true);
    expect(tableConstraint().checked).toBe(false);
    expect(table().checked).toBe(false);
    expect(sequence().checked).toBe(false);

    {
      const mock = mockFetch();
      mock.mockResolvedValue(responseSuccessAlter("WHATEVER"));
      tableConstraint().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledTimes(1);
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(all().checked).toBe(true);
    expect(all().indeterminate).toBe(true);
    expect(column().checked).toBe(true);
    expect(columnConstraint().checked).toBe(true);
    expect(tableConstraint().checked).toBe(true);
    expect(table().checked).toBe(false);
    expect(sequence().checked).toBe(false);

    {
      const mock = mockFetch();
      mock.mockResolvedValue(responseSuccessAlter("WHATEVER"));
      table().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledTimes(1);
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(all().checked).toBe(true);
    expect(all().indeterminate).toBe(true);
    expect(column().checked).toBe(true);
    expect(columnConstraint().checked).toBe(true);
    expect(tableConstraint().checked).toBe(true);
    expect(table().checked).toBe(true);
    expect(sequence().checked).toBe(false);

    {
      const mock = mockFetch();
      mock.mockResolvedValue(responseSuccessAlter("WHATEVER"));
      sequence().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledTimes(1);
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(all().checked).toBe(true);
    expect(all().indeterminate).toBe(false);
    expect(column().checked).toBe(true);
    expect(columnConstraint().checked).toBe(true);
    expect(tableConstraint().checked).toBe(true);
    expect(table().checked).toBe(true);
    expect(sequence().checked).toBe(true);

    {
      all().click();
      await flushPromises();
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(all().checked).toBe(false);
    expect(all().indeterminate).toBe(false);
    expect(column().checked).toBe(false);
    expect(columnConstraint().checked).toBe(false);
    expect(tableConstraint().checked).toBe(false);
    expect(table().checked).toBe(false);
    expect(sequence().checked).toBe(false);

    {
      all().click();
      await flushPromises();
    }
    expect(await formatHtml(checkboxCollector())).toMatchSnapshot();
    expect(all().checked).toBe(true);
    expect(all().indeterminate).toBe(false);
    expect(column().checked).toBe(true);
    expect(columnConstraint().checked).toBe(true);
    expect(tableConstraint().checked).toBe(true);
    expect(table().checked).toBe(true);
    expect(sequence().checked).toBe(true);
  });

  it("should keep the state after reload", async () => {
    const response: ApiSchema = {
      tables: [
        {
          name: "ExpandedTable",
          columns: [
            {
              name: "expandedColumn",
              type: "expandedColumnType",
              constraints: [
                {
                  name: "expandedColumnConstraint",
                  type: "PrimaryKey",
                  existence: "missing",
                },
              ],
            },
            {
              name: "collapsedColumn",
              type: "collapsedColumnType",
              constraints: [
                { name: "collapsedColumnConstraint", type: "PrimaryKey" },
              ],
            },
          ],
          constraints: [
            { name: "expandedTableConstraintName", type: "PrimaryKey" },
          ],
        },
        {
          name: "CollapsedTable",
          columns: [
            {
              name: "columnInCollapsedTable",
              type: "columnInCollapsedTableType",
              constraints: [
                {
                  name: "constraintInCollapsedTableColumn",
                  type: "PrimaryKey",
                },
              ],
            },
          ],
          constraints: [
            { name: "constraintInCollapsedTable", type: "PrimaryKey" },
          ],
        },
      ],
      sequences: [
        {
          name: "SomeSequence",
          type: "someSequenceType",
          start: 55,
          existence: "missing",
        },
      ],
    };

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccess(response));
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }

    const addConstraint = () => checkbox("add", 0);
    const createSequenc = () => checkbox("create", 0);

    // expand table
    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();

    // expand column
    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();

    expect(addConstraint().checked).toBe(false);
    expect(createSequenc().checked).toBe(false);

    // check "add constraint"
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessAlter("SOME SQL"));
      addConstraint().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=constraint&table=ExpandedTable&name=expandedColumnConstraint&method=add",
      );
    }

    // check "create sequence"
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessAlter("SOME SQL"));
      createSequenc().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=sequence&name=SomeSequence&method=add",
      );
    }

    await expect(await formatHtml(tree())).toMatchFileSnapshot(
      "Schema-reload.output.html",
    );
    expect(addConstraint().checked).toBe(true);
    expect(createSequenc().checked).toBe(true);

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccess(response));
      (document.querySelectorAll(".reload").item(0) as HTMLElement).click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    await expect(await formatHtml(tree())).toMatchFileSnapshot(
      "Schema-reload.output.html",
    );
    expect(addConstraint().checked).toBe(true);
    expect(createSequenc().checked).toBe(true);

    (response.tables![0].columns![0].type as string) =
      "expandedColumnTypeChange";
    (response.tables![0].columns![0].constraints![0].clause as string) =
      "expandedColumnConstraintConditionChange";
    (response.tables![0].constraints![0].clause as string) =
      "expandedTableConstraintConditionChange";
    (response.sequences![0].type as string) = "someSequenceTypeChange";

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccess(response));
      (document.querySelectorAll(".reload").item(0) as HTMLElement).click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    await expect(await formatHtml(tree())).toMatchFileSnapshot(
      "Schema-reload-changed.output.html",
    );
    expect(addConstraint().checked).toBe(true);
    expect(createSequenc().checked).toBe(true);
  });

  it("should render an error message", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseFailure("myError"));
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a connect button", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseFailure("model not connected message"),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccess({} satisfies ConnectResponse));
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [{ name: "myTable1Name", existence: "missing" }],
        } satisfies ApiSchema),
      );
      (
        document.querySelectorAll(".tree button").item(0) as HTMLElement
      ).click();
      await flushPromises();
      expect(mock).toHaveBeenNthCalledWith(
        1,
        "/myApiPath/connect",
        request({} satisfies ConnectRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(2, "/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(sql()).toBeNull();

    const create = () => checkbox("create", 0);
    // test reactivity after connect
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter("CREATE TABLE myTable1Name"),
      );
      create().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=table&name=myTable1Name&method=add",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();
  });

  it("should patches encoded for java", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [{ name: "myTable1Name", existence: "missing" }],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledTimes(1);
    }
    expect(sql()).toBeNull();

    const create = () => checkbox("create", 0);
    const encode = () => checkbox("encoded for java", 0);
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter('CREATE TABLE "myTable1Name"'),
      );
      create().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledTimes(1);
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    encode().click();
    await flushPromises();
    expect(await formatHtml(sql())).toMatchSnapshot();
  });

  it("should join ALTER TABLE statements on the same table", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTableName",
              columns: [
                { name: "myColumn1Name", type: "string", existence: "missing" },
                { name: "myColumn2Name", type: "string", existence: "missing" },
                { name: "myColumn3Name", type: "string", existence: "missing" },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledTimes(1);
    }
    expect(sql()).toBeNull();

    const add1 = () => checkbox("add", 0);
    const add2 = () => checkbox("add", 1);
    const add3 = () => checkbox("add", 2);
    const encode = () =>
      checkbox("join ALTER TABLE statements on the same table", 0);
    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          'ALTER TABLE "myTableName" ADD COLUMN "myColumn1Name" string',
        ),
      );
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          'ALTER TABLE "myTableName" ADD COLUMN "myColumn2Name" string',
        ),
      );
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          'ALTER TABLE "myTableName" ADD COLUMN "myColumn3Name" string',
        ),
      );
      add1().click();
      add2().click();
      add3().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledTimes(3);
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    encode().click();
    await flushPromises();
    expect(await formatHtml(sql())).toMatchSnapshot();
  });

  it("should run patches", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            { name: "myTable1", existence: "missing" },
            { name: "myTable2", existence: "missing" },
            {
              name: "myTable3",
              columns: [
                { name: "myColumn1", type: "string", existence: "missing" },
                { name: "myColumn2", type: "string", existence: "missing" },
                { name: "myColumn3", type: "string", existence: "missing" },
              ],
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledTimes(1);
    }
    expect(sql(0)).toBeNull();
    expect(sql(1)).toBeNull();

    (document.querySelectorAll(".bullet").item(3) as HTMLElement).click();
    await flushPromises();

    const sql1 = 'CREATE TABLE "myTable1"';
    const sql2 = 'CREATE TABLE "myTable2"';
    const sqlAdd1 = 'ALTER TABLE "myTable3" ADD COLUMN "myColumn1" string';
    const sqlAdd2 = 'ALTER TABLE "myTable3" ADD COLUMN "myColumn2" string';
    const sqlAdd3 = 'ALTER TABLE "myTable3" ADD COLUMN "myColumn3" string';
    {
      const create1 = () => checkbox("create", 0);
      const create2 = () => checkbox("create", 1);
      const add1 = () => checkbox("add", 0);
      const add2 = () => checkbox("add", 1);
      const add3 = () => checkbox("add", 2);
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessAlter(sql1));
      mock.mockResolvedValueOnce(responseSuccessAlter(sql2));
      mock.mockResolvedValueOnce(responseSuccessAlter(sqlAdd1));
      mock.mockResolvedValueOnce(responseSuccessAlter(sqlAdd2));
      mock.mockResolvedValueOnce(responseSuccessAlter(sqlAdd3));
      create1().click();
      create2().click();
      add1().click();
      add2().click();
      add3().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledTimes(5);
    }
    expect(await formatHtml(sql())).toMatchSnapshot();
    expect(sql(1)).toBeNull();

    const run = () => button("RUN", 0);
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessPatch(11, 1111090909));
      mock.mockResolvedValueOnce(responseSuccessPatch(22, 2222090909));
      // TODO sqlAdd1-3 should be joined into one sql statement
      mock.mockResolvedValueOnce(responseSuccessPatch(33, 3333090909));
      mock.mockResolvedValueOnce(responseSuccessPatch(44, 4444090909));
      mock.mockResolvedValueOnce(responseSuccessPatch(55, 5555090909));
      run().click();
      await flushPromises();
      expect(mock).toHaveBeenNthCalledWith(
        1,
        "/myApiPath/schema/patch",
        requestPatch(sql1),
      );
      expect(mock).toHaveBeenNthCalledWith(
        2,
        "/myApiPath/schema/patch",
        requestPatch(sql2),
      );
      // TODO sqlAdd1-3 should be joined into one sql statement
      expect(mock).toHaveBeenNthCalledWith(
        3,
        "/myApiPath/schema/patch",
        requestPatch(sqlAdd1),
      );
      expect(mock).toHaveBeenNthCalledWith(
        4,
        "/myApiPath/schema/patch",
        requestPatch(sqlAdd2),
      );
      expect(mock).toHaveBeenNthCalledWith(
        5,
        "/myApiPath/schema/patch",
        requestPatch(sqlAdd3),
      );
      expect(mock).toHaveBeenCalledTimes(5);
    }
    expect(await formatHtml(sql(0))).toMatchSnapshot();
    expect(await formatHtml(sql(1))).toMatchSnapshot();

    const copy = () => button("copy", 0);
    {
      const mock = mockClipboardWriteText();
      mock.mockResolvedValueOnce();
      copy().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        // TODO should consider "encoded for java"
        'CREATE TABLE "myTable1"\n' +
          'CREATE TABLE "myTable2"\n' +
          // TODO sqlAdd1-3 should be joined into one sql statement
          'ALTER TABLE \"myTable3\" ADD COLUMN \"myColumn1\" string\n' +
          'ALTER TABLE \"myTable3\" ADD COLUMN \"myColumn2\" string\n' +
          'ALTER TABLE \"myTable3\" ADD COLUMN \"myColumn3\" string\n',
      );
    }

    const encode = () => checkbox("encoded for java", 0);
    encode().click();
    await flushPromises();
    expect(await formatHtml(sql(0))).toMatchSnapshot();
    expect(sql(1)).toBeTruthy();
    {
      const mock = mockClipboardWriteText();
      mock.mockResolvedValueOnce();
      copy().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        // TODO should consider "encoded for java"
        'CREATE TABLE "myTable1"\n' +
          'CREATE TABLE "myTable2"\n' +
          // TODO sqlAdd1-3 should be joined into one sql statement
          'ALTER TABLE \"myTable3\" ADD COLUMN \"myColumn1\" string\n' +
          'ALTER TABLE \"myTable3\" ADD COLUMN \"myColumn2\" string\n' +
          'ALTER TABLE \"myTable3\" ADD COLUMN \"myColumn3\" string\n',
      );
    }

    const flush = () => button("flush", 0);
    flush().click();
    await flushPromises();
    expect(sql(0)).toBeTruthy();
    expect(sql(1)).toBeNull(); // log disappeared, patches is at 0 again
  });

  it("should run patches with error", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            { name: "myTable1", existence: "missing" },
            { name: "myTable2", existence: "missing" },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledTimes(1);
    }
    expect(sql(0)).toBeNull();
    expect(sql(1)).toBeNull();

    const sql1 = 'CREATE TABLE "myTable1"';
    const sql2 = 'CREATE TABLE "myTable2"';
    {
      const create1 = () => checkbox("create", 0);
      const create2 = () => checkbox("create", 1);
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessAlter(sql1));
      mock.mockResolvedValueOnce(responseSuccessAlter(sql2));
      create1().click();
      create2().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledTimes(2);
    }
    expect(sql(0)).toBeTruthy();
    expect(sql(1)).toBeNull();

    const run = () => button("RUN", 0);
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseFailure('table "myTable1" already exists'),
      );
      run().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/patch",
        requestPatch(sql1),
      );
    }
    expect(await formatHtml(sql(0))).toMatchSnapshot();
    expect(sql(1)).toBeTruthy();

    const encode = () => checkbox("encoded for java", 0);
    encode().click();
    await flushPromises();
    expect(await formatHtml(sql(0))).toMatchSnapshot();
    expect(sql(1)).toBeTruthy();
  });
});

async function mountComponent() {
  mount(Schema, { target: document.body }); // `document` exists because of jsdom
  await flushPromises();
}

function tree(): HTMLElement {
  return document.querySelectorAll(".tree").item(0) as HTMLElement;
}

function checkboxCollector(): HTMLElement {
  return document.querySelectorAll(".checkboxCollector").item(0) as HTMLElement;
}

function responseSuccessAlter(sql: string) {
  return responseSuccess({
    sql,
  } satisfies SchemaAlterResponse);
}

function requestPatch(sql: string) {
  return request({
    sql,
  } satisfies SchemaPatchRequest);
}

function responseSuccessPatch(rows: number, elapsedNanos: number) {
  return responseSuccess({
    rows,
    elapsedNanos,
  } satisfies SchemaPatchResponse);
}

function checkbox(labelText: string, index: number): HTMLInputElement {
  return checkboxSelector((t) => t === labelText, index);
}

function checkboxStart(labelText: string, index: number = 0): HTMLInputElement {
  return checkboxSelector((t) => t.startsWith(labelText), index);
}

function checkboxSelector(
  labelText: (t: string) => boolean,
  index: number = 0,
): HTMLInputElement {
  return Array.from(document.querySelectorAll("label"))
    .filter(
      (label) =>
        label.textContent &&
        labelText(
          label.textContent.replaceAll("\n", " ").replace(/ +/g, " ").trim(),
        ),
    )
    .map(
      (label) =>
        label.querySelector('input[type="checkbox"]') as HTMLInputElement,
    )[index];
}

function select(firstOptionText: string, index: number = 0): HTMLSelectElement {
  return Array.from(document.querySelectorAll("select")).filter(
    (select) => select.options[0]?.textContent === firstOptionText,
  )[index] as HTMLSelectElement;
}

function button(text: string, index: number): HTMLButtonElement {
  return Array.from(document.querySelectorAll("button")).filter(
    (button) => button.textContent === text,
  )[index] as HTMLButtonElement;
}

function sql(index: number = 0): HTMLElement {
  const div = document.querySelectorAll(".sql").item(0);
  if (!div) return null as unknown as HTMLElement; // shall be null instead of undefined for historical reasons
  return div.querySelectorAll("ul").item(index) as HTMLElement;
}
