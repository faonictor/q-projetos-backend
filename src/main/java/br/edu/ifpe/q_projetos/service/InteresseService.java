package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.dto.InteresseDTO;
import br.edu.ifpe.q_projetos.dto.InteresseResponseDTO;
import br.edu.ifpe.q_projetos.model.Interesse;
import br.edu.ifpe.q_projetos.model.Projeto;
import br.edu.ifpe.q_projetos.repository.InteresseRepository;
import br.edu.ifpe.q_projetos.repository.ProjetoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InteresseService {

    private final InteresseRepository repository;
    private final ProjetoRepository projetoRepository;

    public InteresseService(
            InteresseRepository repository,
            ProjetoRepository projetoRepository) {

        this.repository = repository;
        this.projetoRepository = projetoRepository;
    }

    public InteresseResponseDTO salvar(
            InteresseDTO dto) {

        Projeto projeto = projetoRepository
                .findById(dto.getProjetoId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Projeto não encontrado"));

        Interesse interesse = new Interesse();

        interesse.setProjeto(projeto);
        interesse.setNome(dto.getNome());
        interesse.setEmail(dto.getEmail());
        interesse.setSeriePeriodo(dto.getSeriePeriodo());
        interesse.setModalidadePretendida(
                dto.getModalidadePretendida());
        interesse.setAceitouLgpd(
                dto.getAceitouLgpd());

        validarLgpd(interesse);

        return converterParaDTO(
                repository.save(interesse));
    }

    public List<InteresseResponseDTO> listarTodos() {

        return repository.findAll()
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public InteresseResponseDTO buscarPorId(Long id) {

        return converterParaDTO(
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Interesse não encontrado")));
    }

    public InteresseResponseDTO atualizar(
            Long id,
            InteresseDTO dto) {

        Interesse interesse = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Interesse não encontrado"));

        Projeto projeto = projetoRepository
                .findById(dto.getProjetoId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Projeto não encontrado"));

        interesse.setProjeto(projeto);
        interesse.setNome(dto.getNome());
        interesse.setEmail(dto.getEmail());
        interesse.setSeriePeriodo(dto.getSeriePeriodo());
        interesse.setModalidadePretendida(
                dto.getModalidadePretendida());
        interesse.setAceitouLgpd(
                dto.getAceitouLgpd());

        validarLgpd(interesse);

        return converterParaDTO(
                repository.save(interesse));
    }

    public List<InteresseResponseDTO>
            listarLeadsPorProjeto(Long projetoId) {

        return repository.findByProjetoId(projetoId)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }

    public void deletar(Long id) {

        Interesse interesse = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Interesse não encontrado"));

        repository.delete(interesse);
    }

    private void validarLgpd(
            Interesse interesse) {

        if (interesse.getAceitouLgpd() == null
                || !interesse.getAceitouLgpd()) {

            throw new RuntimeException(
                    "É obrigatório aceitar os termos da LGPD.");
        }
    }

    private InteresseResponseDTO converterParaDTO(
            Interesse interesse) {

        InteresseResponseDTO dto =
                new InteresseResponseDTO();

        dto.setId(interesse.getId());

        dto.setProjetoId(
                interesse.getProjeto().getId());

        dto.setTituloProjeto(
                interesse.getProjeto().getTitulo());

        dto.setNome(interesse.getNome());

        dto.setEmail(interesse.getEmail());

        dto.setSeriePeriodo(
                interesse.getSeriePeriodo());

        dto.setModalidadePretendida(
                interesse.getModalidadePretendida());

        dto.setAceitouLgpd(
                interesse.getAceitouLgpd());

        dto.setDataRegistro(
                interesse.getDataRegistro());

        return dto;
    }
}
