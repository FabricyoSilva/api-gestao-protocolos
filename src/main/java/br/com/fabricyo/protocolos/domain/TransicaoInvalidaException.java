package br.com.fabricyo.protocolos.domain;

// Lançada quando se tenta uma transição de estado não permitida
// (ex.: concluir um protocolo já cancelado).
// No tratador de erros é mapeada para HTTP 409 Conflict.
public class TransicaoInvalidaException extends RuntimeException {

    public TransicaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
