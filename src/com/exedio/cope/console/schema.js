function togSch(switchElement)
{
	var bodyElement  = document.getElementById("schBdy");
	var applyElement = document.getElementById("schApp");
	var applyDryElement = document.getElementById("schAppDry");
	var switchSrc = switchElement.src;
	if(switchSrc.substring(switchSrc.length-8)=="true.png")
	{
		setDisplay(bodyElement,  "none");
		setDisplay(applyElement, "none");
		setDisplay(applyDryElement, "none");
		switchElement.src = switchSrc.substring(0, switchSrc.length-8) + "false.png";
	}
	else
	{
		setDisplay(bodyElement,  "block");
		setDisplay(applyElement, "inline");
		setDisplay(applyDryElement, "inline");
		switchElement.src = switchSrc.substring(0, switchSrc.length-9) + "true.png";
	}
}

function togTab(switchElement,table)
{
	var bodyElement = document.getElementById("tabBdy" + table);
	var switchSrc = switchElement.src;
	if(switchSrc.substring(switchSrc.length-8)=="true.png")
	{
		setDisplay(bodyElement, "none");
		switchElement.src = switchSrc.substring(0, switchSrc.length-8) + "false.png";
	}
	else
	{
		setDisplay(bodyElement, "block");
		switchElement.src = switchSrc.substring(0, switchSrc.length-9) + "true.png";
	}
}

function setDisplay(element, display)
{
	if(element!=null)
		element.style.display = display;
}

function tabName(span)
{
	var parentNode = span.parentNode;

	var dropText = document.createTextNode("drop");
	parentNode.replaceChild(dropText, span);

	var dropBox = document.createElement("input");
	dropBox.setAttribute('type', 'checkbox');
	dropBox.setAttribute('name', 'DROP_TABLE');
	dropBox.setAttribute('value', span.innerHTML);
	parentNode.insertBefore(dropBox, dropText);

	var input = document.createElement("input");
	input.setAttribute('name', 'RENAME_TABLE_' + span.innerHTML);
	input.setAttribute('value', span.innerHTML);
	parentNode.insertBefore(input, dropBox);
}

function colName(span, tableName)
{
	var parentNode = span.parentNode;

	var dropText = document.createTextNode("drop");
	parentNode.replaceChild(dropText, span);

	var dropBox = document.createElement("input");
	dropBox.setAttribute('type', 'checkbox');
	dropBox.setAttribute('name', 'DROP_COLUMN');
	dropBox.setAttribute('value', tableName + '#' + span.innerHTML);
	parentNode.insertBefore(dropBox, dropText);

	var input = document.createElement("input");
	input.setAttribute('name', 'RENAME_COLUMN__' + tableName + '#' + span.innerHTML);
	input.setAttribute('value', span.innerHTML);
	parentNode.insertBefore(input, dropBox);
}

function colType(span, tableName, columnName)
{
	var parentNode = span.parentNode;

	var input = document.createElement("input");
	input.setAttribute('name', 'MODIFY_COLUMN__' + tableName + '#' + columnName);
	input.setAttribute('value', span.innerHTML);
	parentNode.replaceChild(input, span);
}

function conName(span, tableName)
{
	var parentNode = span.parentNode;

	var dropText = document.createTextNode("drop");
	parentNode.replaceChild(dropText, span);

	var dropBox = document.createElement("input");
	dropBox.setAttribute('type', 'checkbox');
	dropBox.setAttribute('name', 'DROP_CONSTRAINT');
	dropBox.setAttribute('value', tableName + '#' + span.innerHTML);
	parentNode.insertBefore(dropBox, dropText);

	var nameText = document.createTextNode(span.innerHTML);
	parentNode.insertBefore(nameText, dropBox);
}

function seqName(span)
{
	var parentNode = span.parentNode;

	var dropText = document.createTextNode("drop");
	parentNode.replaceChild(dropText, span);

	var dropBox = document.createElement("input");
	dropBox.setAttribute('type', 'checkbox');
	dropBox.setAttribute('name', 'DROP_SEQUENCE');
	dropBox.setAttribute('value', span.innerHTML);
	parentNode.insertBefore(dropBox, dropText);

	var nameText = document.createTextNode(span.innerHTML);
	parentNode.insertBefore(nameText, dropBox);
}
