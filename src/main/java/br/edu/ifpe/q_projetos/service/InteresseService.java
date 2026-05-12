package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.model.Interesse;
import br.edu.ifpe.q_projetos.repository.InteresseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InteresseService {

    @Autowired
    private InteresseRepository repository;

    public Interesse salvar(Interesse interesse) {

        validarLgpd(interesse);

        return repository.save(interesse);
    }

    public List<Interesse> listarTodos() {
        return repository.findAll();
    }

    public Interesse buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Interesse não encontrado."
                        ));
    }

    public Interesse atualizar(Long id, Interesse interesseAtualizado) {

        Interesse interesse = buscarPorId(id);

        interesse.setProjeto(interesseAtualizado.getProjeto());
        interesse.setNome(interesseAtualizado.getNome());
        interesse.setEmail(interesseAtualizado.getEmail());
        interesse.setSeriePeriodo(
                interesseAtualizado.getSeriePeriodo());

        interesse.setModalidadePretendida(
                interesseAtualizado.getModalidadePretendida());

        interesse.setAceitouLgpd(
                interesseAtualizado.getAceitouLgpd());

        validarLgpd(interesse);

        return repository.save(interesse);
    }

    public void deletar(Long id) {

        Interesse interesse = buscarPorId(id);

        repository.delete(interesse);
    }

    private void validarLgpd(Interesse interesse) {

        if (interesse.getAceitouLgpd() == null
                || !interesse.getAceitouLgpd()) {

            throw new RuntimeException(
                    "É obrigatório aceitar os termos da LGPD."
            );
        }
    }
}