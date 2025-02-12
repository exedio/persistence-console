beforeEach(() => {
  document.body.innerHTML = ""; // `document` exists because of jsdom

  global.fetch = (input: RequestInfo | URL): never => {
    const url: string = isRequest(input)
      ? "RequestInfo=" + input.url
      : "URL=" + input.toString();
    assert.fail("fetch must be mocked in unit tests: " + url);
  };

  // @ts-expect-error global any
  global.apiPath = "/myApiPath/";
});

// a type guard
function isRequest(object: unknown): object is Request {
  if (!object) return false;
  return typeof object === "object" && object.hasOwnProperty("url");
}
