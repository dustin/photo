// Category editor
// arch-tag: B30EAD50-4EBB-455F-80CD-CAD01F261756

function getData(el, which) {
	stuff=el.getElementsByTagName(which);
	return stuff[0].firstChild.nodeValue;
}

// Set up a category editor
function setupCategoryEditor(el, url, webappurl) {
	var editor=new Ajax.InPlaceEditor(el, url);
	Object.extend(editor, {
		// Overrode this to prevent some javascript errors when trying to
		// grab the new form element
		enterEditMode: function(evt) {
			this.editing = true;
			this.onEnterEditMode();
			if (this.options.externalControl) {
				Element.hide(this.options.externalControl);
			}
			Element.hide(this.element);
			this.createForm();
			this.element.parentNode.insertBefore(this.form, this.element);
			if (evt) {
				Event.stop(evt);
			}
			return false;
		},
		createEditField: function() {
			var text=this.getText();

			var field=document.createElement("select");
			field.name="value";

			this.editField=field;
			this.form.appendChild(this.editField);

			new Ajax.Request(webappurl + 'ajax/cats/write', {
				onSuccess: function(req) {
					var cats=req.responseXML.getElementsByTagName("cat");
					$A(cats).each( function(cat, idx) {
						var op=document.createElement("option");
						op.value=getData(cat, "value");
						op.text=getData(cat, "key");
						if(window.ActiveXObject) {
							field.options.add(op);
						} else {
							field.appendChild(op);
						}

						if(op.text == text) {
							field.selectedIndex=idx;
						}
					});
				}
				});
		}
	});
	Event.stopObserving(editor.element, 'click', editor.onclickListener);
	editor.onclickListener=editor.enterEditMode.bindAsEventListener(editor);
	Event.observe(editor.element, 'click', editor.onclickListener);
}
