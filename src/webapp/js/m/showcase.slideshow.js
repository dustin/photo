/*
	This file is part of JonDesign's SmoothSlideshow v2.0.

	JonDesign's SmoothSlideshow is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	JonDesign's SmoothSlideshow is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with Foobar; if not, write to the Free Software
	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

	Main Developer: Jonathan Schemoul (JonDesign: http://www.jondesign.net/)
	Contributed code by:
    - Christian Ehret (bugfix)
    - Simon Willison (addLoadEvent)
    - Nitrix (Bugfix: added a .bind(this);)
*/

// declaring the class
var showcaseSlideShow = Class.create();

// implementing the class
showcaseSlideShow.prototype = {
	initialize: function(element, data) {
		this.currentIter = 0;
		this.lastIter = 0;
		this.maxIter = 0;
		this.slideShowElement = element;
		this.slideShowData = data;
		this.slideShowInit = 1;
		this.slideElements = Array();

		element.style.display="block";

		this.maxIter = data.length;
		for(i=0;i<data.length;i++)
		{
			var currentImg = document.createElement('div');
			currentImg.className = "slideElement";
			currentImg.style.position="absolute";
			currentImg.style.left="0px";
			currentImg.style.top="0px";
			currentImg.style.margin="0px";
			currentImg.style.border="0px";
			currentImg.style.backgroundImage="url('" + data[i][0] + "')";
			currentImg.style.backgroundPosition="center center";

			element.appendChild(currentImg);
			currentImg.currentOpacity = new fx.Opacity(currentImg, {duration: 500});
			currentImg.setStyle('opacity',0);
			this.slideElements[parseInt(i)] = currentImg;
		}
		if (data.length>1)
		{
			var leftArrow = document.createElement('a');
			leftArrow.className = 'left';
			leftArrow.onclick = this.pushPrevSlideShow.bind(this);
			element.appendChild(leftArrow);

			var rightArrow = document.createElement('a');
			rightArrow.className = 'right';
			rightArrow.onclick = this.pushNextSlideShow.bind(this);
			element.appendChild(rightArrow);
		}
		currentImg.currentOpacity = new fx.Opacity(currentImg, {duration: 400});
		currentImg.setStyle('opacity',0);
		
		this.loadingElement = document.createElement('div');
		this.loadingElement.className = 'loadingElement';
		element.appendChild(this.loadingElement);

		this.slideInfoZone = document.createElement('div');
		this.slideInfoZone.className = 'slideInfoZone';
		element.appendChild(this.slideInfoZone);
		this.slideInfoZone.style.opacity = 0;

		this.doSlideShow(1);
	},
	destroySlideShow: function(element) {
		var myClassName = element.className;
		var newElement = document.createElement('div');
		newElement.className = myClassName;
		element.parentNode.replaceChild(newElement, element);
	},
	pushNextSlideShow: function() {
		setTimeout(this.hideInfoSlideShow.bind(this),10);
		setTimeout(this.nextSlideShow.bind(this),500);
	},
	pushPrevSlideShow: function() {
		setTimeout(this.hideInfoSlideShow.bind(this),10);
		setTimeout(this.prevSlideShow.bind(this),500);
	},
	startSlideShow: function() {
		this.loadingElement.style.display = "none";
		this.lastIter = this.maxIter - 1;
		this.currentIter = 0;
		this.slideShowInit = 0;
		this.slideElements[parseInt(this.currentIter)].setStyle('opacity', 1);
		setTimeout(this.showInfoSlideShow.bind(this),1000);
	},
	nextSlideShow: function() {
		this.lastIter = this.currentIter;
		this.currentIter++;
		if (this.currentIter >= this.maxIter)
		{
			this.currentIter = 0;
			this.lastIter = this.maxIter - 1;
		}
		this.slideShowInit = 0;
		this.doSlideShow.bind(this)(1);
	},
	prevSlideShow: function() {
		this.lastIter = this.currentIter;
		this.currentIter--;
		if (this.currentIter <= -1)
		{
			this.currentIter = this.maxIter - 1;
			this.lastIter = 0;
		}
		this.slideShowInit = 0;
		this.doSlideShow.bind(this)(2);
	},
	doSlideShow: function(position) {
		if (this.slideShowInit == 1)
		{
			imgPreloader = new Image();
			// once image is preloaded, start slideshow
			imgPreloader.onload=function(){
				setTimeout(this.startSlideShow.bind(this),10);
			}.bind(this);
			imgPreloader.src = this.slideShowData[0][0];
		} else {
			if (position == 1)
			{
				if (this.currentIter != 0) {
					this.slideElements[parseInt(this.currentIter)].currentOpacity.options.onComplete = function() {
						this.slideElements[parseInt(this.lastIter)].setStyle('opacity',0);
					}.bind(this);
					this.slideElements[parseInt(this.currentIter)].currentOpacity.custom(0, 1);
				} else {
					this.slideElements[parseInt(this.currentIter)].setStyle('opacity',1);
					this.slideElements[parseInt(this.lastIter)].currentOpacity.custom(1, 0);
				}
			} else {
				if (this.currentIter != this.maxIter - 1) {
					this.slideElements[parseInt(this.currentIter)].setStyle('opacity',1);
					this.slideElements[parseInt(this.lastIter)].currentOpacity.custom(1, 0);
				} else {
					this.slideElements[parseInt(this.currentIter)].currentOpacity.options.onComplete = function() {
						this.slideElements[parseInt(this.lastIter)].setStyle('opacity',0);
					}.bind(this);
					this.slideElements[parseInt(this.currentIter)].currentOpacity.custom(0, 1);
				}
			}
			setTimeout(this.showInfoSlideShow.bind(this),1000);
		}
	},
	showInfoSlideShow: function() {
		this.slideShowElement.removeChild(this.slideInfoZone);
		this.slideInfoZone = document.createElement('div');
		this.slideInfoZone.className = 'slideInfoZone';
		this.slideInfoZone.styles = new fx.Styles(this.slideInfoZone);
		this.slideInfoZone.setStyle('opacity',0);
		var slideInfoZoneTitle = document.createElement('h2');
		slideInfoZoneTitle.innerHTML = this.slideShowData[this.currentIter][2]
		this.slideInfoZone.appendChild(slideInfoZoneTitle);
		var slideInfoZoneDescription = document.createElement('p');
		slideInfoZoneDescription.innerHTML = this.slideShowData[this.currentIter][3];
		this.slideInfoZone.appendChild(slideInfoZoneDescription);
		this.slideShowElement.appendChild(this.slideInfoZone);
		this.slideInfoZone.normalHeight = this.slideInfoZone.getStyle('height', true).toInt();
		this.slideInfoZone.styles.custom({'opacity': [0, 0.7], 'height': [0, this.slideInfoZone.normalHeight]});
	},
	hideInfoSlideShow: function() {
		this.slideInfoZone.styles.custom({'opacity': [0.7, 0]});
		//this.slideInfoZone.styles.custom({'opacity': [0.7, 0], 'height': [this.slideInfoZone.normalHeight, 0]});
	},
	goTo: function(num) {
		this.currentIter = num;
		if (num == 0) this.lastIter = this.maxIter-1;
		else this.lastIter = num-1;
		this.slideShowInit = 0;
		for(i=0;i<this.maxIter;i++)
		{
			this.slideElements[i].currentOpacity.setOpacity(0);
		}
		this.doSlideShow.bind(this)();
	}
};

function initShowcaseSlideShow(element, data) {
	var slideshow = new showcaseSlideShow(element, data);
	return slideshow;
}

function addLoadEvent(func) {
	var oldonload = window.onload;
	if (typeof window.onload != 'function') {
		window.onload = func;
	} else {
		window.onload = function() {
			oldonload();
			func();
		}
	}
}