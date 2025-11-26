<script lang="ts">
  import { fly } from "svelte/transition";
  import { get, post } from "@/api/api";
  import {
    type SchemaAlterResponse,
    isNotConnected,
    type SchemaMaintainRequest,
    type SchemaMaintainResponse,
    type Schema as ApiSchema,
    type SchemaMaintainOperation,
  } from "@/api/types";
  import {
    Schema,
    type Existence,
    type Comparison,
    type Constraint,
    type Bullet,
    type ExpandableBullet,
  } from "@/UseSchema.svelte.js";
  import { PromiseTracker } from "@/api/PromiseTracker.svelte";
  import PromiseTrackerReload from "@/api/PromiseTrackerReload.svelte";
  import Connect from "@/Connect.svelte";
  import {
    encodeJava,
    type Fixable,
    workOnFixes,
    type FixedFixable,
  } from "@/SchemaFix";
  import { useWithStoreSingle } from "@/utils";
  import { successMessage } from "@/UseSchemaMaintain";

  function maintain(
    operation: SchemaMaintainOperation,
    create: boolean = false,
  ): boolean {
    const confirmMessage = maintainConfirmMessage(operation);
    if (
      confirmMessage &&
      !confirm(
        confirmMessage +
          "\nDo you really want to do this?" +
          (create ? "\nAfterwards all tables will be recreated." : ""),
      )
    )
      return false;

    maintainRunning = true;
    maintainMessage = operation + " started ...";
    maintainCreateMessage = undefined;
    maintainPost(operation)
      .then((r) => {
        maintainMessage = successMessage(operation, r);

        if (!create) {
          return Promise.resolve();
        } else {
          maintainCreateMessage = "create started ...";

          return maintainPost("create")
            .then((r2) => {
              maintainCreateMessage = successMessage("create", r2);
            })
            .catch((e2) => {
              maintainCreateMessage = "create failed: " + e2.message;
            });
        }
      })
      .catch((e) => {
        maintainMessage = operation + " failed: " + e.message;
      })
      .finally(() => {
        maintainRunning = false;
        if (operation !== "delete") return schemaT.reload();
      });
    return true;
  }

  function maintainConfirmMessage(operation: SchemaMaintainOperation) {
    switch (operation) {
      case "tearDown":
        return "This operation will desperately try to drop all your database tables.";
      case "drop":
        return "This operation will drop all your database tables.";
      case "delete":
        return "This operation will delete the contents of your database tables.";
      default:
        return undefined;
    }
  }

  function maintainPost(
    operation: SchemaMaintainOperation,
  ): Promise<SchemaMaintainResponse> {
    return post<SchemaMaintainRequest, SchemaMaintainResponse>(
      "schema/maintain",
      {
        operation: operation,
      },
    );
  }

  let maintainRunning: boolean = $state(false);
  let maintainMessage: string | undefined = $state(undefined);
  let maintainCreateMessage: string | undefined = $state(undefined);

  const schemaStore = new Map<string, Schema>();

  const schemaT = new PromiseTracker(() =>
    get<ApiSchema>("schema").then((response) =>
      useWithStoreSingle(
        schemaStore,
        (source) => new Schema(source),
        (target, source) => target.update(source),
        response,
      ),
    ),
  );

  type Modify = "adjust" | "recreate";

  const fixes: FixedFixable[] = $derived(schemaT.last()?.fixables() ?? []);

  function setFix(
    set: boolean,
    fixable: Fixable,
    method: "add" | "drop" | "modify" | "rename",
    value: string | undefined,
  ) {
    fixable.fix = set ? { method, value } : undefined;
  }

  function renameFromValue(fixable: Fixable): string {
    for (const i of fixes) {
      if (
        i.subject === fixable.subject &&
        i.tableName === fixable.tableName &&
        i.fix.value === fixable.name
      )
        return i.name;
    }
    return RENAME_NONE;
  }

  type Patch = {
    readonly fix: FixedFixable;
    readonly url: string;
    readonly promise: Promise<SchemaAlterResponse>;
  };

  const patches: Patch[] = $derived(
    workOnFixes(fixes).map((fix) => {
      const url = checkboxToUrl(fix);
      return {
        fix,
        url,
        promise: urlToPromise(url),
      };
    }),
  );

  const patchesCacheByUrl = new Map<string, Promise<SchemaAlterResponse>>(); // must not be SvelteMap!

  function urlToPromise(url: string): Promise<SchemaAlterResponse> {
    const cached = patchesCacheByUrl.get(url);
    if (cached) return cached;

    const result = get<SchemaAlterResponse>("schema/alter?" + url);
    patchesCacheByUrl.set(url, result);
    return result;
  }

  function checkboxToUrl({
    subject,
    tableName,
    name,
    fix,
  }: FixedFixable): string {
    return (
      "subject=" +
      subject +
      (tableName ? "&table=" + tableName : "") +
      "&name=" +
      name +
      "&method=" +
      fix.method +
      (fix.value ? "&value=" + fix.value : "")
    );
  }

  const RENAME_NONE = "<NONE>";

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type ReadonlyConstraintArray = readonly Constraint[];

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type ReadonlyStringArray = readonly string[];

  // workaround problem in svelte IDEA plugin, otherwise this method could be inlined
  function asInputElement(target: EventTarget | null): HTMLInputElement {
    return target as HTMLInputElement;
  }
