package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.model.VinculoEquipe;
import br.edu.ifpe.q_projetos.repository.VinculoEquipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
            VinculoEquipe vinculo,
            Long usuarioLogado
    ) {

        validarCoordenador(
                vinculo.getIdProjeto(),
                usuarioLogado
        );

        return repository.save(vinculo);
    }

    public List<VinculoEquipe> listar() {
        return repository.findAll();
    }

    public VinculoEquipe buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Vínculo não encontrado"
                        ));
    }

    public VinculoEquipe atualizar(
            Long id,
            VinculoEquipe novo,
            Long usuarioLogado
    ) {

        validarCoordenador(
                novo.getIdProjeto(),
                usuarioLogado
        );

        VinculoEquipe vinculo = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Vínculo não encontrado"
                        ));

        vinculo.setPapel(novo.getPapel());
        vinculo.setAtivo(novo.getAtivo());

        return repository.save(vinculo);
    }

    public void deletar(
            Long id,
            Long usuarioLogado
    ) {

        VinculoEquipe vinculo = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Vínculo não encontrado"
                        ));

        validarCoordenador(
                vinculo.getIdProjeto(),
                usuarioLogado
        );

        repository.deleteById(id);
    }

    private void validarCoordenador(
            Long idProjeto,
            Long usuarioLogado
    ) {

        VinculoEquipe vinculo =
                repository.findByIdProjetoAndIdUsuario(
                        idProjeto,
                        usuarioLogado
                );

        if (vinculo == null) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuário sem vínculo no projeto"
            );
        }

        if (vinculo.getPapel()
                != VinculoEquipe.Papel.COORDENADOR) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Apenas coordenadores podem gerenciar membros"
            );
        }
    }
}