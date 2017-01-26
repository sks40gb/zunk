/*
Slideshow 1.3
April 3, 2007
For usage details see http://yellow5.us/projects/slideshow/

Creative Commons Attribution 2.0 License
http://creativecommons.org/licenses/by/2.0/
 */

if (!xb)
{
	var xb =
        {
		createElement: function(sElement)
		{
			if (document.createElementNS) return document.createElementNS('http://www.w3.org/1999/xhtml', sElement);
			if (document.createElement) return document.createElement(sElement);

			return null;
		},

		getElementsByAttribute: function(ndNode, sAttributeName, sAttributeValue)
		{
			var aReturnElements = [];

			if (!ndNode.all && !ndNode.getElementsByTagName) return aReturnElements;

			var rAttributeValue = RegExp('(^|\\s)' + sAttributeValue + '(\\s|$)');
			var sValue, aElements = ndNode.all || ndNode.getElementsByTagName('*');

			for (var nIndex = 0; aElements[nIndex]; ++nIndex)
			{
				if (!aElements[nIndex].getAttribute) continue;
				sValue = (sAttributeName == 'class') ? aElements[nIndex].className : aElements[nIndex].getAttribute(sAttributeName);
				if ((typeof(sValue) != 'string') || (sValue.length == 0)) continue;

				if (rAttributeValue.test(sValue)) aReturnElements.push(aElements[nIndex]);
			}

			return aReturnElements;
		},

		getStyle: function(ndNode, sStyle)
		{
			if (document.defaultView && document.defaultView.getComputedStyle) return document.defaultView.getComputedStyle(ndNode, '').getPropertyValue(sStyle);

			if (!ndNode.currentStyle) return true;

			var nIndex = sStyle.indexOf('-');
			while (nIndex >= 0)
			{
				sStyle = sStyle.substring(0, nIndex) + sStyle.substring(nIndex + 1, nIndex + 2).toUpperCase() + nIndex.substring(nIndex + 2);
				nIndex = sStyle.indexOf('-');
			}

			return ndNode.currentStyle[sStyle];
		},

		getOption: function(ndNode, sOption)
		{
			var sText = ndNode.getAttribute(sOption);
			if (sText) return sText;

			var sDefault = (arguments.length == 3) ? arguments[2] : false;
			var aMatch = ndNode.className.match(RegExp('(?:^|\\s)' + sOption + '=(?:\\\'|\\\")([^\\\'\\\"]+)(?:\\\'|\\\"|$)'));

			return aMatch ? aMatch[1] : sDefault;
		}
	};
}

// This is a variation of the addEvent script written by Dean Edwards (dean.edwards.name).
if (!events)
{
	var events =
        {
		nEventID: 1,

		add: function(ndElement, sType, fnHandler)
		{
			if (!fnHandler.$$nEventID) fnHandler.$$nEventID = this.nEventID++;
			if (ndElement.objEvents === undefined) ndElement.objEvents = {};

			var aHandlers = ndElement.objEvents[sType];
			if (!aHandlers)
			{
				aHandlers = ndElement.objEvents[sType] = {};
				if (ndElement['on' + sType]) aHandlers[0] = ndElement['on' + sType];
			}

			aHandlers[fnHandler.$$nEventID] = fnHandler;
			ndElement['on' + sType] = this.handle;

			return true;
		},

		exists: function(ndElement, sType, fnHandler)
		{
			return (ndElement.objEvents && ndElement.objEvents[sType] && ndElement.objEvents[sType][fnHandler.$$nEventID]);
		},

		handle: function(e)
		{
			e = e || events.fix(event);

			var bReturn = true, aHandlers = this.objEvents[e.type];
			for (var nIndex in aHandlers)
			{
				this.$$handle = aHandlers[nIndex];
				if (this.$$handle(e) === false) bReturn = false;
			}

			return bReturn;
		},

		fix: function(e)
		{
			e.preventDefault = this.fix.preventDefault;
			e.stopPropagation = this.fix.stopPropagation;

			return e;
		}
	};

	events.fix.preventDefault = function()
	{
		this.returnValue = false;

		return true;
	}

	events.fix.stopPropagation = function()
	{
		this.cancelBubble = true;

		return true;
	}
}