</script>

<div class="maintain">
  <button disabled={maintainRunning} onclick={() => maintain("create")}
    >create</button
  >
  <button disabled={maintainRunning} onclick={() => maintain("tearDown")}
    >tear down</button
  >
  <button disabled={maintainRunning} onclick={() => maintain("drop")}
    >drop</button
  >
  &nbsp;
  <button disabled={maintainRunning} onclick={() => maintain("tearDown", true)}
    >tear down & create</button
  >
  <button disabled={maintainRunning} onclick={() => maintain("drop", true)}
    >drop & create</button
  >
  &nbsp;
  <button disabled={maintainRunning} onclick={() => maintain("delete")}
    >delete</button
  >
  {#if maintainMessage}
    <div>
      {maintainMessage}{maintainCreateMessage
        ? ", " + maintainCreateMessage
        : ""}
    </div>
  {/if}
</div>

<div class="container">
  <div class="tree">
    {#await schemaT.promise()}
      fetching data
    {:then schema}
      {@render bullet(schema)}
      Schema
      <PromiseTrackerReload tracker={schemaT} />
      <ul>
        {#each schema.tables() as table (table.name)}
          {@const tableExpanded = table.expanded}
          <li class="table">
            {@render bulletExpandable(table)}
            {table.name}
            {@render existence(
              table.existence,
              table,
              table.renameFrom(schema),
              table.renameTo(schema),
            )}
            {#if tableExpanded}
              {@render remainder(table.remainingErrors)}
            {/if}
            {#if tableExpanded && (table.columns().length || table.constraints().length)}
              <ul in:fly={{ y: -10, duration: 200 }}>
                {#each table.columns() as column (column.name)}
                  {@const columnExpanded = column.expanded}
                  <li class="column">
                    {@render bulletExpandable(column)}
                    {column.name}
                    {@render existence(
                      column.existence,
                      column,
                      column.renameFrom(table),
                      column.renameTo(table),
                    )}
                    {@render comparison(
                      column,
                      column.type,
                      "adjust",
                      columnExpanded,
                    )}
                    {#if columnExpanded}
                      {@render remainder(column.remainingErrors)}
                    {/if}
                    {#if columnExpanded && column.constraints().length > 0}
                      <ul in:fly={{ y: -10, duration: 200 }}>
                        {@render constraints(column.constraints())}
                      </ul>
                    {/if}
                  </li>
                {/each}
                {@render constraints(table.constraints())}
              </ul>
            {/if}
          </li>
        {/each}
        {#each schema.sequences() as sequence (sequence.name)}
          <li class="sequence">
            {@render bullet(sequence)}
            <span class="nodeType">sequence</span>
            {sequence.name}
            {@render existence(
              sequence.existence,
              sequence,
              [], // TODO unused sequences
              [], // TODO missing sequences
            )}
            {@render comparison(sequence, sequence.type, undefined, true)}
            {@render comparison(sequence, sequence.start, undefined, true)}
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
  {#if patches.length > 0}
    <ul class="sql">
      {#each patches as { fix, url, promise } (url)}
        <li>
          {#await promise}
            <span class="nodeType">{fix.subject}</span>
            {fix.name}
            {fix.fix.method}
          {:then response}
            <small>{encodeJava(response.sql)}</small>
          {:catch error}
            <span class="red">{error.message}</span>
          {/await}
        </li>
      {/each}
    </ul>
  {/if}
</div>

{#snippet constraints(constraints: ReadonlyConstraintArray)}
  {#each constraints as constraint (constraint.name)}
    <li>
      {@render bullet(constraint)}
      <span class="nodeType">{constraint.type}</span>
      {constraint.nameShort()}
      {@render existence(constraint.existence, constraint, [], [])}
      {@render comparison(constraint, constraint.clause, "recreate", true)}
      {@render remainder(constraint.remainingErrors)}
    </li>
  {/each}
{/snippet}

{#snippet bulletExpandable(bullet: ExpandableBullet)}
  <button
    class={["bullet", bullet.bulletColor]}
    onclick={() => (bullet.expanded = !bullet.expanded)}
  >
    {bullet.expanded ? "-" : "+"}
  </button>
{/snippet}

{#snippet bullet(bullet: Bullet)}
  <button class={["bullet", bullet.bulletColor]} disabled={true}>.</button>
{/snippet}

{#snippet existence(
  existence: Existence,
  fixable: Fixable,
  renameFrom: String[],
  renameTo: String[],
)}
  {#if existence}
    {@const method = existence.text === "missing" ? "add" : "drop"}
    {@const fix = fixable.fix}
    <span class={existence.color}>{existence.text}</span>
    <label
      ><input
        type="checkbox"
        checked={fix && fix.method === method}
        oninput={(e) => {
          setFix(asInputElement(e.target).checked, fixable, method, undefined);
        }}
      />{existence.text === "missing"
        ? fixable.subject === "table" || fixable.subject === "sequence"
          ? "create"
          : "add"
        : "drop"}
    </label>
    {#if renameFrom.length}
      <label>
        <select
          value={renameFromValue(fixable)}
          oninput={(e) => {
            const schema = schemaT.last();
            if (!schema) return;

            const value = asInputElement(e.target).value;
            if (value !== RENAME_NONE) {
              setFix(
                true,
                schema.fixable({
                  ...fixable,
                  name: value,
                }),
                "rename",
                fixable.name,
              );
            } else {
              schema.dropRenames(fixable);
            }
          }}
        >
          <option value={RENAME_NONE}>rename from ...</option>
          {#each renameFrom as option}
            <option value={option}>
              {option}
            </option>
          {/each}
        </select>
      </label>
    {/if}
    {#if renameTo.length}
      <label>
        <select
          value={fix && fix.method === "rename" ? fix.value : RENAME_NONE}
          oninput={(e) => {
            const value = asInputElement(e.target).value;
            setFix(value !== RENAME_NONE, fixable, "rename", value);
          }}
        >
          <option value={RENAME_NONE}>rename to ...</option>
          {#each renameTo as option}
            <option value={option}>
              {option}
            </option>
          {/each}
        </select>
      </label>
    {/if}
  {/if}
{/snippet}

{#snippet comparison(
  fixable: Fixable,
  value: Comparison | undefined,
  modify: Modify | undefined,
  expanded: Boolean,
)}
  {#if value}
    {#if expanded}
      {#if value.actual}
        <span class={value.color}>{value.name} mismatch:</span>
        {@render comparisonAdjust(fixable, modify)}
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
      {@render comparisonAdjust(fixable, modify)}
    {/if}
  {/if}
{/snippet}

{#snippet comparisonAdjust(fixable: Fixable, modify: Modify | undefined)}
  {#if modify}
    <label
      ><input
        type="checkbox"
        checked={fixable.fix?.method === "modify"}
        oninput={(e) => {
          setFix(
            asInputElement(e.target).checked,
            fixable,
            "modify",
            undefined,
          );
        }}
      />{modify}</label
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
  div.maintain {
    margin-bottom: 20px;
  }

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
