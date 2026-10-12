package br.ufsm.poli.csi.tapw.prova1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "embarcacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa uma embarcacao registrada no estado")
public class Embarcacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID da embarcacao")
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    @Schema(description = "Nome da embarcação, entre 3 e 100 caracteres", example = "Titanic")
    private String nome;

    @NotBlank
    @Size(min = 1, max = 20)
    @Column(name = "registro", length = 20, nullable = false, unique = true)
    @Schema(description = "registro da embarcação")
    private String registro;

    @NotNull
    @Min(1)
    @Max(15)
    @Schema(description = "Quantidade de tripulantes na embarcação, entre 1 e 15", example = "5")
    private int quantidadeTripulantes;

    @NotNull
    @Min(0)
    @Schema(description = "Quantidade de passageiros na embarcação", example = "60")
    private int quantidadePassageiros;

    @NotNull
    @Min(1960)
    @Max(2026)
    @Schema(description = "Ano de fabricação da embarcação (1960-2026)", example = "2000")
    private int anoFabricacao;

    @NotNull
    @Min(1)
    @Schema(description = "Comprimento em metros da embarcação", example = "300.00")
    private BigDecimal comprimento;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "situacao")
    @Schema(description = "Situação que a embarcação se encontra", example = "ATIVA")
    private Situacao situacao;


}
