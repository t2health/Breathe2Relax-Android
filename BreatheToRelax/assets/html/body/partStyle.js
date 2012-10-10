var winW = 320, winH = 320;
winW = screen.width;
winH = screen.height;
if(winW > 480 || winH > 480) {
	document.write('<link type="text/css" rel="stylesheet" media="all" href="partsL.css" />');
}
else {
	document.write('<link type="text/css" rel="stylesheet" media="all" href="parts.css" />');
}
