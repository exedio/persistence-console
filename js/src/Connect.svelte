<script lang="ts">
  import { post } from "@/api/api";
  import type { ConnectRequest, ConnectResponse } from "@/api/types";
  import { PromiseTracker } from "@/api/PromiseTracker.svelte.js";

  const { tracker } = $props<{
    tracker: PromiseTracker<unknown>;
  }>();

  function connect() {
    return post<ConnectRequest, ConnectResponse>(
      "connect",
      {} satisfies ConnectRequest,
    ).then((r) => tracker.reload());
  }
</script>

<button onclick={() => connect()}>connect</button>
