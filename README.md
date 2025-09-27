# SIAES - Sistema de Gestão Integrada

![SIAES](https://img.shields.io/badge/SIAES-Sistema%20de%20Gest%C3%A3o-blue)

Sistema Integrado de Atendimento e Execução de Serviços desenvolvido em **Spring Boot**, com autenticação JWT e API documentada via **Swagger**.

---

## 🚀 Requisitos

- Docker ≥ 20.x
- Docker Compose ≥ 2.x
- Java 21
- Maven 3.8+

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