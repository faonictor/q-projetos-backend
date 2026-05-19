package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.model.VinculoEquipe;
import br.edu.ifpe.q_projetos.repository.VinculoEquipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class VinculoEquipeService {

        private final VinculoEquipeRepository repository;

        public VinculoEquipeService(
                        VinculoEquipeRepository repository) {
                this.repository = repository;
        }

        public VinculoEquipe salvar(
                        VinculoEquipe vinculo,
                        Long usuarioLogado) {

                validarCoordenador(
                                vinculo.getIdProjeto(),
                                usuarioLogado);

                return repository.save(vinculo);
        }

        public List<VinculoEquipe> listar() {
                return repository.findAll();
        }

        public VinculoEquipe buscarPorId(Long id) {

                return repository.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Vínculo não encontrado"));
        }

        public VinculoEquipe atualizar(
                        Long id,
                        VinculoEquipe novo,
                        Long usuarioLogado) {

                validarCoordenador(
                                novo.getIdProjeto(),
                                usuarioLogado);

                VinculoEquipe vinculo = repository.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Vínculo não encontrado"));

                vinculo.setPapel(novo.getPapel());
                vinculo.setAtivo(novo.getAtivo());

                return repository.save(vinculo);
        }

        public void deletar(
                        Long id,
                        Long usuarioLogado) {

                VinculoEquipe vinculo = repository.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Vínculo não encontrado"));

                validarCoordenador(
                                vinculo.getIdProjeto(),
                                usuarioLogado);

                repository.deleteById(id);
        }

        private void validarCoordenador(Long idProjeto, Long usuarioLogado) {

                // 1. Buscamos o vínculo e já "desempacotamos" ou lançamos a exceção se estiver
                // vazio
                VinculoEquipe vinculo = repository.findByIdProjetoAndIdUsuario(idProjeto, usuarioLogado)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.FORBIDDEN,
                                                "Usuário sem vínculo no projeto"));

                // 2. Agora que temos a entidade real, podemos acessar os métodos dela
                // normalmente
                if (vinculo.getPapel() != VinculoEquipe.Papel.COORDENADOR) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "Apenas coordenadores podem gerenciar membros");
                }

                // Aproveite para checar se o vínculo está ativo, caso seja necessário
                if (!Boolean.TRUE.equals(vinculo.getAtivo())) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "Vínculo de coordenação inativo para este projeto");
                }
        }
}