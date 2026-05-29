# Documento de Especificação de Requisitos – Plataforma Q-Projetos (Versão Atual)

## Source Q-Projetos

| Backend | Frontend |
| --- | --- |
| [![GitHub repo](https://img.shields.io/badge/github-repo-blue?logo=github)](https://github.com/faonictor/q-projetos-backend/) | [![GitHub repo](https://img.shields.io/badge/github-repo-green?logo=github)](https://github.com/guiza01/q-projetos/) |

## 1. Visão Geral

A *Plataforma Q-Projetos* consiste numa plataforma web projetada para funcionar como um catálogo institucional. O sistema visa centralizar a divulgação de projetos de ensino, pesquisa e extensão, facilitando o acesso da comunidade académica às informações e a gestão de vagas para participantes (bolsistas e voluntários).

Para além da gestão de vagas, o sistema atuará como uma vitrine ativa para captação de interessados ("leads"), integrando autenticação externa e notificações automáticas para agilizar a comunicação entre estudantes e coordenadores.

## 2. Atores do Sistema

Os atores representam os diferentes perfis que integrarão o quadro de usuários do sistema:

* **Administrador:** Responsável pela gestão global do sistema, aprovação/reprovação de projetos e manutenção de perfis e parcerias.
* **Visitante:** Usuário anônimo ou autenticado (Estudante/Servidor) que navega pelo catálogo em busca de informações ou vagas.
* **Coordenador:** Usuário autenticado com perfil `ROLE_COORD` e vínculo `SERVIDOR`, responsável pelo cadastro e gestão de um projeto específico.
* **Estudante:** Usuário autenticado via Google ou localmente, com vínculo `ESTUDANTE`, que interage com a plataforma, favoritando projetos e manifestando interesse.

## 3. Perfis dos Atores

### 1. Perfis de Acesso ao Sistema (Nível Global)

* **Administrador Global (ROLE_ADMIN):** Possui privilégios máximos. Responsável pela homologação (aprovação/reprovação) de projetos, gestão de utilizadores (incluindo alteração de Role e Vínculo) e moderação de conteúdo.
* **Coordenador (ROLE_COORD):** Perfil atribuído manualmente pelo Administrador apenas a usuários com vínculo **SERVIDOR**. Permite a criação de propostas e gestão de seus próprios projetos.
* **Usuário Autenticado (ROLE_USER):** Estudantes ou Servidores sem cargo de gestão. Permite navegação na vitrine, visualização de editais e manifestação de interesse.
* **Usuário Anônimo (ROLE_GUEST):** Acesso restrito apenas à visualização (modo de leitura) da vitrine pública de projetos aprovados.

### 2. Perfis de Atuação (Nível de Vínculo ao Projeto)

* **Coordenador / Orientador:** Autoridade máxima do projeto. Possui permissão exclusiva para editar informações, gerenciar membros e visualizar a lista de interessados (leads) de seu projeto.
* **Colaborador / Coorientador:** Servidor que contribui para a execução do projeto.
* **Bolsista / Voluntário:** Estudante oficialmente vinculado ao projeto após seleção.

## 4. Modelo de Dados (Entidades Principais)

**[ENT01] - Projeto:**
* **Id:** Identificador único.
* **Título:** Nome oficial do projeto.
* **Tipo:** Ensino, Pesquisa ou Extensão.
* **Descrição:** Resumo e objetivos.
* **Datas:** Ciclo de vida (Início/Término) e período de inscrição.
* **Links:** Edital e/ou Formulário Externo (Obrigatório preencher ao menos um).
* **Vagas:** Quantidade disponível.
* **Modalidade:** Bolsista, Voluntário ou Ambos.
* **Banner:** Imagem em formato **Base64** (máx. 2MB).
* **Status Moderação:** PENDENTE, PUBLICADO ou REPROVADO.

**[ENT02] - Interesse (Lead):**
* **Registro:** Captura nome e e-mail (auto via Google/Sistema) + série/período e modalidade pretendida.

**[ENT03] - Usuário (User):**
* **Role:** ROLE_ADMIN, ROLE_COORD ou ROLE_USER.
* **Vínculo:** SERVIDOR (Docente/TAE) ou ESTUDANTE.
* **Segurança:** ROLE_COORD só pode ser atribuída a quem possui vínculo SERVIDOR.

**[ENT05] - Vínculo de Equipe:**
* **Unicidade:** Um usuário só pode possuir **um único papel ativo** por projeto (ex: não pode ser Coordenador e Bolsista simultaneamente).

## 5. Requisitos Funcionais

### **Módulo Público**
* **RF01:** Listar apenas projetos com status "Publicado".
* **RF02:** Cálculo dinâmico de status: O sistema exibe "Aberto", "Encerrado" ou "Aguardando" em tempo real comparando a data atual com o calendário de inscrições.

### **Módulo Coordenador**
* **RF11:** Cadastro de projetos com upload de banner em Base64.
* **RF19 (Novo):** Dashboard "Meus Projetos" para visualização rápida apenas dos projetos vinculados ao coordenador logado.

### **Módulo Perfil**
* **RF20 (Novo):** O usuário logado pode atualizar seus dados pessoais (nome, e-mail, senha) através de uma rota dedicada (`/perfil`), sem acesso a campos administrativos.

### **[CEN05] - Moderação e Governança (Admin)**
* **RF16:** Painel de moderação para editar dados, excluir ou marcar projetos como REPROVADO.
* **RF17:** Atribuição manual de Role e Vínculo. O sistema impede a promoção de um Estudante a Coordenador sem a mudança prévia de vínculo para Servidor.

## 6. Regras de Negócio e Validações

### **[RN01] - Moderação Global**
Projetos novos ou alterados em campos estruturais retornam ao status **PENDENTE** até nova homologação do Administrador.

### **[RN03] - Ciclo de Vida em Tempo Real**
O status de vigência é calculado dinamicamente em cada requisição. Não há necessidade de agendadores de tarefas (schedulers) para mudança de status de exibição.

### **[RN05] - Restrição de Coordenação Isolada**
Um Coordenador possui autonomia exclusiva sobre os projetos aos quais está formalmente vinculado.

### **[RN06] - Processamento de Imagens**
Imagens recebidas via String Base64. Devem ser .jpg ou .png e possuir tamanho máximo de **2MB**.

### **[RN07] - Obrigatoriedade de Link**
Impedir a publicação se os campos `Link do Edital` e `Link de Inscrição Externo` estiverem ambos vazios.

### **[RN09] - Exclusividade de Papel**
O sistema garante a unicidade da relação Usuário-Projeto, permitindo apenas um vínculo de equipe por integrante em cada projeto.

***Desenvolvido com dedicação pela equipe do IFPE - Afogados da Ingazeira.***
