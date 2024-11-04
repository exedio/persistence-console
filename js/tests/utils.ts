import { format } from "prettier/standalone";
import parsers from "prettier/plugins/html";

export function flushPromises(): Promise<void> {
  return new Promise((resolve) => setImmediate(resolve));
}

export function formatHtml(element: HTMLElement): Promise<string> {
  return format(element.innerHTML, {
    parser: "html",
    plugins: [parsers],
  });
}
