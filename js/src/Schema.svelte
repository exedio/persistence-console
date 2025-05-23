<script lang="ts">
  import { fly } from "svelte/transition";
  import { get } from "@/api/api";
  import type { SchemaResponse } from "@/api/types";
  import { SvelteSet } from "svelte/reactivity";
  import {
    type UseColumn,
    useSchema,
    type UseTable,
    type UseExistence,
    type UseComparison,
    type UseConstraint,
  } from "@/UseSchema.js";

  const schema = $state(getSchema());

  function getSchema(): Promise<SchemaResponse> {
    return get<SchemaResponse>("schema");
  }

  function expansionCharacter(expanded: boolean) {
    return expanded ? "-" : "+";
  }

  const expandedTables = $state(new SvelteSet<string>());

  function toggleTable(table: UseTable) {
    if (expandedTables.has(table.name)) expandedTables.delete(table.name);
    else expandedTables.add(table.name);
  }

  const expandedColumns = $state(new SvelteSet<string>());

  function isColumnExpanded(table: UseTable, column: UseColumn) {
    let key = table.name + "." + column.name;
    return expandedColumns.has(key);
  }

  function toggleColumn(table: UseTable, column: UseColumn) {
    let key = table.name + "." + column.name;
    if (expandedColumns.has(key)) expandedColumns.delete(key);
    else expandedColumns.add(key);
  }

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type ReadonlyUseConstraintArray = readonly UseConstraint[];

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type ReadonlyStringArray = readonly string[];

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type Boolean = boolean;
</script>

{#await schema}
  <div>fetching data</div>
{:then schemaApi}
  {@const schema = useSchema(schemaApi)}
  <button class={["bullet", schema.bulletColor]} disabled={true}> . </button>
  Schema
  <ul>
    {#each schema.tables as table (table.name)}
      {@const tableExpanded = expandedTables.has(table.name)}
      <li class="table">
        <button
          class={["bullet", table.bulletColor]}
          onclick={() => toggleTable(table)}
        >
          {expansionCharacter(tableExpanded)}
        </button>
        <span class="nodeType">tab</span>
        {table.name}
        {@render renderExistence(table.existence)}
        {#if tableExpanded}
          {@render renderRemainder(table.remainingErrors)}
        {/if}
        {#if tableExpanded && (table.columns.length || table.constraints.length)}
          <ul in:fly={{ y: -10, duration: 200 }}>
            {#each table.columns as column (column.name)}
              {@const columnExpanded = isColumnExpanded(table, column)}
              <li class="column">
                <button
                  class={["bullet", column.bulletColor]}
                  onclick={() => toggleColumn(table, column)}
                >
                  {expansionCharacter(columnExpanded)}
                </button>
                <span class="nodeType">col</span>
                {column.name}
                {@render renderExistence(column.existence)}
                {@render renderComparison(column.type, columnExpanded)}
                {#if columnExpanded}
                  {@render renderRemainder(column.remainingErrors)}
                {/if}
                {#if columnExpanded && column.constraints.length > 0}
                  <ul in:fly={{ y: -10, duration: 200 }}>
                    {@render renderConstraints(column.constraints)}
                  </ul>
                {/if}
              </li>
            {/each}
            {@render renderConstraints(table.constraints)}
          </ul>
        {/if}
      </li>
    {/each}
    {#each schema.sequences as sequence (sequence.name)}
      <li class="sequence">
        <button class={["bullet", sequence.bulletColor]} disabled={true}>
          .
        </button>
        <span class="nodeType">seq</span>
        {sequence.name}
        {@render renderExistence(sequence.existence)}
        {@render renderComparison(sequence.type, true)}
        {@render renderComparison(sequence.start, true)}
        {@render renderRemainder(sequence.remainingErrors)}
      </li>
    {/each}
  </ul>
{:catch error}
  <div>{error.message}</div>
{/await}

{#snippet renderConstraints(constraints: ReadonlyUseConstraintArray)}
  {#each constraints as constraint (constraint.name)}
    <li>
      <button class={["bullet", constraint.bulletColor]} disabled={true}>
        .
      </button>
      <span class="nodeType">{constraint.type}</span>
      {constraint.nameShort()}
      {@render renderExistence(constraint.existence)}
      {@render renderComparison(constraint.clause, true)}
      {@render renderRemainder(constraint.remainingErrors)}
    </li>
  {/each}
{/snippet}

{#snippet renderExistence(existence: UseExistence)}
  {#if existence}
    <span class={existence.color}>{existence.text}</span>
  {/if}
{/snippet}

{#snippet renderComparison(value: UseComparison | undefined, expanded: Boolean)}
  {#if value}
    {#if expanded}
      {#if value.actual}
        <span class={value.color}>{value.name} mismatch:</span>
        <table>
          <tbody>
            <tr><td>required:</td><td>{value.shortener(value.expected)}</td></tr
            >
            <tr><td>actual:</td><td>{value.shortener(value.actual)}</td></tr>
          </tbody>
        </table>
      {:else}
        {value.shortener(value.expected)}
      {/if}
    {:else if value.actual}
      <span class={value.color}>{value.name} mismatch</span>
    {/if}
  {/if}
{/snippet}

{#snippet renderRemainder(remainder: ReadonlyStringArray)}
  {#if remainder.length}
    <ul>
      {#each remainder as r}
        <li>{r}</li>
      {/each}
    </ul>
  {/if}
{/snippet}

<style>
  li {
    list-style-type: none;
  }

  button.bullet {
    border: 1px black solid;
    border-radius: 2px;
    padding: 0;
    width: 1.2em;
    font-size: 40%;
    background: #ffffff;
  }

  button.red {
    background: #dd3a3a;
  }

  button.yellow {
    background: #ffff00;
  }

  span.nodeType {
    font-size: 60%;
  }

  span.red {
    color: #880000;
  }

  span.yellow {
    color: #887800;
  }
</style>
