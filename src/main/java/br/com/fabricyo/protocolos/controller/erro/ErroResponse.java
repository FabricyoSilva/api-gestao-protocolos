package br.com.fabricyo.protocolos.controller.erro;

import java.util.List;

// Formato único de erro devolvido pela API.
// 'campos' só é preenchido em erros de validação; nos demais vem nulo.
public record ErroResponse(
        int status,
        String mensagem,
        List<ErroCampo> campos
) {

    public record ErroCampo(String campo, String motivo) {
    }
}
