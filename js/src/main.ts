import { type Component, mount } from "svelte";
import { default as Suspicions } from "@/Suspicions.svelte";
import { default as Hashes } from "@/Hashes.svelte";
import Schema from "@/Schema.svelte";
import UniqueCache from "@/UniqueCache.svelte";

mountIfPresent("schema", Schema);
mountIfPresent("uniqueCache", UniqueCache);
mountIfPresent("suspicions", Suspicions);
mountIfPresent("hashes", Hashes);

function mountIfPresent(elementId: string, component: Component) {
  const element = document.getElementById(elementId);
  if (element) {
    mount(component, {
      target: element,
    });
  }
}
