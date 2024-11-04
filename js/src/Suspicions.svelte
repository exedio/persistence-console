<script lang="ts">
  import "@/table-grey.css";
  import { get } from "@/api/api";
  import type { SuspicionsResponse } from "@/api/types";

  const features = $state(getSuspicions());

  function getSuspicions(): Promise<SuspicionsResponse[]> {
    return get<SuspicionsResponse[]>("suspicions");
  }
</script>

<table class="grey">
  <caption>Suspicions</caption>
  <thead>
    <tr>
      <th>Feature</th>
      <th>Suspicion</th>
    </tr>
  </thead>
  <tbody>
    {#await features}
      <tr>
        <td colspan="2" class="empty">fetching data</td>
      </tr>
    {:then features}
      {#if features.length > 0}
        {#each features as feature (feature.type + "." + feature.name)}
          {#each feature.suspicions as suspicion, index}
            <tr>
              {#if index === 0}
                <td rowspan={feature.suspicions.length}
                  >{feature.type}.{feature.name}</td
                >
              {/if}
              <td>{suspicion}</td>
            </tr>
          {/each}
        {/each}
      {:else}
        <tr>
          <td colspan="2" class="empty"
            >There are no suspicions for the model.</td
          >
        </tr>
      {/if}
    {:catch error}
      <tr>
        <td colspan="2" class="error">{error.message}</td>
      </tr>
    {/await}
  </tbody>
</table>
