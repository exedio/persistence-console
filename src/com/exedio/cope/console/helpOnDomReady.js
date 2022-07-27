document.
	querySelectorAll("img[help]").
	forEach(button =>
		button.addEventListener("click", function(event) {
			document.
				querySelectorAll("div[class='help']").
				forEach(div => div.style.display = "block")
		})
	);
