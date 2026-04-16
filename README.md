# 📚 Q-Projetos - API (Back-end)

Bem-vindo ao repositório oficial da API do **Q-Projetos**, a plataforma de vitrine e divulgação de projetos de pesquisa e extensão do **IFPE - Campus Afogados da Ingazeira**.

Este projeto atua como o back-end da plataforma, sendo responsável por garantir a segurança, integridade e o fornecimento dos dados em formato JSON para a aplicação Front-end (desenvolvida em Ionic/Angular).

---

## 🛠️ Tecnologias Utilizadas

O ecossistema base escolhido para o back-end foca em robustez, escalabilidade e adoção de padrões de mercado:

* **Linguagem:** Java (versão 17 ou superior)
* **Framework Principal:** Spring Boot (versão 3.x)
* **Acesso a Dados:** Spring Data JPA / Hibernate
* **Banco de Dados:** MySQL
* **Gerenciador de Dependências:** Maven
* **Facilitador de Código:** Lombok

---

## 📂 Arquitetura do Projeto (Padrão MVC)

Para mantermos o código organizado, escalável e fácil de entender, utilizamos a arquitetura em camadas. Todo o código fonte está na pasta `src/main/java/br/edu/ifpe/q_projetos/`:

* **`model/` (Entidades):** O "molde" dos nossos dados. As classes aqui representam as tabelas exatas no MySQL (ex: `Projeto.java`).
* **`repository/` (Acesso a Dados):** Interfaces que comunicam com o banco de dados. Elas traduzem os nossos comandos Java para SQL automaticamente.
* **`service/` (Regras de Negócio):** O "cérebro" do sistema. Local onde as validações, verificações e regras do IFPE são executadas antes de salvar qualquer informação no banco.
* **`controller/` (Rotas/Endpoints):** A "porta de entrada". Recebe as requisições HTTP da internet (do app Ionic), repassa para o *Service* trabalhar, e devolve a resposta.

---

## 💻 Pré-requisitos (Configuração da Máquina)

Para rodar este projeto no seu computador, certifique-se de ter as seguintes ferramentas instaladas:

1. **Java Development Kit (JDK):** Versão 17 ou 21 (LTS).
   * *Para verificar se já tem, abra o terminal e digite:* `java -version`
2. **MySQL Server:** Banco de dados rodando na porta padrão `3306`.
3. **IDE:** VS Code (com Extension Pack for Java), IntelliJ IDEA ou Eclipse.
4. **Lombok:** Certifique-se de que a extensão/plugin do Lombok está ativada na sua IDE para que ela reconheça os *Getters e Setters* ocultos.

---

## 🚀 Como rodar o projeto localmente

**1. Clone o repositório**
```bash
git clone https://github.com/faonictor/q-projetos-backend.git
cd q-projetos-backend
```

**2. Configure o Banco de Dados**
Abra o arquivo `src/main/resources/application.properties` e altere as credenciais (usuário e senha) para coincidir com o MySQL da sua máquina local:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/q_projetos?createDatabaseIfNotExist=true&useTimezone=true&serverTimezone=UTC
spring.datasource.username=seu_usuario_local
spring.datasource.password=sua_senha_local
```
*(Nota: Não se preocupe em criar o banco `q_projetos` manualmente. O comando `createDatabaseIfNotExist=true` fará isso por você ao rodar a aplicação).*

**3. Inicie o Servidor**
* **Via IDE:** Encontre o arquivo `QProjetosApplication.java` e clique em *Run*.
* **Via Terminal:** Execute o comando:
```bash
mvn spring-boot:run
```

Se tudo der certo, você verá o logotipo do Spring no terminal e a mensagem indicando que a aplicação iniciou na porta **8080**.

---

## 📡 Endpoints da API (Documentação Inicial)

Abaixo estão as rotas base já disponíveis para consumo do Front-end. A URL base local é `http://localhost:8080`.

| Método HTTP | Rota | Descrição | Retorno (Status) |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/projetos` | Lista todos os projetos cadastrados. | `200 OK` (Array JSON) |
| **GET** | `/api/projetos/{id}` | Busca os detalhes de um projeto específico. | `200 OK` ou `404 Not Found` |
| **POST** | `/api/projetos` | Cadastra um novo projeto. | `201 Created` (Objeto JSON) |
| **DELETE** | `/api/projetos/{id}` | Exclui um projeto pelo seu ID. | `204 No Content` |

**Exemplo de Corpo JSON para o POST:**
```json
{
  "nome": "Jogo Educativo com Arduino",
  "responsavel": "Maria Silva",
  "ativo": true,
  "dataInicio": "2026-04-15"
}
```
*(O campo `id` e `dataCriacao` são gerados automaticamente pelo sistema).*

---

### 👨‍💻 Contribuição

#### 1. O Padrão de Commits
Nós utilizamos o padrão *Conventional Commits* para manter o histórico claro. Todo commit deve começar com um prefixo que explica o que foi feito:

* **`feat:` (Feature)** -> Use quando estiver a criar algo **novo**.
	  * *Exemplo:* `git commit -m "feat: cria endpoint para cadastro de usuários"`
	
* **`fix:` (Fix)** -> Use quando estiver a **consertar** um bug ou erro em algo que já existia.
	  * *Exemplo:* `git commit -m "fix: corrige erro de formatação na data do projeto"`
	
* `refactor:` para melhorar um código sem mudar a sua função).
	* *Exemplo:* `git commit -m "refactor: ajuste função service cadastrar"`

---
#### 2. Passo a Passo do Desenvolvedor
Siga **exatamente** esta ordem toda vez que for iniciar um trabalho novo:

#### PASSO A: Atualize a sua máquina (Antes de começar)
Nunca comece a programar sem antes garantir que tem a versão mais recente do projeto. A nossa branch principal de trabalho é a `development`.

```bash
# 1. Vá para a branch development
git switch development

# 2. Vá para a branch development
git branch

# 2. Puxe as atualizações mais recentes da nuvem
git pull origin development
````

#### PASSO B: Crie a sua própria branch
Nunca programe diretamente na `development` ou na `main`. Crie uma branch isolada para a sua tarefa.

```bash
# O comando -b cria a branch e já muda para ela.
# Use um nome curto e sem espaços (ex: feature/cadastro-usuario ou fix/rota-delete)
#Exemplo comando:

git switch -c nome-da-sua-branch-aqui
```

#### PASSO C: Trabalhe e faça Commits
Escreva o seu código, salve os arquivos e adicione-os ao Git.

```Bash
# 1. Checa quais arquivos estão prontos pro commit
git status

# 2. Adicione os arquivos alterados
git add .

# 3. Crie o commit com o prefixo correto
git commit -m "feat: adiciona nova funcionalidade X"
```

#### PASSO D: Finalize e envie para a Nuvem
Quando a sua tarefa estiver pronta e testada, é hora de enviar a sua branch para o GitHub.

```Bash
git push origin nome-da-sua-branch-aqui
```

***Desenvolvido com dedicação pela equipe do IFPE - Afogados da Ingazeira.***