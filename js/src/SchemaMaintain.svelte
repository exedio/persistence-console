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

  function onClick(
    operation: SchemaMaintainOperation,
    create: boolean = false,
  ): boolean {
    const confirmMessage = getConfirmMessage(operation);
    if (
      confirmMessage &&
      !confirm(
        confirmMessage +
          "\nDo you really want to do this?" +
          (create ? "\nAfterwards all tables will be recreated." : ""),
      )
    )
      return false;

    running = true;
    message = operation + " started ...";
    createMessage = undefined;
    doPost(operation)
      .then((r) => {
        message = successMessage(operation, r);

        if (!create) {
          return Promise.resolve();
        } else {
          createMessage = "create started ...";

          return doPost("create")
            .then((r2) => {
              createMessage = successMessage("create", r2);
            })
            .catch((e2) => {
              createMessage = "create failed: " + e2.message;
            });
        }
      })
      .catch((e) => {
        message = operation + " failed: " + e.message;
      })
      .finally(() => {
        running = false;
        if (operation !== "delete") return reloadTracker.reload();
      });
    return true;
  }

  function getConfirmMessage(operation: SchemaMaintainOperation) {
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

  function doPost(
    operation: SchemaMaintainOperation,
  ): Promise<SchemaMaintainResponse> {
    return post<SchemaMaintainRequest, SchemaMaintainResponse>(
      "schema/maintain",
      {
        operation: operation,
      },
    );
  }

  let running: boolean = $state(false);
  let message: string | undefined = $state(undefined);
  let createMessage: string | undefined = $state(undefined);
</script>

<button disabled={running} onclick={() => onClick("create")}
  >create</button
>
<button disabled={running} onclick={() => onClick("tearDown")}
  >tear down</button
>
<button disabled={running} onclick={() => onClick("drop")}>drop</button
>
&nbsp;
<button disabled={running} onclick={() => onClick("tearDown", true)}
  >tear down & create</button
>
<button disabled={running} onclick={() => onClick("drop", true)}
  >drop & create</button
>
&nbsp;
<button disabled={running} onclick={() => onClick("delete")}
  >delete</button
>
{#if message}
  <div>
    {message}{createMessage ? ", " + createMessage : ""}
  </div>
{/if}