function Slideshow(ndSlideshow)
{
	if (!ndSlideshow) return false;

	var sDirection = (((arguments.length > 1) && arguments[1] && arguments[1].match(/^(top|right|bottom|left|up|down)$/)) ? arguments[1].toLowerCase() : 'left').replace(/^up$/, 'top').replace(/^down$/, 'bottom');
	var nMoveDistance = ((arguments.length > 2) && !isNaN(parseFloat(arguments[2]))) ? parseFloat(arguments[2]) : 1;
	var nMoveTime = ((arguments.length > 3) && !isNaN(parseFloat(arguments[3]))) ? parseFloat(arguments[3]) : 40;
	var bReverseOrder = (arguments.length > 4) && arguments[4];
	var sClientLength = ((sDirection == 'top') || (sDirection == 'bottom')) ? 'clientHeight' : 'clientWidth';
	var sLength = (sClientLength == 'clientHeight') ? 'height' : 'width';
	var sOppositeLength = (sClientLength == 'clientHeight') ? 'width' : 'height';

	var bInit = false;
	var aImages = null;
	var nIntervalID = null;
	var ndSlidingBox = null, ndContainer = null;
	var nSlideshowLength = null;
	var nSlideshowImages = null;

	var Init = function()
	{
		bInit = true;

		for (var nImage = 0; ndSlideshow.childNodes[nImage]; ++nImage)
		{
			if (!ndSlideshow.childNodes[nImage].style) ndSlideshow.removeChild(ndSlideshow.childNodes[nImage--]);
		}

		if (!ndSlideshow.childNodes.length) return false;
		if (bReverseOrder) ReverseChildren(ndSlideshow);

		var sOppositeValue = xb.getStyle(ndSlideshow, sOppositeLength);

		ndContainer = xb.createElement('div');
		ndContainer.style.position = 'relative';
		ndContainer.style.overflow = 'hidden';
		ndContainer.style.margin = '0';
		ndContainer.style.padding = '0';
		ndContainer.style.border = '0';
		ndContainer.style[sOppositeLength] = sOppositeValue;

		ndSlidingBox = xb.createElement('div');
		ndSlidingBox.style.position = 'absolute';
		ndSlidingBox.style.margin = '0';
		ndSlidingBox.style.padding = '0';
		ndSlidingBox.style.border = '0';
		ndSlidingBox.style.top = 'auto';
		ndSlidingBox.style.right = 'auto';
		ndSlidingBox.style.bottom = 'auto';
		ndSlidingBox.style.left = 'auto';
		ndSlidingBox.style[sLength] = '10000px';
		ndContainer.appendChild(ndSlidingBox);
		ndSlideshow.appendChild(ndContainer);

		nSlideshowLength = 0;
		while (ndSlideshow.childNodes.length > 1)
		{
			ndSlidingBox.appendChild(ndSlideshow.removeChild(ndSlideshow.firstChild));

			ndSlidingBox.lastChild.style.display = 'block';
			ndSlidingBox.lastChild.style.position = 'absolute';
			ndSlidingBox.lastChild.style.top = 'auto';
			ndSlidingBox.lastChild.style.right = 'auto';
			ndSlidingBox.lastChild.style.bottom = 'auto';
			ndSlidingBox.lastChild.style.left = 'auto';
			ndSlidingBox.lastChild.style[sDirection] = nSlideshowLength + 'px';
			ndSlidingBox.lastChild.style[sOppositeLength] = ndContainer.style[sOppositeLength];

			ndSlidingBox.style[sLength] = (parseFloat(ndSlidingBox.style[sLength]) + ndSlidingBox.lastChild[sClientLength]) + 'px';
			nSlideshowLength += ndSlidingBox.lastChild[sClientLength];
		}

		aImages = ndSlidingBox.childNodes;
		nSlideshowImages = aImages.length;

		return true;
	};

	this.Start = function()
	{
		return Start();
	};

	this.Stop = function()
	{
		return Stop();
	};

	var Start = function()
	{
		if (!bInit && !Init()) return false;

		if (!events.exists(ndSlideshow, 'mouseover', Stop)) events.add(ndSlideshow, 'mouseover', Stop);
		if (!events.exists(ndSlideshow, 'mouseout', Start)) events.add(ndSlideshow, 'mouseout', Start);

		if (nIntervalID !== null) window.clearInterval(nIntervalID);
		nIntervalID = window.setInterval(Move, nMoveTime);

		return true;
	};

	var Stop = function()
	{
		window.clearInterval(nIntervalID);
		nIntervalID = null;

		return true;
	};

	var Move = function()
	{
		if ((ndSlidingBox.style[sDirection] == 'auto') || ((parseFloat(ndSlidingBox.style[sDirection]) + nSlideshowLength) <= 0))
		{
			ndSlidingBox.style.top = 'auto';
			ndSlidingBox.style.right = 'auto';
			ndSlidingBox.style.bottom = 'auto';
			ndSlidingBox.style.left = 'auto';
			ndSlidingBox.style[sDirection] = '0px';
		}

		ndSlidingBox.style[sDirection] = (parseFloat(ndSlidingBox.style[sDirection]) - nMoveDistance) + 'px';

		var nNewImages = (nSlideshowImages * (parseInt(ndSlideshow[sClientLength] / nSlideshowLength, 10) + 2)) - aImages.length;
		var nPosition = parseFloat(aImages[aImages.length - 1].style[sDirection]) + aImages[aImages.length - 1][sClientLength];
		for (var nImage = 0; nImage < nNewImages; ++nImage)
		{
			ndSlidingBox.appendChild(aImages[(nImage % nSlideshowImages)].cloneNode(true));
			ndSlidingBox.style[sLength] = (parseFloat(ndSlidingBox.style[sLength]) + aImages[(nImage % nSlideshowImages)][sClientLength]) + 'px';
			aImages[aImages.length - 1].style.position = 'absolute';
			aImages[aImages.length - 1].style[sDirection] = nPosition + 'px';

			nPosition += aImages[(nImage % nSlideshowImages)][sClientLength];
		}

		return true;
	};

	var ReverseChildren = function(ndParent)
	{
		var aChildren = ndParent.childNodes;
		for (var nChild = aChildren.length - 1; nChild >= 0; --nChild)
		{
			ndParent.appendChild(ndParent.removeChild(aChildren[nChild]));
		}

		return true;
	};

	return true;
}

events.add(window, 'load', function()
{
	var ndElement, sDirection, nDistance, nTime, bReverse;

	var aSlideshows = [];
	var aElements = xb.getElementsByAttribute(document, 'class', 'slideshow');
	for (var nIndex = 0; aElements[nIndex]; ++nIndex)
	{
		ndElement = aElements[nIndex];

		sDirection = xb.getOption(ndElement, 'ss-direction', 'left');
		nDistance = xb.getOption(ndElement, 'ss-distance', 1);
		nTime = xb.getOption(ndElement, 'ss-time', 40);
		bReverse = ndElement.getAttribute('ss-reverse') || ndElement.className.match(/(?:^|\s)ss-reverse(?:\s|$)/);

		aSlideshows[aSlideshows.length] = new Slideshow(ndElement, sDirection, nDistance, nTime, bReverse);
		aSlideshows[aSlideshows.length - 1].Start();
	}

	return true;
});
