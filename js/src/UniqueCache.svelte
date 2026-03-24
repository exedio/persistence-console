<script lang="ts">
  import "@/table-grey.css";
  import { get } from "@/api/api";
  import type { UniqueConstraintMetric as Metric } from "@/api/types";
  import { PromiseTracker } from "@/api/PromiseTracker.svelte";
  import PromiseTrackerReload from "@/api/PromiseTrackerReload.svelte";
  import { condense } from "@/UniqueCacheCondense";

  const metricsTracker = new PromiseTracker(() =>
    get<Metric[]>("metrics?prefix=com.exedio.cope.UniqueConstraint").then(
      (response) => condense(response),
    ),
  );
</script>

<table class="grey">
  <caption>
    Unique Constraint Caches
    <PromiseTrackerReload tracker={metricsTracker} />
  </caption>
  <thead>
    <tr>
      <th rowspan="2">Constraint</th>
      <th colspan="2">Result</th>
    </tr>
    <tr>
      <th>Hit</th>
      <th>Miss</th>
    </tr>
  </thead>
  <tbody>
    {#await metricsTracker.promise()}
      <tr>
        <td colspan="3" class="empty">fetching data</td>
      </tr>
    {:then metrics}
      {#each metrics as metric}
        <tr>
          <td>{metric.feature}</td>
          <td class="number" title={metric.hitDescription}>{metric.hit}</td>
          <td class="number" title={metric.missDescription}>{metric.miss}</td>
        </tr>
      {:else}
        <tr>
          <td colspan="3" class="empty"
            >There are no metrics of unique constraints for the model.</td
          >
        </tr>
      {/each}
    {:catch error}
      <tr>
        <td colspan="3" class="error">{error.message}</td>
      </tr>
    {/await}
  </tbody>
</table>
