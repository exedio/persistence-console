import { format } from "prettier/standalone";
import parsers from "prettier/plugins/html";

export function flushPromises(): Promise<void> {
  return new Promise((resolve) => setImmediate(resolve));
}

export function formatHtml(
  element: Element | undefined | null,
): Promise<string> {
  if (!element) return Promise.reject();
  return format(
    element.innerHTML
      .replace(/<!---->/g, "")
      .replaceAll(/\bsvelte-[0-9a-z]{5,7}\b/g, "svelte"),
    {
      parser: "html",
      plugins: [parsers],
    },
  );
}
