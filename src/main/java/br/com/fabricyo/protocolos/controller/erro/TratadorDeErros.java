package br.com.fabricyo.protocolos.controller.erro;

import br.com.fabricyo.protocolos.service.ProtocoloNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratadorDeErros {

    // Protocolo inexistente -> 404 Not Found.
    @ExceptionHandler(ProtocoloNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(ProtocoloNaoEncontradoException ex) {
        ErroResponse corpo = new ErroResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }

    // Falha de validação do @Valid -> 400 Bad Request, listando cada campo inválido.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        List<ErroResponse.ErroCampo> campos = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> new ErroResponse.ErroCampo(erro.getField(), erro.getDefaultMessage()))
                .toList();

        ErroResponse corpo = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                campos
        );
        return ResponseEntity.badRequest().body(corpo);
    }
}
