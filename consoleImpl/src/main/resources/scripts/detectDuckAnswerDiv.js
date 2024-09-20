const divs = document.querySelectorAll('div');
let foundDiv = null;

divs.forEach(div => {
  const p = div.querySelector('p');
  if (p && p.textContent.includes('Lol kek') && !p.textContent.startsWith('Напиши слово')) {
    foundDiv = div;
  }
});

if (foundDiv) {
  return (foundDiv.className);
} else {
  return ('Div не найден');
}
