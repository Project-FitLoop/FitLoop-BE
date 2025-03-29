package fitloop.common.exception.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import fitloop.common.exception.errorcode.CommonErrorCode;
import fitloop.common.exception.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FitLoopAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorResponse errorResponse = ErrorResponse.from(CommonErrorCode.FORBIDDEN);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(CommonErrorCode.FORBIDDEN.getStatus().value());
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
