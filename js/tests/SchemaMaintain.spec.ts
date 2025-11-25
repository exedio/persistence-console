import {
  mockFetch,
  request,
  responseFailure,
  responseSuccess,
} from "@t/mockApi";
import { flushPromises, formatHtml } from "@t/utils";
import { expect } from "vitest";
import type { SchemaMaintainResponse, Schema as ApiSchema } from "@/api/types";
import { mount } from "svelte";
import Schema from "@/Schema.svelte";

async function prepare() {
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
  expect(await formatHtml(maintain())).toMatchFileSnapshot(
    "SchemaMaintain-initial.output.html",
  );
}

describe("Schema Maintain", () => {
  it("should create", async () => {
    await prepare();
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessMaintain());
      (document.querySelectorAll("button").item(0) as HTMLInputElement).click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/maintain",
        request({
          operation: "create",
        }),
      );
    }
    expect(await formatHtml(maintain())).toMatchSnapshot();
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseFailure("myError"));
      (document.querySelectorAll("button").item(0) as HTMLInputElement).click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/maintain",
        request({
          operation: "create",
        }),
      );
    }
    expect(await formatHtml(maintain())).toMatchSnapshot();
  });
});

async function mountComponent() {
  mount(Schema, { target: document.body }); // `document` exists because of jsdom
  await flushPromises();
}

function maintain(): HTMLElement {
  return document.querySelectorAll(".maintain").item(0) as HTMLElement;
}

function responseSuccessMaintain() {
  return responseSuccess({
    elapsedNanos: 123456789,
  } satisfies SchemaMaintainResponse);
}
