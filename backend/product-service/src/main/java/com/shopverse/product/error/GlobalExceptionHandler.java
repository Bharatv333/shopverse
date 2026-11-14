package com.shopverse.product.error;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Comparator;
import java.util.List;

// One place that turns exceptions into clean JSON errors (RFC 9457 "problem details") for EVERY controller.
// Extending ResponseEntityExceptionHandler gives us ready-made handlers for Spring's own errors
// (bad JSON, wrong HTTP method, missing parameter, ...). We only override the ones we want to change.
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // One entry in the "errors" list: which field, and what's wrong with it.
    public record FieldProblem(String field, String message){ }

    // ---------- our own exceptions ----------

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail notFound(NotFoundException e){
        return problem(HttpStatus.NOT_FOUND, "Not Found", e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail conflict(ConflictException e){
        return problem(HttpStatus.CONFLICT, "Conflict", e.getMessage());
    }

    // Two people saved the same row at once (@Version from Lesson 6)
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail editConflict(ObjectOptimisticLockingFailureException e){
        return problem(HttpStatus.CONFLICT, "Conflict", "Someone else changed this item at the same time. Reload and try again.");
    }

    // IllegalArgumentException: our own checks. PropertyReferenceException: ?sort=noSuchField
    @ExceptionHandler({IllegalArgumentException.class, PropertyReferenceException.class})
    public ProblemDetail badRequest(RuntimeException e){
        return problem(HttpStatus.BAD_REQUEST, "Bad request", e.getMessage());
    }

    // The safety net. Log the full stack trace for US, show a boring message to the CLIENT.
    // Never send e.getMessage() here: it can leak table names, SQL or file paths.
    @ExceptionHandler(Exception.class)
    public ProblemDetail unexpected(Exception e){
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", "Something went wrong on our side, Please try again later.");
    }

    // ---------- validation errors ----------

    // @Valid on a @RequestBody failed -> list every bad field
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request){
        List<FieldProblem> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldProblem(fe.getField(), fe.getDefaultMessage()))
                .sorted(Comparator.comparing(FieldProblem::field))
                .toList();
        ProblemDetail body = validationProblem(errors);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    // A constraint directly on a method parameter failed, e.g. @PositiveOrZero on a @RequestParam
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request){
        List<FieldProblem> errors = ex.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> new FieldProblem(result.getMethodParameter().getParameterName(), error.getDefaultMessage())))
                .sorted(Comparator.comparing(FieldProblem::field))
                .toList();
        ProblemDetail body = validationProblem(errors);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    // ---------- helpers ----------
    private ProblemDetail validationProblem(List<FieldProblem> errors){
        ProblemDetail body = problem(HttpStatus.BAD_REQUEST, "Validation failed", "Some fields are invalid. See 'errors' for details.");
        body.setProperty("errors", errors);
        return body;
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail){
        ProblemDetail body = ProblemDetail.forStatusAndDetail(status, detail);
        body.setTitle(title);
        return body;
    }
}
