package fitloop.member;

import fitloop.common.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public enum AuthErrorCode implements ErrorCode {
    MISSING_ISSUER_TOKEN(HttpStatus.UNAUTHORIZED, "issuer가 존재하지 않는 토큰입니다"),
    NOT_WOOHAENGSHI_TOKEN(HttpStatus.UNAUTHORIZED, "발급자가 잘못된 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다"),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰 형식입니다"),
    INCORRECTLY_CONSTRUCTED_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 구조의 토큰입니다"),
    FAILED_SIGNATURE_TOKEN(HttpStatus.UNAUTHORIZED, "서명에 실패한 토큰입니다"),
    INVALID_CLAIM_TYPE(HttpStatus.UNAUTHORIZED, "토큰의 claim값은 Long 타입이어야 합니다"),
    NOT_EXIST_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "액세스 토큰이 존재하지 않습니다."),
    INCORRECT_CONSTRUCT_HEADER(HttpStatus.UNAUTHORIZED, "잘못된 형식의 인증 헤더입니다."),
    INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, "접근 권한이 잘못되었습니다."),
    FAIL_TO_SIGN_IN(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Refresh Token을 찾을 수 없습니다."),
    AUTHORIZATION_HEADER_NOT_FOUND(HttpStatus.NOT_FOUND, "Authorization 헤더를 찾을 수 없습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다.");

    private final HttpStatus status;
    private final String message;

    AuthErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
