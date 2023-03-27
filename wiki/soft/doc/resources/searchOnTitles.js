var tags = null;
var paths = new Object();
var noResult = {l: "No results found"};
var articleLevel = 0;
var lastItem = 0;
var searchMask = null;
var alwaysShow = false;

$(function () {
    $.widget( "custom.catcomplete", $.ui.autocomplete, {
	   alwaysShow: alwaysShow,
      _create: function() {
        this._super();
        this.widget().menu( "option", "items", "> :not(.ui-autocomplete-category)" );
      },
      _renderMenu: function( ul, items ) {
        var that = this,
        currentCategory = "";
		  if (items.length >= 1) {
          $.each( items, function( index, item ) {
            var li;
            if ( item.category != currentCategory ) {
              ul.append( "<li class='ui-autocomplete-category'>" + item.category + "</li>" );
              currentCategory = item.category;
            }
            li = that._renderItemData( ul, item );
            if ( item.category ) {
              li.attr( "aria-label", item.category + " : " + item.label );
            }
          });
		  }
		  if (alwaysShow) {
  		    ul.append( "<li class='ui-autocomplete-category'>" + lastItem.name + "</li>" );
		    var searchMask = this.element.val();
		    lastItem.label = searchMask;
		    li = that._renderItemData( ul, lastItem );
		  }
      },	  
    });
   var loc = window.location.href;
   var split = loc.split("/");
   if (split.length > 3) {
      if (split[split.length - 2] === "articles") {
         articleLevel = 1;
      } else if (split.length > 4 && split[split.length - 3] === "articles") {
         articleLevel = 2;
      }
   }
   var data = getArticles();
	var offset = 0;
	if (data[data.length - 1].category === "fulltext") {
	   offset = 1;
	}
   tags = new Array(data.length - offset);
   for (i = 0; i < data.length; i++) {
     var item = new Object();
     item.name = data[i].name;
     item.category = data[i].category;
     item.title = data[i].title;
     item.titleID = data[i].titleID;
     if (item.category === "articles") {
       item.label = item.name;
     } else if (item.category === "fulltext") {
       item.label = "";		
	    alwaysShow = true;
     } else {
       item.label = item.title;
     }
     var url = data[i].url;
	  if (item.category !== "fulltext") {
       tags[i] = item;
	  }
     paths[item.name] = url;
	  lastItem = item;
    }
    $("#tags").catcomplete({
      source: tags,
      select: function (event, ui) {
        if (ui.item.name !== null) {
          var path = paths[ui.item.name];
          var title = ui.item.title;
          if (path !== null) {
            if (title != "") {
              path = path + "#" + ui.item.titleID;
            }
			   if (path === "fullTextSearch.html") {
			     path = path + "?search=" + ui.item.label;
			   }
            if (articleLevel === 0) {
              window.location.href = path;
            } else if (articleLevel === 1) {
              window.location.href = "../" + path;
            } else {
              window.location.href = "../.." + path;
            }
          }
        }
      },
      response: function( event, ui ) {
        if (!ui.content.length && alwaysShow) {
            var noResult = { category:"containing...", label: "toto" };
            ui.content.push(noResult);
        }
	  }		  
    });
   $.extend($.ui.autocomplete.prototype, {
      _renderItem: function (ul, item) {
         searchMask = this.element.val();
         var regEx = new RegExp(searchMask, "ig");
         var replaceMask = "<b style=\"font-weight:bold;text-decoration:underline;\">$&</b>";
         var html = null;
         if (item.category === "articles") {
            html = item.name.replace(regEx, replaceMask);
         } else if (item.category === "titles") {
            html = "<i>" + item.name + "</i> : " + item.title.replace(regEx, replaceMask);
         } else {
            html = item.label;
         }

         return $("<li></li>")
                 .data("item.autocomplete", item)
                 .append($("<a></a>").html(html))
                 .appendTo(ul);
      }	  
   });
});
