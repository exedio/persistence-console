import { mount } from "svelte";
import type { UniqueConstraintMetric as Metric } from "@/api/types";
import { mockFetch, responseFailure, responseSuccess } from "@t/mockApi";
import { flushPromises, formatHtml } from "@t/utils";
import { expect } from "vitest";
import UniqueCache from "@/UniqueCache.svelte";

describe("UniqueCache", () => {
  it("should render an empty table", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseSuccess([] satisfies Metric[]));
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith(apiUrl);
    }
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });

  it("should render a non-empty table", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(
        responseSuccess([
          {
            name: "com.exedio.cope.UniqueConstraint.cache",
            description: "myDescription1Hit",
            tags: {
              feature: "myFeature1",
              result: "hit",
            },
            count: 155,
          },
          {
            name: "com.exedio.cope.UniqueConstraint.cache",
            description: "myDescription1Miss",
            tags: {
              feature: "myFeature1",
              result: "miss",
            },
            count: 166,
          },
          {
            name: "com.exedio.cope.UniqueConstraint.cache",
            description: "myDescription2Miss",
            tags: {
              feature: "myFeature2",
              result: "miss",
            },
            count: 266,
          },
          {
            name: "com.exedio.cope.UniqueConstraint.cache",
            description: "myDescription2Hit",
            tags: {
              feature: "myFeature2",
              result: "hit",
            },
            count: 255,
          },
          {
            name: "com.exedio.cope.UniqueConstraint.cache",
            description: "myDescriptionHitOnly",
            tags: {
              feature: "myFeatureHitOnly",
              result: "hit",
            },
            count: 3077,
          },
          {
            name: "com.exedio.cope.UniqueConstraint.cache",
            description: "myDescriptionMissOnly",
            tags: {
              feature: "myFeatureMissOnly",
              result: "miss",
            },
            count: 4077,
          },
        ] satisfies Metric[]),
      );
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith(apiUrl);
    }
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });

  it("should render an error message", async () => {
    {
      const mock = mockFetch();
      mock.mockResolvedValueOnce(responseFailure("myError"));
      await mountComponent();
      expect(mock).toHaveBeenCalledExactlyOnceWith(apiUrl);
    }
    expect(await formatHtml(document.body)).toMatchSnapshot();
  });
});

async function mountComponent() {
  mount(UniqueCache, { target: document.body }); // `document` exists because of jsdom
  await flushPromises();
}

const apiUrl = "/myApiPath/metrics?prefix=com.exedio.cope.UniqueConstraint";
