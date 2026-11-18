package br.com.fabricyo.protocolos.repository;

import br.com.fabricyo.protocolos.domain.Protocolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ProtocoloRepository
        extends JpaRepository<Protocolo, UUID>, JpaSpecificationExecutor<Protocolo> {

    Optional<Protocolo> findByNumero(String numero);

    // Busca o próximo valor da sequência criada na migration V1.
    // nativeQuery porque nextval() é específico do PostgreSQL, não existe em JPQL.
    @Query(value = "SELECT nextval('protocolo_numero_seq')", nativeQuery = true)
    long proximoNumeroSequencial();
}
