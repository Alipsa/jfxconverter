var maxCount = 30;
var index = null;
var theArray = null;
var theNames = null;
var theMeta = null;

function initSearch() {
String.prototype.removeDiacritics = function() {
    var diacritics = [
        [/[\300-\306]/g, 'A'],
        [/[\340-\346]/g, 'a'],
        [/[\310-\313]/g, 'E'],
        [/[\350-\353]/g, 'e'],
        [/[\314-\317]/g, 'I'],
        [/[\354-\357]/g, 'i'],
        [/[\322-\330]/g, 'O'],
        [/[\362-\370]/g, 'o'],
        [/[\331-\334]/g, 'U'],
        [/[\371-\374]/g, 'u'],
        [/[\321]/g, 'N'],
        [/[\361]/g, 'n'],
        [/[\307]/g, 'C'],
        [/[\347]/g, 'c'],
    ];
    var s = this;
    for (var i = 0; i < diacritics.length; i++) {
        s = s.replace(diacritics[i][0], diacritics[i][1]);
    }
    return s;
}
  var match = RegExp('[?&]search=([^&]*)').exec(window.location.search);
  value = match && decodeURIComponent(match[1].replace(/\+/g, ' '));
  value = value.removeDiacritics();
  if (! index) {
    index = elasticlunr.Index.load(searchDump, "{expand: true}");
  }
  var result = index.search(value);

  if (result.length) {

    if (! theArray) {
	  theArray = [];
     theMeta = [];
     theNames = [];
	  var data = getFullTextContent();
	  var i;
	  for (i = 0; i < data.length; i++) {
	     var id = data[i].id;
        var meta = data[i].meta;
		  if (! theArray[id]) {
		    theArray[id] = data[i].url;
          theNames[id] = data[i].name;
          theMeta[id] = data[i].meta;
		  }
	  }
	}

	var title = "<h1>Results for: ";
	title = title.concat(value, "</h1>");
	document.getElementById("title").innerHTML = title;

	var list = "<ul>";
	var count = result.length;
	if (count > maxCount) {
	  count = maxCount;
	}
	var j;
	for (j = 0; j < count; j++) {
	  var ref = result[j].ref;
	  list = list.concat("<li>");
     var link = theArray[ref] + "?search=" + value;
     var meta = theMeta[ref];
     var desc = theNames[ref];
	  list = list.concat("<a href=\"", link, "\">", desc, "</a>");
     list = list.concat(": ", meta);
	  list = list.concat("</li>");
    }
	list = list.concat("</ul>");
	document.getElementById("results").innerHTML = list;
  }
}
