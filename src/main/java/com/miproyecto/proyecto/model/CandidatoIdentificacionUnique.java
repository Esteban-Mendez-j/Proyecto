package com.miproyecto.proyecto.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.miproyecto.proyecto.service.CandidatoService;
import com.miproyecto.proyecto.service.EncryptionService;

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
 * Validate that the identificacion value isn't taken yet.
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
        private final EncryptionService encryptionService;

        public CandidatoIdentificacionUniqueValidator(final CandidatoService candidatoService,
                final HttpServletRequest request, final EncryptionService encryptionService) {
            this.candidatoService = candidatoService;
            this.request = request;
            this.encryptionService = encryptionService;
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
            final String currentId = pathVariables.get("idUsuario");

            if (currentId != null){
                Long idDescrypt = encryptionService.decrypt(currentId);
                
                if (value.equalsIgnoreCase(candidatoService.get(idDescrypt).getIdentificacion()) ) {
                    // value hasn't changed
                    return true;
                }
            }
            return !candidatoService.identificacionExists(value);
            
        }

    }

}
