<script lang="ts">
  import { patchEncodings, type PatchEncoding } from "@/SchemaFix";

  const {
    copyHandler,
  }: { copyHandler: (mode: PatchEncoding) => Promise<void> } = $props();

  let popover: HTMLDivElement | undefined = $state(undefined);
  let notificationVisible: boolean = $state(false);

  const copy = async (mode: PatchEncoding) => {
    await copyHandler(mode).then(() => {
      if (typeof popover?.hidePopover === "function") popover?.hidePopover();
      notificationVisible = true;
      globalThis.setTimeout(() => {
        notificationVisible = false;
      }, 2000);
    });
  };
</script>

<button id="copybutton" popovertarget="modes" popovertargetaction="toggle"
  >copy</button
>
{#if notificationVisible}
  <span id="notification">Copied ✓</span>
{/if}
<div id="modes" popover bind:this={popover}>
  {#each patchEncodings as mode (mode)}
    <button class="mode" onclick={() => copy(mode)}>{mode}</button>
  {/each}
</div>

<style>
  #copybutton {
    float: inline-end;
    anchor-name: --copy-button-anchor;
  }
  #modes {
    border: none;
    position: fixed;
    position-area: bottom;
    justify-self: anchor-center;
    top: calc(anchor(bottom) + 5px);
    /* can't set display: flex on popover unconditionally because the Popover API controls visibility via display attribute */
    &:popover-open {
      display: flex;
      flex-flow: column nowrap;
    }
    > button {
      background-color: rgba(from #4b77d1 r g b/0.3);
      border: none;
      &:hover {
        background-color: #4b77d1;
        color: white;
      }
      &:first-child {
        border-top-left-radius: 5px;
        border-top-right-radius: 5px;
      }
      &:last-child {
        border-bottom-left-radius: 5px;
        border-bottom-right-radius: 5px;
      }
      &:not(:last-child) {
        border-bottom: 1px solid #4b77d1;
      }
    }
  }
  #notification {
    position: fixed;
    position-anchor: --copy-button-anchor;
    position-area: block-start;
    justify-self: anchor-center;
    bottom: calc(anchor(start) + 5px);

    padding: 0.5em;
    border-radius: 5px;
    margin-right: 0.5em;
    background-color: #4b77d1;
    color: white;
  }
</style>
