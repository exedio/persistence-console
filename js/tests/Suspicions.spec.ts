import { mount } from "svelte";
import { default as Suspicions } from "@/Suspicions.svelte";
import { mockFetch, responseFailure, responseSuccess } from "@t/mockApi";
import { flushPromises, formatHtml } from "@t/utils";
import { expect } from "vitest";
import type { SuspicionsResponse } from "@/api/types";

describe("Suspicions", () => {
  it("should render an empty table", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess([] satisfies SuspicionsResponse[]),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/suspicions");
    }
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });

  it("should render a non-empty table", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess([
          {
            type: "myOnlyType",
            name: "myOnlyName",
            suspicions: ["myOnlySuspicion"],
          },
          {
            type: "myMultiType",
            name: "myMultiName",
            suspicions: ["myFirstSuspicion", "mySecondSuspicion"],
          },
        ] satisfies SuspicionsResponse[]),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/suspicions");
    }
    expect(await formatHtml(document.body)).toMatchSnapshot();

    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess([
          {
            type: "myOnlyTypeReloaded",
            name: "myOnlyNameReloaded",
            suspicions: ["myOnlySuspicionReloaded"],
          },
        ] satisfies SuspicionsResponse[]),
      );
      (document.querySelectorAll(".reload").item(0) as HTMLElement).click();
      await flushPromises();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/suspicions");
    }
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });

  it("should render an error message", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseFailure("myError"));
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith("/myApiPath/suspicions");
    }
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });
});

async function mountComponent() {
  mount(Suspicions, { target: document.body }); // `document` exists because of jsdom
  await flushPromises();
}
