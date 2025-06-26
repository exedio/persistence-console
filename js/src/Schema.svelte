<script lang="ts">
  import { fly } from "svelte/transition";
  import { get } from "@/api/api";
  import {
    type AlterSchemaResponse,
    isNotConnected,
    type SchemaResponse,
  } from "@/api/types";
  import { SvelteMap } from "svelte/reactivity";
  import {
    type UseColumn,
    useSchema,
    type UseTable,
    type UseExistence,
    type UseComparison,
    type UseConstraint,
    type Color,
  } from "@/UseSchema.js";
  import { PromiseTracker } from "@/api/PromiseTracker.svelte";
  import PromiseTrackerReload from "@/api/PromiseTrackerReload.svelte";
  import Connect from "@/Connect.svelte";
  import { Expander } from "@/Expander.js";

  const schemaT = new PromiseTracker(() => get<SchemaResponse>("schema"));

  const expandedTables = new Expander<UseTable>((t) => t.name);

  const expandedColumns = new Expander<UseColumn>(
    (c) => c.tableName + "." + c.name,
  );

  type Checkbox = {
    readonly subject: "table" | "column" | "constraint" | "sequence";
    readonly tableName: string | undefined; // undefined for subject "table" and "sequence"
    readonly name: string;
    readonly existence: Existence;
  };

  const checkboxes = new SvelteMap<String, Checkbox>();

  type Fix = {
    readonly checkbox: Checkbox;
    readonly promise: Promise<AlterSchemaResponse>;
  };

  const fixes: Fix[] = $derived(
    Array.from(checkboxes.values()).map((cb) => ({
      checkbox: cb,
      promise: checkboxToPromise(cb),
    })),
  );

  const fixesCache = new Map<String, Promise<AlterSchemaResponse>>(); // must not be SvelteMap!

  function checkboxToPromise(checkbox: Checkbox): Promise<AlterSchemaResponse> {
    const key =
      checkbox.subject + "." + checkbox.tableName + "." + checkbox.name;
    const cached = fixesCache.get(key);
    if (cached) return cached;

    const result = checkboxToPromiseUncached(checkbox);
    fixesCache.set(key, result);
    return result;
  }

  function checkboxToPromiseUncached({
    subject,
    tableName,
    name,
    existence,
  }: Checkbox): Promise<AlterSchemaResponse> {
    const method = existence === "missing" ? "add" : "drop";
    switch (subject) {
      case "table":
        return get<AlterSchemaResponse>(
          "schema/" + method + "Table?name=" + name,
        );
      case "column":
        return get<AlterSchemaResponse>(
          "schema/" + method + "Column?table=" + tableName + "&name=" + name,
        );
      case "constraint":
        return get<AlterSchemaResponse>(
          "schema/" +
            method +
            "Constraint?table=" +
            tableName +
            "&name=" +
            name,
        );
      case "sequence":
        return get<AlterSchemaResponse>(
          "schema/" + method + "Sequence?name=" + name,
        );
    }
  }

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type ReadonlyUseConstraintArray = readonly UseConstraint[];

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type ReadonlyStringArray = readonly string[];

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type Boolean = boolean;

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type String = string;

  // workaround problem in svelte IDEA plugin, otherwise this method could be inlined
  function asInputElement(target: EventTarget | null): HTMLInputElement {
    return target as HTMLInputElement;
  }

  type Existence = "missing" | "unused";
</script>

