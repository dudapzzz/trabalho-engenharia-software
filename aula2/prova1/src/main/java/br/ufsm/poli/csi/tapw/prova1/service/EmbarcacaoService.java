package br.ufsm.poli.csi.tapw.prova1.service;

import br.ufsm.poli.csi.tapw.prova1.model.Embarcacao;
import br.ufsm.poli.csi.tapw.prova1.model.EmbarcacaoRepository;
import br.ufsm.poli.csi.tapw.prova1.model.Situacao;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Schema(description = "Services da api de embarcação")
public class EmbarcacaoService {
    private final EmbarcacaoRepository repository;

    public EmbarcacaoService(EmbarcacaoRepository repository){this.repository = repository;}

    public void salvar(Embarcacao embarcacao){
        this.repository.save(embarcacao);
    }

    public List<Embarcacao> listar(){
        return this.repository.findAll();
    }

    public Embarcacao getEmbarcacao(Long id){
        return this.repository.findById(id).get();
    }

    public void atualizar(Embarcacao embarcacao){
        Embarcacao e = this.repository.getReferenceById(embarcacao.getId());
        e.setNome(embarcacao.getNome());
        e.setRegistro(embarcacao.getRegistro());
        e.setComprimento(embarcacao.getComprimento());
        e.setAnoFabricacao(embarcacao.getAnoFabricacao());
        e.setQuantidadePassageiros(embarcacao.getQuantidadePassageiros());
        e.setQuantidadeTripulantes(embarcacao.getQuantidadeTripulantes());
        e.setSituacao(embarcacao.getSituacao());
        this.repository.save(e);
    }

    public void excluir(Long id){
        Embarcacao e = this.repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Embarcação não encontrada"));

        if (e.getSituacao() == Situacao.ATIVA){
            throw new IllegalArgumentException("Embarcações ativas não podem ser excluídas!");
        }
        this.repository.delete(e);
    }
}
