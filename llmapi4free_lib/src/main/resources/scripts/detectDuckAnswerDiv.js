function findDiv() {
    const divs = document.querySelectorAll('div');
    let foundDiv = null;

    divs.forEach(div => {
        const p = div.querySelector('p');
        if (p && p.textContent.includes('Lol kek') && !p.textContent.startsWith('Напиши слово')) {
            foundDiv = div;
        }
    });

    if (foundDiv) {
        return foundDiv.className; // Return the class name as a string
    } else {
        return 'Div not found'; // Return a plain string if no div is found
    }
}

findDiv();