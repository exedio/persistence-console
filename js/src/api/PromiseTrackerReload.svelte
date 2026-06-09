<script lang="ts">
  import { PromiseTracker } from "@/api/PromiseTracker.svelte";

  const { tracker } = $props<{
    tracker: PromiseTracker<unknown>;
  }>();
</script>

<button
  class="reload"
  title="Reload"
  disabled={tracker.pending()}
  onclick={() => tracker.reload()}
  >↺
</button>

<style>
  button.reload {
    border: 0;
    background: rgba(0, 0, 0, 0);
  }

  button.reload:disabled {
    color: gray;
  }
</style>
