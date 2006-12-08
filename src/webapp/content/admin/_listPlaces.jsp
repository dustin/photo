<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<html:xhtml/>

<script type="text/javascript">
	var newUrl='<c:url value="/ajax/place/new"/>';
	// <![CDATA[
	Event.observe(window, 'load', function() {
		$('newlink').onclick=function() {
			Element.hide($('newlink'));
			Element.show($('newformdiv'));
			return false;
		};
		$('newform').onsubmit=function() {
			var name=$F('namefield');
			var lon=$F('lonfield');
			var lat=$F('latfield');
			if(name == "" || lon == "" || lat == "") {
				alert("Fill in all the form values");
				return(false);
			}

			Form.disable("newform");
			$("namefield").value="";

			var postBody=$H({name: name, lon: lon, lat: lat}).toQueryString();

			new Ajax.Request(newUrl, {
				method: 'post',
				postBody: postBody,
				onComplete: function(req) {
					Form.enable("newform");
				},
				onFailure: function(req) {
					alert("Failed to add place");
				},
				onSuccess: function(req) {
					var newid=req.responseText;
					var tr=document.createElement("tr");

					var tbuild=function(pfx, value) {
						var td=document.createElement("td");
						var span=document.createElement("span");
						span.id=pfx + newid;
						span.appendChild(document.createTextNode(value));
						td.appendChild(span);
						tr.appendChild(td);
					};

					tbuild("n", name);
					tbuild("lon", lon);
					tbuild("lat", lat);

					$('placebody').appendChild(tr);
					new Effect.Highlight(tr);

					Element.hide($('newformdiv'));
					Element.show($('newlink'));
				}
				});

			return(false);
		};
		}, false);
	// ]]>
</script>

<table>
	<thead>
		<tr>
			<th>Name</th>
			<th>Longitude</th>
			<th>Latitude</th>
		</tr>
	</thead>
	<tbody id="placebody">
		<c:forEach var="place" items="${places}">
		<tr>
			<td> <span id="<c:out value='n${place.id}'/>"><c:out
				value="${place.name}"/></span></td>
			<td><span id="<c:out value='lon${place.id}'/>"><c:out
				value="${place.longitude}"/></span></td>
			<td><span id="<c:out value='lat${place.id}'/>"><c:out
				value="${place.latitude}"/></span></td>
		</tr>
		<script type="text/javascript">
			new Ajax.InPlaceEditor("<c:out value='n${place.id}'/>",
				'<c:url value="/ajax/place/name?id=${place.id}"/>');
			new Ajax.InPlaceEditor("<c:out value='lon${place.id}'/>",
				'<c:url value="/ajax/place/lon?id=${place.id}"/>');
			new Ajax.InPlaceEditor("<c:out value='lat${place.id}'/>",
				'<c:url value="/ajax/place/lat?id=${place.id}"/>');
		</script>
		</c:forEach>
	</tbody>
</table>

<div><a id="newlink" href="#">New Place</a></div>
<div style="display: none" id="newformdiv">
	<form id="newform" method="post" action="#">
		<fieldset>
			<legend>New Place Form</legend>
			<label for="namefield">Name</label>
			<input type="text" id="namefield" name="namefield"/><br/>
			<label for="lonfield">Longitude</label>
			<input type="text" id="lonfield" name="lon"/><br/>
			<label for="latfield">Longitude</label>
			<input type="text" id="latfield" name="lat"/><br/>
			<input type="submit" value="Add"/>
		</fieldset>
	</form>
</div>

<%-- arch-tag: 3AE58A9B-C7A6-4712-8A84-115F8A57E58A --%>
