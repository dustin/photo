// I got this from the following URL:
// http://www.kryogenix.org/code/browser/annimg/annimg.html
// arch-tag: 6608DED9-0313-48FB-9E05-77CD90086D48

var aI = {

  init: function() {
    if (!document.getElementById ||
        !document.createElement ||
        !document.getElementsByTagName) 
        return;
    var anni = document.getElementsByTagName('img');
    for (var i=0;i<anni.length;i++) {
      if ((anni[i].className.search(/\bannotated\b/) != -1) &&
          (anni[i].getAttribute('usemap') != null)) {
          aI.prepImage(anni[i]);
      }
    }
  },
  
  prepImage: function(img) {
    var mapName = img.getAttribute('usemap');
    if (mapName.substr(0,1) == '#') mapName = mapName.substr(1);
    var mapObjs = document.getElementsByName(mapName);
    if (mapObjs.length != 1) return;
    var mapObj = mapObjs[0];
    var areas = mapObj.getElementsByTagName('area');
    img.areas = [];
    for (var j=areas.length-1;j>=0;j--) {
      if (areas[j].getAttribute('shape').toLowerCase() == 'rect') {
        var coo = areas[j].getAttribute('coords').split(',');
        if (coo.length != 4) break;
        var a = document.createElement('a');
        a.associatedCoords = coo;
        a.style.width = (parseInt(coo[2]) - parseInt(coo[0])) + 'px';
        a.style.height = (parseInt(coo[3]) - parseInt(coo[1])) + 'px';
        var thisAreaPosition = aI.__getAreaPosition(img,coo);
        a.style.left = thisAreaPosition[0] + 'px';
        a.style.top = thisAreaPosition[1] + 'px';
        a.className = 'annotation';
        var href = areas[j].getAttribute('href');
        if (href) {
          a.href = href;
        } else {
          // set an explicit href, otherwise it doesn't count as a link
          // for IE
          a.href = "#";
        }
        a.title = areas[j].getAttribute('title');
        var s = document.createElement('span');
        s.appendChild(document.createTextNode(''));
        a.appendChild(s);
        
        img.areas[img.areas.length] = a;
        document.getElementsByTagName('body')[0].appendChild(a);

        aI.addEvent(a,"mouseover",
          function() {
            clearTimeout(aI.hiderTimeout);
          }
          );
          
        if ((typeof showNiceTitle == 'function') && 
            (typeof hideNiceTitle == 'function')) {
          a.setAttribute('nicetitle',a.title);
          a.title = '';
          aI.addEvent(a,"mouseover",showNiceTitle);
          aI.addEvent(a,"mouseout",hideNiceTitle);
        }

      }
    }
    
    aI.addEvent(img,"mouseover",aI.showAreas);
    aI.addEvent(img,"mouseout",aI.hideAreas);
  },
  
  __getAreaPosition: function(img,coo) {
    var aleft = (img.offsetLeft + parseInt(coo[0]));
    var atop = (img.offsetTop + parseInt(coo[1]));
    var oo = img;
    while (oo.offsetParent) {
      oo = oo.offsetParent;
      aleft += oo.offsetLeft;
      atop += oo.offsetTop;
    }
    return [aleft,atop];
  },
  
  __setAreas: function(t,disp) {
    if (!t || !t.areas) return;
    for (var i=0;i<t.areas.length;i++) {
      t.areas[i].style.display = disp;
    }
  },
  
  showAreas: function(e) {
    var t = null;
    if (e && e.target) t = e.target;
    if (window.event && window.event.srcElement) t = window.event.srcElement;
    // Recalculate area positions
    for (var k=0;k<t.areas.length;k++) {
      var thisAreaPosition = aI.__getAreaPosition(t,t.areas[k].associatedCoords);
      t.areas[k].style.left = thisAreaPosition[0] + 'px';
      t.areas[k].style.top = thisAreaPosition[1] + 'px';
      
    }
    aI.__setAreas(t,'block');
  },
  hideAreas: function(e) {
    var t = null;
    if (e && e.target) t = e.target;
    if (window.event && window.event.srcElement) t = window.event.srcElement;
    clearTimeout(aI.hiderTimeout);
    aI.hiderTimeout = setTimeout(
      function() { aI.__setAreas(t,'none') }, 300);
  },
  
    addEvent: function(elm, evType, fn, useCapture) {
    // cross-browser event handling for IE5+, NS6 and Mozilla
    // By Scott Andrew
    if (elm.addEventListener){
      elm.addEventListener(evType, fn, useCapture);
      return true;
    } else if (elm.attachEvent){
      var r = elm.attachEvent("on"+evType, fn);
      return r;
    } else {
      elm['on'+evType] = fn;
    }
  }
}

aI.addEvent(window,"load",aI.init);
