package fitloop.common.exception.response;


import org.springframework.validation.FieldError;


public record ValidErrorResponse(String field, String message) {

    public static ValidErrorResponse from(FieldError error) {
        return new ValidErrorResponse(error.getField(), error.getDefaultMessage());
    }

}
