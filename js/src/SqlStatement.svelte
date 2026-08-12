<script lang="ts">
  import highlighter from "sql-highlight";
  const {
    statement,
    highlight,
  }: {
    statement: string;
    highlight: boolean;
  } = $props();

  const segments = $derived(
    highlight ? highlighter.getSegments(statement) : undefined,
  );
</script>

{#if segments}
  {#each segments as { name, content }, idx (idx)}
    <span class={["sql", name]}>{content}</span>
  {/each}
{:else}
  {statement}
{/if}

<style>
  .sql {
    &.identifier {
      color: black;
    }
    &.keyword {
      color: #0033b3;
      font-weight: 500;
    }
    &.number {
      color: #0033b3;
      font-style: italic;
    }
    &.string {
      color: #0033b3;
      font-style: italic;
    }
    &.function {
      color: purple;
      font-style: italic;
      font-weight: 500;
    }
  }
</style>
