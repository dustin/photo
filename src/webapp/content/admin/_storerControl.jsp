<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-logic' prefix='logic' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-html' prefix='html' %>
<%@ taglib uri='http://jakarta.apache.org/struts/tags-bean' prefix='bean' %>

<script type="text/javascript">
var baseurl='<c:url value="/ajax/storer/"/>';
var lastShownExceptionCount=-1;
// <![CDATA[
	Event.observe(window, 'load', updateStatus, false);

	function updateText(xml, which) {
		var el=$(which);
		clearThing(el);
		el.appendChild(
			document.createTextNode(getElementText(xml, which, 0)));
	}

	function appendException(x) {
		var li=document.createElement("li");
		// First div:  the abbreviated exception
		var div=document.createElement("div");
		div.className="exc";
		div.appendChild(document.createTextNode(
			getElementText(x, "class", 0) + "(" +
			getElementText(x, "msg", 0)+ ")"));
		li.appendChild(div);

		// Second ol:  the stack
		var stackList=document.createElement("ol");
		stackList.className="excstack";
		var stack=x.getElementsByTagName("frame");
		for(var i=0; i<stack.length; i++) {
			var frame=stack[i];
			var fin=document.createElement("li");
			fin.appendChild(document.createTextNode(
				getElementText(frame, "class", 0) + "."
					+ getElementText(frame, "method", 0) + "():"
					+ getElementText(frame, "line", 0)));

			stackList.appendChild(fin);
		}
		li.appendChild(stackList);
		$("exceptionlist").appendChild(li);
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
				$A(['flushes', 'added', 'notifications', 'totalExceptions',
					'flushing']).each(function(el) {
					updateText(xml, el);
					});

				var ex=xml.getElementsByTagName("exception");
				if(ex.length > 0) {
					var totalExc=getElementText(xml, "totalExceptions", 0);
					if(totalExc != lastShownExceptionCount) {
						// Do exceptions
						clearThing($("exceptionlist"));
						Element.show($("exceptionlist"));
						Element.show($("exceptionheader"));
						for(var i=0; i<ex.length; i++) {
							appendException(ex[i]);
						}
					}
				} else {
					Element.hide($("exceptionlist"));
					Element.hide($("exceptionheader"));
				}
				lastShownExceptionCount=totalExc;

				var running = getElementText(xml, "flushing", 0) == "true";
				if(running) {
					Form.disable($("startform"));
				} else {
					Form.enable($("startform"));
				}
				setTimeout('updateStatus();', 5000);
			}
		});
	}

	function updateStatus() {
		getStatus(baseurl);
	}

	function storerControl(what) {
		getStatus(baseurl + what);
	}
// ]]>
</script>

<html:xhtml/>

<h1>Storer Control</h1>

<p>
	This page shows you statistics on the asynchronous image storer and allows
	you to issue manual runs of the storer in case you doubt its awesomeness.
</p>

<h2>Status</h2>

<div>
	Number of runs:  <span id="flushes">Unknown</span><br/>
	Currently running:  <span id="flushing">Unknown</span><br/>
	Number of images added:  <span id="added">Unknown</span><br/>
	Number of notifications:  <span id="notifications">Unknown</span><br/>
	Number of exceptions:  <span id="totalExceptions">Unknown</span><br/>
</div>

<h2 id="exceptionheader">Recent Exceptions</h2>
<ul id="exceptionlist">
	<li>N/A</li>
</ul>

<h2>Control</h2>

<form id="startform" method="post"
	onsubmit="storerControl('start'); return false;">
	<div><input type="submit" value="Start"/></div>
</form>

<%-- arch-tag: 5493B3BE-F4CF-47BC-9B89-C4AE5A8E80FA --%>
