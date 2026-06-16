# Resumo de Integração e Resolução de Conflitos (Merge)

Este documento descreve detalhadamente o processo de cópia da branch `stage/teste-integracao` para a nova branch `stage/teste-integracao-equipes` e o merge sucessivo de 8 branches de funcionalidades, incluindo os conflitos encontrados em cada etapa e a respectiva resolução adotada para manter a coerência do sistema (DTOs, segurança, validações de negócio e concorrência).

---

## 1. Preparação da Branch
A nova branch de integração foi criada a partir da base atualizada de testes:
```bash
git checkout -b stage/teste-integracao-equipes stage/teste-integracao
```

---

## 2. Histórico de Merges e Resolução de Conflitos

### 1. `feat/vitorio-interesseDTO-interesseResponseDTO`
* **Status:** Sucesso (Sem conflitos)
* **Ações:** Integradas as classes iniciais de DTO para a manifestação de interesse (`InteresseDTO` e `InteresseResponseDTO`).

### 2. `feat-carlos-atividade-usuario`
* **Status:** Sucesso (Sem conflitos)
* **Ações:** Integrada a funcionalidade de atualização do perfil do próprio usuário e log de atividades.

### 3. `feat/laurindo-tratamento-de-erros`
* **Status:** Conflitos resolvidos
* **Conflitos e Resoluções por Arquivo:**
  * **[InteresseDTO.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/dto/InteresseDTO.java) e [InteresseResponseDTO.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/dto/InteresseResponseDTO.java):**
    * *Conflito:* Divergência no nome da chave estrangeira (`projetoId` na branch do Vitório e `idProjeto` na do Laurindo).
    * *Resolução:* Adotado o padrão CamelCase `projetoId` para coincidir com o mapeamento dinâmico do Spring Data JPA na entidade `Interesse`. Foram mantidas as anotações do Jakarta Bean Validation (`@NotNull`, `@NotBlank`, `@Email`) e anotações Lombok (`@Builder`, `@AllArgsConstructor`) trazidas pela branch de tratamento de erros.
  * **[InteresseController.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/controller/InteresseController.java) e [InteresseService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/InteresseService.java):**
    * *Conflito:* Métodos duplicados e diferença no tratamento de exceções.
    * *Resolução:* Implementada a injeção via construtor para as dependências adicionadas. O fluxo do endpoint `salvar` foi unificado usando as exceções especializadas (`RegraNegocioException` e `RecursoNaoEncontradoException`) e encapsulado no padrão HTTP `ResponseEntity` com validação `@Valid`. Foi adicionada a verificação de permissão de acesso ao projeto (`validarPermissaoAcessoProjeto`) para coibir visualizações não autorizadas.
  * **[UsuarioController.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/controller/UsuarioController.java) e [UsuarioService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/UsuarioService.java):**
    * *Conflito:* Duplicidade no endpoint de atualização de perfil (`/perfil`) entre o método `atualizarMeuPerfil` do Carlos e o método `atualizarPerfil` do Laurindo.
    * *Resolução:* Mantido um único endpoint seguro `/perfil` exigindo `@PreAuthorize("isAuthenticated()")`. A lógica de atualização foi unificada no serviço usando `SecurityUtils.getLoggedUser` para recuperar com segurança os dados do usuário autenticado no Spring Security.
  * **[ProjetoService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/ProjetoService.java):**
    * *Conflito:* Marcadores de conflito nos imports.
    * *Resolução:* Imports organizados e limpos para remover as duplicidades.

### 4. `origin/feat/karen-equipe-b-vinculo`
* **Status:** Conflitos resolvidos
* **Conflitos e Resoluções por Arquivo:**
  * **[VinculoEquipeController.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/controller/VinculoEquipeController.java) e [VinculoEquipeService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/VinculoEquipeService.java):**
    * *Conflito:* A branch da Karen continha endpoints rudimentares que expunham diretamente a entidade de banco de dados (`VinculoEquipe`) ao invés de utilizar os DTOs e validações da branch de segurança de equipes (`VinculoEquipeDTO` e `VinculoEquipeResponseDTO`).
    * *Resolução:* Preservada a lógica DTO de alto nível, os tratamentos de exceções estruturadas e as regras de segurança (como a `validarCoordenador` para gerenciar membros). Os comentários de documentação OpenAPI/Swagger (`@Tag` e `@Operation`) da branch da Karen foram aproveitados nos endpoints DTO correspondentes.
  * **[VinculoEquipeRepository.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/repository/VinculoEquipeRepository.java):**
    * *Conflito:* Consultas personalizadas divergentes.
    * *Resolução:* Mescladas as assinaturas das consultas de ambas as branches, incluindo `existsByIdProjetoAndIdUsuarioAndAtivoTrue` da Karen e as buscas por ID de projeto do HEAD.

