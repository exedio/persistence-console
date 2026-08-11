import { format } from "prettier/standalone";
import parsers from "prettier/plugins/html";

export function flushPromises(): Promise<void> {
  return new Promise((resolve) => setImmediate(resolve));
}

function replaceAndFormat(text: string): Promise<string> {
  return format(
    text
      .replace(/<!---->/g, "")
      .replaceAll(/\bsvelte-[0-9a-z]{5,7}\b/g, "svelte"),
    { parser: "html", plugins: [parsers] },
  );
}

export function formatOuterHtml(
  element: Element | undefined | null,
): Promise<string> {
  if (!element) return Promise.reject();
  return replaceAndFormat(element.outerHTML);
}

export function formatHtml(
  element: Element | undefined | null,
): Promise<string> {
  if (!element) return Promise.reject();
  return replaceAndFormat(element.innerHTML);
}
