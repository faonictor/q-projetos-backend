# Guia de Testes Manuais com Exemplos JSON (v3.0)

Este guia orienta o passo a passo para cadastrar a massa de dados via API e testar as funcionalidades do sistema. 

**Importante:** Execute o script `Massa_Dados_Testes.sql` APENAS para limpar o banco antes de iniciar. A criação de usuários será feita por aqui para garantir o hash correto da senha.

---

## 1. Cadastro de Usuários (Massa de Dados)
Execute os seguintes requests para popular os usuários base. 
**POST** `/api/auth/register`

**A. Administrador:**
```json
{
  "nome": "Administrador Geral",
  "email": "admin@ifpe.edu.br",
  "senha": "admin123",
  "role": "ROLE_ADMIN",
  "vinculo": "SERVIDOR"
}
```

**B. Professor Coordenador:**
```json
{
  "nome": "Professor Coordenador",
  "email": "coordenador@ifpe.edu.br",
  "senha": "admin123",
  "role": "ROLE_USER",
  "vinculo": "SERVIDOR"
}
```

**C. Estudante Ativo:**
```json
{
  "nome": "Estudante Ativo",
  "email": "estudante@discente.ifpe.edu.br",
  "senha": "admin123",
  "role": "ROLE_USER",
  "vinculo": "ESTUDANTE"
}
```

---

## 2. Login e Autenticação
**Acesso:** Envie um `POST` para `/api/auth/login`.

**Exemplo (Login Admin):**
```json
{
  "email": "admin@ifpe.edu.br",
  "senha": "admin123"
}
```

**Ação:** Copie o campo `token` da resposta e configure-o como **Bearer Token** no Postman (aba Authorization) para as requisições seguintes.

---

## 3. Gerenciamento de Projetos (Logado como Admin ou Coordenador)

### Criar Novo Projeto (Gera Status PENDENTE)
**POST** `/api/projetos`
```json
{
  "titulo": "Novo Projeto de Automação",
  "tipo": "PESQUISA",
  "descricao": "Estudo de automação industrial",
  "dataInicio": "2026-08-01",
  "dataTermino": "2027-08-01",
  "dataInicioInscricao": "2026-07-01",
  "dataFimInscricao": "2026-07-31",
  "linkEdital": "https://ifpe.edu.br/edital123",
  "linkInscricaoExterno": "https://forms.gle/exemplo",
  "vagas": 3,
  "modalidade": "BOLSISTA",
  "banner": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==",
  "idCoordenadorManual": 2
}
```
*(Nota: `idCoordenadorManual` associa o projeto ao usuário criado no passo 1.B. Se não passar este campo, o projeto ficará vinculado a quem fez a requisição).*

### Aprovar Projeto (Logado como Admin)
**POST** `/api/projetos/1/aprovar`

---

## 4. Leads de Interesse (Logado como Estudante)

### Registrar Interesse (RN02 - Unicidade)
**POST** `/api/interesses`
```json
{
  "idProjeto": 1,
  "nome": "Estudante Ativo",
  "email": "estudante@discente.ifpe.edu.br",
  "seriePeriodo": "6º Período ADS",
  "modalidadePretendida": "BOLSISTA",
  "aceitouLgpd": true
}
```
*Tente enviar esta requisição duas vezes para testar o erro 400 da Regra de Negócio (RN02).*

---

## 5. Favoritos e Histórico (Logado como Estudante)

### Favoritar um Projeto
**POST** `/api/favoritos`
```json
{
  "idProjeto": 1
}
```

### Ver Meu Histórico
**GET** `/api/favoritos/meu-historico`
*Retorna os projetos favoritados pelo usuário logado.*

---

## 6. Equipe (Logado como Coordenador do Projeto)

### Adicionar Membro à Equipe
**POST** `/api/vinculos`
```json
{
  "idProjeto": 1,
  "idUsuario": 3,
  "papel": "BOLSISTA",
  "ativo": true
}
```

---

## 7. Teste de Falhas (Validações)

### Erro de Banner Inválido
Tente enviar um `banner` que não comece com `data:image/` no endpoint de criação de projeto.
**Resposta esperada:** 400 - "Regra de Negócio: Formato de imagem inválido..."

### Erro de Link Obrigatório
Tente criar um projeto omitindo os campos `linkEdital` e `linkInscricaoExterno`.
**Resposta esperada:** 400 - "Regra de Negócio: Pelo menos um link deve ser fornecido."
