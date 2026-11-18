package br.com.fabricyo.protocolos.dto;

import org.springframework.data.domain.Page;

import java.util.List;

// Contrato estável de resposta paginada, independente do formato interno
// do Page do Spring. Genérico para reutilizar com qualquer tipo de conteúdo.
public record PaginaResponse<T>(
        List<T> conteudo,
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas,
        boolean ultima
) {

    public static <T> PaginaResponse<T> de(Page<T> page) {
        return new PaginaResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
