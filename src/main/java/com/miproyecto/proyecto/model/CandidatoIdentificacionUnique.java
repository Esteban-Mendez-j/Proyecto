package com.miproyecto.proyecto.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.miproyecto.proyecto.service.CandidatoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Validar que el valor de identificación no esté tomado aún.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = CandidatoIdentificacionUnique.CandidatoIdentificacionUniqueValidator.class
)
public @interface CandidatoIdentificacionUnique {

    String message() default "{Exists.candidato.identificacion}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class CandidatoIdentificacionUniqueValidator implements ConstraintValidator<CandidatoIdentificacionUnique, String> {

        private final CandidatoService candidatoService;
        private final HttpServletRequest request;

        public CandidatoIdentificacionUniqueValidator(final CandidatoService candidatoService,
                final HttpServletRequest request) {
            this.candidatoService = candidatoService;
            this.request = request;
        }

        // value es el valor de la identificacion que se esta pasando 

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String dataBaseId = pathVariables.get("idUsuario");

            if (dataBaseId != null){
                Long id = Long.parseLong(dataBaseId);
                
                if (value.equalsIgnoreCase(candidatoService.get(id).getIdentificacion()) ) {
                    return true;
                }
            }
            return !candidatoService.identificacionExists(value);
            
        }

    }

}
