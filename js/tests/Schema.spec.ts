import { mount } from "svelte";
import { default as Schema } from "@/Schema.svelte";
import { mockFetch, responseFailure, responseSuccess } from "@t/mockApi";
import { flushPromises, formatHtml } from "@t/utils";
import { expect } from "vitest";
import type { SchemaResponse } from "@/api/types";

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
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(2) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();

    (document.querySelectorAll(".bullet").item(1) as HTMLElement).click();
    await flushPromises();
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();
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
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });

  it("should render an error message", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(responseFailure("myError"));
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });
});

async function mountComponent() {
  mount(Schema, { target: document.body }); // `document` exists because of jsdom
  await flushPromises();
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
