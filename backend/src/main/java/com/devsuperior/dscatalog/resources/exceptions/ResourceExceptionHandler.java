package com.devsuperior.dscatalog.resources.exceptions;

import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

//vai interceptar alguma exceção que acontecer na camada de resource(controlador rest),e vai tratar a exceção.
@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class) //para saber o tipo de exceção que vai interceptar
    public ResponseEntity<StandardError> entityNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;//variavel para armazenar o not found, para não ficar repetido abaixo //not found é erro 404
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now()); //pega o horario atual com now()
        err.setStatus(status.value()); //para converter de tipo numerado pra inteiro usa-se value()
        err.setError("Resource not found");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DatabaseException.class) //para saber o tipo de exceção que vai interceptar
    public ResponseEntity<StandardError> database(DatabaseException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;//variavel para armazenar o bad request, para não ficar repetido abaixo //bad request é erro 400
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now()); //pega o horario atual com now()
        err.setStatus(status.value());//converte tipo numerado pra inteiro, usamos value()
        err.setError("Database exception");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

}
