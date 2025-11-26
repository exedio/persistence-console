import {
  mockFetch,
  request,
  responseFailure,
  responseSuccess,
} from "@t/mockApi";
import { flushPromises, formatHtml } from "@t/utils";
import { expect } from "vitest";
import type {
  SchemaMaintainResponse,
  Schema as ApiSchema,
  SchemaMaintainRequest,
} from "@/api/types";
import { mount } from "svelte";
import Schema from "@/Schema.svelte";

async function prepare() {
  {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(responseSuccessEmptySchema);
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/schema");
  }
  await expect(await formatHtml(maintain())).toMatchFileSnapshot(
    "SchemaMaintain-initial.output.html",
  );
}

describe("Schema Maintain", () => {
  it("should create", async () => {
    await prepare();
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessMaintain());
      mock.mockResolvedValueOnce(responseSuccessEmptySchema);
      (document.querySelectorAll("button").item(0) as HTMLInputElement).click();
      await flushPromises();
      expect(mock).toHaveBeenNthCalledWith(
        1,
        "/myApiPath/schema/maintain",
        request({
          operation: "create",
        } satisfies SchemaMaintainRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(2, "/myApiPath/schema");
      expect(mock).toHaveBeenCalledTimes(2);
    }
    expect(await formatHtml(maintain())).toMatchSnapshot();
  });

  it("should create and fail", async () => {
    await prepare();
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseFailure("myError"));
      mock.mockResolvedValueOnce(responseSuccessEmptySchema);
      (document.querySelectorAll("button").item(0) as HTMLInputElement).click();
      await flushPromises();
      expect(mock).toHaveBeenNthCalledWith(
        1,
        "/myApiPath/schema/maintain",
        request({
          operation: "create",
        } satisfies SchemaMaintainRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(2, "/myApiPath/schema");
      expect(mock).toHaveBeenCalledTimes(2);
    }
    expect(await formatHtml(maintain())).toMatchSnapshot();
  });

  it("should tear down", async () => {
    await prepare();
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessMaintain());
      mock.mockResolvedValueOnce(responseSuccessEmptySchema);
      window.confirm = function (message?: string): boolean {
        expect(message).toBe(
          "This operation will desperately try to drop all your database tables.\nDo you really want to do this?",
        );
        return true;
      };
      (document.querySelectorAll("button").item(1) as HTMLInputElement).click();
      await flushPromises();
      expect(mock).toHaveBeenNthCalledWith(
        1,
        "/myApiPath/schema/maintain",
        request({
          operation: "tearDown",
        } satisfies SchemaMaintainRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(2, "/myApiPath/schema");
      expect(mock).toHaveBeenCalledTimes(2);
    }
    expect(await formatHtml(maintain())).toMatchSnapshot();
  });

  it("should drop", async () => {
    await prepare();
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessMaintain());
      mock.mockResolvedValueOnce(responseSuccessEmptySchema);
      window.confirm = function (message?: string): boolean {
        expect(message).toBe(
          "This operation will drop all your database tables.\nDo you really want to do this?",
        );
        return true;
      };
      (document.querySelectorAll("button").item(2) as HTMLInputElement).click();
      await flushPromises();
      expect(mock).toHaveBeenNthCalledWith(
        1,
        "/myApiPath/schema/maintain",
        request({
          operation: "drop",
        } satisfies SchemaMaintainRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(2, "/myApiPath/schema");
      expect(mock).toHaveBeenCalledTimes(2);
    }
    expect(await formatHtml(maintain())).toMatchSnapshot();
  });

  it("should drop and abort in confirm", async () => {
    await prepare();
    {
      window.confirm = function (message?: string): boolean {
        expect(message).toBe(
          "This operation will drop all your database tables.\nDo you really want to do this?",
        );
        return false;
      };
      (document.querySelectorAll("button").item(2) as HTMLInputElement).click();
      await flushPromises();
    }
    await expect(await formatHtml(maintain())).toMatchFileSnapshot(
      "SchemaMaintain-initial.output.html",
    );
  });

  it("should tear down & create", async () => {
    await prepare();
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessMaintain());
      mock.mockResolvedValueOnce(responseSuccessMaintain(234567891));
      mock.mockResolvedValueOnce(responseSuccessEmptySchema);
      window.confirm = function (message?: string): boolean {
        expect(message).toBe(
          "This operation will desperately try to drop all your database tables.\nDo you really want to do this?\nAfterwards all tables will be recreated.",
        );
        return true;
      };
      (document.querySelectorAll("button").item(3) as HTMLInputElement).click();
      await flushPromises();
      expect(mock).toHaveBeenNthCalledWith(
        1,
        "/myApiPath/schema/maintain",
        request({
          operation: "tearDown",
        } satisfies SchemaMaintainRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(
        2,
        "/myApiPath/schema/maintain",
        request({
          operation: "create",
        } satisfies SchemaMaintainRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(3, "/myApiPath/schema");
      expect(mock).toHaveBeenCalledTimes(3);
    }
    expect(await formatHtml(maintain())).toMatchSnapshot();
  });

  it("should drop & create", async () => {
    await prepare();
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessMaintain());
      mock.mockResolvedValueOnce(responseSuccessMaintain(234567891));
      mock.mockResolvedValueOnce(responseSuccessEmptySchema);
      window.confirm = function (message?: string): boolean {
        expect(message).toBe(
          "This operation will drop all your database tables.\nDo you really want to do this?\nAfterwards all tables will be recreated.",
        );
        return true;
      };
      (document.querySelectorAll("button").item(4) as HTMLInputElement).click();
      await flushPromises();
      expect(mock).toHaveBeenNthCalledWith(
        1,
        "/myApiPath/schema/maintain",
        request({
          operation: "drop",
        } satisfies SchemaMaintainRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(
        2,
        "/myApiPath/schema/maintain",
        request({
          operation: "create",
        } satisfies SchemaMaintainRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(3, "/myApiPath/schema");
      expect(mock).toHaveBeenCalledTimes(3);
    }
    expect(await formatHtml(maintain())).toMatchSnapshot();
  });

  it("should drop & create and fail", async () => {
    await prepare();
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseFailure("myError"));
      mock.mockResolvedValueOnce(responseSuccessEmptySchema);
      window.confirm = function (message?: string): boolean {
        return true;
      };
      (document.querySelectorAll("button").item(4) as HTMLInputElement).click();
      await flushPromises();
      expect(mock).toHaveBeenNthCalledWith(
        1,
        "/myApiPath/schema/maintain",
        request({
          operation: "drop",
        } satisfies SchemaMaintainRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(2, "/myApiPath/schema");
      expect(mock).toHaveBeenCalledTimes(2);
    }
    expect(await formatHtml(maintain())).toMatchSnapshot();
  });

  it("should drop & create and fail on create", async () => {
    await prepare();
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessMaintain());
      mock.mockResolvedValueOnce(responseFailure("myError"));
      mock.mockResolvedValueOnce(responseSuccessEmptySchema);
      window.confirm = function (message?: string): boolean {
        return true;
      };
      (document.querySelectorAll("button").item(4) as HTMLInputElement).click();
      await flushPromises();
      expect(mock).toHaveBeenNthCalledWith(
        1,
        "/myApiPath/schema/maintain",
        request({
          operation: "drop",
        } satisfies SchemaMaintainRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(
        2,
        "/myApiPath/schema/maintain",
        request({
          operation: "create",
        } satisfies SchemaMaintainRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(3, "/myApiPath/schema");
      expect(mock).toHaveBeenCalledTimes(3);
    }
    expect(await formatHtml(maintain())).toMatchSnapshot();
  });

  it("should delete", async () => {
    await prepare();
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccessMaintain());
      window.confirm = function (message?: string): boolean {
        expect(message).toBe(
          "This operation will delete the contents of your database tables.\nDo you really want to do this?",
        );
        return true;
      };
      (document.querySelectorAll("button").item(5) as HTMLInputElement).click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/schema/maintain",
        request({
          operation: "delete",
        } satisfies SchemaMaintainRequest),
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

function responseSuccessMaintain(elapsedNanos: number = 123456789) {
  return responseSuccess({
    elapsedNanos: elapsedNanos,
  } satisfies SchemaMaintainResponse);
}

const responseSuccessEmptySchema = responseSuccess({
  tables: undefined,
  sequences: undefined,
} satisfies ApiSchema);
