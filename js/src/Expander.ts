import { SvelteSet } from "svelte/reactivity";

export class Expander<E> {
  private expanded = new SvelteSet<string>();

  constructor(private readonly keyFunction: (e: E) => string) {}

  has(element: E): boolean {
    return this.expanded.has(this.keyFunction(element));
  }

  toggle(element: E): void {
    const key: string = this.keyFunction(element);
    if (this.expanded.has(key)) this.expanded.delete(key);
    else this.expanded.add(key);
  }
}
