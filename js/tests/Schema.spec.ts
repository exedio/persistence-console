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
  AlterSchemaResponse,
  ConnectRequest,
  ConnectResponse,
  SchemaResponse,
} from "@/api/types";

describe("Schema", () => {
  it("should render an empty schema", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        tables: undefined,
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a minimal table", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        tables: [
          {
            name: "myOnlyName",
            columns: undefined,
            constraints: undefined,
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    const mockReloaded = mockFetch();
    mockReloaded.mockResolvedValueOnce(
      responseSuccess({
        tables: [
          {
            name: "myOnlyNameReloaded",
            columns: undefined,
            constraints: undefined,
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    (document.querySelectorAll(".reload").item(0) as HTMLElement).click();
    await flushPromises();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a normal table without constraints", async () => {
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
                error: undefined,
                constraints: undefined,
              },
            ],
            constraints: undefined,
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a normal table with constraints", async () => {
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
                error: undefined,
                constraints: [
                  {
                    name: "myColumn1Constraint1Name",
                    type: "PrimaryKey",
                    clause: undefined,
                    error: undefined,
                  },
                  {
                    name: "myColumn1Constraint2Name",
                    type: "Check",
                    clause: "myColumn1Constraint2Clause",
                    error: undefined,
                  },
                  {
                    name: "myTable1Name_myColumn1Name_ConstraintTildeNameColumn",
                    type: "PrimaryKey",
                    clause: undefined,
                    error: undefined,
                  },
                  {
                    name: "myColumn1ConstraintArrowName",
                    type: "Check",
                    clause:
                      'myColumn1ConstraintArrowClause hsqldb:"myColumn1Name" mysql:`myColumn1Name`',
                    error: undefined,
                  },
                ],
              },
            ],
            constraints: [
              {
                name: "myTable1Constraint1Name",
                type: "PrimaryKey",
                clause: undefined,
                error: undefined,
              },
              {
                name: "myTable1Name_constraintTildeNameTable",
                type: "PrimaryKey",
                clause: undefined,
                error: undefined,
              },
            ],
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a missing table", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        tables: [
          {
            name: "myTable1Name",
            columns: undefined,
            constraints: undefined,
            error: {
              existence: "missing",
              remainder: undefined,
            },
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const mockFix = mockFetch();
    mockFix.mockResolvedValueOnce(
      responseSuccessAlter("CREATE TABLE myTable1Name"),
    );
    checkbox().click();
    await flushPromises();
    expect(mockFix).toHaveBeenCalledExactlyOnceWith(
      "/myApiPath/alterSchema?subject=table&name=myTable1Name&method=add",
    );
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a missing table and patch fails", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        tables: [
          {
            name: "myTable1Name",
            columns: undefined,
            constraints: undefined,
            error: {
              existence: "missing",
              remainder: undefined,
            },
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();

    const mockFix = mockFetch();
    mockFix.mockResolvedValueOnce(responseFailure("myError"));
    checkbox().click();
    await flushPromises();
    expect(mockFix).toHaveBeenCalledExactlyOnceWith(
      "/myApiPath/alterSchema?subject=table&name=myTable1Name&method=add",
    );
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused table", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        tables: [
          {
            name: "myTable1Name",
            columns: undefined,
            constraints: undefined,
            error: {
              existence: "unused",
              remainder: undefined,
            },
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const mockFix = mockFetch();
    mockFix.mockResolvedValueOnce(
      responseSuccessAlter("DROP TABLE myTable1Name"),
    );
    checkbox().click();
    await flushPromises();
    expect(mockFix).toHaveBeenCalledExactlyOnceWith(
      "/myApiPath/alterSchema?subject=table&name=myTable1Name&method=drop",
    );
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused table to be renamed to a missing", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        tables: [
          {
            name: "myUnusedTableName",
            columns: undefined,
            constraints: undefined,
            error: {
              existence: "unused",
              remainder: undefined,
            },
          },
          {
            name: "myMissingTableName",
            columns: undefined,
            constraints: undefined,
            error: {
              existence: "missing",
              remainder: undefined,
            },
          },
          {
            name: "myMissingTableName2",
            columns: undefined,
            constraints: undefined,
            error: {
              existence: "missing",
              remainder: undefined,
            },
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const mockFix = mockFetch();
    mockFix.mockResolvedValueOnce(
      responseSuccessAlter(
        'ALTER TABLE "myUnusedTableName" RENAME TO "myMissingTableName"',
      ),
    );
    select().value = "myMissingTableName";
    select().dispatchEvent(new Event("input", { bubbles: true }));
    await flushPromises();
    expect(mockFix).toHaveBeenCalledExactlyOnceWith(
      "/myApiPath/alterSchema?subject=table&name=myUnusedTableName&method=rename&value=myMissingTableName",
    );
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(await formatHtml(sql())).toMatchSnapshot();

    select().value = "";
    select().dispatchEvent(new Event("input", { bubbles: true }));
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a table with a remaining error", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        tables: [
          {
            name: "myTable1Name",
            columns: undefined,
            constraints: undefined,
            error: {
              existence: undefined,
              remainder: ["remainder1", "remainder2"],
            },
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a missing column", async () => {
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
                  type: undefined,
                  remainder: undefined,
                },
                constraints: undefined,
              },
            ],
            constraints: undefined,
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const mockFix = mockFetch();
    mockFix.mockResolvedValueOnce(
      responseSuccessAlter("ALTER TABLE myTable1Name ADD COLUMN myColumn1Name"),
    );
    checkbox().click();
    await flushPromises();
    expect(mockFix).toHaveBeenCalledExactlyOnceWith(
      "/myApiPath/alterSchema?subject=column&table=myTable1Name&name=myColumn1Name&method=add",
    );
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused column", async () => {
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
                  type: undefined,
                  remainder: undefined,
                },
                constraints: undefined,
              },
            ],
            constraints: undefined,
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const mockFix = mockFetch();
    mockFix.mockResolvedValueOnce(
      responseSuccessAlter(
        "ALTER TABLE myTable1Name DROP COLUMN myColumn1Name",
      ),
    );
    checkbox().click();
    await flushPromises();
    expect(mockFix).toHaveBeenCalledExactlyOnceWith(
      "/myApiPath/alterSchema?subject=column&table=myTable1Name&name=myColumn1Name&method=drop",
    );
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused NOT NULL column", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        tables: [
          {
            name: "myTable1Name",
            columns: [
              {
                name: "myColumn1Name",
                type: "myColumn1Type not null",
                error: {
                  existence: "unused",
                  type: undefined,
                  remainder: undefined,
                },
                constraints: undefined,
              },
            ],
            constraints: undefined,
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a column with a wrong type", async () => {
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
                  existence: undefined,
                  type: "myColumn1TypeX",
                  remainder: undefined,
                },
                constraints: undefined,
              },
            ],
            constraints: undefined,
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const mockFix = mockFetch();
    mockFix.mockResolvedValueOnce(
      responseSuccessAlter(
        "ALTER TABLE myTable1Name ALTER myColumn1Name TYPE myColumn1Type",
      ),
    );
    checkbox().click();
    await flushPromises();
    expect(mockFix).toHaveBeenCalledExactlyOnceWith(
      "/myApiPath/alterSchema?subject=column&table=myTable1Name&name=myColumn1Name&method=modify",
    );
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a column with a remaining error", async () => {
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
                  existence: undefined,
                  type: undefined,
                  remainder: ["remainder1", "remainder2"],
                },
                constraints: undefined,
              },
            ],
            constraints: undefined,
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a missing constraint", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        tables: [
          {
            name: "myTable1Name",
            columns: undefined,
            constraints: [
              {
                name: "myConstraint1Name",
                type: "Check",
                clause: "myConstraint1Clause",
                error: {
                  existence: "missing",
                  clause: undefined,
                  clauseRaw: undefined,
                  remainder: undefined,
                },
              },
            ],
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const mockFix = mockFetch();
    mockFix.mockResolvedValueOnce(
      responseSuccessAlter(
        "ALTER TABLE myTable1Name ADD CONSTRAINT myConstraint1Name",
      ),
    );
    checkbox().click();
    await flushPromises();
    expect(mockFix).toHaveBeenCalledExactlyOnceWith(
      "/myApiPath/alterSchema?subject=constraint&table=myTable1Name&name=myConstraint1Name&method=add",
    );
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused constraint", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        tables: [
          {
            name: "myTable1Name",
            columns: undefined,
            constraints: [
              {
                name: "myConstraint1Name",
                type: "Check",
                clause: "myConstraint1Clause",
                error: {
                  existence: "unused",
                  clause: undefined,
                  clauseRaw: undefined,
                  remainder: undefined,
                },
              },
            ],
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const mockFix = mockFetch();
    mockFix.mockResolvedValueOnce(
      responseSuccessAlter(
        "ALTER TABLE myTable1Name DROP CONSTRAINT myConstraint1Name",
      ),
    );
    checkbox().click();
    await flushPromises();
    expect(mockFix).toHaveBeenCalledExactlyOnceWith(
      "/myApiPath/alterSchema?subject=constraint&table=myTable1Name&name=myConstraint1Name&method=drop",
    );
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a constraint with a wrong clause", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        tables: [
          {
            name: "myTable1Name",
            columns: undefined,
            constraints: [
              {
                name: "myConstraint1Name",
                type: "Check",
                clause: "myConstraint1Clause",
                error: {
                  existence: undefined,
                  clause: "myConstraint1ClauseX",
                  clauseRaw: "myConstraint1ClauseX Raw",
                  remainder: undefined,
                },
              },
            ],
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const mockFix = mockFetch();
    mockFix.mockResolvedValueOnce(
      responseSuccessAlter(
        "ALTER TABLE myTable1Name DROP CONSTRAINT myConstraint1Name",
      ),
    );
    mockFix.mockResolvedValueOnce(
      responseSuccessAlter(
        "ALTER TABLE myTable1Name ADD CONSTRAINT myConstraint1Name",
      ),
    );
    checkbox().click();
    await flushPromises();
    expect(mockFix).toHaveBeenNthCalledWith(
      1,
      "/myApiPath/alterSchema?subject=constraint&table=myTable1Name&name=myConstraint1Name&method=drop",
    );
    expect(mockFix).toHaveBeenNthCalledWith(
      2,
      "/myApiPath/alterSchema?subject=constraint&table=myTable1Name&name=myConstraint1Name&method=add",
    );
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a constraint with shortener", async () => {
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
                error: undefined,
                constraints: [
                  {
                    name: "myConstraint1Name",
                    type: "Check",
                    clause: "(`myColumn1Name`) myConstraint1Clause", // MySQL
                    error: {
                      existence: undefined,
                      clause: '("myColumn1Name") myConstraint1ClauseX', // hsqldb, PostgreSQL
                      clauseRaw: '("myColumn1Name") myConstraint1ClauseX Raw',
                      remainder: undefined,
                    },
                  },
                ],
              },
            ],
            constraints: undefined,
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a constraint with a remaining error", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        tables: [
          {
            name: "myTable1Name",
            columns: undefined,
            constraints: [
              {
                name: "myConstraint1Name",
                type: "Check",
                clause: "myConstraint1Clause",
                error: {
                  existence: undefined,
                  clause: undefined,
                  clauseRaw: undefined,
                  remainder: ["remainder1", "remainder2"],
                },
              },
            ],
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a minimal sequence", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        tables: undefined,
        sequences: [
          {
            name: "myOnlyName",
            type: "myType",
            start: 55,
            error: undefined,
          },
        ],
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a missing sequence", async () => {
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
              type: undefined,
              start: undefined,
              remainder: undefined,
            },
          },
        ],
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const mockFix = mockFetch();
    mockFix.mockResolvedValueOnce(
      responseSuccessAlter("CREATE SEQUENCE myOnlyName"),
    );
    checkbox().click();
    await flushPromises();
    expect(mockFix).toHaveBeenCalledExactlyOnceWith(
      "/myApiPath/alterSchema?subject=sequence&name=myOnlyName&method=add",
    );
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a unused sequence", async () => {
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
              type: undefined,
              start: undefined,
              remainder: undefined,
            },
          },
        ],
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();
    expect(sql()).toBeNull();

    const mockFix = mockFetch();
    mockFix.mockResolvedValueOnce(
      responseSuccessAlter("DROP SEQUENCE myOnlyName"),
    );
    checkbox().click();
    await flushPromises();
    expect(mockFix).toHaveBeenCalledExactlyOnceWith(
      "/myApiPath/alterSchema?subject=sequence&name=myOnlyName&method=drop",
    );
    expect(await formatHtml(sql())).toMatchSnapshot();

    checkbox().click();
    await flushPromises();
    expect(sql()).toBeNull();
  });

  it("should render a sequence with wrong type", async () => {
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
              existence: undefined,
              type: "myTypeX",
              start: undefined,
              remainder: undefined,
            },
          },
        ],
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a sequence with wrong start", async () => {
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
              existence: undefined,
              type: undefined,
              start: 66,
              remainder: undefined,
            },
          },
        ],
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a sequence with a remaining error", async () => {
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
              existence: undefined,
              type: undefined,
              start: undefined,
              remainder: ["remainder1", "remainder2"],
            },
          },
        ],
      } satisfies SchemaResponse),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render an error message", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(responseFailure("myError"));
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();
  });

  it("should render a connect button", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(responseFailure("model not connected message"));
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();

    const mockButton = mockFetch();
    mockButton.mockResolvedValueOnce(
      responseSuccess({} satisfies ConnectResponse),
    );
    mockButton.mockResolvedValueOnce(
      responseSuccess({
        tables: [
          {
            name: "myTable1Name",
            columns: undefined,
            constraints: undefined,
            error: undefined,
          },
        ],
        sequences: undefined,
      } satisfies SchemaResponse),
    );
    (document.querySelectorAll(".tree button").item(0) as HTMLElement).click();
    await flushPromises();
    expect(mockButton).toHaveBeenNthCalledWith(
      1,
      "/myApiPath/connect",
      request({} satisfies ConnectRequest),
    );
    expect(mockButton).toHaveBeenNthCalledWith(2, "/myApiPath/schema");
    expect(await formatHtml(tree())).toMatchSnapshot();
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
  } satisfies AlterSchemaResponse);
}

function checkbox(): HTMLElement {
  return document
    .querySelectorAll("input[type='checkbox']")
    .item(0) as HTMLElement;
}

function select(): HTMLSelectElement {
  return document.querySelectorAll("select").item(0) as HTMLSelectElement;
}

function sql(): HTMLElement {
  return document.querySelectorAll(".sql").item(0) as HTMLElement;
}

beforeAll(() => {
  // https://github.com/jsdom/jsdom/issues/3429
  // @ts-expect-error
  Element.prototype.animate = () => ({});
});

afterAll(() => {
  // @ts-expect-error
  Element.prototype.animate = undefined;
});
