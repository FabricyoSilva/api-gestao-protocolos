package br.com.fabricyo.protocolos.repository;

import br.com.fabricyo.protocolos.domain.Protocolo;
import br.com.fabricyo.protocolos.domain.Status;
import br.com.fabricyo.protocolos.dto.ProtocoloFiltro;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

// Monta o WHERE dinamicamente a partir do filtro. Cada condição retorna
// null quando o critério está ausente, e o Spring Data ignora os nulos ao
// compor com allOf. Assim um único caminho cobre todas as combinações.
public final class ProtocoloSpecifications {

    private ProtocoloSpecifications() {
    }

    public static Specification<Protocolo> comFiltro(ProtocoloFiltro filtro) {
        return Specification.allOf(
                statusIgual(filtro.status()),
                solicitanteContem(filtro.solicitante()),
                abertaAPartirDe(filtro.aberturaDe()),
                abertaAte(filtro.aberturaAte())
        );
    }

    private static Specification<Protocolo> statusIgual(Status status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<Protocolo> solicitanteContem(String termo) {
        return (root, query, cb) -> (termo == null || termo.isBlank())
                ? null
                : cb.like(cb.lower(root.get("solicitante")), "%" + termo.toLowerCase() + "%");
    }

    private static Specification<Protocolo> abertaAPartirDe(LocalDate de) {
        return (root, query, cb) ->
                de == null ? null : cb.greaterThanOrEqualTo(root.get("abertura"), de.atStartOfDay());
    }

    private static Specification<Protocolo> abertaAte(LocalDate ate) {
        // < dia seguinte à meia-noite: inclui todo o dia informado em 'ate'.
        return (root, query, cb) ->
                ate == null ? null : cb.lessThan(root.get("abertura"), ate.plusDays(1).atStartOfDay());
    }
}
