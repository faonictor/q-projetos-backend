package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.dto.InteresseRequestDTO;
import br.edu.ifpe.q_projetos.dto.InteresseResponseDTO;
import br.edu.ifpe.q_projetos.model.Interesse;
import br.edu.ifpe.q_projetos.model.Projeto;
import br.edu.ifpe.q_projetos.model.VinculoEquipe;
import br.edu.ifpe.q_projetos.repository.InteresseRepository;
import br.edu.ifpe.q_projetos.repository.VinculoEquipeRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InteresseService {

    private final InteresseRepository repository;
    private final VinculoEquipeRepository vinculoRepository;

    public InteresseService(
            InteresseRepository repository,
            VinculoEquipeRepository vinculoRepository) {

        this.repository = repository;
        this.vinculoRepository = vinculoRepository;
    }

    // ✅ SALVAR
    public InteresseResponseDTO salvar(InteresseRequestDTO dto) {

        validarLgpd(dto.getAceitouLgpd());

        // 🔒 valida unicidade
        if (repository.existsByEmailAndProjetoId(dto.getEmail(), dto.getIdProjeto())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Já existe um interessado com esse email para este projeto"
            );
        }

        Interesse interesse = new Interesse();

        interesse.setNome(dto.getNome());
        interesse.setEmail(dto.getEmail());
        interesse.setSeriePeriodo(dto.getSeriePeriodo());
        interesse.setModalidadePretendida(dto.getModalidadePretendida());
        interesse.setAceitouLgpd(dto.getAceitouLgpd());

        Projeto projeto = new Projeto();
        projeto.setId(dto.getIdProjeto());
        interesse.setProjeto(projeto);

        return toDTO(repository.save(interesse));
    }

    // ✅ LISTAR (COM SEGURANÇA)
    public List<InteresseResponseDTO> listarTodos(Long usuarioLogado, boolean isAdmin) {

        List<Interesse> interesses;

        if (isAdmin) {
            interesses = repository.findAll();
        } else {
            List<Long> projetosIds = vinculoRepository
                    .findByIdUsuarioAndPapel(usuarioLogado, VinculoEquipe.Papel.COORDENADOR)
                    .stream()
                    .map(VinculoEquipe::getIdProjeto)
                    .toList();

            interesses = repository.findByProjetoIdIn(projetosIds);
        }

        return interesses.stream().map(this::toDTO).toList();
    }

    public InteresseResponseDTO buscarPorId(Long id) {

        return toDTO(repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Interesse não encontrado"
                )));
    }

    // ✅ ATUALIZAR
    public InteresseResponseDTO atualizar(Long id, InteresseRequestDTO dto) {

        Interesse interesse = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Interesse não encontrado"
                ));

        validarLgpd(dto.getAceitouLgpd());

        if (repository.existsByEmailAndProjetoId(dto.getEmail(), dto.getIdProjeto())
                && !interesse.getEmail().equals(dto.getEmail())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Já existe um interessado com esse email para este projeto"
            );
        }

        interesse.setNome(dto.getNome());
        interesse.setEmail(dto.getEmail());
        interesse.setSeriePeriodo(dto.getSeriePeriodo());
        interesse.setModalidadePretendida(dto.getModalidadePretendida());
        interesse.setAceitouLgpd(dto.getAceitouLgpd());

        Projeto projeto = new Projeto();
        projeto.setId(dto.getIdProjeto());
        interesse.setProjeto(projeto);

        return toDTO(repository.save(interesse));
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    // 🔄 MAPPER
    private InteresseResponseDTO toDTO(Interesse i) {
        return InteresseResponseDTO.builder()
                .id(i.getId())
                .idProjeto(i.getProjeto().getId())
                .nome(i.getNome())
                .email(i.getEmail())
                .seriePeriodo(i.getSeriePeriodo())
                .modalidadePretendida(i.getModalidadePretendida())
                .dataRegistro(i.getDataRegistro())
                .build();
    }

    private void validarLgpd(Boolean aceitou) {
        if (aceitou == null || !aceitou) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "É obrigatório aceitar os termos da LGPD"
            );
        }
    }
}
