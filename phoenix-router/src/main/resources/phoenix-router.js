pathRegex = new RegExp("[^:]+://[^/]+(.*)");
hostRegex = new RegExp("[^:]+://([^/]+).*");
var body = document.body; 

function pathOf(href) {
	path = href.replace(pathRegex, "$1");
	return path == "" ? "/" : path;
}

function shouldReplace(href) {
	loc = window.location
	curHost = loc.host;
	aHost = href.replace(hostRegex, "$1");
	return curHost != aHost;
}

function newHrefFor(oldHref) {
	loc = window.location
	newHref = loc.protocol + "//" + loc.host;
	newHref = newHref + pathOf(oldHref);
	return newHref;
}

body.addEventListener && body.addEventListener('click', function(e){
    e.preventDefault();
    if(e.target.tagName.toLowerCase() === 'a'){
        var href = e.target.href;
        if(shouldReplace(href)){
        	href = newHrefFor(href);
        }
        location.href = href;
    }
}, false);