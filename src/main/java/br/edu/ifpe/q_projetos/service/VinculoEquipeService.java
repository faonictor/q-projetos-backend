package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.model.VinculoEquipe;
import br.edu.ifpe.q_projetos.repository.VinculoEquipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VinculoEquipeService {

    private final VinculoEquipeRepository repository;

    public VinculoEquipeService(
            VinculoEquipeRepository repository
    ) {
        this.repository = repository;
    }

    public VinculoEquipe salvar(
            VinculoEquipe vinculo
    ) {

        boolean existe =
                repository.existsByIdProjetoAndIdUsuarioAndAtivoTrue(
                        vinculo.getIdProjeto(),
                        vinculo.getIdUsuario()
                );

        if (existe) {
            throw new RuntimeException(
                    "Usuário já possui vínculo ativo neste projeto"
            );
        }

        return repository.save(vinculo);
    }

    public List<VinculoEquipe> listar() {
        return repository.findAll();
    }

    public VinculoEquipe buscarPorId(
            Long id
    ) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vínculo não encontrado"
                        ));
    }

    public VinculoEquipe atualizar(
            Long id,
            VinculoEquipe vinculoAtualizado
    ) {

        VinculoEquipe vinculo =
                buscarPorId(id);

        vinculo.setIdProjeto(
                vinculoAtualizado.getIdProjeto()
        );

        vinculo.setIdUsuario(
                vinculoAtualizado.getIdUsuario()
        );

        vinculo.setPapel(
                vinculoAtualizado.getPapel()
        );

        vinculo.setAtivo(
                vinculoAtualizado.getAtivo()
        );

        return repository.save(vinculo);
    }

    public void deletar(
            Long id
    ) {

        buscarPorId(id);

        repository.deleteById(id);
    }
}