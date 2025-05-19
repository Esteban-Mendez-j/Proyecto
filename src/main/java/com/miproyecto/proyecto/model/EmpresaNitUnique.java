package com.miproyecto.proyecto.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.miproyecto.proyecto.service.EmpresaService;

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
 * Validate that the nit value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = EmpresaNitUnique.EmpresaNitUniqueValidator.class
)
public @interface EmpresaNitUnique {

    String message() default "{Exists.empresa.nit}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class EmpresaNitUniqueValidator implements ConstraintValidator<EmpresaNitUnique, String> {

        private final EmpresaService empresaService;
        private final HttpServletRequest request;

        public EmpresaNitUniqueValidator(final EmpresaService empresaService,
                final HttpServletRequest request) {
            this.empresaService = empresaService;
            this.request = request;
        }

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
                Long id = Long.parseLong(currentId);
                
                if (value.equalsIgnoreCase((empresaService.get(id)).getNit())) {
                    // value hasn't changed
                    return true;
                }
            }
            return !empresaService.nitExists(value);
        }

    }

}