<div class="container">
  <div class="tree">
    {#await schemaT.promise()}
      fetching data
    {:then schemaApi}
      {@const schema = useSchema(schemaApi)}
      {@render expanderDisabled(schema.bulletColor)}
      Schema
      <PromiseTrackerReload tracker={schemaT} />
      <ul>
        {#each schema.tables as table (table.name)}
          {@const tableExpanded = expandedTables.has(table)}
          <li class="table">
            {@render expander(expandedTables, table, table.bulletColor)}
            <span class="nodeType">tab</span>
            {table.name}
            {@render existence(table.existence, "table", undefined, table.name)}
            {#if tableExpanded}
              {@render remainder(table.remainingErrors)}
            {/if}
            {#if tableExpanded && (table.columns.length || table.constraints.length)}
              <ul in:fly={{ y: -10, duration: 200 }}>
                {#each table.columns as column (column.name)}
                  {@const columnExpanded = expandedColumns.has(column)}
                  <li class="column">
                    {@render expander(
                      expandedColumns,
                      column,
                      column.bulletColor,
                    )}
                    <span class="nodeType">col</span>
                    {column.name}
                    {@render existence(
                      column.existence,
                      "column",
                      table.name,
                      column.name,
                    )}
                    {@render comparison(column.type, columnExpanded)}
                    {#if columnExpanded}
                      {@render remainder(column.remainingErrors)}
                    {/if}
                    {#if columnExpanded && column.constraints.length > 0}
                      <ul in:fly={{ y: -10, duration: 200 }}>
                        {@render constraints(column.constraints)}
                      </ul>
                    {/if}
                  </li>
                {/each}
                {@render constraints(table.constraints)}
              </ul>
            {/if}
          </li>
        {/each}
        {#each schema.sequences as sequence (sequence.name)}
          <li class="sequence">
            {@render expanderDisabled(sequence.bulletColor)}
            <span class="nodeType">seq</span>
            {sequence.name}
            {@render existence(
              sequence.existence,
              "sequence",
              undefined,
              sequence.name,
            )}
            {@render comparison(sequence.type, true)}
            {@render comparison(sequence.start, true)}
            {@render remainder(sequence.remainingErrors)}
          </li>
        {/each}
      </ul>
    {:catch error}
      {#if isNotConnected(error)}
        <Connect tracker={schemaT} />
      {:else}
        {error.message}
      {/if}
    {/await}
  </div>
  {#if fixes.length > 0}
    <ul class="sql">
      {#each fixes as { checkbox, promise } (checkbox.subject + "." + checkbox.tableName + "." + checkbox.name)}
        <li>
          <span class="nodeType">{checkbox.subject}</span>
          {checkbox.name}
          {#await promise}
            {checkbox.existence}
          {:then response}
            <small>{response.sql}</small>
          {:catch error}
            <span class="red">{error.message}</span>
          {/await}
        </li>
      {/each}
    </ul>
  {/if}
</div>

{#snippet constraints(constraints: ReadonlyUseConstraintArray)}
  {#each constraints as constraint (constraint.name)}
    <li>
      {@render expanderDisabled(constraint.bulletColor)}
      <span class="nodeType">{constraint.type}</span>
      {constraint.nameShort()}
      {@render existence(
        constraint.existence,
        "constraint",
        constraint.tableName,
        constraint.name,
      )}
      {@render comparison(constraint.clause, true)}
      {@render remainder(constraint.remainingErrors)}
    </li>
  {/each}
{/snippet}

{#snippet expander<E>(expander: Expander<E>, element: E, color: Color)}
  <button class={["bullet", color]} onclick={() => expander.toggle(element)}>
    {expander.has(element) ? "-" : "+"}
  </button>
{/snippet}

{#snippet expanderDisabled(color: Color)}
  <button class={["bullet", color]} disabled={true}>.</button>
{/snippet}

{#snippet existence(
  existence: UseExistence,
  subject: "table" | "column" | "constraint" | "sequence",
  tableName: String | undefined,
  name: String,
)}
  {#if existence}
    {@const key = subject + "/" + tableName + "/" + name}
    <span class={existence.color}>{existence.text}</span>
    <label
      ><input
        type="checkbox"
        checked={checkboxes.has(key)}
        oninput={(e) => {
          if (asInputElement(e.target).checked) {
            checkboxes.set(key, {
              subject,
              tableName,
              name,
              existence: existence.text,
            });
          } else {
            checkboxes.delete(key);
          }
        }}
      />{existence.text === "missing"
        ? subject === "table" || subject === "sequence"
          ? "create"
          : "add"
        : "drop"}</label
    >
  {/if}
{/snippet}

{#snippet comparison(value: UseComparison | undefined, expanded: Boolean)}
  {#if value}
    {#if expanded}
      {#if value.actual}
        <span class={value.color}>{value.name} mismatch:</span>
        <table class="comparison">
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

{#snippet remainder(remainder: ReadonlyStringArray)}
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

  table.comparison td {
    vertical-align: top;
  }
</style>
