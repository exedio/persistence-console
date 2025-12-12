// noinspection SqlNoDataSourceInspection

import { mount } from "svelte";
import { default as Schema } from "@/Schema.svelte";
import {
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
} from "@/api/types";

describe("Schema", () => {
  it("should render an empty schema", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: undefined,
          sequences: undefined,
        } satisfies ApiSchema),
      );
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
          tables: [
            {
              name: "myOnlyName",
            },
          ],
          sequences: undefined,
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
          tables: [
            {
              name: "myOnlyName",
            },
          ],
          sequences: undefined,
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
          tables: [
            {
              name: "myOnlyNameReloaded",
            },
          ],
          sequences: undefined,
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
              columns: [
                {
                  name: "myColumn1Name",
                  type: "myColumn1Type",
                },
              ],
            },
          ],
          sequences: undefined,
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
                      clause: undefined,
                    },
                    {
                      name: "myColumn1Constraint2Name",
                      type: "Check",
                      clause: "myColumn1Constraint2Clause",
                    },
                    {
                      name: "myTable1Name_myColumn1Name_ConstraintTildeNameColumn",
                      type: "PrimaryKey",
                      clause: undefined,
                    },
                    {
                      name: "myColumn1ConstraintArrowName",
                      type: "Check",
                      clause:
                        'myColumn1ConstraintArrowClause hsqldb:"myColumn1Name" mysql:`myColumn1Name`',
                    },
                  ],
                },
              ],
              constraints: [
                {
                  name: "myTable1Constraint1Name",
                  type: "PrimaryKey",
                  clause: undefined,
                },
                {
                  name: "myTable1Name_constraintTildeNameTable",
                  type: "PrimaryKey",
                  clause: undefined,
                },
              ],
            },
          ],
          sequences: undefined,
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
          tables: [
            {
              name: "myTable1Name",
              error: {
                existence: "missing",
              },
            },
          ],
          sequences: undefined,
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

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter("CREATE TABLE myTable1Name"),
      );
      checkbox().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=table&name=myTable1Name&method=add",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a missing table and patch fails", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              error: {
                existence: "missing",
              },
            },
          ],
          sequences: undefined,
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseFailure("myError"));
      checkbox().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=table&name=myTable1Name&method=add",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused table", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              error: {
                existence: "unused",
              },
            },
          ],
          sequences: undefined,
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

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter("DROP TABLE myTable1Name"),
      );
      checkbox().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=table&name=myTable1Name&method=drop",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused table to be renamed to a missing", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myUnusedTableName",
              error: {
                existence: "unused",
              },
            },
            {
              name: "myMissingTableName",
              error: {
                existence: "missing",
              },
            },
            {
              name: "myMissingTableName2",
              error: {
                existence: "missing",
              },
            },
          ],
          sequences: undefined,
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();
    expect(select().value).toBe("<NONE>");
    expect(select(1).value).toBe("<NONE>");
    expect(select(2).value).toBe("<NONE>");

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          'ALTER TABLE "myUnusedTableName" RENAME TO "myMissingTableName"',
        ),
      );
      select().value = "myMissingTableName";
      select().dispatchEvent(new Event("input", { bubbles: true }));
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=table&name=myUnusedTableName&method=rename&value=myMissingTableName",
      );
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(await formatHtml(sql())).toMatchSnapshot();
    expect(select().value).toBe("myMissingTableName");
    expect(select(1).value).toBe("myUnusedTableName");
    expect(select(2).value).toBe("<NONE>");

    select().value = "<NONE>";
    select().dispatchEvent(new Event("input", { bubbles: true }));
    await flushPromises();
    expect(sql()).toBeNull();
    expect(select().value).toBe("<NONE>");
    expect(select(1).value).toBe("<NONE>");
    expect(select(2).value).toBe("<NONE>");
  });

  it("should render a missing table to be renamed to an unused", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myMissingTableName",
              error: {
                existence: "missing",
              },
            },
            {
              name: "myUnusedTableName",
              error: {
                existence: "unused",
              },
            },
            {
              name: "myUnusedTableName2",
              error: {
                existence: "unused",
              },
            },
          ],
          sequences: undefined,
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();
    expect(select().value).toBe("<NONE>");
    expect(select(1).value).toBe("<NONE>");
    expect(select(2).value).toBe("<NONE>");

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          'ALTER TABLE "myUnusedTableName" RENAME TO "myMissingTableName"',
        ),
      );
      select().value = "myUnusedTableName";
      select().dispatchEvent(new Event("input", { bubbles: true }));
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=table&name=myUnusedTableName&method=rename&value=myMissingTableName",
      );
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(await formatHtml(sql())).toMatchSnapshot();
    expect(select().value).toBe("myUnusedTableName");
    expect(select(1).value).toBe("myMissingTableName");
    expect(select(2).value).toBe("<NONE>");

    select().value = "<NONE>";
    select().dispatchEvent(new Event("input", { bubbles: true }));
    await flushPromises();
    expect(sql()).toBeNull();
    expect(select().value).toBe("<NONE>");
    expect(select(1).value).toBe("<NONE>");
    expect(select(2).value).toBe("<NONE>");
  });

  it("should render a table with a remaining error", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: [
            {
              name: "myTable1Name",
              error: {
                remainder: ["remainder1", "remainder2"],
              },
            },
          ],
          sequences: undefined,
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
                  error: {
                    existence: "missing",
                  },
                },
              ],
            },
          ],
          sequences: undefined,
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

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          "ALTER TABLE myTable1Name ADD COLUMN myColumn1Name",
        ),
      );
      checkbox().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=column&table=myTable1Name&name=myColumn1Name&method=add",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
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
                  error: {
                    existence: "unused",
                    toleratesInsertIfUnused: true,
                  },
                },
              ],
            },
          ],
          sequences: undefined,
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

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          "ALTER TABLE myTable1Name DROP COLUMN myColumn1Name",
        ),
      );
      checkbox().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=column&table=myTable1Name&name=myColumn1Name&method=drop",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
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
                  error: {
                    existence: "unused",
                  },
                },
              ],
            },
          ],
          sequences: undefined,
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
                  error: {
                    existence: "unused",
                    toleratesInsertIfUnused: true,
                    remainder: [],
                  },
                  constraints: [],
                },
                {
                  name: "myMissingColumnName",
                  type: "myType",
                  error: {
                    existence: "missing",
                    remainder: [],
                  },
                  constraints: [],
                },
                {
                  name: "myMissingColumnName2",
                  type: "myType",
                  error: {
                    existence: "missing",
                    remainder: [],
                  },
                  constraints: [],
                },
              ],
            },
          ],
          sequences: undefined,
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();
    expect(select().value).toBe("<NONE>");
    expect(select(1).value).toBe("<NONE>");
    expect(select(2).value).toBe("<NONE>");

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          'ALTER TABLE "myTableName" ALTER COLUMN "myUnusedColumnName" RENAME TO "myMissingColumnName"',
        ),
      );
      select().value = "myMissingColumnName";
      select().dispatchEvent(new Event("input", { bubbles: true }));
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=column&table=myTableName&name=myUnusedColumnName&method=rename&value=myMissingColumnName",
      );
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(await formatHtml(sql())).toMatchSnapshot();
    expect(select().value).toBe("myMissingColumnName");
    expect(select(1).value).toBe("myUnusedColumnName");
    expect(select(2).value).toBe("<NONE>");

    select().value = "<NONE>";
    select().dispatchEvent(new Event("input", { bubbles: true }));
    await flushPromises();
    expect(sql()).toBeNull();
    expect(select().value).toBe("<NONE>");
    expect(select(1).value).toBe("<NONE>");
    expect(select(2).value).toBe("<NONE>");
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
                  error: {
                    existence: "missing",
                    remainder: [],
                  },
                  constraints: [],
                },
                {
                  name: "myUnusedColumnName",
                  type: "myType",
                  error: {
                    existence: "unused",
                    toleratesInsertIfUnused: true,
                    remainder: [],
                  },
                  constraints: [],
                },
                {
                  name: "myUnusedColumnName2",
                  type: "myType",
                  error: {
                    existence: "unused",
                    toleratesInsertIfUnused: true,
                    remainder: [],
                  },
                  constraints: [],
                },
              ],
            },
          ],
          sequences: undefined,
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();
    expect(select().value).toBe("<NONE>");
    expect(select(1).value).toBe("<NONE>");
    expect(select(2).value).toBe("<NONE>");

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          'ALTER TABLE "myTableName" ALTER COLUMN "myUnusedColumnName" RENAME TO "myMissingColumnName"',
        ),
      );
      select().value = "myUnusedColumnName";
      select().dispatchEvent(new Event("input", { bubbles: true }));
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=column&table=myTableName&name=myUnusedColumnName&method=rename&value=myMissingColumnName",
      );
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(await formatHtml(sql())).toMatchSnapshot();
    expect(select().value).toBe("myUnusedColumnName");
    expect(select(1).value).toBe("myMissingColumnName");
    expect(select(2).value).toBe("<NONE>");

    select().value = "<NONE>";
    select().dispatchEvent(new Event("input", { bubbles: true }));
    await flushPromises();
    expect(sql()).toBeNull();
    expect(select().value).toBe("<NONE>");
    expect(select(1).value).toBe("<NONE>");
    expect(select(2).value).toBe("<NONE>");
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
                  error: {
                    type: "myColumn1TypeX",
                  },
                },
              ],
            },
          ],
          sequences: undefined,
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

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          "ALTER TABLE myTable1Name ALTER myColumn1Name TYPE myColumn1Type",
        ),
      );
      checkbox().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=column&table=myTable1Name&name=myColumn1Name&method=modify",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a column with a remaining error", async () => {
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
                  error: {
                    remainder: ["remainder1", "remainder2"],
                  },
                },
              ],
            },
          ],
          sequences: undefined,
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
                  error: {
                    existence: "missing",
                  },
                },
              ],
            },
          ],
          sequences: undefined,
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

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter(
          "ALTER TABLE myTable1Name ADD CONSTRAINT myConstraint1Name",
        ),
      );
      checkbox().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=constraint&table=myTable1Name&name=myConstraint1Name&method=add",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
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
                  error: {
                    existence: "unused",
                  },
                },
              ],
            },
          ],
          sequences: undefined,
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

    {
      const mockFix = mockFetch();
      mockFix.mockResolvedValueOnce(
        responseSuccessAlter(
          "ALTER TABLE myTable1Name DROP CONSTRAINT myConstraint1Name",
        ),
      );
      checkbox().click();
      await flushPromises();
      expect(mockFix).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=constraint&table=myTable1Name&name=myConstraint1Name&method=drop",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
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
                  error: {
                    clause: "myConstraint1ClauseX",
                    clauseRaw: "myConstraint1ClauseX Raw",
                  },
                },
              ],
            },
          ],
          sequences: undefined,
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
      checkbox().click();
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
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
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
                      error: {
                        clause: '("myColumn1Name") myConstraint1ClauseX', // hsqldb, PostgreSQL
                        clauseRaw: '("myColumn1Name") myConstraint1ClauseX Raw',
                      },
                    },
                  ],
                },
              ],
            },
          ],
          sequences: undefined,
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

  it("should render a constraint with a remaining error", async () => {
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
                  error: {
                    remainder: ["remainder1", "remainder2"],
                  },
                },
              ],
            },
          ],
          sequences: undefined,
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
          tables: undefined,
          sequences: [
            {
              name: "myOnlyName",
              type: "myType",
              start: 55,
            },
          ],
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
          tables: undefined,
          sequences: [
            {
              name: "myOnlyName",
              type: "myType",
              start: 55,
              error: {
                existence: "missing",
              },
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter("CREATE SEQUENCE myOnlyName"),
      );
      checkbox().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=sequence&name=myOnlyName&method=add",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused sequence", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: undefined,
          sequences: [
            {
              name: "myOnlyName",
              type: "myType",
              start: 55,
              error: {
                existence: "unused",
              },
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    {
      const mockFix = mockFetch();
      mockFix.mockResolvedValueOnce(
        responseSuccessAlter("DROP SEQUENCE myOnlyName"),
      );
      checkbox().click();
      await flushPromises();
      expect(mockFix).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=sequence&name=myOnlyName&method=drop",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a sequence with wrong type", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: undefined,
          sequences: [
            {
              name: "myOnlyName",
              type: "myType",
              start: 55,
              error: {
                type: "myTypeX",
              },
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
          tables: undefined,
          sequences: [
            {
              name: "myOnlyName",
              type: "myType",
              start: 55,
              error: {
                start: 66,
              },
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a sequence with a remaining error", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          tables: undefined,
          sequences: [
            {
              name: "myOnlyName",
              type: "myType",
              start: 55,
              error: {
                remainder: ["remainder1", "remainder2"],
              },
            },
          ],
        } satisfies ApiSchema),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }
    expect(await formatHtml(tree())).toMatchSnapshot();
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
                  clause: undefined,
                  error: {
                    existence: "missing",
                  },
                },
              ],
            },
            {
              name: "collapsedColumn",
              type: "collapsedColumnType",
              constraints: [
                {
                  name: "collapsedColumnConstraint",
                  type: "PrimaryKey",
                  clause: undefined,
                },
              ],
            },
          ],
          constraints: [
            {
              name: "expandedTableConstraintName",
              type: "PrimaryKey",
              clause: undefined,
            },
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
                  clause: undefined,
                },
              ],
            },
          ],
          constraints: [
            {
              name: "constraintInCollapsedTable",
              type: "PrimaryKey",
              clause: undefined,
            },
          ],
        },
      ],
      sequences: [
        {
          name: "SomeSequence",
          type: "someSequenceType",
          start: 55,
          error: {
            existence: "missing",
          },
        },
      ],
    };

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccess(response));
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    }

    // expand table
    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();

    // expand column
    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();

    expect(checkbox().checked).toBe(false); // add constraint
    expect(checkbox(1).checked).toBe(false); // create sequence

    // check "add constraint"
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessAlter("SOME SQL"));
      checkbox().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=constraint&table=ExpandedTable&name=expandedColumnConstraint&method=add",
      );
    }

    // check "create sequence"
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessAlter("SOME SQL"));
      checkbox(1).click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=sequence&name=SomeSequence&method=add",
      );
    }

    await expect(await formatHtml(tree())).toMatchFileSnapshot(
      "Schema-reload.output.html",
    );
    expect(checkbox().checked).toBe(true); // add constraint
    expect(checkbox(1).checked).toBe(true); // create sequence

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
    expect(checkbox().checked).toBe(true); // add constraint
    expect(checkbox(1).checked).toBe(true); // create sequence

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
    expect(checkbox().checked).toBe(true); // add constraint
    expect(checkbox(1).checked).toBe(true); // create sequence
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
          tables: [
            {
              name: "myTable1Name",
              error: {
                existence: "missing",
              },
            },
          ],
          sequences: undefined,
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

    // test reactivity after connect
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccessAlter("CREATE TABLE myTable1Name"),
      );
      checkbox().click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/alter?subject=table&name=myTable1Name&method=add",
      );
    }
    expect(await formatHtml(sql())).toMatchSnapshot();
  });
});

async function mountComponent() {
  mount(Schema, { target: document.body }); // `document` exists because of jsdom
  await flushPromises();
}

function tree(): HTMLElement {
  return document.querySelectorAll(".tree").item(0) as HTMLElement;
}

function responseSuccessAlter(sql: string) {
  return responseSuccess({
    sql: sql,
  } satisfies SchemaAlterResponse);
}

function checkbox(index: number = 0): HTMLInputElement {
  return document
    .querySelectorAll("input[type='checkbox']")
    .item(index) as HTMLInputElement;
}

function select(index: number = 0): HTMLSelectElement {
  return document.querySelectorAll("select").item(index) as HTMLSelectElement;
}

function sql(): HTMLElement {
  return document.querySelectorAll(".sql").item(0) as HTMLElement;
}
