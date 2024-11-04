<script lang="ts">
  import { fly } from "svelte/transition";
  import { SvelteMap } from "svelte/reactivity";
  import "@/table-grey.css";
  import { get, post } from "@/api/api";
  import {
    type DoHashRequest,
    type DoHashResponse,
    type HashesResponse,
    toId,
  } from "@/api/types";

  let hashes = $state(getHashes().finally(() => (isHashesLoading = false)));
  let isHashesLoading = $state(true);
  const measurements = $state(new SvelteMap<string, number>());
  let hashToggled: string | undefined = $state.raw(undefined);
  let plainText = $state.raw("");
  let plainTextHashed: string | undefined = $state(undefined);
  const errors: Error[] = $state([]);

  function reload() {
    isHashesLoading = true;
    getHashes()
      .then((h) => (hashes = Promise.resolve(h)))
      .finally(() => (isHashesLoading = false));
  }

  function measure(hash: HashesResponse): Promise<void> {
    return doHash(hash, "example password")
      .then((r) => {
        measurements.set(toId(hash), r.elapsedNanos);
      })
      .catch((e) => {
        errors.push(e);
      });
  }

  function measureAll(hashes: HashesResponse[]) {
    let p = Promise.resolve();
    hashes.forEach((h) => (p = p.then(async () => await measure(h))));
  }

  function toggleHash(hash: string) {
    if (hashToggled === hash) hashToggled = undefined;
    else hashToggled = hash;

    plainTextHashed = undefined;
  }

  function computeHash(hash: HashesResponse) {
    doHash(hash, plainText)
      .then((r) => (plainTextHashed = r.hash))
      .catch((e) => errors.push(e));
  }

  function getHashes(): Promise<HashesResponse[]> {
    return get<HashesResponse[]>("hashes");
  }

  function doHash(
    { type, name }: HashesResponse,
    plainText: string,
  ): Promise<DoHashResponse> {
    return post<DoHashRequest, DoHashResponse>("doHash", {
      type,
      name,
      plainText,
    });
  }
</script>

<table class="grey">
  <caption>
    Hashes
    <button class="reload" disabled={isHashesLoading} onclick={reload}
      >&#128472;
    </button>
  </caption>
  <thead>
    <tr class="relative">
      <th rowspan="2">Type</th>
      <th rowspan="2">Name</th>
      <th colspan="2">Plain Text</th>
      <th colspan="2">Algorithm</th>
      <th rowspan="2" class="time">
        Time<small>/ns</small>
        {#await hashes then hashes}
          {#if hashes.length > 0}
            <button class="measure" onclick={() => measureAll(hashes)}
              >&#128336;
            </button>
          {/if}
        {:catch _error}
          &nbsp;
        {/await}
      </th>
    </tr>
    <tr>
      <th>Limit</th>
      <th>Validator</th>
      <th>ID</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    {#await hashes}
      <tr>
        <td colspan="7" class="empty">fetching data</td>
      </tr>
    {:then hashes}
      {#if hashes.length > 0}
        {#each hashes as hash (toId(hash))}
          {@const hashId = toId(hash)}
          {@const measurement = measurements.get(hashId)}
          <tr class="relative">
            <td>
              <button class="hash" onclick={() => toggleHash(hashId)}>
                {#if hashToggled === hashId}
                  &#8594;
                {:else}
                  &#8595;
                {/if}
              </button>
              {hash.type}
            </td>
            <td>{hash.name}</td>
            <td class="number">{hash.plainTextLimit}</td>
            <td class:notAvailable={!hash.plainTextValidator}
              >{hash.plainTextValidator}</td
            >
            <td>{hash.algorithmID}</td>
            <td>{hash.algorithmDescription}</td>
            <td class="number">
              {#if measurement}
                {measurement.toLocaleString("en-US")}
              {/if}
              <button class="measure" onclick={() => measure(hash)}
                >&#128336;
              </button>
            </td>
          </tr>
          {#if hashToggled === hashId}
            <tr in:fly={{ y: -10, duration: 200 }}>
              <td colspan="7" class="expansion">
                <input bind:value={plainText} placeholder="Plain Text" />
                <button onclick={() => computeHash(hash)}>Hash</button>
                <br />
                <small>{plainTextHashed}</small>
              </td>
            </tr>
          {/if}
        {/each}
      {:else}
        <tr>
          <td colspan="7" class="empty">There are no hashes in the model.</td>
        </tr>
      {/if}
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
    {#each errors as error}
      <li>{error}</li>
    {/each}
  </ul>
{/if}

<style>
  table {
    margin-left: 1em;
  }

  button.reload {
    border: 0;
    background: rgba(0, 0, 0, 0);
  }

  button.reload:disabled {
    color: gray;
  }

  button.measure {
    position: absolute;
    top: 0;
    right: -25px;
    border: 0;
    background: white;
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

  th.time {
    min-width: 7em;
  }
</style>
