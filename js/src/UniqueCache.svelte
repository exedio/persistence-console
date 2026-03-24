<script lang="ts">
  import "@/table-grey.css";
  import { get } from "@/api/api";
  import type { UniqueConstraintMetric as Metric } from "@/api/types";
  import { PromiseTracker } from "@/api/PromiseTracker.svelte";
  import PromiseTrackerReload from "@/api/PromiseTrackerReload.svelte";
  import {
    condense,
    type UniqueConstraintFeature,
  } from "@/UniqueCacheCondense";
  import { format, ratioLog10 } from "@/utils";

  const metricsTracker = new PromiseTracker(() =>
    get<Metric[]>("metrics?prefix=com.exedio.cope.UniqueConstraint").then(
      (response) => condense(response),
    ),
  );

  function formatFeature(constraint: UniqueConstraintFeature): string {
    const s = constraint.feature;
    const suffix = "ImplicitUnique";
    return s.endsWith(suffix) ? s.substring(0, s.length - suffix.length) : s;
  }

  let filterConstraint = $state("");
</script>

<table class="grey">
  <caption>
    Unique Constraint Caches
    <PromiseTrackerReload tracker={metricsTracker} />
  </caption>
  <thead>
    <tr>
      <th rowspan="2">
        Constraint
        <br />
        <input
          class="filter"
          bind:value={filterConstraint}
          placeholder="Filter"
        />
      </th>
      <th colspan="3">Cache</th>
    </tr>
    <tr>
      <th>Hit</th>
      <th>Miss</th>
      <th>log<sub>10</sub>(Hit/Miss)</th>
    </tr>
  </thead>
  <tbody>
    {#await metricsTracker.promise()}
      <tr>
        <td colspan="4" class="empty">fetching data</td>
      </tr>
    {:then metrics}
      {#each metrics as metric (metric.feature)}
        {#if metric.feature.includes(filterConstraint)}
          <tr>
            <td>{formatFeature(metric)}</td>
            <td class="number" title={metric.hitDescription}
              >{format(metric.hit)}</td
            >
            <td class="number" title={metric.missDescription}
              >{format(metric.miss)}</td
            >
            <td class="number">{format(ratioLog10(metric.hit, metric.miss))}</td
            >
          </tr>
        {/if}
      {:else}
        <tr>
          <td colspan="4" class="empty"
            >There are no metrics of unique constraints for the model.</td
          >
        </tr>
      {/each}
    {:catch error}
      <tr>
        <td colspan="4" class="error">{error.message}</td>
      </tr>
    {/await}
  </tbody>
</table>

<style>
  input.filter {
    width: 6em;
    border: none;
    background-color: #eee;
  }
</style>
