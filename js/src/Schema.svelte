<script lang="ts">
  import { fly } from "svelte/transition";
  import { get } from "@/api/api";
  import type { AlterSchemaResponse, SchemaResponse } from "@/api/types";
  import { SvelteSet } from "svelte/reactivity";
  import {
    type UseColumn,
    useSchema,
    type UseTable,
    type UseExistence,
    type UseComparison,
    type UseConstraint,
    type UseSequence,
  } from "@/UseSchema.js";
  import { PromiseTracker } from "@/api/PromiseTracker.svelte";
  import PromiseTrackerReload from "@/api/PromiseTrackerReload.svelte";

  const schemaT = new PromiseTracker(() => get<SchemaResponse>("schema"));

  function expansionCharacter(expanded: boolean) {
    return expanded ? "-" : "+";
  }

  const expandedTables = $state(new SvelteSet<string>());

  function toggleTable(table: UseTable) {
    if (expandedTables.has(table.name)) expandedTables.delete(table.name);
    else expandedTables.add(table.name);
  }

  const expandedColumns = $state(new SvelteSet<string>());

  function isColumnExpanded(column: UseColumn) {
    let key = column.tableName + "." + column.name;
    return expandedColumns.has(key);
  }

  function toggleColumn(column: UseColumn) {
    let key = column.tableName + "." + column.name;
    if (expandedColumns.has(key)) expandedColumns.delete(key);
    else expandedColumns.add(key);
  }

  function addDropTable(table: UseTable, e: Existence) {
    handleAlterSchema(
      get<AlterSchemaResponse>(
        "schema/" + addDrop(e) + "Table?name=" + table.name,
      ),
    );
  }

  function addDropColumn(column: UseColumn, e: Existence) {
    handleAlterSchema(
      get<AlterSchemaResponse>(
        "schema/" +
          addDrop(e) +
          "Column?table=" +
          column.tableName +
          "&name=" +
          column.name,
      ),
    );
  }

  function addDropConstraint(constraint: UseConstraint, e: Existence) {
    handleAlterSchema(
      get<AlterSchemaResponse>(
        "schema/" +
          addDrop(e) +
          "Constraint?table=" +
          constraint.tableName +
          "&name=" +
          constraint.name,
      ),
    );
  }

  function addDropSequence(sequence: UseSequence, e: Existence) {
    handleAlterSchema(
      get<AlterSchemaResponse>(
        "schema/" + addDrop(e) + "Sequence?name=" + sequence.name,
      ),
    );
  }

  function handleAlterSchema(response: Promise<AlterSchemaResponse>) {
    response.then((r) => sql.add(r.sql));
  }

  function addDrop(e: Existence) {
    return e === "missing" ? "add" : "drop";
  }

  const sql = new SvelteSet<String>();

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type ReadonlyUseConstraintArray = readonly UseConstraint[];

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type ReadonlyStringArray = readonly string[];

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type Boolean = boolean;

  type Existence = "missing" | "unused";
</script>

<div class="container">
  <div class="tree">
    {#await schemaT.promise()}
      fetching data
    {:then schemaApi}
      {@const schema = useSchema(schemaApi)}
      <button class={["bullet", schema.bulletColor]} disabled={true}>
        .
      </button>
      Schema
      <PromiseTrackerReload tracker={schemaT} />
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
            {@render renderExistence(table.existence, (e) => {
              addDropTable(table, e);
            })}
            {#if tableExpanded}
              {@render renderRemainder(table.remainingErrors)}
            {/if}
            {#if tableExpanded && (table.columns.length || table.constraints.length)}
              <ul in:fly={{ y: -10, duration: 200 }}>
                {#each table.columns as column (column.name)}
                  {@const columnExpanded = isColumnExpanded(column)}
                  <li class="column">
                    <button
                      class={["bullet", column.bulletColor]}
                      onclick={() => toggleColumn(column)}
                    >
                      {expansionCharacter(columnExpanded)}
                    </button>
                    <span class="nodeType">col</span>
                    {column.name}
                    {@render renderExistence(column.existence, (e) => {
                      addDropColumn(column, e);
                    })}
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
            {@render renderExistence(sequence.existence, (e) => {
              addDropSequence(sequence, e);
            })}
            {@render renderComparison(sequence.type, true)}
            {@render renderComparison(sequence.start, true)}
            {@render renderRemainder(sequence.remainingErrors)}
          </li>
        {/each}
      </ul>
    {:catch error}
      {error.message}
    {/await}
  </div>
  {#if sql.size > 0}
    <ul class="sql">
      {#each sql as s}
        <li>{s}</li>
      {/each}
    </ul>
  {/if}
</div>

{#snippet renderConstraints(constraints: ReadonlyUseConstraintArray)}
  {#each constraints as constraint (constraint.name)}
    <li>
      <button class={["bullet", constraint.bulletColor]} disabled={true}>
        .
      </button>
      <span class="nodeType">{constraint.type}</span>
      {constraint.nameShort()}
      {@render renderExistence(constraint.existence, (e) => {
        addDropConstraint(constraint, e);
      })}
      {@render renderComparison(constraint.clause, true)}
      {@render renderRemainder(constraint.remainingErrors)}
    </li>
  {/each}
{/snippet}

{#snippet renderExistence(
  existence: UseExistence,
  onclick: (e: Existence) => void,
)}
  {#if existence}
    <!-- svelte-ignore <a11y_click_events_have_key_events> -->
    <!-- svelte-ignore <a11y_no_static_element_interactions> -->
    <span class={existence.color} onclick={() => onclick(existence.text)}
      >{existence.text}</span
    >
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
            {#if value.actualRaw}
              <tr><td>raw:</td><td>{value.shortener(value.actualRaw)}</td></tr>
            {/if}
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
  div.container {
    display: flex;
    flex-direction: row;
    flex-wrap: nowrap;
    width: 100vw;
    height: 80vh;
  }

  div.tree {
    overflow-y: scroll;
  }

  ul.sql {
    max-width: 50vw;
    overflow-y: scroll;
    li {
      list-style-type: disc;
    }
  }

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
