function toggleUnspecified(image)
{
	var imageSrc = image.src;
	if(imageSrc.substring(imageSrc.length-8)=="true.png")
	{
		image.src = imageSrc.substring(0, imageSrc.length-8) + "false.png";
		var rows = document.body.getElementsByTagName("tr");
		for(i=0; i<rows.length; i++)
		{
			var row = rows[i];
			if(row.className=="unspecified")
				row.style.display = "none";
		}
	}
	else
	{
		image.src = imageSrc.substring(0, imageSrc.length-9) + "true.png";
		var rows = document.body.getElementsByTagName("tr");
		for(i=0; i<rows.length; i++)
		{
			var row = rows[i];
			if(row.className=="unspecified")
				row.style.display = "table-row";
		}
	}
}
