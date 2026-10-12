package br.ufsm.poli.csi.tapw.prova1.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa a situação da embarcação")
public enum Situacao {
    ATIVA,
    EM_MANUTENCAO,
    INATIVA
}
