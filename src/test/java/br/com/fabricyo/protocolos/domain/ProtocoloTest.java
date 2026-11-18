package br.com.fabricyo.protocolos.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProtocoloTest {

    private Protocolo novoProtocolo() {
        return new Protocolo("2026/000001", "Assunto", "Descricao", "Solicitante");
    }

    @Test
    void novoProtocoloComecaAbertoSemEncerramento() {
        Protocolo protocolo = novoProtocolo();

        assertThat(protocolo.getStatus()).isEqualTo(Status.ABERTO);
        assertThat(protocolo.getAbertura()).isNotNull();
        assertThat(protocolo.getEncerramento()).isNull();
    }

    @Test
    void iniciarAtendimentoMudaDeAbertoParaEmAndamento() {
        Protocolo protocolo = novoProtocolo();

        protocolo.iniciarAtendimento();

        assertThat(protocolo.getStatus()).isEqualTo(Status.EM_ANDAMENTO);
        assertThat(protocolo.getEncerramento()).isNull();
    }

    @Test
    void naoPermiteIniciarAtendimentoDeProtocoloQueNaoEstaAberto() {
        Protocolo protocolo = novoProtocolo();
        protocolo.iniciarAtendimento(); // agora EM_ANDAMENTO

        assertThatThrownBy(protocolo::iniciarAtendimento)
                .isInstanceOf(TransicaoInvalidaException.class)
                .hasMessageContaining("ABERTO");
    }

    @Test
    void concluirAPartirDeAbertoPreencheEncerramento() {
        Protocolo protocolo = novoProtocolo();

        protocolo.concluir();

        assertThat(protocolo.getStatus()).isEqualTo(Status.CONCLUIDO);
        assertThat(protocolo.getEncerramento()).isNotNull();
    }

    @Test
    void concluirAPartirDeEmAndamentoPreencheEncerramento() {
        Protocolo protocolo = novoProtocolo();
        protocolo.iniciarAtendimento();

        protocolo.concluir();

        assertThat(protocolo.getStatus()).isEqualTo(Status.CONCLUIDO);
        assertThat(protocolo.getEncerramento()).isNotNull();
    }

    @Test
    void naoPermiteConcluirProtocoloJaConcluido() {
        Protocolo protocolo = novoProtocolo();
        protocolo.concluir();

        assertThatThrownBy(protocolo::concluir)
                .isInstanceOf(TransicaoInvalidaException.class);
    }

    @Test
    void naoPermiteConcluirProtocoloCancelado() {
        Protocolo protocolo = novoProtocolo();
        protocolo.cancelar();

        assertThatThrownBy(protocolo::concluir)
                .isInstanceOf(TransicaoInvalidaException.class);
    }

    @Test
    void cancelarAPartirDeAbertoPreencheEncerramento() {
        Protocolo protocolo = novoProtocolo();

        protocolo.cancelar();

        assertThat(protocolo.getStatus()).isEqualTo(Status.CANCELADO);
        assertThat(protocolo.getEncerramento()).isNotNull();
    }

    @Test
    void naoPermiteCancelarProtocoloJaConcluido() {
        Protocolo protocolo = novoProtocolo();
        protocolo.concluir();

        assertThatThrownBy(protocolo::cancelar)
                .isInstanceOf(TransicaoInvalidaException.class);
    }

    @Test
    void naoPermiteCancelarProtocoloJaCancelado() {
        Protocolo protocolo = novoProtocolo();
        protocolo.cancelar();

        assertThatThrownBy(protocolo::cancelar)
                .isInstanceOf(TransicaoInvalidaException.class);
    }
}
