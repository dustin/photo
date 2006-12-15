// Search support
var kws = new Array();
var kwmap = new Object();
var imgsPerPage = 50;
var pageNum = 0;
var newestFirst = false;
// Cache of exact matches
var partialCache = new Object();

// Figure out if the given array contains the given key
// This assumes sorted arrays in order to be O(log n)
function arrayContains(a, key) {
	var rv = false;
	var low = 0;
	var high = a.length - 1;
	while(rv == false && low <= high) {
		var middle = Math.floor((low + high) / 2);

		if(key == a[middle]) {
			rv = true;
		} else if(key < a[middle]) {
			high = middle - 1;
		} else {
			low = middle + 1;
		}
	}
	return rv;
}

// Get an intersection of all of the arrays passed in as arguments
function arrayIntersection(a) {
	var rv = null;
	if(a.length < 1) {
		rv=new Array();
	} else {
		rv = a[0];
		for(var i=1; i<a.length; i++) {
			var tmprv=new Array();
			for(var j=0; j<rv.length; j++) {
				if(arrayContains(a[i], rv[j])) {
					tmprv.push(rv[j]);
				}
			}
			rv=tmprv;
		}
	}
	return rv;
}

// Get an intersection of all of the IDs for all of the images with the given
// keyword IDs
function getImageIds(kwidlist) {
	var tmpa=new Array();
	// This gets them in the most efficient order for performing the
	// intersection
	kwidlist.sort();
	kwidlist.reverse();
	for(var i = 0; i<kwidlist.length; i++) {
		tmpa.push(imgs[kwidlist[i]]);
	}
	return arrayIntersection(tmpa);
}

// Set the search results text
function setResults(h) {
	var div=document.getElementById("found");
	div.innerHTML=h;
}

function sortImages(imgs) {
	var newestFirst=($F('sdir') == 'newfirst');
	imgs.sort(function(a, b) {
		var rv=0;
		if(photloc[a] == photloc[b]) {
			rv=0;
		} else if(photloc[a] > photloc[b]) {
			rv=1;
		} else {
			rv=-1;
		}
		if(newestFirst) {
			rv = -rv;
		}
		return(rv);
		});
	return imgs;
}

function getLoc(img) {
	return(photloc[img].substring(0, 7));
}

// Show the results based on the currently selected keywords
function showResults() {
	// Maximum number of images to show
	var total = imgsPerPage;
	var tmpimgs=sortImages(getImageIds(kws));
	var h = "Found " + tmpimgs.length + " images:<br/>"
	var nextbutton='<br/><input type="button" value="next page" '
				+ 'onclick="pageNum++; showResults();"/>';
	if(pageNum > 0) {
			h += '<input type="button" value="prev page" '
				+ 'onclick="pageNum--; showResults();"/>';
	}
	if( ((1+pageNum) * imgsPerPage) < tmpimgs.length) {
		h += nextbutton;
	}
	h += "<br/>";
	for(var i=pageNum * imgsPerPage; i<tmpimgs.length; i++) {
		var img=tmpimgs[i]
		if(total > 0) {
			h += '<a href="pages/' + getLoc(img) + "/" + img + '.html">';
			h += '<img src="pages/' + getLoc(img) + '/' + img + '_tn.jpg"/> ';
			h += '</a>';
		} else if(total == 0) {
			h += nextbutton;
		}
		total -= 1
	}
	setResults(h);
}

// Perform a search
function search() {

	var ppagefield=document.getElementById("perpage");
	imgsPerPage = parseInt(ppagefield.value);

	var kwfield=document.getElementById("keywords");
	var mykws=kwfield.value.split(" ");
	kws=new Array();
	for(var i=0; i<mykws.length; i++) {
		if(kwmap[mykws[i]] != undefined) {
			kws.push(kwmap[mykws[i]]);
		}
	}
	showResults();
}

// Find partial matches for the given keyword
function findPartial(kw) {
	var rv=null;
	// Memoization...otherwise, this tends to get slow with a lot of keywords
	// on a slow machine.
	if(partialCache[kw]) {
		rv=partialCache[kw];
	} else {
		rv = new Array();
		for(var i=0; rv.length < 10 && i<keywords.length; i++) {
			if(keywords[i].substring(0, kw.length) == kw) {
				var o = new Object();
				o.kw=keywords[i];
				o.num=imgs[i].length;
				rv.push(o);
			}
		}
		partialCache[kw] = rv;
	}
	return rv;
}

// Convert the partial list to HTML
function partial2Html(kw, partial, minmatches) {
	var rv=null;
	if(partial.length >= minmatches) {
		rv = "Keywords beginning with <b>" + kw + "</b>:<ul>"
		for(var i=0; i<partial.length; i++) {
			o=partial[i];
			rv += "<li>" + o.kw + " (" + o.num + ")</li>\n";
		}
		rv += "</ul>";
	} else {
		rv = "";
	}
	return rv;
}

// Update the keyword match info text
function updateKwInfo(inp) {
	var div = document.getElementById("keywordlist");
	var h = "";
	var partial = "";
	if(inp.length > 0) {
		var mykws=inp.split(" ");
		var kwtmp=new Array();
		for(var i=0; i<mykws.length; i++) {
			if(mykws[i] == ' ' || mykws[i] == '') {
				// Nothing
			} else if(kwmap[mykws[i]] != undefined) {
				kwtmp.push(kwmap[mykws[i]]);
				h += mykws[i] + "-(" + imgs[kwmap[mykws[i]]].length + ") ";
				var part=findPartial(mykws[i]);
				partial += partial2Html(mykws[i], part, 2);
			} else {
				h += mykws[i] + "-[no match] ";
				var part=findPartial(mykws[i]);
				partial += partial2Html(mykws[i], part, 1);
			}
		}
		var searchtmp=getImageIds(kwtmp);
		var imgsstr="images";
		if(searchtmp.length == 1) {
			imgsstr="image";
		}
		h += " -- " + searchtmp.length + " " + imgsstr + " will result";
	}
	div.innerHTML=h;

	var div2 = document.getElementById("keywordmatches");
	if(partial != '') {
		div2.style.display="block";
		div2.innerHTML = partial;
	} else {
		div2.style.display="none";
		div2.innerHTML = '';
	}
}

// Update the keyword list on every key press
function keyUp(inp) {
	updateKwInfo(inp.value);
}

Event.observe(window, 'load', function() {
	// Build a map of the keywords to their IDs
	for(var i=0; i<keywords.length; i++) {
		kwmap[keywords[i]]=i;
	}
	var kwfield=document.getElementById("keywords");
	if(kwfield.value != '') {
		search();
	}
});
