package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.model.Projeto;
import br.edu.ifpe.q_projetos.repository.ProjetoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository repository;

    public List<Projeto> listarTodos() {
        return repository.findAll();
    }

    public Optional<Projeto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Projeto salvar(Projeto projeto) {

        aplicarRegraModeracao(projeto);

        validarLinkEdital(projeto);

        return repository.save(projeto);
    }

    public Projeto atualizar(Long id, Projeto projetoAtualizado) {

        Projeto projeto = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Projeto não encontrado"));

        projeto.setTitulo(projetoAtualizado.getTitulo());
        projeto.setTipo(projetoAtualizado.getTipo());
        projeto.setDescricao(projetoAtualizado.getDescricao());

        projeto.setDataInicio(projetoAtualizado.getDataInicio());
        projeto.setDataTermino(projetoAtualizado.getDataTermino());

        projeto.setDataInicioInscricao(
                projetoAtualizado.getDataInicioInscricao()
        );

        projeto.setDataFimInscricao(
                projetoAtualizado.getDataFimInscricao()
        );

        projeto.setStatusInscricao(
                projetoAtualizado.getStatusInscricao()
        );

        projeto.setLinkEdital(
                projetoAtualizado.getLinkEdital()
        );

        projeto.setVagas(
                projetoAtualizado.getVagas()
        );

        projeto.setModalidade(
                projetoAtualizado.getModalidade()
        );

        projeto.setBanner(
                projetoAtualizado.getBanner()
        );

        aplicarRegraModeracao(projeto);

        validarLinkEdital(projeto);

        return repository.save(projeto);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public List<Projeto> buscarPorTexto(String texto) {

        return repository
                .findByTituloContainingIgnoreCaseOrDescricaoContainingIgnoreCase(
                        texto,
                        texto
                );
    }

    public List<Projeto> buscarPorTipo(
            Projeto.TipoProjeto tipo
    ) {

        return repository.findByTipo(tipo);
    }

    public List<Projeto> buscarPorStatus(
            Projeto.StatusInscricao status
    ) {

        return repository.findByStatusInscricao(status);
    }

    private void aplicarRegraModeracao(Projeto projeto) {

        projeto.setStatusInscricao(
                Projeto.StatusInscricao.AGUARDANDO
        );
    }

    private void validarLinkEdital(Projeto projeto) {

        if (projeto.getStatusInscricao()
                == Projeto.StatusInscricao.ABERTA
                && (projeto.getLinkEdital() == null
                || projeto.getLinkEdital().isBlank())) {

            throw new RuntimeException(
                    "Projetos abertos precisam possuir link de edital."
            );
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void encerrarProjetosVencidos() {

        List<Projeto> projetos = repository.findAll();

        for (Projeto projeto : projetos) {

            if (projeto.getDataTermino() != null
                    && projeto.getDataTermino()
                    .isBefore(LocalDate.now())) {

                projeto.setStatusInscricao(
                        Projeto.StatusInscricao.ENCERRADA
                );

                repository.save(projeto);
            }
        }
    }
}