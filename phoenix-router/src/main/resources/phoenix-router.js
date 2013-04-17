pathRegex = new RegExp("[^:]+://[^/]+(.*)");
function pathOf(href) {
	path = href.replace(pathRegex, "$1");
	return path == "" ? "/" : path;
}

function shouldReplace(href) {
	return true;
}

function newHrefFor(oldHref) {
	loc = window.location
	newHref = loc.protocol + "//" + "www.dianping.com";
	if(loc.port) {
		newHref = newHref + ":" + loc.port;
	}
	newHref = newHref + pathOf(oldHref);
	return newHref;
}

var as = document.getElementsByTagName("a");
// window.location == window.location.protocol + "//" + window.location.host + window.location.pathname
for(i=0; i<as.length; i++) {
	a = as[i];
	href = a["href"];
	if(shouldReplace()) {
		a["href"] = newHrefFor(a["href"]);
	}
}