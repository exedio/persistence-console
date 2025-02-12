import { mount } from "svelte";
import { default as Hashes } from "@/Hashes.svelte";
import {
  mockFetch,
  request,
  responseFailure,
  responseSuccess,
} from "@t/mockApi";
import { flushPromises, formatHtml } from "@t/utils";
import type { HashesResponse } from "@/api/types";

describe("Hashes", () => {
  it("should render an empty table", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(responseSuccess([]));
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/hashes");
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });

  it("should render a non-empty table", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess([
        {
          type: "myType",
          name: "myName",
          plainTextLimit: 150,
          plainTextValidator: "myValidator",
          algorithmID: "myAlgorithmID",
          algorithmDescription: "myAlgorithmDescription",
        },
        {
          type: "myTypeWithoutLimit",
          name: "myNameWithoutLimit",
          plainTextLimit: 155,
          plainTextValidator: undefined,
          algorithmID: "myAlgorithmIDWithoutLimit",
          algorithmDescription: "myAlgorithmDescriptionWithoutLimit",
        },
      ]),
    );
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/hashes");
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });

  it("should render a table body with all hashes measured", async () => {
    mockFetch().mockResolvedValueOnce(responseSuccess(threeHashes));
    await mountComponent();

    const mock = mockFetch();
    mock
      .mockResolvedValueOnce(
        responseSuccess({
          hash: "DO NOT ASSERT myHash1",
          elapsedNanos: 50010,
        }),
      )
      .mockResolvedValueOnce(
        responseSuccess({
          hash: "DO NOT ASSERT myHash2",
          elapsedNanos: 50020,
        }),
      )
      .mockResolvedValueOnce(
        responseSuccess({
          hash: "DO NOT ASSERT myHash3",
          elapsedNanos: 50030,
        }),
      );
    (document.querySelectorAll(".measure").item(0) as HTMLElement).click();
    await flushPromises();
    expect(mock).toHaveBeenNthCalledWith(
      1,
      "/myApiPath/doHash",
      request({
        type: "myType1",
        name: "myName1",
        plainText: "example password",
      }),
    );
    expect(mock).toHaveBeenNthCalledWith(
      2,
      "/myApiPath/doHash",
      request({
        type: "myType2",
        name: "myName2",
        plainText: "example password",
      }),
    );
    expect(mock).toHaveBeenNthCalledWith(
      3,
      "/myApiPath/doHash",
      request({
        type: "myType3",
        name: "myName3",
        plainText: "example password",
      }),
    );
    expect(mock).toHaveBeenCalledTimes(3);
    expect(
      await formatHtml(document.body.querySelector("tbody")!),
    ).toMatchSnapshot();
  });

  it("should render a table body with one hash measured", async () => {
    mockFetch().mockResolvedValueOnce(responseSuccess(threeHashes));
    await mountComponent();

    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        hash: "DO NOT ASSERT myHash2",
        elapsedNanos: 50020,
      }),
    );
    (document.querySelectorAll(".measure").item(2) as HTMLElement).click();
    await flushPromises();
    expect(mock).toHaveBeenCalledExactlyOnceWith(
      "/myApiPath/doHash",
      request({
        type: "myType2",
        name: "myName2",
        plainText: "example password",
      }),
    );
    expect(
      await formatHtml(document.body.querySelector("tbody")!),
    ).toMatchSnapshot();
  });

  it("should render a table body with one hash expanded", async () => {
    mockFetch().mockResolvedValueOnce(responseSuccess(threeHashes));
    await mountComponent();

    (document.querySelectorAll(".hash").item(1) as HTMLElement).click();
    await flushPromises();
    expect(
      await formatHtml(document.body.querySelector("tbody")!),
    ).toMatchSnapshot();

    const mock = mockFetch();
    mock.mockResolvedValueOnce(
      responseSuccess({
        hash: "myHash2",
        elapsedNanos: 50020,
      }),
    );
    (
      document.querySelectorAll("td.expansion button").item(0) as HTMLElement
    ).click();
    await flushPromises();
    expect(mock).toHaveBeenCalledExactlyOnceWith(
      "/myApiPath/doHash",
      request({
        type: "myType2",
        name: "myName2",
        plainText: "",
      }),
    );
    expect(
      await formatHtml(document.body.querySelector("tbody")!),
    ).toMatchSnapshot();
  });

  it("should render an error message", async () => {
    const mock = mockFetch();
    mock.mockResolvedValueOnce(responseFailure("myError"));
    await mountComponent();
    expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/hashes");
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });
});

async function mountComponent() {
  mount(Hashes, { target: document.body }); // `document` exists because of jsdom
  await flushPromises();
}

const threeHashes: HashesResponse[] = [
  numberedHash(1),
  numberedHash(2),
  numberedHash(3),
];

function numberedHash(n: number): HashesResponse {
  return {
    type: "myType" + n,
    name: "myName" + n,
    plainTextLimit: 150 + n,
    plainTextValidator: undefined,
    algorithmID: "myAlgorithmID" + n,
    algorithmDescription: "myAlgorithmDescription" + n,
  };
}

beforeAll(() => {
  // https://github.com/jsdom/jsdom/issues/3429
  // @ts-ignore
  Element.prototype.animate = () => ({});
});

afterAll(() => {
  // @ts-ignore
  Element.prototype.animate = undefined;
});
