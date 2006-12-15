// Collection editor widget.

function setupAjaxCollectionEditor(el, url, grpUrl, groupName, extra) {

	new Ajax.Request(grpUrl, {
		method: 'get',
		onSuccess: function(req) {
			var grps=req.responseXML.getElementsByTagName(groupName);
			var getData=function (el, which) {
				stuff=el.getElementsByTagName(which);
				return stuff[0].firstChild.nodeValue;
			};
			var ops=extra.concat([]);
			$A(grps).each(function(grp, idx) {
				ops.push(new Array(getData(grp, "value"),
					getData(grp, "key")));
			});
			new Ajax.InPlaceCollectionEditor(el, url, { collection: ops });
		}
		});
}

// Set up a category editor
function setupCategoryEditor(el, url, webappurl) {
	setupAjaxCollectionEditor(el, url, webappurl + 'ajax/cats/write',
		'cat', []);
}
