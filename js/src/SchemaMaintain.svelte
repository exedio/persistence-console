<script lang="ts">
  import { PromiseTracker } from "@/api/PromiseTracker.svelte";
  import type {
    SchemaMaintainOperation,
    SchemaMaintainRequest,
    SchemaMaintainResponse,
  } from "@/api/types";
  import { successMessage } from "@/UseSchemaMaintain";
  import { post } from "@/api/api";

  const { reloadTracker } = $props<{
    reloadTracker: PromiseTracker<unknown>;
  }>();

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
        if (operation !== "delete") return reloadTracker.reload();
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
</script>

<button disabled={maintainRunning} onclick={() => maintain("create")}
  >create</button
>
<button disabled={maintainRunning} onclick={() => maintain("tearDown")}
  >tear down</button
>
<button disabled={maintainRunning} onclick={() => maintain("drop")}>drop</button
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
    {maintainMessage}{maintainCreateMessage ? ", " + maintainCreateMessage : ""}
  </div>
{/if}
