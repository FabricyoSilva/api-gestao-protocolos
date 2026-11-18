package br.com.fabricyo.protocolos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Entrada: apenas os campos que o cliente pode preencher ao abrir um protocolo.
// numero, status, abertura e encerramento são controlados pelo sistema.
public record CriarProtocoloRequest(

        @NotBlank(message = "assunto é obrigatório")
        @Size(max = 150, message = "assunto deve ter no máximo 150 caracteres")
        String assunto,

        @Size(max = 10000, message = "descricao deve ter no máximo 10000 caracteres")
        String descricao,

        @NotBlank(message = "solicitante é obrigatório")
        @Size(max = 150, message = "solicitante deve ter no máximo 150 caracteres")
        String solicitante
) {
}
