package br.com.fabricyo.protocolos.dto;

import br.com.fabricyo.protocolos.domain.Status;

import java.time.LocalDate;

// Critérios de busca, todos opcionais. Um campo nulo = filtro não aplicado.
public record ProtocoloFiltro(
        Status status,
        String solicitante,
        LocalDate aberturaDe,
        LocalDate aberturaAte
) {
}