### 5. `Mayko-favorito-dto`
* **Status:** Sucesso (Sem conflitos)
* **Ações:** Integrada a funcionalidade de adicionar projetos aos favoritos e retorno via `FavoritoResponseDTO`.

### 6. `feat/ageu-validacao-banner`
* **Status:** Conflitos resolvidos
* **Conflitos e Resoluções por Arquivo:**
  * **[RegraNegocioException.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/exception/RegraNegocioException.java):**
    * *Conflito:* Marcadores de conflito adicionados nas chaves de fechamento da classe.
    * *Resolução:* Limpeza dos marcadores redundantes do Git.
  * **[ProjetoService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/ProjetoService.java):**
    * *Conflito:* Duplicidade no escopo de assinatura do método `vincularCoordenador`.
    * *Resolução:* Excluída a declaração parcial trazida pela branch de validação de banner, preservando a lógica robusta de verificação de papel de servidor implementada no HEAD. As regras de validação do formato base64 e tamanho limite de 2MB do banner foram mantidas.

### 7. `feat/luciana-moderacao-e-endpoints-projeto`
* **Status:** Conflitos resolvidos
* **Conflitos e Resoluções por Arquivo:**
  * **[Projeto.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/model/Projeto.java):**
    * *Conflito:* Duplicação da definição do Enum `StatusModeracao` (uma interna e outra externa à classe) e anotações conflitantes do Lombok Builder no campo `statusModeracao`.
    * *Resolução:* Unificado o enum de status de moderação e configurado o valor padrão com `@Builder.Default` para inicializar novos projetos como `PENDENTE`.
  * **[ProjetoRepository.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/repository/ProjetoRepository.java) e [VinculoEquipeRepository.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/repository/VinculoEquipeRepository.java):**
    * *Conflito:* Declaração de consultas JPQL de coordenação divergentes.
    * *Resolução:* Ambas as versões das assinaturas de consulta JPQL foram retidas, permitindo buscas tanto por ID único quanto por listas.
  * **[ProjetoController.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/controller/ProjetoController.java):**
    * *Conflito:* Endpoints duplicados de aprovação e reprovação de moderação criados concorrentemente.
    * *Resolução:* Mantido apenas um par de endpoints `/api/projetos/{id}/aprovar` e `reprovar`, assegurando o uso consistente da anotação `@PreAuthorize("hasRole('ADMIN')")`.
  * **[ProjetoService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/ProjetoService.java):**
    * *Conflito:* Diferença na implementação de `listarMeusProjetos` e verificação de alteração de campos estruturais em `atualizar`.
    * *Resolução:* O método `atualizar` foi mantido com a validação dinâmica de campos estruturais do HEAD. O método `listarMeusProjetos` foi otimizado: ao invés de buscar os IDs de vínculo individualmente no banco de dados e depois buscar os projetos, a consulta JPQL `findProjetosByCoordenador` foi ajustada para fazer uma junção direta filtrando apenas os vínculos ativos (`v.ativo = true`), tornando a requisição muito mais eficiente.

### 8. `feat/vitorio-validacao-de-unicidade`
* **Status:** Conflitos resolvidos
* **Conflitos e Resoluções por Arquivo:**
  * **[InteresseRepository.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/repository/InteresseRepository.java) e [VinculoEquipeRepository.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/repository/VinculoEquipeRepository.java):**
    * *Conflito:* Duplicação das assinaturas de validação de e-mail e de busca em lote.
    * *Resolução:* Resolvidas as duplicidades, mantendo os métodos de validação e consultas necessárias por lote para buscas complexas.
  * **[InteresseResponseDTO.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/dto/InteresseResponseDTO.java):**
    * *Conflito:* Incompatibilidade com as novas propriedades adicionadas (como `projetoId` e `tituloProjeto`).
    * *Resolução:* Preservadas todas as melhorias de DTO consolidadas no HEAD.
  * **[InteresseController.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/controller/InteresseController.java) e [InteresseService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/InteresseService.java):**
    * *Conflito:* A branch do Vitório utilizava um parâmetro inseguro via query string (`usuarioLogado` e `isAdmin`) no endpoint de listagem, o que violava as práticas de segurança do sistema.
    * *Resolução:* Mantida a lógica segura do HEAD que extrai o usuário autenticado de forma confiável pelo contexto do Spring Security (`SecurityUtils.getLoggedUser`). A lógica de unicidade de interesses (`existsByEmailAndProjetoId`) foi mantida lançando a exceção correta `RegraNegocioException`. Removido do projeto o arquivo obsoleto `InteresseRequestDTO.java` para evitar redundância.

