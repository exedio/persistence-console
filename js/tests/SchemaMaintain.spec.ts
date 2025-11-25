import {
  mockFetch,
  request,
  responseFailure,
  responseSuccess,
} from "@t/mockApi";
import { flushPromises, formatHtml } from "@t/utils";
import { expect } from "vitest";
import type { SchemaMaintainResponse, Schema as ApiSchema } from "@/api/types";
import { mountComponent } from "@t/Schema.spec";

describe("Schema Maintain", () => {
  it("should create", async () => {
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
    expect(await formatHtml(maintain())).toMatchSnapshot();
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

function maintain(): HTMLElement {
  return document.querySelectorAll(".maintain").item(0) as HTMLElement;
}

function responseSuccessMaintain() {
  return responseSuccess({
    elapsedNanos: 123456789,
  } satisfies SchemaMaintainResponse);
}
