document
  .querySelectorAll("input[type='submit'][confirmation]")
  .forEach((button) =>
    button.addEventListener("click", function (event) {
      if (!confirm(this.getAttribute("confirmation"))) event.preventDefault();
    }),
  );
