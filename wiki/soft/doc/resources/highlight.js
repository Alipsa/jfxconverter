function highlight(element, keyword) {
  const flags = 'gi';
  const keywordRegex = RegExp(keyword, flags);
  for(var child=element.firstChild; child!==null; child=child.nextSibling) {
    if (child.nodeType !== 3) { // not a text node
      highlight(child, keyword);
    } else if (keywordRegex.test(child.textContent)) {
      const frag = document.createDocumentFragment();
      let lastIdx = 0;
      child.textContent.replace(keywordRegex, function(match, idx) {
        const part = document.createTextNode(child.textContent.slice(lastIdx, idx));
        const highlighted = document.createElement('span');
        highlighted.textContent = match;
        highlighted.classList.add('highlight');
        frag.appendChild(part);
        frag.appendChild(highlighted);
        lastIdx = idx + match.length;
      });
      const end = document.createTextNode(child.textContent.slice(lastIdx));
      frag.appendChild(end);
      child.parentNode.replaceChild(frag, child);
    }
  };
}
