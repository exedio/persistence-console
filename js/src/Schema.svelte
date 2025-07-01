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
  import {
    type SchemaCheckbox as Checkbox,
    workOnCheckboxes,
  } from "@/SchemaCheckbox";

  const schemaT = new PromiseTracker(() => get<SchemaResponse>("schema"));

  const expandedTables = new Expander<UseTable>((t) => t.name);

  const expandedColumns = new Expander<UseColumn>(
    (c) => c.tableName + "." + c.name,
  );

  type Modify = {
    subject: "column" | "constraint";
    tableName: string;
    name: string;
    label: "adjust" | "recreate";
  };

  const checkboxes = new SvelteMap<String, Checkbox>();

  type Fix = {
    readonly checkbox: Checkbox;
    readonly url: string;
    readonly promise: Promise<AlterSchemaResponse>;
  };

  const fixes: Fix[] = $derived(
    workOnCheckboxes(Array.from(checkboxes.values())).map((checkbox) => {
      const url = checkboxToUrl(checkbox);
      return {
        checkbox,
        url,
        promise: urlToPromise(url),
      };
    }),
  );

  const fixesCacheByUrl = new Map<String, Promise<AlterSchemaResponse>>(); // must not be SvelteMap!

  function urlToPromise(url: string): Promise<AlterSchemaResponse> {
    const cached = fixesCacheByUrl.get(url);
    if (cached) return cached;

    const result = get<AlterSchemaResponse>("alterSchema?" + url);
    fixesCacheByUrl.set(url, result);
    return result;
  }

  function checkboxToUrl({
    subject,
    tableName,
    name,
    method,
  }: Checkbox): string {
    return (
      "subject=" +
      subject +
      (tableName ? "&table=" + tableName : "") +
      "&name=" +
      name +
      "&method=" +
      method
    );
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

  // workaround problem in svelte IDEA plugin, otherwise this method could be inlined
  function asModify(
    subject: "column" | "constraint",
    tableName: String,
    name: String,
    label: "adjust" | "recreate",
  ): Modify {
    return {
      subject,
      tableName,
      name,
      label,
    };
  }
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
                    {@render comparison(
                      column.type,
                      asModify("column", table.name, column.name, "adjust"),
                      columnExpanded,
                    )}
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
            {@render comparison(sequence.type, undefined, true)}
            {@render comparison(sequence.start, undefined, true)}
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
      {#each fixes as { checkbox, url, promise } (url)}
        <li>
          <span class="nodeType">{checkbox.subject}</span>
          {checkbox.name}
          {#await promise}
            {checkbox.method}
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
      {@render comparison(
        constraint.clause,
        asModify(
          "constraint",
          constraint.tableName,
          constraint.name,
          "recreate",
        ),
        true,
      )}
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
              method: existence.text === "missing" ? "add" : "drop",
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

{#snippet comparison(
  value: UseComparison | undefined,
  modify: Modify | undefined,
  expanded: Boolean,
)}
  {#if value}
    {#if expanded}
      {#if value.actual}
        <span class={value.color}>{value.name} mismatch:</span>
        {@render comparisonAdjust(modify)}
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
      {@render comparisonAdjust(modify)}
    {/if}
  {/if}
{/snippet}

{#snippet comparisonAdjust(modify: Modify | undefined)}
  {#if modify}
    {@const key = modify.subject + "/" + modify.tableName + "/" + modify.name}
    <label
      ><input
        type="checkbox"
        checked={checkboxes.has(key)}
        oninput={(e) => {
          if (asInputElement(e.target).checked) {
            checkboxes.set(key, {
              subject: modify.subject,
              tableName: modify.tableName,
              name: modify.name,
              method: "modify",
            });
          } else {
            checkboxes.delete(key);
          }
        }}
      />{modify.label}</label
    >
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
