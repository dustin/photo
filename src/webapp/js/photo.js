function field_blur_behavior(field, def) {
	var f=$(field);
	var defaultClass='defaultfield';
	f.onfocus=function() {
		if(f.value === def) {
			f.value='';
			Element.removeClassName(f, 'defaultfield');
		}
	};
	f.onblur=function() {
		if(f.value === '') {
			f.value=def;
			Element.addClassName(f, 'defaultfield');
		}
	}
	Event.observe(window, 'unload', f.onfocus);
	f.onblur();
}

function clearThing(el) {
	while(el.hasChildNodes()) {
		el.removeChild(el.firstChild);
	}
}

function getElementText(el, name, index) {
	var els=el.getElementsByTagName(name);
	var rv="";
	if(els.length > 0) {
		rv=els[0].firstChild.nodeValue;
	}
	return rv;
}

function closeDebug() {
	Element.hide(debug.box);
}

function debug(msg) {
	if (!debug.box) {
		debug.box = document.createElement("div");
		debug.box.setAttribute("style", "background-color: white; " +
			"font-family: monospace; " +
			"border: solid black 3px; " +
			"position: absolute;top:300px;" +
			"padding: 10px;");
		debug.box.setAttribute("onclick", "closeDebug();");
		document.body.appendChild(debug.box);
		debug.box.innerHTML = "<h1 style='text-align:center'>Debug Output</h1>";
	}
	var p = document.createElement("p");
	p.appendChild(document.createTextNode(msg));
	debug.box.appendChild(p);
	Element.show(debug.box);
	new Effect.Highlight(debug.box);
}
