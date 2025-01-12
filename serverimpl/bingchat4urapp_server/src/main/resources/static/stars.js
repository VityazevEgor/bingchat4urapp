function createStar() {
    const star = document.createElement('div');
    star.className = 'star';
    star.style.left = `${Math.random() * 100}vw`;
    star.style.top = `${Math.random() * 100}vh`;
    star.style.animationDelay = `${Math.random() * 5}s`;
    star.style.background = Math.random() > 0.5 ? '#FFFFFF' : '#02FF04';
    document.getElementById('stars').appendChild(star);
}

for (let i = 0; i < 50; i++) {
    createStar();
}