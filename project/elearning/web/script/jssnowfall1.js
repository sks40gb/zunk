


function lib2bwcheck(){
	this.ver=navigator.appVersion;
	this.agent=navigator.userAgent;
	this.dom=document.getElementById?1:0;
	this.opera5=this.agent.indexOf("Opera 5")>-1;
	this.ie5=(this.ver.indexOf("MSIE 5")>-1 && this.dom && !this.opera5)?1:0; 
	this.ie6=(this.ver.indexOf("MSIE 6")>-1 && this.dom && !this.opera5)?1:0;
	this.ie4=(document.all && !this.dom && !this.opera5)?1:0;
	this.ie=this.ie4||this.ie5||this.ie6;
	this.mac=this.agent.indexOf("Mac")>-1;
	this.ns6=(this.dom && parseInt(this.ver) >= 5) ?1:0; 
	this.ns4=(document.layers && !this.dom)?1:0;
	this.bw=(this.ie6 || this.ie5 || this.ie4 || this.ns4 || this.ns6 || this.opera5);
	return this;
}
var bw=new lib2bwcheck()
var px = bw.ns4||window.opera?"":"px";

function run() {
 var css, obj, nest, ooo;
 if ((document.all) && (!bw.opera5)) {
  movy = document.body.clientHeight-64;
  movx = document.body.clientWidth-50;
 } else {
  movx = window.innerWidth-50;
  movy = window.innerHeight-64;
 }
 for (var i=0; i<maxitems; i++) {
  if (ds[i]<=0) {
	sx[i] = Math.round(Math.random()*(sxto-sxfrom)+sxfrom);
	sy[i] = Math.round(Math.random()*(syto-syfrom)+syfrom);
	ds[i] = Math.round(Math.random()*(sdto-sdfrom)+sdfrom);
  }
  ox[i]+=sx[i]; if (ox[i]>movx) ox[i]=0; if (ox[i]<0) ox[i] = movx;
  oy[i]+=sy[i]; if (oy[i]>movy) oy[i]=0; if (oy[i]<0) oy[i] = movy;
  ds[i]--;
  if (bw.ns4) {
	ooo = eval("document.s"+i);
	ooo.moveTo(ox[i], oy[i]);
  } else {
	obj = "s"+i; nest="";
   	css= bw.dom?document.getElementById(obj).style:bw.ie4?document.all[obj].style:bw.ns4?eval(nest+"document.layers." +obj):0;
	css.left = ox[i]; css.top = oy[i];
  }
 }
 setTimeout("run()",tpause,"JavaScript");
}

var maxitems=20;
var sxfrom=-2;
var sxto=2;
var syfrom=1;
var syto=3;
var sdfrom=4;
var sdto=10;
var pcol=Number(255).toString(16);
var tpause=20;
var schar=".";

var fontface = 0;
var fontsize = "6";
if (fontface==0) fontface = 'Arial, Helvetica, sans-serif';
  else if (fontface==1) fontface = 'Times New Roman, serif';
  else if (fontface==2) fontface = 'Courier New, Courier, mono';
  else if (fontface==3) fontface = 'Georgia, Times New Roman, Times, serif';
  else fontface = 'Verdana, Arial, Helvetica, sans-serif';

/*
var maxitems=20;
var sxfrom=-2;
var sxto=2;
var syfrom=1;
var syto=3;
var sdfrom=4;
var sdto=10;
var pcol='00ffff';
var tpause=20;
var schar='.';
*/

var t=0;

t = pcol.length;
for (var i=0; i<6-t; i++) pcol = '0'+pcol;

if (sxfrom>sxto) { t=sxto; sxto=sxfrom; sxfrom=t; }
if (syfrom>syto) { t=syto; syto=syfrom; syfrom=t; }
if (sdfrom>sdto) { t=sdto; sdto=sdfrom; sdfrom=t; }

if ((document.all) && (!bw.opera5)) {
  movy = document.body.clientHeight-64;
  movx = document.body.clientWidth-50;
} else {
  movx = window.innerWidth-50;
  movy = window.innerHeight-64;
}

ox = new Array();
oy = new Array();
sx = new Array();
sy = new Array();
ds = new Array();
pa = new Array();

for (var i=0; i<maxitems; i++) {
  if (bw.ns4) document.writeln("<layer id='s"+i+"'>");
    else document.writeln("<div id='s"+i+"' style='position:absolute; z-index:3;'>");
  document.writeln('<font color=#'+pcol+' face="'+fontface+'" size="'+fontsize+'">'+schar+'</font>');
  if (bw.ns4) document.writeln("</layer>");
    else { document.writeln("</div>");	}
  ox[i] = Math.round(Math.random()*movx);
  oy[i] = Math.round(Math.random()*movy);
  ds[i] = 0;
}

setTimeout("run()",tpause,"JavaScript");



//DO NOT MODIFY
/*
<APPLETINFO>
appletname=jssnowfall
applettype=JAVASCRIPT
created=1171265304687
appletfilename=jssnowfall1.js
</APPLETINFO>
<JAVASCRIPT>
fontsize=6
maxitems=20
sxfrom=-2
sxto=2
syfrom=1
syto=3
sdfrom=4
sdto=10
tpause=20
schar=.
fontface=0
pcol=255
</JAVASCRIPT>
<HTMLGENERATOR>
null</HTMLGENERATOR>
*/