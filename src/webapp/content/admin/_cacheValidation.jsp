<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>

<script type="text/javascript">
var baseurl='<c:url value="/ajax/validation/"/>';
// <![CDATA[
	Event.observe(window, 'load', updateStatus, false);

	function updateText(xml, which) {
		var el=$(which);
		clearThing(el);
		el.appendChild(
			document.createTextNode(getElementText(xml, which, 0)));
	}

	function getStatus(url) {
		new Ajax.Request(url, {
			onFailure: function(req) {
				alert("Failed to load status info: " + req.responseText);
			},
			onComplete: function(req) {
			},
			onSuccess: function(req) {
				var xml=req.responseXML;
				$A(['runs', 'todo', 'running', 'todo', 'done']).each(function(el) {
					updateText(xml, el);
					});
				var running = getElementText(xml, "running", 0) == "true";
				if(xml.getElementsByTagName("todo").length > 0) {
					Element.show($('stats'));
					var todof=parseInt(getElementText(xml, "todo", 0));
					var donef=parseInt(getElementText(xml, "done", 0));
					var percent=0;
					if(todof != 0) {
						percent=parseInt("" + ((donef*100)/todof));
					}
					$('meter').style.width=percent + "%";
					clearThing($('percent'));
					$('percent').appendChild(document.createTextNode(percent));
					if(running) {
						$('meter').style.background="green";
					} else {
						$('meter').style.background="blue";
					}
				}
				var errors=xml.getElementsByTagName("error");
				var errorsUl=$('errors');
				clearThing(errorsUl);
				if(errors.length > 0) {
					for(var i=0; i<errors.length; i++) {
						var li=document.createElement("li");
						errorsUl.appendChild(li);
						li.appendChild(
							document.createTextNode(errors[i].firstChild.nodeValue));
					}
					Element.show(errorsUl);
				} else {
					Element.hide(errorsUl);
				}
				if(running) {
					setTimeout('updateStatus();', 500);
				} else {
					Form.enable($("startform"));
				}
			}
		});
	}

	function updateStatus() {
		getStatus(baseurl);
	}

	function validationControl(what) {
		if(what == "start") {
			Form.disable($("startform"));
			Form.enable($("cancelform"));
		} else {
			Form.enable($("startform"));
			Form.disable($("cancelform"));
		}
		getStatus(baseurl + what);
	}
// ]]>
</script>

<html:xhtml/>

<h1>Cache Validation</h1>

<p>
	Cache validation allows for building out and doing base validation on the
	disk-based image cache of the image database.
</p>

<h2>Status</h2>

<div>
	Number of runs:  <span id="runs">Unknown</span><br/>
	Currently running:  <span id="running">Unknown</span><br/>

	<div id="stats" style="display: none;">
		Todo:  <span id="todo">Unknown</span>,
			completed:  <span id="done">Unknown</span>
			(<span id="percent">0</span>%)<br/>
		<div id="meter" style="height: 1em; background: blue; margin: 5px;"></div>
		<ul id="errors" style="display: none">
			<li>None</li>
		</ul>
	</div>
</div>

<h2>Control</h2>

<form id="startform" method="post"
	onsubmit="validationControl('start'); return false;">
	<div><input type="submit" value="Start"/></div>
</form>

<form id="cancelform" method="post"
	onsubmit="validationControl('cancel'); return false;">
	<div><input type="submit" value="Cancel"/></div>
</form>
