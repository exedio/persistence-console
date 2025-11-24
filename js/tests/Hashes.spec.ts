import { mount } from "svelte";
import { default as Hashes } from "@/Hashes.svelte";
import {
  mockFetch,
  request,
  responseFailure,
  responseSuccess,
} from "@t/mockApi";
import { flushPromises, formatHtml } from "@t/utils";
import type { HashRequest, HashResponse, Hash as ApiHash } from "@/api/types";

describe("Hashes", () => {
  it("should render an empty table", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccess([] satisfies ApiHash[]));
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/hashes");
    }
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });

  it("should render a non-empty table", async () => {
    {
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
        ] satisfies ApiHash[]),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/hashes");
    }
    expect(await formatHtml(document.body)).toMatchSnapshot();

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess([
          {
            type: "myType",
            name: "myName",
            plainTextLimit: 155,
            plainTextValidator: "myValidatorChanged",
            algorithmID: "myAlgorithmIDChanged",
            algorithmDescription: "myAlgorithmDescriptionChanged",
          },
          {
            type: "myTypeReloaded",
            name: "myNameReloaded",
            plainTextLimit: 155,
            plainTextValidator: "myValidatorReloaded",
            algorithmID: "myAlgorithmIDReloaded",
            algorithmDescription: "myAlgorithmDescriptionReloaded",
          },
        ] satisfies ApiHash[]),
      );
      (document.querySelectorAll(".reload").item(0) as HTMLElement).click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/hashes");
    }
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });

  it("should render a table body with all hashes measured", async () => {
    mockFetch().mockResolvedValueOnce(responseSuccess(threeHashes));
    await mountComponent();

    {
      const mock = mockFetch();
      mock
        .mockResolvedValueOnce(
          responseSuccess({
            hash: "DO NOT ASSERT myHash1",
            elapsedNanos: 50010,
          } satisfies HashResponse),
        )
        .mockResolvedValueOnce(
          responseSuccess({
            hash: "DO NOT ASSERT myHash2",
            elapsedNanos: 50020,
          } satisfies HashResponse),
        )
        .mockResolvedValueOnce(
          responseSuccess({
            hash: "DO NOT ASSERT myHash3",
            elapsedNanos: 50030,
          } satisfies HashResponse),
        );
      (document.querySelectorAll(".measure").item(0) as HTMLElement).click();
      await flushPromises();
      expect(mock).toHaveBeenNthCalledWith(
        1,
        "/myApiPath/hashes/hash",
        request({
          type: "myType1",
          name: "myName1",
          plainText: "example password",
        } satisfies HashRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(
        2,
        "/myApiPath/hashes/hash",
        request({
          type: "myType2",
          name: "myName2",
          plainText: "example password",
        } satisfies HashRequest),
      );
      expect(mock).toHaveBeenNthCalledWith(
        3,
        "/myApiPath/hashes/hash",
        request({
          type: "myType3",
          name: "myName3",
          plainText: "example password",
        } satisfies HashRequest),
      );
      expect(mock).toHaveBeenCalledTimes(3);
    }
    expect(
      await formatHtml(document.body.querySelector("tbody")!),
    ).toMatchSnapshot();
  });

  it("should render a table body with one hash measured", async () => {
    mockFetch().mockResolvedValueOnce(responseSuccess(threeHashes));
    await mountComponent();

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          hash: "DO NOT ASSERT myHash2",
          elapsedNanos: 50020,
        } satisfies HashResponse),
      );
      (document.querySelectorAll(".measure").item(2) as HTMLElement).click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/hashes/hash",
        request({
          type: "myType2",
          name: "myName2",
          plainText: "example password",
        } satisfies HashRequest),
      );
    }
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

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess({
          hash: "myHash2",
          elapsedNanos: 50020,
        } satisfies HashResponse),
      );
      (
        document.querySelectorAll("td.expansion button").item(0) as HTMLElement
      ).click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith(
        "/myApiPath/hashes/hash",
        request({
          type: "myType2",
          name: "myName2",
          plainText: "",
        } satisfies HashRequest),
      );
    }
    expect(
      await formatHtml(document.body.querySelector("tbody")!),
    ).toMatchSnapshot();
  });

  it("should render an error message", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseFailure("myError"));
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/hashes");
    }
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });
});

async function mountComponent() {
  mount(Hashes, { target: document.body }); // `document` exists because of jsdom
  await flushPromises();
}

const threeHashes: ApiHash[] = [
  numberedHash(1),
  numberedHash(2),
  numberedHash(3),
];

function numberedHash(n: number): ApiHash {
  return {
    type: "myType" + n,
    name: "myName" + n,
    plainTextLimit: 150 + n,
    plainTextValidator: undefined,
    algorithmID: "myAlgorithmID" + n,
    algorithmDescription: "myAlgorithmDescription" + n,
  };
}
