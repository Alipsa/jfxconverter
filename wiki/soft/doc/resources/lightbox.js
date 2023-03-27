function closeModal() {
  document.getElementById('__lightbox__').style.display = "none";
}

function openImage(image) {
  var lightbox =  document.getElementById('__lightbox__');
  var lightboxContent = document.getElementById('__lightboxContent__');
  var modalCaption = document.getElementById('__lightboxCaption__');
  var captionID = image.id + '_caption';
  var caption = document.getElementById(captionID);
  if (caption != null) {
     modalCaption.innerHTML = caption.textContent;
  } else {
     modalCaption.innerHTML = '';
  }
  lightboxContent.src = image.src;
  lightbox.style.display = "block";
  lightbox.style.background = "white";
}
