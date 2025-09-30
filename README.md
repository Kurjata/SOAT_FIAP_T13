# SIAES - Sistema de Gestão Integrada

![SIAES](https://img.shields.io/badge/SIAES-Sistema%20de%20Gest%C3%A3o-blue)

Bem-vindo(a) ao projeto **SOAT**! Este repositório faz parte da **Pós-Graduação em Arquitetura de Software da FIAP** e tem como objetivo demonstrar uma aplicação desenvolvida em **Java** com **Spring Boot** e persistência de dados em **Postgree**, além de estar preparada para execução em **Docker**.

## Sobre o Projeto

- **Instituição**: FIAP
- **Programa**: Pós-Graduação em Arquitetura de Software
- **Stack Tecnológica**:
  - **Java** (versão 21)
  - **Spring Boot**
  - **Postgree** (banco de dados)
  - **Docker**
- Maven 3.8+

## Como Este Projeto se Destaca

1. **Arquitetura Hexagonal**: Essa arquitetura foca em separar claramente as regras de negócio (core da aplicação) das tecnologias externas (como banco de dados, APIs, etc.), facilitando testes, manutenção e evolução do sistema.
2. **Uso de Postgree**: Utiliza banco de dados relacional com esquema rígido, garantindo integridade referencial, suporte avançado a transações e consultas SQL otimizadas..
3. **Facilidade de Deploy**: Com Docker, é possível containerizar e executar a aplicação de forma simples, garantindo portabilidade e padronização do ambiente.
4. **Documentação com Swagger**: A aplicação inclui uma interface Swagger para facilitar a exploração das APIs, tornando o desenvolvimento e testes mais ágeis.

---

## 🐳 Executando com Docker Compose

O projeto possui configuração completa para execução via Docker Compose.

```bash
# Build e execução do projeto
docker compose up --build -d

# Verificar logs
docker compose logs -f

# Parar containers
docker compose down
```
📌 Endpoints do Swagger

Após subir a aplicação, você pode acessar a documentação Swagger em:
```
http://localhost:8080/swagger-ui/index.html
```

## 👤 Usuários de Teste

Para acessar a API via Swagger ou autenticar via endpoint `/auth/login`, utilize os seguintes usuários:

| Usuário        | Login        | Senha     | Role          |
| -------------- | ----------- | -------- | ------------- |
| Administrador  | admin       | admin | ADMIN         |
| Colaborador    | collaborator | collaborator | COLLABORATOR  |

> ⚠️ Senhas em ambiente de produção devem ser alteradas.


🔑 Exemplos de Autenticação

Login
```
POST /auth/login
Content-Type: application/json
{
    "login": "admin",
    "password": "admin"
}

```

Retorno
```
{
    "token": "jwt-token-aqui",
    "refreshToken": "refresh-token-aqui",
    "username": "Administrador",
    "role": "ADMIN"
}

```

Use o token retornado no Swagger ou em requisições autenticadas no header:
> Authorization: Bearer <token>

🛠️ Observações

O JWT expira em 8 horas e o refresh token em 30 dias.
