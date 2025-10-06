# Tech Challenge – Backend de Agendamento Hospitalar

Este repositório entrega o backend solicitado no Tech Challenge da fase 3, estruturado em dois microserviços Spring Boot focados em segurança, comunicação assíncrona e exposição de dados via REST e GraphQL.

## Arquitetura
- **scheduling-service (porta 8080)**: gerencia consultas, aplica regras de segurança, expõe endpoints REST e GraphQL e publica eventos no RabbitMQ quando uma consulta é criada ou atualizada.
- **notification-service (porta 8081)**: consome os eventos gerados pelo agendamento, registra notificações no próprio banco e simula o envio de lembretes ao paciente.
- **RabbitMQ**: responsável pelo tráfego assíncrono (`appointments.events` / `appointments.notifications`). Um `docker-compose.yml` com RabbitMQ + console de administração foi incluído.
- **Persistência**: cada serviço utiliza banco em memória H2, simplificando a configuração local.

```
┌────────────────┐        RabbitMQ        ┌────────────────────┐
│ Scheduling API │  ───────────────►      │ Notification API   │
│  REST + GraphQL│  appointments.events   │  REST              │
└────────────────┘        queue           └────────────────────┘
```

## Segurança
Ambos os serviços usam **Spring Security** com autenticação basic. Usuários pré-configurados:

| Usuário   | Senha      | Perfis                    |
|-----------|------------|---------------------------|
| doctor1   | doctor123  | `ROLE_DOCTOR`             |
| nurse1    | nurse123   | `ROLE_NURSE`              |
| patient1  | patient123 | `ROLE_PATIENT`            |

As anotações `@PreAuthorize` restringem o acesso conforme os requisitos:
- Médicos e enfermeiros podem criar e alterar consultas, visualizar histórico completo e consultar notificações de qualquer paciente.
- Pacientes acessam apenas os próprios dados (`/api/appointments/mine`, `/api/notifications/mine`, queries GraphQL específicas).

## Endpoints Principais
### Scheduling Service (8080)
- `POST /api/appointments` – cria consulta *(doctor / nurse)*
- `PUT /api/appointments/{id}` – atualiza consulta *(doctor / nurse)*
- `GET /api/appointments/{id}` – detalhes (restrito ao responsável ou paciente)
- `GET /api/appointments?patientId=&doctorId=&status=&from=&to=` – busca avançada *(doctor / nurse)*
- `GET /api/appointments/mine?includePast=false` – consultas do paciente autenticado
- `POST /graphql` – GraphQL com queries:
  - `patientAppointments(patientId: "patient1")` *(doctor/nurse)*
  - `myUpcomingAppointments` *(patient)*
  - `doctorAppointments(doctorId: "dr-01")`
- GraphiQL disponível em `http://localhost:8080/graphiql`

### Notification Service (8081)
- `GET /api/notifications` – lista notificações com filtros *(doctor / nurse)*
- `GET /api/notifications/{id}` – detalhes (restrito ao responsável ou paciente dono)
- `GET /api/notifications/mine` – notificações do paciente autenticado

## Comunicação Assíncrona
O `scheduling-service` publica um `AppointmentNotificationPayload` sempre que uma consulta é criada ou editada. O `notification-service` escuta a fila `appointments.notifications.queue`, registra a notificação e simula o envio (log). Falhas são capturadas e sinalizadas com `NotificationStatus.FAILED`.

## Execução Local
### Pré-requisitos
- Java 17+
- Maven 3.9+
- Docker (para execução do RabbitMQ)

### Subindo o RabbitMQ
```bash
docker compose up -d
```
RabbitMQ ficará disponível em `localhost:5672` e o painel em `http://localhost:15672` (credenciais padrão `guest/guest`).

### Rodando os serviços
Em terminais distintos:
```bash
mvn -pl scheduling-service spring-boot:run
mvn -pl notification-service spring-boot:run
```

Os bancos H2 ficam acessíveis via `/h2-console` em cada serviço (`jdbc:h2:mem:schedulingdb` ou `notificationdb`).

### Build & Testes
```bash
mvn clean verify
```
> Em ambientes com diretório Maven somente leitura, a etapa pode falhar ao baixar dependências (erro de permissão em `~/.m2`). Execute o comando em uma máquina local com permissões adequadas.

## Postman Collection
A collection `postman/tech-challenge.postman_collection.json` foi adicionada com exemplos de:
- Criação e edição de consultas
- Consulta REST e GraphQL
- Visualização de notificações (global e do paciente)
Basta importar o arquivo e configurar os endpoints (`localhost`) e credenciais Basic Auth conforme perfis desejados.


