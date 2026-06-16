# Relatório de Análise de Aderência e Qualidade de Código (Requisitos vs. Implementação)

Este documento apresenta uma auditoria detalhada da branch de integração `stage/teste-integracao-equipes` frente às especificações do **[Documento de Requisitos (doc-req.md)](file:///C:/q-projetos-backend/doc-req.md)**. O foco está exclusivamente na API/Back-end, avaliando o que foi implementado corretamente, o que está parcial ou incorreto, e mapeando as responsabilidades individuais por funcionalidade de acordo com o histórico de commits e merges.

---

## 1. Visão Geral da Cobertura de Requisitos

A tabela abaixo resume o status de implementação de cada requisito do back-end:

| ID | Cenário / Requisito | Tipo | Status no Código | Colaborador Responsável | Observação / Desvio Identificado |
| :--- | :--- | :---: | :---: | :--- | :--- |
| **RF01** | Vitrine Pública com status "Publicado" | Funcional | **Parcial / Incorreto** | Luciana | O endpoint público `listarTodos()` lista todos os projetos no banco, incluindo os com status `PENDENTE` ou `REPROVADO`, violando a privacidade da vitrine. |
| **RF03** | Busca livre por Título, Descrição ou Coordenador | Funcional | **Parcial / Incorreto** | Luciana | A busca por texto livre não filtra pelo nome do coordenador do projeto, apenas por título e descrição. |
| **RF04** | Filtros rápidos por Categoria e Status de Inscrição | Funcional | **Parcial** | Luciana | Implementado no DTO, mas a busca por status não filtra os projetos que não estejam no estado `PUBLICADO`. |
| **RF06** | Favoritar projetos ("Gostei") | Funcional | **Implementado 100%** | Mayko | Vinculação e desvinculação associadas corretamente ao usuário logado via JWT (OAuth2). |
| **RF09** | Formulário de interesse com Série/Período e Modalidade | Funcional | **Implementado 100%** | Vitório / Laurindo | DTOs e validações `@Valid` funcionando corretamente. |
| **RF10** | Notificação por E-mail ao Coordenador no Lead | Funcional | **Não Implementado** | Laurindo / Vitório | Não existe envio de e-mail ao cadastrar interesse no back-end. |
| **RF12** | Edição de projetos por Coordenadores/Membros da Equipe | Funcional | **Parcial / Incorreto** | Luciana | Apenas o coordenador do projeto (ou ADMIN) pode editar. Colaboradores, bolsistas e voluntários do projeto são barrados, violando o requisito. |
| **RF14** | Área restrita de Leads por Projeto | Funcional | **Implementado 100%** | Laurindo / Karen | Apenas coordenadores e colaboradores ativos no projeto podem visualizar os interessados. |
| **RF15** | Histórico do Estudante (Favoritos + Interesses) | Funcional | **Parcial** | Mayko (Favoritos) / Vitório (Interesses) | O histórico lista apenas os projetos favoritados. Não há endpoint ou método para o estudante listar seus próprios interesses. |
| **RF16** | Painel de Moderação do Admin (Pendente/Exclusão) | Funcional | **Parcial** | Luciana | O Admin aprova, reprova e deleta (se reprovado), mas não há método ou endpoint para marcar voluntariamente um projeto como `PENDENTE` após aprovação. |
| **RF17** | Admin atribuir perfil "Coordenador" a Servidores | Funcional | **Implementado 100%** | Carlos / Laurindo | O Admin consegue mudar a Role para `ROLE_COORD`, validando se o usuário possui vínculo `SERVIDOR`. |
| **RN01** | Retorno automático a `PENDENTE` em edições estruturais | Regra de Negócio | **Implementado 100%** | Luciana | Alterações em Título, Tipo ou Modalidade mudam o status do projeto para `PENDENTE`. |
| **RN02** | Unicidade de Manifestação de Interesse | Regra de Negócio | **Implementado 100%** | Vitório | O sistema bloqueia duplicidade de e-mail por projeto na manifestação de interesse. |
| **RN03** | Validação diária de vigência dos projetos | Regra de Negócio | **Não Implementado** | Luciana / Ageu | Não existe rotina agendada (`@Scheduled`) de validação diária. A validação ocorre dinamicamente no mapeamento do DTO. |
| **RN05** | Restrição de Coordenação Isolada | Regra de Negócio | **Implementado 100%** | Karen / Laurindo | A segurança de dados impede que um coordenador veja dados de projetos de outros coordenadores. |
| **RN06** | Upload de Banner (Tamanho e Formato) | Regra de Negócio | **Parcial / Incorreto** | Ageu | Banner persistido em Base64 no DB (não em disco físico). Aceita qualquer formato `data:image/` (não apenas `.jpg`, `.jpeg` e `.png`). |
| **RN07** | Obrigatoriedade de Link Externo ou Edital | Regra de Negócio | **Parcial / Incorreto** | Ageu | Sem suporte para upload de PDF de edital. Validação de links obrigatórios ocorre apenas no `salvar()`, mas não no `atualizar()`, permitindo burlas. |
| **RN08** | Notificação Assíncrona de Novo Lead | Regra de Negócio | **Não Implementado** | Laurindo / Vitório | Não há rotina ou componente de envio de e-mails na plataforma. |

---

## 2. Análise Detalhada dos Desvios e Gaps de Regras de Negócio

### 1. Vazamento na Vitrine Pública (RF01 / RN01)
* **O Requisito:** A vitrine pública deve exibir apenas projetos aprovados ("Publicado"). Projetos novos ou alterados em campos estruturais devem retornar a "Pendente" e sair da exibição até nova homologação.
* **O Código Atual ([ProjetoService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/ProjetoService.java#L36-L40)):**
  ```java
  public List<ProjetoResponseDTO> listarTodos() {
      return repository.findAll().stream()
              .map(this::toResponseDTO)
              .collect(Collectors.toList());
  }
  ```
* **O Desvio:** O método de listagem executa `repository.findAll()`, retornando todos os registros do banco indistintamente. Projetos criados que estão `PENDENTE` ou que foram marcados como `REPROVADO` aparecem na listagem pública da vitrine.
* **Responsável:** **Luciana** (`feat/luciana-moderacao-e-endpoints-projeto`). Implementou a moderação de status, mas não integrou a restrição de status nos métodos de consulta pública.

### 2. Busca Livre Incompleta (RF03)
* **O Requisito:** Permitir a busca por texto livre filtrando resultados por título, descrição ou nome do coordenador.
* **O Código Atual ([ProjetoRepository.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/repository/ProjetoRepository.java#L16-L20)):**
  ```java
  List<Projeto> findByTituloContainingIgnoreCaseOrDescricaoContainingIgnoreCase(
          String titulo,
          String descricao
  );
  ```
* **O Desvio:** A busca textual está restrita apenas às colunas de título e descrição do projeto. Ela ignora por completo a verificação sobre o nome do coordenador, descumprindo a especificação de busca unificada.
* **Responsável:** **Luciana** (`feat/luciana-moderacao-e-endpoints-projeto`).

### 3. Exclusão Incorreta de Membros da Equipe na Edição (RF12)
* **O Requisito:** Permitir que o Coordenador **ou** Colaboradores **ou** Bolsistas/Voluntários do projeto editem as informações do seu respectivo projeto.
* **O Código Atual ([ProjetoService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/ProjetoService.java#L279-L307)):**
  ```java
  VinculoEquipe vinculo = vinculoRepository.findByIdProjetoAndIdUsuario(projetoId, logado.getId())
          .orElseThrow(() -> new RecursoNaoEncontradoException("Acesso negado: Você não possui permissão sobre este projeto."));

  if (!VinculoEquipe.Papel.COORDENADOR.equals(vinculo.getPapel())) {
      throw new RecursoNaoEncontradoException("Acesso negado: Apenas o COORDENADOR possui permissão para alterar o projeto.");
  }
  ```
* **O Desvio:** A validação de permissão bloqueia explicitamente qualquer membro cujo papel de vínculo não seja `COORDENADOR`. Isso impede que colaboradores ou estudantes vinculados à equipe façam atualizações nas informações do projeto, violando frontalmente o requisito de edição coletiva.
* **Responsável:** **Luciana** (`feat/luciana-moderacao-e-endpoints-projeto`).

### 4. Ausência de Envio de E-mails (RF10 / RN08)
* **O Requisito:** Disparar notificação automática (e-mail) ao Coordenador com os dados de contato do aluno que demonstrou interesse. Esta operação deve ser assíncrona.
* **O Código Atual ([InteresseService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/InteresseService.java#L41-L62)):**
  O método `salvar()` apenas persiste o objeto no banco de dados e retorna o DTO. Não existe nenhuma chamada para serviços de mensageria, anotação `@Async` ou integração SMTP configurada.
* **O Desvio:** Funcionalidade de e-mail e de envio assíncrono completamente inexistentes.
* **Responsável:** **Laurindo** (`feat/laurindo-tratamento-de-erros`) e **Vitório** (`feat/vitorio-interesseDTO-interesseResponseDTO`).

### 5. Histórico de Manifestações de Interesse Inexistente (RF15)
* **O Requisito:** Prover ao estudante uma área de "Meu Histórico", listando seus projetos favoritados **e** as manifestações de interesse (leads) realizadas.
* **O Código Atual:**
  * O histórico de favoritos foi devidamente coberto por **Mayko** via `FavoritoService.listarMeuHistorico()`.
  * No entanto, o `InteresseService` e `InteresseController` não possuem nenhum método ou endpoint para retornar os registros de interesse filtrados pelo e-mail do estudante autenticado. O `InteresseRepository` sequer possui a query `findByEmail`.
* **O Desvio:** O estudante não consegue listar seus interesses passados na API.
* **Responsável:** **Vitório** (`feat/vitorio-validacao-de-unicidade`) e **Laurindo** (`feat/laurindo-tratamento-de-erros`).

### 6. Armazenamento Físico e Formatos de Imagem (RN06)
* **O Requisito:** Aceitar apenas imagens `.jpg`, `.jpeg` e `.png` limitadas a 2MB. Renomear com UUID e persistir no diretório físico do servidor.
* **O Código Atual ([ProjetoService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/ProjetoService.java#L223-L234)):**
  ```java
  if (!base64.startsWith("data:image/")) { ... }
  if (base64.length() > 2800000) { ... } // Valida tamanho estático da string
  ```
* **O Desvio:**
  * Não há persistência em disco físico. A imagem é mantida como string Base64 em coluna `@Lob` (coluna `MEDIUMTEXT` no banco).
  * A validação aceita qualquer prefixo `data:image/` (incluindo `.gif`, `.webp`, `.svg`, etc.), sem validar as extensões exigidas pelo requisito.
* **Responsável:** **Ageu** (`feat/ageu-validacao-banner`).

### 7. Burla de Links Obrigatórios e Ausência de Upload de Edital (RN07)
* **O Requisito:** Para que o projeto seja publicado, deve-se preencher o link do edital, link de inscrição externo ou fazer upload do PDF do edital. Impedir submissão se estiverem vazios.
* **O Código Atual:**
  * Não há suporte a arquivos físicos (PDF) no modelo ou banco.
  * A validação `validarLinksObrigatorios` verifica se ambos os links estão vazios e é acionada **apenas** no método `salvar()`.
* **O Desvio:** Como a validação não é disparada no método `atualizar()`, o coordenador consegue criar o projeto com links válidos e, posteriormente, removê-los na edição parcial do projeto, salvando o registro com ambos os campos nulos e burlando a regra.
* **Responsável:** **Ageu** (`feat/ageu-validacao-banner`).

### 8. Ausência de Validação de Vigência Diária (RN03)
* **O Requisito:** Validar diariamente a data de término dos projetos. Se posterior à data atual, altera automaticamente para "Encerrado".
* **O Código Atual:** A verificação de vigência ocorre de forma dinâmica no mapeamento `toResponseDTO` (alterando o status da resposta baseado no `LocalDate.now()`). Não há rotina do Spring Scheduler (`@Scheduled`) rodando em plano de fundo ou persistindo a mudança de estado no banco.
* **O Desvio:** Embora o status seja entregue corretamente na API graças à lógica dinâmica de mapeamento do DTO, a regra de atualização em lote no banco descrita na RN03 (job diário) não foi criada.
* **Responsável:** **Luciana** / **Ageu** (nas branches de projeto).

---

## 3. Avaliação por Colaborador

### 🏆 Mayko (`Mayko-favorito-dto`)
* **Aderência aos Requisitos:** **100% de Sucesso**.
* **Pontos Fortes:** Implementou o módulo de favoritos perfeitamente, garantindo o uso correto de DTOs, encapsulamento das entidades e fornecendo o histórico do próprio usuário autenticado.

### ⚠️ Vitório (`feat/vitorio-...`)
* **Aderência aos Requisitos:** **Parcial**.
* **O que fez corretamente:** Criou a estrutura inicial de DTOs de interesse e implementou com maestria a lógica de validação de unicidade de interesse (**RN02**), evitando duplicidades.
* **O que deixou de fazer:** Não implementou o histórico de interesses do estudante (**RF15**) e não incorporou a chamada de e-mail.

### ⚠️ Laurindo (`feat/laurindo-tratamento-de-erros`)
* **Aderência aos Requisitos:** **Parcial**.
* **O que fez corretamente:** Implementou um tratamento de erros global robusto e centralizou a segurança de perfil.
* **O que deixou de fazer:** Unificou o fluxo de interesses/leads, mas não implementou as notificações por e-mail (**RN08**) ou o histórico de interesses do aluno.

### ⚠️ Karen (`origin/feat/karen-equipe-b-vinculo`)
* **Aderência aos Requisitos:** **Parcial**.
* **O que fez corretamente:** Mapeou os vínculos de equipe fundamentais para as regras de coordenação (**RN05**).
* **O que deixou de fazer:** Expôs a entidade JPA diretamente na controller e criou queries de concorrência com o fluxo do HEAD (resolvido posteriormente nos merges).

### ⚠️ Carlos (`feat-carlos-atividade-usuario`)
* **Aderência aos Requisitos:** **Parcial**.
* **O que fez corretamente:** Lógica de perfil inicial e verificação de auditoria.
* **O que deixou de fazer:** Duplicou endpoints de perfil (corrigido no merge com o Laurindo).

### ❌ Ageu (`feat/ageu-validacao-banner`)
* **Aderência aos Requisitos:** **Baixa / Insuficiente**.
* **O que deixou de fazer/Erros:**
  1. A validação de banner aceita formatos proibidos e não armazena no disco físico do servidor (**RN06**).
  2. Não implementou o upload de arquivos PDF de edital.
  3. A validação de links obrigatórios possui uma brecha grave: não roda no método `atualizar()`, permitindo burlar a **RN07**.

### ❌ Luciana (`feat/luciana-moderacao-e-endpoints-projeto`)
* **Aderência aos Requisitos:** **Baixa / Insuficiente**.
* **O que deixou de fazer/Erros:**
  1. Permitiu vazamento de projetos pendentes e reprovados na vitrine pública (**RF01** / **RN01**).
  2. Não incluiu o nome do coordenador no filtro de busca (**RF03**).
  3. Bloqueou colaboradores, bolsistas e voluntários de editarem projetos, contrariando a regra funcional (**RF12**).
  4. Não implementou a transição manual de projetos homologados de volta para `PENDENTE` pelo Admin (**RF16**).
  5. Não desenvolveu o agendamento diário de vigência de projetos (**RN03**).

---

## 4. Recomendações de Ações Corretivas (Back-end)

1. **Correção do Endpoint de Vitrine (Urgente):**
   Filtrar a consulta de `listarTodos()` no `ProjetoService` para retornar apenas projetos com `statusModeracao = PUBLICADO`.
2. **Correção de Permissão de Edição:**
   Alterar `validarPermissaoEdicao` para aceitar outros membros da equipe (Colaboradores/Bolsistas/Voluntários) e não apenas o `COORDENADOR`.
3. **Reforço na Validação do POM:**
   Adicionar a validação de links obrigatórios da **RN07** no método `atualizar()` do `ProjetoService` e validar as extensões de banner especificamente (PNG, JPG, JPEG).
4. **Implementação de Histórico de Interesse:**
   Adicionar a query `findByEmail` no `InteresseRepository` e expor um endpoint `/api/interesses/meus` para o estudante visualizar suas manifestações de interesse no painel.
5. **Desenvolvimento do Serviço de Notificações e Agendador:**
   Criar um serviço de simulação de e-mail (usando logs em dev ou JavaMailSender em prod) anotado com `@Async`, além de uma rotina `@Scheduled` para vigência de término de projetos.
