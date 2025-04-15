// search.js

// Obtener los elementos necesarios del DOM
const searchBtn = document.getElementById('searchBtn');
const searchInput = document.getElementById('searchInput');
const filterForm = document.getElementById('filterForm');

// Verificar si los elementos existen antes de agregar el event listener
if (searchBtn && searchInput && filterForm) {
    searchBtn.addEventListener('click', () => {
        // Agregar el valor del input de búsqueda al filtro antes de enviar
        const tituloInput = document.createElement('input');
        tituloInput.setAttribute('type', 'hidden');
        tituloInput.setAttribute('name', 'titulo');
        tituloInput.setAttribute('value', searchInput.value);

        // Agregar el campo oculto al formulario
        filterForm.appendChild(tituloInput);

        // Enviar el formulario
        filterForm.submit();
    });
}
