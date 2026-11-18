package br.com.fabricyo.protocolos.service;

// Lançada quando um protocolo buscado não existe.
// No passo do tratamento de erros, mapearemos esta exceção para HTTP 404.
public class ProtocoloNaoEncontradoException extends RuntimeException {

    public ProtocoloNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
