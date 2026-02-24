<script lang="ts">
  import { fly } from "svelte/transition";
  import { get, post } from "@/api/api";
  import {
    type SchemaAlterResponse,
    isNotConnected,
    type Schema as ApiSchema,
    type SchemaPatchRequest,
    type SchemaPatchResponse,
  } from "@/api/types";
  import {
    Schema,
    type Existence,
    type AdditionalErrors,
    type Comparison,
    type Constraint,
    type Bullet,
    type ExpandableBullet,
    Collapser,
    Table,
    Sequence,
    FixAggregator,
  } from "@/UseSchema.svelte.js";
  import { PromiseTracker } from "@/api/PromiseTracker.svelte";
  import PromiseTrackerReload from "@/api/PromiseTrackerReload.svelte";
  import Connect from "@/Connect.svelte";
  import {
    encodePatch,
    type Fixable,
    workOnFixes,
    type FixedFixable,
    setFix,
  } from "@/SchemaFix";
  import { useWithStoreSingle } from "@/utils";
  import SchemaMaintain from "@/SchemaMaintain.svelte";

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

  function spanClassExistence(
    existence: { color: "red" | "yellow" },
    fixable: Fixable,
  ): string {
    return fixable.fix ? existence.color + "Fixed" : existence.color;
  }

  function spanClassComparison(
    comparison: Comparison,
    fixable: Fixable,
  ): string | undefined {
    return fixable.fix ? comparison.color + "Fixed" : comparison.color;
  }

  type Patch = {
    readonly fix: FixedFixable;
    readonly url: string;
    readonly promise: Promise<SchemaAlterResponse>;
  };

  let patchesEncodedForJava = $state(true);
  let patchesAlterTablesJoined = $state(true);

  const patches: Patch[] = $derived(
    workOnFixes(fixes, patchesAlterTablesJoined).map((fix) => {
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

  function hasMore(fix: FixedFixable): boolean {
    return fix.joinable === "middle" || fix.joinable === "tail";
  }

  type PatchLog = {
    readonly sql: string;
    readonly success?: SchemaPatchResponse;
    readonly failure?: string;
  };

  const patchesLog: PatchLog[] = $state([]);

  function runPatches(): void {
    let promise = Promise.resolve();
    patches.forEach((p) => (promise = promise.then(() => runPatch(p.promise))));
    promise.catch(() => {}); // error handled in runPatch already
  }

  function runPatch(request: Promise<SchemaAlterResponse>): Promise<void> {
    return request.then((response) =>
      post<SchemaPatchRequest, SchemaPatchResponse>("schema/patch", {
        sql: response.sql,
      }).then(
        (patchResponse) => {
          patchesLog.push({
            sql: response.sql,
            success: patchResponse,
          });
        },
        (error) => {
          patchesLog.push({
            sql: response.sql,
            failure: error.message,
          });
          return Promise.reject(error); // rethrow the error to stop the chain in runPatches
        },
      ),
    );
  }

  function truncatePatchFailure(message: string) {
    const prefix = "schema/patch: response code 400 ";
    if (!message.startsWith(prefix)) return message;

    return message.substring(prefix.length);
  }

  function copyPatches(): void {
    Promise.all(patches.map((patch) => patch.promise)).then((responses) => {
      navigator.clipboard.writeText(
        responses.map((response) => response.sql + "\n").join(""),
      );
    });
  }

  function flushPatchesLog(): void {
    patchesLog.length = 0; // make it empty
  }

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type ReadonlyConstraintArray = readonly Constraint[];

  // workaround problem in svelte IDEA plugin, otherwise this type could be inlined
  type String = string;

  // workaround problem in svelte IDEA plugin, otherwise this method could be inlined
  function asInputElement(target: EventTarget | null): HTMLInputElement {
    return target as HTMLInputElement;
  }
</script>

<div class="maintain">
  <SchemaMaintain reloadTracker={schemaT} />
</div>

<div class="container">
  <div class="tree">
    {#await schemaT.promise()}
      fetching data
    {:then schema}
      {@const tableCollapser = schema.collapser(undefined)}
      {@const sequenceCollapser = schema.collapser("sequence")}
      <div class="checkboxCollector">
        {@render bullet(schema)}
        Schema
        <PromiseTrackerReload tracker={schemaT} />
        <br />
        <label
          ><input
            type="checkbox"
            bind:checked={schema.showAllTablesSequences}
          />show all tables / sequences
        </label><br />
        {@render fixAggregator(schema.nodesMissingWithoutRename)}
        {@render fixAggregator(schema.nodesUnusedWithoutRename)}
        {@render fixAggregator(schema.columnsWithTypeMismatch)}
        {@render fixAggregator(schema.constraintsWithClauseMismatch)}
      </div>
      <ul>
        {#each schema.tables() as table (table.name)}
          {@render collapsed(table, tableCollapser)}
          {#if !tableCollapser || table.bulletColor}
            <li>
              {@render bulletExpandable(table)}
              {table.name}
              {@render existence(
                table.existence,
                table,
                table.renameFrom(schema),
                table.renameTo(schema),
              )}
              {#if table.expanded}
                {@render additionalErrors(table)}
                {#if table.columns().length || table.constraints().length}
                  <ul in:fly={{ y: -10, duration: 200 }}>
                    {#each table.columns() as column (column.name)}
                      <li>
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
                          column.expanded,
                        )}
                        {#if column.expanded}
                          {@render additionalErrors(column)}
                          {#if column.constraints().length > 0}
                            <ul in:fly={{ y: -10, duration: 200 }}>
                              {@render constraints(column.constraints())}
                            </ul>
                          {/if}
                        {/if}
                      </li>
                    {/each}
                    {@render constraints(table.constraints())}
                  </ul>
                {/if}
              {/if}
            </li>
          {/if}
        {/each}
        {@render collapsed(undefined, tableCollapser)}
        {#each schema.sequences() as sequence (sequence.name)}
          {@render collapsed(sequence, sequenceCollapser)}
          {#if !sequenceCollapser || sequence.bulletColor}
            <li>
              {@render bullet(sequence)}
              <span class="nodeType">sequence</span>
              {sequence.name}
              {@render existence(
                sequence.existence,
                sequence,
                sequence.renameFrom(schema),
                sequence.renameTo(schema),
              )}
              {@render comparison(sequence, sequence.type, undefined, true)}
              {@render comparison(sequence, sequence.start, undefined, true)}
              {@render additionalErrors(sequence)}
            </li>
          {/if}
        {/each}
        {@render collapsed(undefined, sequenceCollapser)}
      </ul>
    {:catch error}
      {#if isNotConnected(error)}
        <Connect tracker={schemaT} />
      {:else}
        {error.message}
      {/if}
    {/await}
  </div>
  {#if patchesLog.length > 0 || patches.length > 0}
    <div class="sql">
      <button class="run" onclick={() => runPatches()}>RUN</button>
      <label
        ><input type="checkbox" bind:checked={patchesEncodedForJava} />encoded
        for java</label
      ><br />
      <label
        ><input type="checkbox" bind:checked={patchesAlterTablesJoined} />join
        ALTER TABLE statements on the same table</label
      ><br />
      {#if patchesLog.length > 0}
        <button class="run" onclick={() => flushPatchesLog()}>flush</button>
        <ul>
          {#each patchesLog as { sql, success, failure }}
            <li class={{ failure }}>
              {encodePatch(undefined, patchesEncodedForJava, sql)}
              {#if failure}
                <br />
              {/if}
              {#if patchesEncodedForJava}<!-- the end-of-line comment -->
                //
              {:else}
                --
              {/if}
              {#if success}
                {#if success.rows > 0}
                  {success.rows}&nbsp;rows,
                {/if}
                {Math.round(success.elapsedNanos / 1000000)}ms
              {/if}
              {#if failure}
                {truncatePatchFailure(failure)}
              {/if}
            </li>
          {/each}
        </ul>
        <hr />
      {/if}
      <button class="run" onclick={() => copyPatches()}>copy</button>
      <ul>
        {#each patches as { fix, url, promise } (url)}
          <li class={{ more: hasMore(fix) }}>
            {#await promise}
              <span class="nodeType">{fix.subject}</span>
              {fix.name}
              {fix.fix.method}
            {:then response}
              {encodePatch(fix.joinable, patchesEncodedForJava, response.sql)}
            {:catch error}
              <span class="red">{error.message}</span>
            {/await}
          </li>
        {/each}
      </ul>
    </div>
  {/if}
</div>

{#snippet fixAggregator(fixes: FixAggregator<Fixable>)}
  {#if fixes.all.length > 0}
    <label
      ><input
        type="checkbox"
        checked={fixes.checked}
        indeterminate={fixes.indeterminate}
        oninput={(e) => {
          fixes.setFixes(asInputElement(e.target).checked);
        }}
      />{fixes.label} <small>({fixes.checkedLength}/{fixes.all.length})</small>
    </label><br />
  {/if}
{/snippet}

{#snippet collapsed(
  tableOrSequence: Table | Sequence | undefined,
  collapser: Collapser | undefined,
)}
  {@const segment = collapser?.isShown(tableOrSequence)}
  {#if segment}
    <li>
      {@render bullet(segment.first)}
      {#if collapser?.nodeType}
        <span class="nodeType">{collapser.nodeType}</span>
      {/if}
      {#if segment.count > 1}
        {segment.first.name} ... {segment.last.name}
        <small>({segment.count})</small>
      {:else}
        {segment.first.name}
      {/if}
    </li>
  {/if}
{/snippet}

{#snippet constraints(constraints: ReadonlyConstraintArray)}
  {#each constraints as constraint (constraint.name)}
    <li>
      {@render bullet(constraint)}
      <span class="nodeType">{constraint.type}</span>
      {constraint.nameShort()}<button
        class="clipboard"
        title={constraint.name}
        onclick={() => navigator.clipboard.writeText(constraint.name)}
        >&#x25A2;</button
      >
      {@render existence(constraint.existence, constraint, [], [])}
      {@render comparison(constraint, constraint.clause, "recreate", true)}
      {@render additionalErrors(constraint)}
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
    <span class={spanClassExistence(existence, fixable)}>{existence.text}</span>
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
          class="renameFrom"
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
          class="renameTo"
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
        <span class={spanClassComparison(value, fixable)}
          >{value.name} mismatch:</span
        >
        {@render comparisonAdjust(fixable, modify)}
        <table class="comparison">
          <tbody>
            <tr>
              <td>required:</td>
              <td>{@render shortened(value.shortener, value.expected)}</td>
            </tr>
            <tr>
              <td>actual:</td>
              <td>{@render shortened(value.shortener, value.actual)}</td>
            </tr>
            {#if value.actualRaw}
              <tr>
                <td>raw:</td>
                <td>{@render shortened(value.shortener, value.actualRaw)}</td>
              </tr>
            {/if}
          </tbody>
        </table>
      {:else}
        {@render shortened(value.shortener, value.expected)}
      {/if}
    {:else if value.actual}
      <span class={spanClassComparison(value, fixable)}
        >{value.name} mismatch</span
      >
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

{#snippet shortened(shortener: (s: String) => String, value: String)}
  {shortener(value)}<button
    class="clipboard"
    title={value}
    onclick={() => navigator.clipboard.writeText(value)}>&#x25A2;</button
  >
{/snippet}

{#snippet additionalErrors(rr: AdditionalErrors)}
  {#if rr.additionalErrors.length}
    <ul>
      {#each rr.additionalErrors as r}
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

  div.sql {
    max-width: 50vw;
    overflow-y: scroll;
    ul {
      font-size: 60%;
      padding-left: 1.5em;
      li {
        list-style-type: disc;
      }
      li.more {
        list-style-type: circle;
        padding-left: 2em;
      }
      li.failure {
        color: #880000;
      }
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

  button.redFixed {
    background: #d88d8d;
  }

  button.yellow {
    background: #ffff00;
  }

  button.yellowFixed {
    background: #f8f8a7;
  }

  span.nodeType {
    font-size: 60%;
  }

  span.red {
    color: #880000;
  }

  span.redFixed {
    color: #884444;
    text-decoration: line-through;
  }

  span.yellow {
    color: #887800;
  }

  span.yellowFixed {
    color: #87785b;
    text-decoration: line-through;
  }

  table.comparison td {
    vertical-align: top;
  }

  select.renameTo {
    width: 5.3em; /* just show 'rename' when collapsed */
  }

  select.renameFrom {
    width: 5.3em; /* just show 'rename' when collapsed */
  }

  div.checkboxCollector {
    position: sticky;
    top: 0;
    z-index: 1;
    background: white;
  }

  button.run {
    float: right;
  }

  button.clipboard {
    color: lightgray;
    border-width: 0;
    background: white;
    padding: 0;
  }

  button.clipboard:active {
    color: black;
  }
</style>
