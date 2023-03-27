var tags = null;
var paths = new Object();
var noResult = {l: "No results found"};
var articleLevel = 0;

$(function () {
   var loc = window.location.href;
   var split = loc.split("/");
   var jsonPath = "./resources/articles.json";
   if (split.length > 3) {
      if (split[split.length - 2] === "articles") {
         jsonPath = "../resources/articles.json";
         articleLevel = 1;
      } else if (split.length > 4 && split[split.length - 3] === "articles") {
         jsonPath = "../../resources/articles.json";
         articleLevel = 2;
      }
   }
   $.ajax({
      url: jsonPath,
      dataType: "text",
      success: function (text) {
         var data = jQuery.parseJSON(text);
         tags = new Array(data.length);
         for (i = 0; i < data.length; i++) {
            var name = data[i].name;
            var url = data[i].url;
            tags[i] = name;
            paths[name] = url;
         }
         $("#tags").autocomplete({
            source: tags,
            select: function (event, ui) {
               if (ui.item.label !== null) {
                  var path = paths[ui.item.label];
                  if (path !== null) {
                     if (articleLevel === 0) {
                        window.location.href = path;
                     } else if (articleLevel === 1) {
                        window.location.href = "../" + path;
                     } else {
                        window.location.href = "../.." + path;
                     }
                  }
               }
            }
         });
      }});
   $.extend($.ui.autocomplete.prototype, {
      _renderItem: function (ul, item) {
         var searchMask = this.element.val();
         var regEx = new RegExp(searchMask, "ig");
         var replaceMask = "<b style=\"font-weight:bold;text-decoration:underline;\">$&</b>";
         var html = item.label.replace(regEx, replaceMask);

         return $("<li></li>")
                 .data("item.autocomplete", item)
                 .append($("<a></a>").html(html))
                 .appendTo(ul);
      }
   });
});
