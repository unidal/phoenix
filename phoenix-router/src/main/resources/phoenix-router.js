var pathRegex = new RegExp("[^:]+://([^/]+)(.*)");
var hostRegex = new RegExp("[^:]+://([^/]+).*");
var body = document.body; 

function setCookie(name, value) {
	var exdate=new Date();
	exdate.setDate(exdate.getDate() + 7);
	var escapedValue = escape(value) + "; expires=" + exdate.toUTCString();
	document.cookie = name + "=" + escapedValue;
}

function pathOf(href) {
	setCookie("phoenixVirtualServer", href.replace(pathRegex, "$1"));
	var path = href.replace(pathRegex, "$2");
	return path == "" ? "/" : path;
}

function shouldReplace(href) {
	// replace href with potentially other host only
	if(href && (href.substr(0, 7) == "http://" || href.substr(0, 8) == "https://")) {
		var loc = window.location
		var curHost = loc.host;
		var aHost = href.replace(hostRegex, "$1");
		return curHost != aHost;
	} else {
		return false;
	}
}

function newHrefFor(oldHref) {
	var loc = window.location
	var newHref = loc.protocol + "//" + loc.host;
	newHref = newHref + pathOf(oldHref);
	return newHref;
}

function findAnchor(target) {
	var curNode = target;
	while(curNode) {
		if(curNode.tagName.toLowerCase() === 'a') {
			return curNode;
		} else {
			curNode = curNode.parentNode;
		}
	}
	return null;
}

body.addEventListener && body.addEventListener('click', function(e){
	var anchor = findAnchor(e.target);
    if(anchor){
        var href = anchor.href;
       	console.log("intercept " + href);
        if(shouldReplace(href)){
    		//e.preventDefault();
    		var newHref = newHrefFor(href);
        	console.log("will go to " + newHref);
        	anchor.href = newHref;
        }
    }
}, false);
