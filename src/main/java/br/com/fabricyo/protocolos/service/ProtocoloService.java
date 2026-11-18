package br.com.fabricyo.protocolos.service;

import br.com.fabricyo.protocolos.domain.Protocolo;
import br.com.fabricyo.protocolos.dto.CriarProtocoloRequest;
import br.com.fabricyo.protocolos.dto.PaginaResponse;
import br.com.fabricyo.protocolos.dto.ProtocoloFiltro;
import br.com.fabricyo.protocolos.dto.ProtocoloResponse;
import br.com.fabricyo.protocolos.repository.ProtocoloRepository;
import br.com.fabricyo.protocolos.repository.ProtocoloSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class ProtocoloService {

    private final ProtocoloRepository repository;

    public ProtocoloService(ProtocoloRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProtocoloResponse criar(CriarProtocoloRequest request) {
        String numero = gerarNumero();

        Protocolo protocolo = new Protocolo(
                numero,
                request.assunto(),
                request.descricao(),
                request.solicitante()
        );

        Protocolo salvo = repository.save(protocolo);
        return ProtocoloResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public PaginaResponse<ProtocoloResponse> listar(ProtocoloFiltro filtro, Pageable paginacao) {
        Page<ProtocoloResponse> pagina = repository
                .findAll(ProtocoloSpecifications.comFiltro(filtro), paginacao)
                .map(ProtocoloResponse::de);
        return PaginaResponse.de(pagina);
    }

    @Transactional(readOnly = true)
    public ProtocoloResponse buscarPorId(UUID id) {
        return ProtocoloResponse.de(buscarEntidade(id));
    }

    @Transactional
    public ProtocoloResponse iniciarAtendimento(UUID id) {
        Protocolo protocolo = buscarEntidade(id);
        protocolo.iniciarAtendimento();
        return ProtocoloResponse.de(protocolo);
    }

    @Transactional
    public ProtocoloResponse concluir(UUID id) {
        Protocolo protocolo = buscarEntidade(id);
        protocolo.concluir();
        return ProtocoloResponse.de(protocolo);
    }

    @Transactional
    public ProtocoloResponse cancelar(UUID id) {
        Protocolo protocolo = buscarEntidade(id);
        protocolo.cancelar();
        return ProtocoloResponse.de(protocolo);
    }

    private Protocolo buscarEntidade(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProtocoloNaoEncontradoException(
                        "Protocolo não encontrado: " + id));
    }

    // Monta o número visível no formato AAAA/NNNNNN, por exemplo 2026/000123.
    // O sequencial vem do banco (nextval), garantindo unicidade sob concorrência.
    private String gerarNumero() {
        long sequencial = repository.proximoNumeroSequencial();
        int ano = LocalDate.now().getYear();
        return String.format("%d/%06d", ano, sequencial);
    }
}
