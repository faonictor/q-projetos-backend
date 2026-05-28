# Documento de Especificação de Requisitos – Plataforma Q-Projetos (v2.0)

## Source Q-Projetos

| Backend | Frontend |
| --- | --- |
| [![GitHub repo](https://img.shields.io/badge/github-repo-blue?logo=github)](https://github.com/faonictor/q-projetos-backend/) | [![GitHub repo](https://img.shields.io/badge/github-repo-green?logo=github)](https://github.com/guiza01/q-projetos/) |

## 1. Visão Geral

A *Plataforma Q-Projetos* consiste numa plataforma web projetada para funcionar como um catálogo institucional. O sistema visa centralizar a divulgação de projetos de ensino, pesquisa e extensão, facilitando o acesso da comunidade académica às informações e a gestão de vagas para participantes (bolsistas e voluntários).

Para além da gestão de vagas, o sistema atuará como uma vitrine ativa para captação de interessados ("leads"), integrando autenticação externa e notificações automáticas para agilizar a comunicação entre estudantes e coordenadores.

## 2. Atores do Sistema

Os atores representam os diferentes perfis que integrarão o quadro de usuários do sistema:

* **Administrador:** Responsável pela gestão global do sistema, aprovação de projetos e manutenção de perfis e parcerias.
* **Visitante:** Usuário anônimo ou autenticado, estudante da instituição provedora ou parceira que navega pelo catálogo em busca de informações ou vagas.
* **Coordenador:** Usuário autenticado e com perfil de acesso adequado, para realização de cadastro, atualização e gestão de informações e membros de um projeto específico.
* **Estudante:** Usuário autenticado via Google, que utiliza o login social para interagir com a plataforma, favoritar projetos e manifestar interesse direto.

## 3. Perfis dos Atores

### 1. Perfis de Acesso ao Sistema (Nível Global)

* **Administrador Global (ROLE_ADMIN):** Possui privilégios máximos. É responsável pela homologação e aprovação de novos projetos no catálogo, gestão de utilizadores, configuração de parâmetros do sistema e moderação de conteúdo.
* **Usuário Autenticado (ROLE_USER):** Qualquer utilizador institucional ou externo que possua uma conta registrada e tenha realizado autenticação. O acesso permite a navegação na vitrine pública de projetos aprovados, visualização de editais e alteração de formulários. Estudantes não vinculados a projetos utilizarão preferencialmente o login Google para este perfil. *(Nota: O direito de criar um novo projeto ou apenas candidatar-se a vagas dependerá dos atributos de perfil internos deste utilizador, como "Servidor" ou "Estudante").*
* **Usuário Anônimo (ROLE_GUEST):** Utilizador sem autenticação (não logado). O seu acesso é restrito apenas à visualização (modo de leitura) da vitrine pública de projetos aprovados, sem permissão para interagir com formulários ou acessar a áreas restritas.

### 2. Perfis de Atuação (Nível de Vínculo ao Projeto)

* **Coordenador / Orientador:** Autoridade máxima dentro do escopo de um projeto. Possui permissão exclusiva para editar as informações do projeto associado (título, descrição, banner), abrir ou fechar períodos de inscrição e visualizar a lista de interessados (leads).
* **Colaborador / Coorientador:** Servidor técnico-administrativo, docente ou pesquisador externo que contribui para a execução do projeto.
* **Bolsista / Voluntário:** Estudante oficialmente vinculado ao projeto após seleção.

## 4. Modelo de Dados (Entidades Principais)

**[ENT01] - Projeto:**
Entidade principal do sistema.

* **Id:** (Inteiro) Identificador único do projeto
* **Título:** (Texto) Nome oficial do projeto.
* **Tipo:** (Categoria) Ensino, Pesquisa ou Extensão.
* **Descrição:** (Texto longo) Resumo e objetivos do projeto.
* **Data de Criação / Início / Término:** (Data) Representa o ciclo de vida do projeto.
* **Data Início Inscrição / Término:** (Data) Representa o período de inscrição (definir se aberto/fechado)
* **Link do Edital:** (URL) Documento oficial de regulamentação.
* **Vagas:** (Inteiro) Quantidade de vagas disponíveis.
* **Modalidade:** (Categoria) Representa o tipo ofertado - Bolsista e/ou Voluntário.
* **Banner de Identificação:** (Imagem/Url) Imagem de divulgação do projeto.

**[ENT02] - Interesse (Lead):**

* **Id:** (Inteiro) Identificador único do registro de interesse.
* **Id_Projeto:** (Chave Estrangeira) Identifica a qual projeto o estudante se candidatou.
* **Nome:** (Texto) Nome completo do estudante (capturado automaticamente via Google).
* **E-mail:** (Texto) E-mail do estudante (capturado automaticamente via Google OAuth2).
* **Série/Período:** (Texto) Campo de preenchimento manual pelo estudante.
* **Modalidade Pretendida:** (Categoria) Se o interesse é para vaga de Bolsista ou Voluntário.
* **Data do Registro:** (Timestamp) Data e hora em que o interesse foi manifestado.

**[ENT03] - Usuário (User):**
*(Obs: Esta entidade é essencial para gerenciar quem acessa o sistema, distinguindo entre quem faz o login institucional/social e quem possui permissões administrativas.)*

* **Id:** (Número Inteiro) Identificador único.
* **Nome:** (Texto) Nome do Coordenador ou Administrador.
* **E-mail:** (Texto) E-mail institucional usado para login.
* **Senha:** (Hash) Para logins locais (Coordenadores/Admin).
* **Perfil Global (Role):** (Texto) ROLE_ADMIN, ROLE_COORD ou ROLE_USER.
* **Vínculo:** (Enum) Identifica se é Servidor (Docente/TAE) ou Estudante (importante para restringir quem pode criar projetos).

**[ENT04] - Favorito (Gostei):**

* **Id:** (Número Inteiro) Identificador único.
* **Id_Usuario:** (Chave Estrangeira) Estudante logado que interagiu.
* **Id_Projeto:** (Chave Estrangeira) Projeto marcado como "Gostei".
* **Data do Registro:** (Timestamp) Data e hora em que o interesse foi manifestado.

**[ENT05] - Vínculo de Equipe (Membros):**

* **Id:** (Número Inteiro) Identificador único.
* **Id_Projeto:** (Chave Estrangeira) Vínculo com o projeto.
* **Id_Usuario:** (Chave Estrangeira) Vínculo com o integrante institucional.
* **Papel:** (Enum) Coordenador, Colaborador, Bolsista ou Voluntário.
* **Ativo:** (Boolean) Ativo sim ou não

## 5. Requisitos Funcionais e Cenários de Uso

### **Módulo Externo (Público)**

#### **[CEN01] - Exploração do Catálogo (Vitrine)**

* **RF01:** O sistema deve listar na página inicial todos os projetos que possuam o status "Publicado".
* **RF02:** O sistema deve exibir os projetos em formato de cards, apresentando obrigatoriamente a imagem de capa (banner) carregada do servidor.
* **RF03:** O sistema deve permitir a busca por texto livre, filtrando resultados por título, descrição ou nome do coordenador.
* **RF04:** O sistema deve oferecer filtros rápidos por Categoria (Ensino, Pesquisa, Extensão) e por Status de Inscrição (Aberto/Encerrado).
* **RF05:** O sistema deve disponibilizar uma página de detalhes para cada projeto, apresentando a descrição completa, o número de vagas e a modalidade (Bolsista/Voluntário), se está aberto o período de inscrição, ciclo de vida do projeto, entre outros.

#### **[CEN02] - Manifestação de Interesse e Direcionamento Externo**

* **RF06:** O sistema deve permitir que o usuário autenticado via Google marque projetos como favoritos ("Gostei"), salvando-os em seu perfil.
* **RF07:** O sistema deve exibir o botão "Inscrever-se", que redireciona o usuário para o link externo (Google Forms/Edital) cadastrado pelo coordenador.
* **RF08:** O sistema deve disponibilizar o botão "Tenho Interesse", exclusivo para usuários autenticados, que abre um formulário simplificado.
* **RF09:** O formulário de interesse deve coletar obrigatoriamente a "Série/Período" e a "Modalidade Pretendida".
* **RF10:** Após a confirmação do interesse, o sistema deve registrar o Lead no banco de dados e disparar uma notificação automática (e-mail) ao Coordenador com os dados de contato do aluno.

### **Módulo Logado (Admin)**

#### **[CEN03] - Gestão de Conteúdo (Coordenador)**

* **RF11:** O sistema deve permitir que o Coordenador cadastre propostas de projetos, preenchendo os dados e realizando o upload de arquivos de imagem (JPG/PNG) e/ou arquivo do edital do projeto (PDF).
* **RF12:** O sistema deve permitir que o Coordenador ou Colaborador ou Bolsistas/Voluntários do projeto, editem as informações de seus projetos. Porém, alterações em campos estruturais devem retornar o projeto ao status "Pendente" para nova moderação do Coordenador/Colaborador do projeto.
* **RF13:** O sistema deve permitir que o Coordenador altere manualmente o status das inscrições para "Fechado", ocultando os botões de interação na vitrine.

#### **[CEN04] - Painéis de Acompanhamento (Leads e Histórico)**

* **RF14:** O sistema deve prover ao Coordenador/Colaborador uma área restrita para visualizar a lista consolidada de alunos que demonstraram interesse em seus projetos (Nome, E-mail, Série e Modalidade).
* **RF15:** O sistema deve prover ao Estudante uma área de "Meu Histórico", listando os projetos favoritados e as manifestações de interesse realizadas.

### **[CEN05] - Moderação e Governança (Admin)**

* **RF16:** O sistema deve prover ao Administrador um painel de moderação para, se necessário, editar todos os dados, excluir projetos ou marcar projetos como pendentes se necessário (para sairem da publicação externa).
* **RF17:** O sistema deve permitir que o Administrador atribua manualmente o perfil de "Coordenador" a usuários do sistema.
* **RF18:** O sistema deve permitir a gestão global de categorias e usuários institucionais.

## 6. Requisitos Não Funcionais

* **[RNF01] - Tecnologia:** Back-end Java/Spring Boot, Banco MySQL.
* **[RNF02] - Segurança:** Autenticação via Google OAuth2 para estudantes; perfis restritos para gestão.
* **[RNF03] - Usabilidade:** Interface responsiva, modo noturno e acessibilidade.
* **[RNF04] - Armazenamento:** Gerenciamento de arquivos físicos para os banners dos projetos e possibilidade de upload de editais.

## 7. Regras de Negócio e Validações

Esta seção define as restrições, políticas e validações lógicas que regem o comportamento do sistema Q-Projetos, garantindo a integridade dos dados e a conformidade com o modelo institucional.

### **[RN01] - Moderação Global de Projetos**

O sistema deve obrigatoriamente submeter qualquer projeto novo ou alterado (em campos estruturais como título, tipo ou modalidade) à moderação do Coordenador do Projeto ou Administrador. O projeto permanecerá com status "Pendente" e não será exibido na vitrine pública até que seja homologado.

### **[RN02] - Unicidade de Manifestação de Interesse**

Um usuário (Estudante) devidamente autenticado via Google só poderá registrar "Interesse" uma única vez para cada projeto específico. Caso o usuário tente acionar o botão novamente, o sistema deve informar que o interesse já foi registrado e redirecioná-lo para o seu histórico.

### **[RN03] - Validação de Vigência e Status Automático**

O sistema deve validar diariamente a data de término dos projetos. Projetos cuja data atual seja posterior à data de término cadastrada devem ter seu status alterado automaticamente para "Encerrado".

* **Consequência:** Projetos encerrados ocultam os botões "Tenho Interesse" e "Inscrever-se" na vitrine.

### **[RN04] - Transparência e LGPD (Proteção de Dados)**

Ao acionar o fluxo de "Tenho Interesse", o sistema deve exibir obrigatoriamente um aviso de consentimento: *"Ao confirmar, você autoriza o compartilhamento do seu Nome e E-mail institucional/Google com o coordenador deste projeto para fins de contato e seleção"*. O registro do lead só ocorre após o aceite implícito ou explícito desta condição.

### **[RN05] - Restrição de Coordenação Isolada**

Um Coordenador possui autonomia administrativa exclusiva sobre os projetos em que está formalmente vinculado. O sistema deve impedir (via controle de acesso no back-end) que um Coordenador visualize a lista de interessados ou edite dados de projetos pertencentes a outros docentes.

### **[RN06] - Regras de Upload de Banner**

O sistema deve validar os arquivos de imagem de capa (banner) seguindo os critérios:

* **Formatos aceitos:** Apenas .jpg, .jpeg e .png.
* **Tamanho máximo:** 2MB por arquivo.
* **Persistência:** O arquivo deve ser renomeado para um padrão único (ex: UUID) para evitar conflitos no diretório físico do servidor.

### **[RN07] - Obrigatoriedade de Link Externo**

Para que um projeto seja publicado com o status "Aberto", além de preencher uma data de inscrição futura a data atual, o Coordenador deve obrigatoriamente preencher o campo `Link do Edital`, `Link de Inscrição Externo` ou realizar o `Upload do Edital`. Caso ambos estejam vazios, o sistema deve emitir um alerta e impedir a submissão para moderação.

### **[RN08] - Notificações de Lead (Assíncronas)**

Para cada novo registro na entidade **[ENT02]**, o sistema deve disparar um e-mail ao Coordenador. Esta operação deve ser assíncrona para não impactar a experiência de navegação do estudante (o estudante recebe a confirmação de sucesso enquanto o e-mail é processado em segundo plano).

## 8. Erratas e Atualizações (Versão 2.0)

Esta seção detalha as modificações realizadas no sistema em relação à especificação original para fins de adequação técnica e pedagógica.

### **[ERR01] - Processamento de Imagens (Banner)**
*   **Original:** [RN06] e [RNF04] previam upload de arquivos físicos e armazenamento no servidor.
*   **Atualização:** A lógica de upload foi substituída pelo recebimento de imagens em formato **Base64 (Binary)**. As validações de formato (.jpg, .png) e tamanho (2MB) foram mantidas e são processadas diretamente no back-end antes da persistência no banco de dados.

### **[ERR02] - Obrigatoriedade de Link Externo (RN07)**
*   **Original:** Previa `Link do Edital`, `Link de Inscrição Externo` ou `Upload do Edital`.
*   **Atualização:** O sistema agora exige obrigatoriamente que pelo menos um dos dois campos de URL (`linkEdital` ou `linkInscricaoExterno`) seja preenchido. A funcionalidade de upload físico de arquivos PDF foi postergada.

### **[ERR03] - Moderação e Status de Inscrição**
*   **Atualização:** A entidade `Projeto` agora conta com o campo explícito `statusModeracao` (PENDENTE, PUBLICADO, REPROVADO) para atender à **[RN01]**. O status de exibição ("Aberto", "Encerrado", "Aguardando") é calculado dinamicamente em tempo de execução com base nas datas de inscrição, garantindo maior integridade em relação ao tempo real.

### **[ERR04] - Padrões de Implementação e Segurança**
*   **Injeção de Dependências:** Foi adotado o padrão pedagógico de injeção via campos utilizando a anotação **`@Autowired`**, conforme diretriz de ensino.
*   **Controle de Acesso:** A segurança das rotas e métodos foi reforçada com o uso da anotação **`@PreAuthorize`**, permitindo um controle granular de permissões (hasRole, hasAnyRole, isAuthenticated) diretamente na camada de Controller.

### **[ERR05] - Notificações (RN08)**
*   **Status:** A implementação da lógica de envio de e-mail assíncrono foi postergada para sprints futuras, mantendo-se apenas o registro do Lead no banco de dados no momento atual.

