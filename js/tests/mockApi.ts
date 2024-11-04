import type { Mock } from "vitest";

export function responseSuccess(responseBody: unknown): Response {
  return {
    ...responseFallback,
    ok: true,
    status: 200,
    headers: headersContentType("application/json;charset=UTF-8"), // corresponds to Api#APPLICATION_JSON
    json: () => Promise.resolve(responseBody),
  };
}

export function responseFailure(responseBody: string): Response {
  return {
    ...responseFallback,
    ok: false,
    status: 400,
    headers: headersContentType("text/plain;charset=UTF-8"), // corresponds to Api#doRequest
    text: () => Promise.resolve(responseBody),
  };
}

const responseFallback: Omit<Response, "ok" | "status" | "headers"> = {
  // https://developer.mozilla.org/de/docs/Web/API/Response/bytes
  bytes: doNotCall("bytes"), // TODO don't know why this is needed, required since typescript: 5.7.0
  json: doNotCall("json"),
  text: doNotCall("text"),
  arrayBuffer: doNotCall("arrayBuffer"),
  blob: doNotCall("blob"),
  body: null,
  bodyUsed: false,
  formData: doNotCall("formData"),
  clone: doNotCall("clone"),
  redirected: false,
  statusText: "DO NOT ASSERT statusText",
  type: "default",
  url: "DO NOT ASSERT url",
};

function headersContentType(contentType: string): Headers {
  return {
    ...headersFallback,
    get: (key: string): string | null => {
      if (key === "Content-Type") return contentType;
      assert.fail("DO NOT CALL headers");
    },
  };
}

const headersFallback: Omit<Headers, "get"> = {
  append: doNotCall("append"),
  delete: doNotCall("delete"),
  getSetCookie: doNotCall("getSetCookie"),
  has: doNotCall("has"),
  set: doNotCall("set"),
  forEach: doNotCall("forEach"),
  [Symbol.iterator]: doNotCall("iterator"),
  entries: doNotCall("entries"),
  keys: doNotCall("keys"),
  values: doNotCall("values"),
};

function doNotCall(name: string) {
  return () => assert.fail("DO NOT CALL " + name);
}

export function mockFetch(): Mock<
  (request: URL, init: RequestInit) => Promise<Response>
> {
  const mock = vi.fn((request, init) => {
    assert.fail("DO NOT CALL " + request + " " + init);
  });
  global.fetch = mock;
  return mock;
}

export function request(body: unknown): RequestInit {
  return {
    body: JSON.stringify(body),
    headers: [["Content-Type", "application/json;charset=UTF-8"]],
    method: "POST",
  };
}
