String.prototype.prependcontext = function() {
    if (typeof contextpath == "undefined") {
        throw new Error("global variable contextpath not found, maybe you not include common.ftl.");
    }
    var result = (this.startsWith("/") ? "" : "/") + this;
    return result.startsWith(contextpath) ? result: contextpath + result;
}

String.prototype.trim = function() {
    if (this == void 0) {throw new Error("Illegal argument error.");}
    return this.replace(/(^\s+)|(\s+$)/g, "");
}

String.prototype.isBlank = function() {
    if (this == void 0) {throw new Error("Illegal argument error.");}
    return this == null || this.trim().length == 0;
}

String.prototype.startsWith = function(str) {
    if (this == void 0) {throw new Error("Illegal argument error.");}
    return this.substr(0, str.length) == str;
}