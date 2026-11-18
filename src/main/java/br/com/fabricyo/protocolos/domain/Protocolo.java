package br.com.fabricyo.protocolos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "protocolo")
public class Protocolo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @Column(nullable = false, length = 150)
    private String assunto;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, length = 150)
    private String solicitante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime abertura;

    private LocalDateTime encerramento;

    protected Protocolo() {
        // Construtor exigido pelo JPA.
    }

    public Protocolo(String numero, String assunto, String descricao, String solicitante) {
        this.numero = numero;
        this.assunto = assunto;
        this.descricao = descricao;
        this.solicitante = solicitante;
        this.status = Status.ABERTO;
        this.abertura = LocalDateTime.now();
    }

    // Inicia o atendimento: só é permitido a partir de um protocolo ABERTO.
    public void iniciarAtendimento() {
        if (status != Status.ABERTO) {
            throw new TransicaoInvalidaException(
                    "Só é possível iniciar o atendimento de um protocolo ABERTO. Status atual: " + status);
        }
        this.status = Status.EM_ANDAMENTO;
    }

    // Conclui o protocolo e registra o encerramento.
    // Estados finais (CONCLUIDO, CANCELADO) não podem ser concluídos.
    public void concluir() {
        if (estaEncerrado()) {
            throw new TransicaoInvalidaException(
                    "Um protocolo " + status + " não pode ser concluído.");
        }
        this.status = Status.CONCLUIDO;
        this.encerramento = LocalDateTime.now();
    }

    // Cancela o protocolo e registra o encerramento.
    // Estados finais (CONCLUIDO, CANCELADO) não podem ser cancelados.
    public void cancelar() {
        if (estaEncerrado()) {
            throw new TransicaoInvalidaException(
                    "Um protocolo " + status + " não pode ser cancelado.");
        }
        this.status = Status.CANCELADO;
        this.encerramento = LocalDateTime.now();
    }

    private boolean estaEncerrado() {
        return status == Status.CONCLUIDO || status == Status.CANCELADO;
    }

    public UUID getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public String getAssunto() {
        return assunto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getAbertura() {
        return abertura;
    }

    public LocalDateTime getEncerramento() {
        return encerramento;
    }
}
