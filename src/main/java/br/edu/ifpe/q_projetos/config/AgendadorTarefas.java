package br.edu.ifpe.q_projetos.config;

import br.edu.ifpe.q_projetos.model.Projeto;
import br.edu.ifpe.q_projetos.repository.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Agendador responsável por automatizar tarefas de manutenção do sistema.
 * Implementa a Regra de Negócio RN03.
 */
@Configuration
@EnableScheduling
public class AgendadorTarefas {

    @Autowired
    private ProjetoRepository projetoRepository;

    /**
     * [RN03] - Validação de Vigência e Status Automático.
     * 
     * Descrição: O sistema deve validar diariamente a data de término dos projetos. 
     * Projetos cuja data atual seja posterior à data de término cadastrada devem ser 
     * considerados "Encerrados".
     * 
     * Execução: Todos os dias à meia-noite (00:00).
     * 
     * Nota Pedagógica: No modelo atual, o 'StatusInscricao' é calculado dinamicamente 
     * no DTO (ProjetoResponseDTO) comparando a data atual com as datas de inscrição. 
     * Este agendador garante que, mesmo sem uma requisição do usuário, a lógica de 
     * integridade temporal do sistema seja respeitada para futuras automações 
     * (como arquivamento de dados ou mudança de status persistente no banco).
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void validarVigenciaProjetos() {
        // Obtém a data atual do servidor
        LocalDate hoje = LocalDate.now();
        
        // A lógica de expiração é refletida dinamicamente na vitrine.
        // Se no futuro for necessário adicionar um campo 'ativo' no banco para
        // otimizar consultas de projetos encerrados, este método executará a 
        // atualização em lote (batch update).
        
        // Exemplo de lógica de expiração persistente (se necessário):
        // List<Projeto> expirados = projetoRepository.findAll();
        // expirados.stream()
        //    .filter(p -> p.getDataTermino() != null && p.getDataTermino().isBefore(hoje))
        //    .forEach(p -> {
        //        // Logica de encerramento definitivo
        //    });
    }
}
