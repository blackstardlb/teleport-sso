package nl.blackstardlb.sso.controller

import mu.KLoggable
import mu.KLogger
import nl.blackstardlb.sso.exceptions.AppAuthenticationFailed
import nl.blackstardlb.sso.exceptions.NoPrincipalException
import nl.blackstardlb.sso.exceptions.UserNotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionControllerAdvice : ResponseEntityExceptionHandler(), KLoggable {
    override val logger: KLogger by lazy { logger() }

    @ExceptionHandler(NoPrincipalException::class)
    fun handleUnauthorized(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
        logger.error("UnauthorizedException", ex)
        val bodyOfResponse = "Unauthorized"
        return handleExceptionInternal(
            ex,
            bodyOfResponse,
            HttpHeaders(),
            HttpStatus.UNAUTHORIZED,
            request
        )
    }

    @ExceptionHandler(UserNotFoundException::class, AppAuthenticationFailed::class)
    fun handleForbidden(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
        logger.error("ForbiddenException", ex)
        val bodyOfResponse = "Forbidden"
        return handleExceptionInternal(
            ex,
            bodyOfResponse,
            HttpHeaders(),
            HttpStatus.FORBIDDEN,
            request
        )
    }
}