---

## 3. Correção dos 38 Problemas Identificados

Foi detectada e corrigida uma lista de 38 problemas estáticos e dinâmicos (erros de tempo de execução, bloqueio de testes e warnings severos de acoplamento de código). O detalhamento das correções inclui:

### 1. Inconsistência de Mapeamento no Repositório e Limpeza (5 problemas)
* **[UsuarioRepository.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/repository/UsuarioRepository.java):** Removida a assinatura incorreta `Optional<Favorito> findByUsername(String username)`. Por estar declarada num repositório de `Usuario`, a query gerava falhas de coerência de tipos e não era compatível com o domínio. Adicionalmente, foi removido o import redundante de `br.edu.ifpe.q_projetos.model.Favorito` que se tornou obsoleto.
* **[FavoritoService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/FavoritoService.java):** Modificado o fluxo de recuperação de ID do usuário logado de `findByUsername` para `findByEmail`, utilizando o atributo correspondente correto do banco de dados para recuperar o objeto `Usuario`.
* **[FavoritoRepository.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/repository/FavoritoRepository.java) e [ProjetoRepository.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/repository/ProjetoRepository.java):** Removidas as anotações desnecessárias `@Repository` e seus imports. Como as interfaces estendem `JpaRepository`, a detecção e o registro pelo Spring Data JPA ocorrem de forma automática, tornando as anotações redundantes.

### 2. Bloqueio no Mapeamento do Contexto de Teste (1 problema)
* **[QProjetosApplicationTests.java](file:///C:/q-projetos-backend/src/test/java/br/edu/ifpe/q_projetos/QProjetosApplicationTests.java):** Adicionado bloco de inicialização estática (`static initializer`) para forçar o carregamento das variáveis de ambiente usando `Dotenv`. Isso garantiu que os testes de integração do Spring Boot fossem executados sem falha de contexto ao buscar a variável de assinatura do JWT (`JWT_SECRET`).

### 3. Refatoração de Injeção de Dependências via Construtor (35 warnings/problemas de qualidade de código)
Foram eliminados todos os acoplamentos via `@Autowired` em nível de propriedade (*field injection*), que representavam o principal volume de warnings de código estático (code smells). Os atributos foram convertidos para `private final` e inicializados por construtores públicos nas seguintes 10 classes:
* **[DataSeeder.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/config/DataSeeder.java)**
* **[ProjetoController.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/controller/ProjetoController.java)**
* **[UsuarioController.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/controller/UsuarioController.java)**
* **[VinculoEquipeController.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/controller/VinculoEquipeController.java)**
* **[AuthController.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/security/AuthController.java)**
* **[AuthService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/security/AuthService.java)**
* **[OAuth2SuccessHandler.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/security/OAuth2SuccessHandler.java)**
* **[ProjetoService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/ProjetoService.java)**
* **[UsuarioService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/UsuarioService.java)**
* **[VinculoEquipeService.java](file:///C:/q-projetos-backend/src/main/java/br/edu/ifpe/q_projetos/service/VinculoEquipeService.java)**

### 4. Alerta de Versões Desatualizadas no POM (2 problemas)
* **[pom.xml](file:///C:/q-projetos-backend/pom.xml):** Atualizada a versão do parent do Spring Boot (`spring-boot-starter-parent`) de `4.0.6` para `4.1.0` para resolver por completo os alertas de versão desatualizada (tanto de patch quanto minor version) indicados no gerenciador de dependências, garantindo a utilização de bibliotecas atualizadas e corrigidas do ecossistema Spring Boot.

---

## 4. Validação Final
O projeto backend foi compilado localmente e teve todos os seus testes executados com sucesso:
```bash
.\mvnw clean compile test
```
**Resultado:** `BUILD SUCCESS` (Compilação e testes integrados executados com sucesso total, sem falhas nem erros estáticos remanescentes).
