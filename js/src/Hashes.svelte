<script lang="ts">
  import { fly } from "svelte/transition";
  import "@/table-grey.css";
  import { get, SELF } from "@/api/api";
  import { type Hash as ApiHash } from "@/api/types";
  import { PromiseTracker } from "@/api/PromiseTracker.svelte.js";
  import PromiseTrackerReload from "@/api/PromiseTrackerReload.svelte";
  import { Hash } from "@/UseHashes.svelte";
  import { format, useWithStore, shortifyPeer } from "@/utils";

  const hashesStore = new Map<string, Hash>();

  const hashes = new PromiseTracker(() =>
    get<ApiHash[]>("hashes").then((response) =>
      useWithStore(
        hashesStore,
        toId,
        (source) => new Hash(source),
        (target, source) => target.update(source),
        response,
      ),
    ),
  );
  const errors: Error[] = $state([]);

  const hosts = new PromiseTracker(() =>
    get<string[]>("peers").then((x) => [SELF].concat(x)),
  );

  let hostsLast = $derived(hosts.last() || [SELF]);

  function measureAll(hashes: Hash[]) {
    let promise = Promise.resolve();
    hashes.forEach(
      (h) => (promise = promise.then(() => h.measureHost(errors, SELF))), // TODO measure peers as well
    );
  }

  function toId(response: ApiHash): string {
    return response.type + "." + response.name;
  }
</script>

<table class="grey">
  <caption>
    Hashes
    <PromiseTrackerReload tracker={hashes} />
  </caption>
  <thead>
    <tr class="relative">
      <th rowspan="2">Type</th>
      <th rowspan="2"
        >Name{#await hashes.promise() then hashes}
          {#if hashes.length > 0}
            <button class="measure" onclick={() => measureAll(hashes)}
              >&#128336;&#xfe0e;</button
            >
          {/if}
        {:catch _error}
          <!-- eslint-disable-next-line svelte/no-unused-svelte-ignore -->
          <!-- svelte-ignore block_empty -->
        {/await}</th
      >
      <th colspan="2">Plain Text</th>
      <th colspan="2">Algorithm</th>
      <th colspan={hostsLast.length} class="time">
        Time<small>/ns</small>
      </th>
    </tr>
    <tr>
      <th>Limit</th>
      <th>Validator</th>
      <th>ID</th>
      <th>Description</th>
      <!-- eslint-disable-next-line svelte/require-each-key -- hosts are not necessarily unique -->
      {#each hostsLast as host}
        <th class:self={host === SELF} title={host === SELF ? undefined : host}
          >{shortifyPeer(host)}</th
        >
      {/each}
    </tr>
  </thead>
  <tbody>
    {#await hashes.promise()}
      <tr>
        <td colspan="7" class="empty">fetching data</td>
      </tr>
    {:then hashes}
      {#each hashes as hash (toId(hash))}
        <tr class="relative">
          <td>
            <button class="hash" onclick={() => hash.toggle()}>
              {#if hash.isToggled()}
                &#8594;
              {:else}
                &#8595;
              {/if}
            </button>
            {hash.type}
          </td>
          <td
            >{hash.name}<button
              class="measure"
              onclick={() => hash.measure(errors, hostsLast)}
              >&#128336;&#xfe0e;</button
            ></td
          >
          <td class="number">{hash.plainTextLimit}</td>
          <td class:notAvailable={!hash.plainTextValidator}
            >{hash.plainTextValidator}</td
          >
          <td>{hash.algorithmID}</td>
          <td>{hash.algorithmDescription}</td>
          <!-- eslint-disable-next-line svelte/require-each-key -- hosts are not necessarily unique -->
          {#each hostsLast as host}
            <td class="number">
              {format(hash.getMeasurement(host))}
            </td>
          {/each}
        </tr>
        {#if hash.isToggled()}
          <tr in:fly={{ y: -10, duration: 200 }}>
            <td colspan="7" class="expansion">
              <input bind:value={hash.plainText} placeholder="Plain Text" />
              <button onclick={() => hash.computeHash(errors)}>Hash</button>
              <br />
              <small>{hash.getPlainTextHashed()}</small>
            </td>
          </tr>
        {/if}
      {:else}
        <tr>
          <td colspan="7" class="empty">There are no hashes in the model.</td>
        </tr>
      {/each}
    {:catch error}
      <tr>
        <td colspan="7" class="error">{error.message}</td>
      </tr>
    {/await}
  </tbody>
</table>

{#if errors.length > 0}
  <h2>Errors</h2>
  <ul>
    <!-- eslint-disable-next-line svelte/require-each-key -- errors are not unique -->
    {#each errors as error}
      <li>{error}</li>
    {/each}
  </ul>
{/if}

<style>
  table {
    margin-left: 1em;
  }

  button.measure {
    position: absolute;
    top: 0;
    right: -25px;
    border: 0;
    background: white;
    font-size: 120%;
  }

  button.hash {
    position: absolute;
    top: 0;
    left: -20px;
    border: 0;
    background: white;
  }

  td.expansion {
    max-width: 40px;
  }

  td.expansion input {
    width: 90%;
  }

  tr.relative {
    position: relative;
  }

  th.self {
    font-style: italic;
  }

  th.time {
    min-width: 7em;
  }
</style>
