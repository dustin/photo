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
function getImageIds(kwidlist) {
	var tmpa=new Array();
	// This gets them in the most efficient order for performing the union
	kwidlist.sort();
	kwidlist.reverse();
	for(var i = 0; i<kwidlist.length; i++) {
		tmpa.push(imgs[kwidlist[i]]);
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
	var tmpimgs=getImageIds(kws);
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
	showResults();
}

function findPartial(kw, minmatches) {
	var matches=0;
	var rv="";
	for(var i=0; matches < 10 && i<keywords.length; i++) {
		if(keywords[i].substring(0, kw.length) == kw) {
			matches++;
			rv += "<li>" + keywords[i] + " (" + imgs[i].length + ")</li>\n";
		}
	}
	if(matches >= minmatches) {
		rv = "Keywords beginning with <b>" + kw + "</b>:<ul>" + rv + "</ul>";
	} else {
		rv = "";
	}
	return rv;
}

function pressedKey(inp) {
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
				partial += findPartial(mykws[i], 2);
			} else {
				h += mykws[i] + "-[no match] ";
				partial += findPartial(mykws[i], 1);
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

onload = function() {
	// initForm(document.bigform);
	for(var i=0; i<keywords.length; i++) {
		kwmap[keywords[i]]=i;
	}
}
