package br.ufsm.poli.csi.tapw.prova1.controller;

import br.ufsm.poli.csi.tapw.prova1.model.Embarcacao;
import br.ufsm.poli.csi.tapw.prova1.service.EmbarcacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/embarcacoes")
@Tag(name = "Salas", description = "Endpoints relacionados às embarcaçoes")
public class EmbarcacaoController {
    private final EmbarcacaoService service;

    public EmbarcacaoController(EmbarcacaoService service) {this.service = service;}

    @PostMapping
    @Transactional
    @Operation(summary = "Criar uma nova embarcação", description = "Salva uma nova embarcação no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Embarcação criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity salvar(@RequestBody @Valid Embarcacao embarcacao, UriComponentsBuilder uriBuilder){
        this.service.salvar(embarcacao);
        URI uri = uriBuilder.path("embarcacoes/{id}").buildAndExpand(embarcacao.getId()).toUri();
        return ResponseEntity.created(uri).body(embarcacao);
    }

    @GetMapping()
    @Operation(summary = "Listar salas", description = "Retorna uma lista de todas as embarcações cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de embarcações retornada com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Embarcacao.class))),
            @ApiResponse(responseCode = "404", description = "Nenhuma embarcação encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public List<Embarcacao> listar(){
        return this.service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter embarcação", description = "Retorna os detalhes de uma embarcação específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Embarcação retornada com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Embarcacao.class))),
            @ApiResponse(responseCode = "404", description = "Nenhuma embarcação encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public Embarcacao embarcacao(@PathVariable Long id){
        return this.service.getEmbarcacao(id);
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Atualizar uma embarcação existente", description = "Atualiza os dados de uma embarcação registrada no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Embarcação atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Embarcação não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity atualizar(@RequestBody Embarcacao embarcacao){
        this.service.atualizar(embarcacao);
        return ResponseEntity.ok(embarcacao);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir Embarcação", description = "Remove uma Embarcação do banco de dados utilizando seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Embarcação excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Embarcação não encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity deletar(@PathVariable Long id) {
        this.service.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
