

# Pós Tech FIAP - Tech Challenge - Grupo 17

## SIAES - Sistema de Gestão Integrada

![SIAES](https://img.shields.io/badge/SIAES-Sistema%20de%20Gest%C3%A3o-blue)

Bem-vindo(a) ao projeto **SOAT**! Este repositório faz parte da **Pós-Graduação em Arquitetura de Software da FIAP** e tem como objetivo demonstrar uma aplicação desenvolvida em **Java** com **Spring Boot** e persistência de dados em **PostgreSQL**, além de estar preparada para execução em **Docker**.

<div align="center">

### 👨‍💼👩‍💼‍ Autores

Este é um projeto desenvolvido por:

![](https://img.shields.io/badge/RM367742-Douglas%20Andrade%20Severa-blue)
<br>
![](https://img.shields.io/badge/RM367169-Edmar%20Dias%20Santos-blue)
<br>
![](https://img.shields.io/badge/RM367946-Vinícius%20Louzada%20Valente-blue)
<br>
![](https://img.shields.io/badge/RM362288-Felipe%20Martines%20Kurjata-blue)
</div>

## 💻 Proposta

Desenvolver a primeira versão (MVP) do back-end do sistema da oficina,
com foco em gestão de ordens de serviço, clientes e peças, aplicando Domain
Driven Design (DDD) e garantindo boas práticas de Qualidade de Software e
Segurança.

Link da explicação do projeto: https://www.youtube.com/watch?v=7FwvDX7eNpA

## 🛠 Stack Tecnológica
[![Java](https://img.shields.io/badge/java_21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://docs.oracle.com/en/java/javase/21/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)](https://docs.spring.io/spring-boot/documentation.html)
[![PostgreSQL](https://img.shields.io/badge/postgresql-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/docs/current/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://docs.docker.com/manuals/)
[![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)](https://swagger.io/docs/)


## 💫 Como Este Projeto se Destaca

1. **Domain-Driven Design (DDD)**: Essa abordagem foca em modelar o software de acordo com o domínio do negócio, colocando as regras e a linguagem do domínio no centro da aplicação. O DDD busca alinhar o código à realidade do problema que o sistema resolve, promovendo uma estrutura organizada em camadas (como domínio, aplicação e infraestrutura) que facilita a manutenção, a compreensão e a evolução do sistema.
2. **Uso de PostgreSQL**: Utiliza banco de dados relacional com esquema rígido, garantindo integridade referencial, suporte avançado a transações e consultas SQL otimizadas.
3. **Facilidade de Deploy**: Com Docker, é possível containerizar e executar a aplicação de forma simples, garantindo portabilidade e padronização do ambiente.
4. **Documentação com Swagger**: A aplicação inclui uma interface Swagger para facilitar a exploração das APIs, tornando o desenvolvimento e testes mais ágeis.

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

| Usuário       | Login        | Senha        | Role         |
| ------------- | ------------ | ------------ | ------------ |
| Administrador | admin        | admin        | ADMIN        |
| Colaborador   | collaborator | collaborator | COLLABORATOR |
| Cliente       | client       | 123          | CLIENT       |


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




## 📚 Dictionary - Ubiquitous Language

### Contexto: Gestão de Ordens de Serviço (Domínio Principal)

- **Ordem de Serviço | Service Order | OS**: O documento principal que detalha todos os serviços, peças e insumos necessários para um reparo. Representa o ciclo de vida completo do trabalho no veículo.

- **Orçamento | Estimate**: A estimativa de custo gerada automaticamente, com base nos serviços e peças incluídas na OS. É enviado ao cliente para aprovação.
- **Status OS**: O estágio atual da Ordem de Serviço. Os valores definidos são: Recebida, Em diagnóstico, Aguardando Aprovação, Em execução, Finalizada e Entregue.
- **Serviço Solicitado** | **Service Labor** : Um trabalho específico requerido pelo cliente (ex: "Troca de Óleo").
- **Reparo Adicional | Additional Repair**: Um serviço que não foi solicitado inicialmente, mas que foi diagnosticado como necessário. Precisa de aprovação do cliente.
- **Tempo Médio de Execução | Average Execution Time**: O tempo estimado para a conclusão de um serviço, usado para monitorar a eficiência da oficina.

#### Contexto: Gestão de Clientes e Veículos (Subdomínio de Suporte)

- **Cliente | client**: A pessoa ou empresa que solicita um serviço.
- **Veículo | Vehicle**: O carro ou moto que será consertado.
- **Placa | Plate**: O identificador único do veículo.
- **Histórico do Veículo | Vehicle History**: O registro de todas as Ordens de Serviço já realizadas em um veículo

#### Contexto: Gestão de Peças e Insumos (Subdomínio de Suporte)

- **Peça | Part**: Um componente físico que será instalado no veículo (ex: "Filtro de Óleo").
- **Insumo | Supply**: Um material consumível usado no serviço, mas que não é uma peça (ex: "Óleo do Motor").
- **Controle de Estoque | Stock Control**: O processo de gerenciamento da quantidade de peças e insumos disponíveis.

#### Contexto: Gestão de Serviços (Subdomínio de Suporte)

- **Serviço | Service**: Uma descrição padronizada de um trabalho a ser realizado (ex: "Troca de Óleo", "Alinhamento e Balanceamento").
- **Preço Padrão do Serviço | Standard Service Price**: O valor pré-definido para um serviço específico.

### Domínios e Subdomínios

#### Domínio Principal (Core Domain)

- **Gestão de Ordens de Serviço | Service Order Management (OS)** e Acompanhamento de Serviços: Este é o nosso domínio principal. A criação, a alteração de status, o acompanhamento em tempo real e a autorização de serviços são as funcionalidades mais importantes para a oficina. É o diferencial competitivo. A "Ordem de Serviço" é um dos agregados mais importantes aqui.

#### Subdomínios de Suporte (Supporting Subdomains)

- **Gestão de Peças e Insumos | Parts and Supplies Management**: Essencial para o fluxo, mas o sistema de controle de estoque não é o diferencial competitivo da oficina, e sim uma parte de apoio para garantir que a Ordem de Serviço seja concluída.
- **Gestão de Serviços | Service Management**: É outro suporte para o domínio principal. É como um "cardápio" de opções. O sistema precisa saber quais serviços estão disponíveis e qual é o preço para poder gerar um orçamento e criar uma Ordem de Serviço.
- **Gestão de Clientes e Veículos | Customer and Vehicle Management**: O CRUD (criação, leitura, atualização, exclusão) de clientes e veículos é crucial para o negócio, mas é uma funcionalidade de suporte ao domínio principal de "Gestão de OS".

#### Subdomínios Genéricos (Generic Subdomains)

- **Autenticação e Autorização |
  Authentication and Authorization**: A segurança do sistema (autenticação JWT) é um requisito genérico. Podemos usar bibliotecas ou frameworks para isso (como Spring Security, por exemplo) sem precisar reinventar a roda.


## 📚 Domain Storytelling - Fluxo de Criação e Acompanhamento da Ordem de Serviço

![Domain_Storytelling](https://github.com/user-attachments/assets/34610f6a-bd65-4bf3-bf1f-b80f4b5daa79)


## 📚 MIRO - Event Storming

![Event_Storming](https://github.com/user-attachments/assets/7bdce63e-70bc-4755-875e-de87e3a920f4)

Para mais detalhes acesse MIRO com o Domain Storytelling / Event Storming:
[Event Storming](https://miro.com/welcomeonboard/RmZjaThmY2QrTmVGa2lFSW5kaDFwVmdCUm5RQWVndHplYUFUWTAwUFNxbW4vaDRLQ1ZOQzgyNkRTaVprZVFlK0FjMVU5V2o0bmxQcmE1eUNXemxqVlVkYTIxbVFDN3hPMXhLTUNEaXkyMUVVcGZOcFhqNFhOb282L2FQbmJLbkR0R2lncW1vRmFBVnlLcVJzTmdFdlNRPT0hdjE=?share_link_id=156423143991)

## 🔐 Análise de Vulnerabilidades

A análise de vulnerabilidades é realizada de forma estática pelo SonarQube, permitindo identificar riscos de segurança diretamente no código antes da execução.

## 🧪 Qualidade de Código e Cobertura de Testes

O projeto adota ferramentas de **análise de qualidade**, **cobertura de testes** e **segurança estática** para garantir a confiabilidade e a manutenibilidade do código.

---

### 🧩 JaCoCo — Cobertura de Testes

**JaCoCo (Java Code Coverage)** é utilizado para medir o quanto do código-fonte é efetivamente coberto por testes automatizados.

Durante a execução dos testes (`mvn test` ou `mvn verify`), o JaCoCo:
- Instrumenta o código para medir cobertura;
- Gera relatórios em formatos **HTML**, **CSV** e **XML**;
- Indica quais classes, métodos e linhas foram ou não executados pelos testes;
- Identifica partes do código não cobertas por testes;
- Prepara o arquivo jacoco.xml este arquivo é lido pelo SonarQube;

📁 **Relatório padrão gerado:**

```
target\site\jacoco\jacoco.xml
```
```
target\site\jacoco\index.html
```

💡 *Configuração no `pom.xml`:*

```
             <plugin>
				<groupId>org.jacoco</groupId>
				<artifactId>jacoco-maven-plugin</artifactId>
				<version>0.8.14</version>
				<executions>
					<execution>
						<goals>
							<goal>prepare-agent</goal>
						</goals>
					</execution>
					<execution>
						<id>report</id>
						<phase>verify</phase>
						<goals>
							<goal>report</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
```
## 🔍 JaCoCo - Relatório

![index_rel_jacoco](https://github.com/user-attachments/assets/9c95a309-c565-4f2b-ad31-0458f2584845)


###  🧠 SonarQube — Análise Estática e Métricas de Qualidade

O **SonarQube** é utilizado para realizar **análise estática de código (SAST)** e consolidar métricas de qualidade, incluindo:
- 🐞 *Bugs e vulnerabilidades*;
- 🚨 *Code Smells* (más práticas e complexidade);
- 🧮 *Cobertura de testes* (dados fornecidos pelo JaCoCo);
- 🔐 *Alertas de segurança*.

📘 **Por que utilizar:**
- Detecta problemas antes da execução em produção;
- Mantém padrões de qualidade uniformes entre os módulos;
- Permite monitorar a evolução da qualidade do projeto ao longo do tempo.

### 🔗 Integração entre JaCoCo e SonarQube

O **SonarQube** consome o relatório XML gerado pelo **JaCoCo** para exibir as métricas de cobertura.

### 📊 Iniciando o teste no SonarQube

Neste estudo, o SonarQube foi integrado ao ambiente definido pelo docker-compose, utilizando o seguinte caminho:

```
http://localhost:9000
```
### Primeiro acesso
- Realize o login inicial com as credenciais padrão:
  - **Usuário:** `admin`
  - **Senha:** `admin`
- Após o login, será necessário gerar um **token de autenticação**.
  - Esse token será utilizado para configurar a análise de código no projeto.
  - Para gerar o token, acesse:
```
"http://localhost:9000/account/security"
```
### Observações
- Certifique-se de que o container do SonarQube esteja em execução antes de acessar o endereço.
- O token gerado deve ser armazenado com segurança pelo que pesquisei só é possivél a visualização nesse momento caso contrario excluir e gerar outro.

O Token será utilizado no comando abaixo:
```
-Dsonar.login=<seu_token_aqui>
```
execute o camando:
```
mvn clean verify sonar:sonar 
"-Dsonar.projectKey=tech_challenge_oficina" 
"-Dsonar.host.url=http://localhost:9000" 
"-Dsonar.login=<seu_token_aqui>" 
"-Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml"
```

Relatório SonarQube

<img width="1035" height="919" alt="sonar" src="https://github.com/user-attachments/assets/99bab23d-5a56-4f96-b8e6-1c37b8859544" />


### ⚙️ OWASP Dependency-Check

O OWASP Dependency-Check é uma ferramenta que analisa as dependências do projeto e identifica vulnerabilidades conhecidas (CVE) com base no banco de dados NVD (National Vulnerability Database).

O plugin verifica bibliotecas inseguras e gera relatórios em HTML, XML e JSON, permitindo identificar riscos de segurança nas dependências utilizadas.

Plugin Utilizado:

```
             <plugin>
				<groupId>org.owasp</groupId>
				<artifactId>dependency-check-maven</artifactId>
				<version>12.1.8</version>
				<executions>
					<execution>
						<goals>
							<goal>check</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
```
Para executar:
```
mvn verify

```
Relatório por padrão:
```
target/dependency-check-report.html
```

Relatório OWASP Dependency-Check

<img width="941" height="910" alt="owsap" src="https://github.com/user-attachments/assets/5239a7d1-e396-467e-8951-20290d1159ef" />

