// Search support
var kws = new Array();
var kwmap = new Object();
var imgsPerPage = 50;
var pageNum = 0;
var newestFirst = false;

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

// Get a union of all of the arrays passed in as arguments
function arrayUnion(a) {
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

// This should perform some kind of and logic across selections
function getImageIds() {
	var tmpa=new Array();
	for(var i = 0; i<kws.length; i++) {
		tmpa.push(imgs[kws[i]]);
	}
	return arrayUnion(tmpa);
}

function setResults(h) {
	var div=document.getElementById("found");
	div.innerHTML=h;
}

function showResults() {
	// Maximum number of images to show
	var total = imgsPerPage;
	var tmpimgs=getImageIds();
	if(newestFirst) {
		tmpimgs.reverse();
	}
	// h = tmpimgs.join(" ") + "<br>\n";
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
			h += '<a href="pages/' + photloc[img] + "/" + img + '.html">';
			h += '<img src="pages/' + photloc[img] + '/' + img + '_tn.jpg"/> ';
			h += '</a>';
		} else if(total == 0) {
			h += nextbutton;
		}
		total -= 1
	}
	setResults(h);
}

function search() {

	var ppagefield=document.getElementById("perpage");
	imgsPerPage = parseInt(ppagefield.value);

	var sdirfield=document.getElementById("sdir");
	if(sdirfield.value == 'newfirst') {
		newestFirst = true;
	} else {
		newestFirst = false;
	}

	var kwfield=document.getElementById("keywords");
	var mykws=kwfield.value.split(" ");
	kws=new Array();
	for(var i=0; i<mykws.length; i++) {
		if(kwmap[mykws[i]] != undefined) {
			kws.push(kwmap[mykws[i]]);
		}
	}
	kws.sort();
	kws.reverse();
	showResults();
}

function pressedKey(inp) {
	var div = document.getElementById("keywordlist");
	var h = "";
	if(inp.length > 0) {
		var mykws=inp.split(" ");
		for(var i=0; i<mykws.length; i++) {
			if(mykws[i] == ' ' || mykws[i] == '') {
				// Nothing
			} else if(kwmap[mykws[i]] != undefined) {
				h += mykws[i] + "-(" + imgs[kwmap[mykws[i]]].length + ") ";
			} else {
				h += mykws[i] + "-[no match] ";
			}
		}
	}
	div.innerHTML=h;
}

onload = function() {
	// initForm(document.bigform);
	for(var i=0; i<keywords.length; i++) {
		kwmap[keywords[i]]=i;
	}
}
