document.getElementById('buscarCliente').addEventListener('input', function () {
    const filtro = this.value.toLowerCase();
    const opciones = document.querySelectorAll('#usuarioSelect option');

    opciones.forEach(option => {
        const texto = option.getAttribute('data-search');
        if (texto && texto.toLowerCase().includes(filtro)) {
            option.style.display = 'block';
        } else if (option.value === "") {
            option.style.display = 'block'; // opción "Seleccione..."
        } else {
            option.style.display = 'none';
        }
    });
});

document.querySelectorAll('.sidebar-menu a').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        document.querySelector(this.getAttribute('href')).scrollIntoView({
            behavior: 'smooth'
        });
    });
});