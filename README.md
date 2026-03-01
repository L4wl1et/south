# South — Beta Access Registration System

Sistema de cadastro para acesso antecipado ao beta de jogos, desenvolvido como desafio técnico para demonstrar boas práticas de desenvolvimento Java com Spring Boot.

---

## Tecnologias

- **Java 21** com Virtual Threads habilitadas
- **Spring Boot 3.5.11**
- **MongoDB** — banco de dados não relacional
- **RabbitMQ** — mensageria assíncrona
- **Docker + Docker Compose** — orquestração dos containers
- **Testcontainers** — testes de integração com infraestrutura real

---

## Arquitetura

O projeto segue os princípios de **Clean Architecture** organizado em quatro camadas:
```
com.project.south
├── domain          → Modelos, interfaces de repositório e exceções (sem dependência de framework)
├── application     → Serviços de negócio e DTOs
├── infrastructure  → Implementações concretas: MongoDB, RabbitMQ e configurações
└── presentation    → Controllers REST e tratamento global de exceções
```

### Fluxo de inscrição
```
POST /api/games/{gameId}/registrations
        │
        ▼
  Inscrição criada com status PENDING
        │
        ▼
  Mensagem publicada no RabbitMQ
        │
        ▼
  Consumer processa de forma assíncrona
        │
        ├── Vagas disponíveis → status CONFIRMED
        └── Sem vagas         → status WAITLISTED

  Cancelamento de CONFIRMED → promove o primeiro WAITLISTED para CONFIRMED
```

### Controle de concorrência

- **Optimistic Locking** com `@Version` no MongoDB — garante que atualizações concorrentes no mesmo documento sejam detectadas e tratadas
- **Consumers concorrentes** no RabbitMQ — configurados com 3 a 10 threads paralelas para processar mensagens simultaneamente
- **Virtual Threads (Java 21)** — habilitadas para melhor throughput sob alto volume de requisições

---

## Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e em execução

---

## Como executar

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/south.git
cd south
```

### 2. Suba os containers
```bash
docker compose up --build
```

Esse comando irá:
- Buildar a imagem da aplicação Spring Boot
- Subir o container do MongoDB na porta `27017`
- Subir o container do RabbitMQ nas portas `5672` (AMQP) e `15672` (Management UI)
- Subir a aplicação na porta `8080`

Aguarde até ver no terminal:
```
south-app | Started SouthApplication in X seconds
```

### 3. Acesse a aplicação

- **API:** `http://localhost:8080`
- **RabbitMQ Management:** `http://localhost:15672` (usuário: `guest` / senha: `guest`)

---

## Endpoints

### Games

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/games` | Criar um novo game |
| GET | `/api/games` | Listar todos os games |
| GET | `/api/games/{id}` | Buscar game por ID |
| PUT | `/api/games/{id}` | Atualizar game |
| DELETE | `/api/games/{id}` | Deletar game |

### Registrations

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/games/{gameId}/registrations` | Registrar jogador no beta |
| GET | `/api/games/{gameId}/registrations` | Listar inscrições do game |
| GET | `/api/games/{gameId}/registrations/{id}` | Buscar inscrição por ID |
| PATCH | `/api/games/{gameId}/registrations/{id}/cancel` | Cancelar inscrição |

---

## Exemplos de uso

### Criar um game
```bash
curl -X POST http://localhost:8080/api/games \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Cyber Realms Beta",
    "description": "Acesso antecipado ao beta",
    "totalSlots": 5,
    "registrationOpen": true
  }'
```

### Registrar um jogador
```bash
curl -X POST http://localhost:8080/api/games/{gameId}/registrations \
  -H "Content-Type: application/json" \
  -d '{
    "playerName": "Fylip",
    "email": "fylip@email.com"
  }'
```

### Consultar status da inscrição
```bash
curl http://localhost:8080/api/games/{gameId}/registrations/{registrationId}
```

### Cancelar inscrição
```bash
curl -X PATCH http://localhost:8080/api/games/{gameId}/registrations/{registrationId}/cancel
```

---

## Status de inscrição

| Status | Descrição |
|--------|-----------|
| `PENDING` | Inscrição recebida, aguardando processamento |
| `CONFIRMED` | Vaga confirmada |
| `WAITLISTED` | Sem vagas disponíveis, na fila de espera |
| `CANCELLED` | Inscrição cancelada |

---

## Testes

### Testes unitários
```bash
./mvnw test -Dtest="GameServiceTest,RegistrationServiceTest"
```

### Testes de integração

Requer Docker Desktop em execução.
```bash
./mvnw test -Dtest="RegistrationIntegrationTest"
```

### Todos os testes
```bash
./mvnw test
```

---

## Parar os containers
```bash
docker compose down
```

Para remover também os volumes de dados:
```bash
docker compose down -v
```