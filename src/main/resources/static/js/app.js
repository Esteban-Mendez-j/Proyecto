/**
 * Register an event at the document for the specified selector,
 * so events are still catched after DOM changes.
 */
function handleEvent(eventType, selector, handler) {
  document.addEventListener(eventType, function(event) {
    if (event.target.matches(selector + ', ' + selector + ' *')) {
      handler.apply(event.target.closest(selector), arguments);
    }
  });
}

handleEvent('submit', '.js-submit-confirm', function(event) {
  if (!confirm(this.getAttribute('data-confirm-message'))) {
    event.preventDefault();
    return false;
  }
  return true;
});

function initDatepicker() {
  document.querySelectorAll('.js-datepicker, .js-timepicker, .js-datetimepicker').forEach(($item) => {
    const flatpickrConfig = {
      allowInput: true,
      time_24hr: true,
      enableSeconds: true
    };
    if ($item.classList.contains('js-datepicker')) {
      flatpickrConfig.dateFormat = 'Y-m-d';
    } else if ($item.classList.contains('js-timepicker')) {
      flatpickrConfig.enableTime = true;
      flatpickrConfig.noCalendar = true;
      flatpickrConfig.dateFormat = 'H:i:S';
    } else { // datetimepicker
      flatpickrConfig.enableTime = true;
      flatpickrConfig.altInput = true;
      flatpickrConfig.altFormat = 'Y-m-d H:i:S';
      flatpickrConfig.dateFormat = 'Y-m-dTH:i:S';
      // workaround label issue
      flatpickrConfig.onReady = function() {
        const id = this.input.id;
        this.input.id = null;
        this.altInput.id = id;
      };
    }
    flatpickr($item, flatpickrConfig);
  });
}
initDatepicker();


document.addEventListener("DOMContentLoaded", function() {
  // Mensaje de éxito
  const successMessage = "[[${successMessage}]]".trim();
  if (successMessage !== "") {
      Swal.fire({
          icon: 'success',
          title: 'Éxito',
          text: successMessage,
          timer: 2000,  // Se muestra por 2 segundos
          showConfirmButton: false
      });
  }

  // Mensaje de error
  const errorMessage = "[[${errorMessage}]]".trim();
  if (errorMessage !== "") {
      Swal.fire({
          icon: 'error',
          title: 'Error',
          text: errorMessage,
          timer: 2000,  // Se muestra por 2 segundos
          showConfirmButton: false
      });
  }
});



