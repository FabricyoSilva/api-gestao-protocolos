package br.com.fabricyo.protocolos.dto;

import br.com.fabricyo.protocolos.domain.Protocolo;
import br.com.fabricyo.protocolos.domain.Status;

import java.time.LocalDateTime;
import java.util.UUID;

// Saída: o que a API devolve. Inclui os campos gerados pelo sistema.
public record ProtocoloResponse(
        UUID id,
        String numero,
        String assunto,
        String descricao,
        String solicitante,
        Status status,
        LocalDateTime abertura,
        LocalDateTime encerramento
) {

    // Converte a entidade de domínio no DTO de saída.
    public static ProtocoloResponse de(Protocolo protocolo) {
        return new ProtocoloResponse(
                protocolo.getId(),
                protocolo.getNumero(),
                protocolo.getAssunto(),
                protocolo.getDescricao(),
                protocolo.getSolicitante(),
                protocolo.getStatus(),
                protocolo.getAbertura(),
                protocolo.getEncerramento()
        );
    }
}
