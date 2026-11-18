-- Sequência que garante números de protocolo únicos sob concorrência.
-- O banco serializa o nextval(), então duas inserções simultâneas nunca
-- recebem o mesmo valor.
CREATE SEQUENCE protocolo_numero_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE protocolo (
    id            UUID         NOT NULL,
    numero        VARCHAR(20)  NOT NULL,
    assunto       VARCHAR(150) NOT NULL,
    descricao     TEXT,
    solicitante   VARCHAR(150) NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    abertura      TIMESTAMP    NOT NULL,
    encerramento  TIMESTAMP,
    CONSTRAINT pk_protocolo PRIMARY KEY (id),
    CONSTRAINT uk_protocolo_numero UNIQUE (numero)
);
