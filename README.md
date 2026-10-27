# 🅿️ API - Vagas
 
API REST responsável pelo cadastro e controle das vagas de estacionamento. O projeto faz parte de uma arquitetura composta por serviços independentes, na qual cada API possui uma responsabilidade específica dentro do sistema.
 
A **API de Vagas** é responsável por criar, consultar, atualizar e excluir vagas, além de controlar o **estado** de cada vaga (disponível, ocupada ou indisponível) e fornecer um **resumo de disponibilidade**. A **API de Reservas** utiliza esta API para validar se uma vaga pode ser reservada e para marcá-la como ocupada ou liberada no check-in e no check-out.
 
A aplicação foi desenvolvida utilizando **Java com Spring Boot**, seguindo a mesma arquitetura em camadas da API de Reservas.
 
---
 
## 🎯 Objetivos
 
O objetivo desta API é fornecer os recursos necessários para gerenciar as vagas do estacionamento e o estado atual de cada uma. Uma vaga possui:
 
- um nome (por exemplo `A10`);
- um preço por hora;
- um status;
- a data de criação.
A API também possui regras para impedir situações inválidas, como:
 
  - duas vagas com o mesmo nome;
  - editar ou excluir uma vaga que está ocupada;
  - ocupar uma vaga que não esteja disponível;
  - liberar uma vaga que não esteja ocupada.
---
 
## 🏛️ Arquitetura
 
O projeto utiliza uma arquitetura em camadas, cada uma possuindo uma responsabilidade específica.
 
```
Cliente (Aplicação ou Reserva API)
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
H2 Database
```
 
### 🎛️ Controller
 
O Controller representa a porta de entrada da API. Ele recebe as requisições HTTP, obtém os dados enviados pelo cliente, chama o Service e devolve a resposta HTTP. Exemplos:
 
```
POST   /vagas/nova-vaga
GET    /vagas/listar-vagas
PUT    /vagas/atualizar-vagas/{id}
DELETE /vagas/deletar-vagas/{id}
PATCH  /vagas/{id}/ocupar
PATCH  /vagas/{id}/liberar
GET    /vagas/resumo
```
 
### 🧠 Service
 
O Service concentra as regras de negócio da aplicação. É nessa camada que são realizadas validações como:
 
- verificar se o nome da vaga foi informado e se já não existe outra vaga com o mesmo nome;
- verificar se o preço por hora é maior que zero;
- garantir que toda vaga nasça com o status `DISPONIVEL`;
- impedir a edição e a exclusão de vagas ocupadas;
- controlar as mudanças de estado (ocupar, liberar, tornar indisponível e disponível);
- montar o resumo de disponibilidade.
### 📦 Repository
 
O Repository é responsável pela comunicação com o banco de dados. O projeto utiliza:
 
```
JpaRepository<Vaga, Long>
```
 
Com isso, operações básicas como inserir, consultar, atualizar e excluir registros são fornecidas pelo Spring Data JPA. Além disso também existem consultas específicas para:
 
```
Buscar vagas por status
Verificar se já existe uma vaga com determinado nome
Contar vagas por status
```
 
### 👤 Model
 
A classe `Vaga` representa a entidade persistida no banco de dados. Ela é mapeada para a tabela:
 
```
vagas
```
 
Principais atributos:
 
```
id
nomeVaga
precoHora
status
createdAt
```
 
O enum `StatusVaga` define os estados possíveis, e o record `ResumoVagas` representa o resultado do endpoint de resumo.
 
---
 
## 📁 Estrutura do projeto
 
A estrutura atual segue a separação por responsabilidades:
 
```
src/
└── main/
    ├── java/
    │   └── com/
    │       └── riobranco/
    │           └── x/
    │               └── vaga_api/
    │                   ├── controller/
    │                   │   └── VagaController.java
    │                   │
    │                   ├── model/
    │                   │   ├── Vaga.java
    │                   │   ├── StatusVaga.java
    │                   │   └── ResumoVagas.java
    │                   │
    │                   ├── repository/
    │                   │   └── VagaRepository.java
    │                   │
    │                   └── service/
    │                       └── VagaService.java
    │
    └── resources/
        └── application.properties
```
 
---
 
## ⚙️ Tecnologias utilizadas
 
- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- H2 Database
- Maven
- REST API
- JSON
- Postman para testes
---
 
## 🔧 Configuração
 
Arquivo `application.properties`:
 
```properties
# A API de Reservas usa a porta 8080, por isso esta usa a 8081
server.port=8081
 
# Banco H2 em memória
spring.datasource.url=jdbc:h2:mem:vagadb
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
 
# Inclui a mensagem de erro no corpo das respostas de erro
server.error.include-message=always
```
 
---
 
## 🗄️ Banco de dados
 
Durante o desenvolvimento, a aplicação utiliza o **H2 Database**, com um banco **próprio**, independente do banco da API de Reservas. A entidade `Vaga` é persistida na tabela `vagas`, o acesso ao banco é realizado através do `Spring Data JPA` e do `VagaRepository`.
 
O console do H2 fica disponível em `http://localhost:8081/h2-console`, usando a URL JDBC `jdbc:h2:mem:vagadb`.
 
> O H2 está configurado em memória: os dados são perdidos sempre que a aplicação é reiniciada.
 
---
 
## 📁 Modelo de Vaga
 
A entidade possui a seguinte estrutura conceitual:
 
```
Vaga
│
├── id
├── nomeVaga
├── precoHora
├── status
└── createdAt
```
 
### `id`
 
Identificador único da vaga. É gerado automaticamente pelo banco de dados.
 
### `nomeVaga`
 
Nome de identificação da vaga (por exemplo `A10`). Deve ser único.
 
### `precoHora`
 
Valor cobrado por hora de utilização da vaga. Deve ser maior que zero. A API de Reservas copia esse valor para a reserva no momento em que ela é criada.
 
### `status`
 
Representa o estado atual da vaga. Estados utilizados:
 
```
DISPONIVEL
OCUPADA
INDISPONIVEL
```
 
- `DISPONIVEL`: a vaga está livre e pode receber reservas e check-in.
- `OCUPADA`: há um veículo utilizando a vaga (check-in realizado).
- `INDISPONIVEL`: a vaga foi desativada pelo administrador (por exemplo, para manutenção) e não pode ser reservada.
Toda vaga nasce `DISPONIVEL`. As transições permitidas são:
 
```
DISPONIVEL ── ocupar ──────────► OCUPADA
    ▲                               │
    └───────────── liberar ─────────┘
 
DISPONIVEL ── indisponivel ────► INDISPONIVEL
    ▲                               │
    └───────────── disponivel ──────┘
```
 
| Ação | Estado exigido | Novo estado | Se violar a regra |
| ---- | -------------- | ----------- | ----------------- |
| ocupar | `DISPONIVEL` | `OCUPADA` | 409 |
| liberar | `OCUPADA` | `DISPONIVEL` | 409 |
| indisponivel | qualquer, exceto `OCUPADA` | `INDISPONIVEL` | 409 |
| disponivel | qualquer, exceto `OCUPADA` | `DISPONIVEL` | 409 |
| editar | qualquer, exceto `OCUPADA` | não muda | 409 |
| excluir | qualquer, exceto `OCUPADA` | vaga removida | 409 |
 
> O `status` da vaga representa o estado **atual** (físico). O conflito de horários entre reservas futuras é uma regra da API de Reservas.
 
### `createdAt`
 
Data e hora em que a vaga foi cadastrada, gerada automaticamente.
 
---
 
## 🔗 Integração com a API de Reservas
 
A API de Vagas **não chama** nenhuma outra API. Quem a utiliza é a API de Reservas:
 
| Momento na Reserva | Endpoint utilizado desta API |
| ------------------ | ---------------------------- |
| Criar reserva | `GET /vagas/listar-vagas/{id}`, para verificar se a vaga existe e está `DISPONIVEL` e obter `nomeVaga` e `precoHora` |
| Check-in | `PATCH /vagas/{id}/ocupar` |
| Check-out | `PATCH /vagas/{id}/liberar` |
 
Os demais endpoints (cadastro, edição, exclusão, desativação e resumo) são destinados ao administrador, pela aplicação web ou mobile.
 
---
 
## 📌 Endpoints
 
| Método | Endpoint                         | Função                          |
| ------ | -------------------------------- | ------------------------------- |
| POST   | `/vagas/nova-vaga`               | Criar vaga                      |
| GET    | `/vagas/listar-vagas`            | Listar vagas                    |
| GET    | `/vagas/listar-vagas/{id}`       | Buscar vaga                     |
| PUT    | `/vagas/atualizar-vagas/{id}`    | Atualizar vaga                  |
| DELETE | `/vagas/deletar-vagas/{id}`      | Excluir vaga                    |
| PATCH  | `/vagas/{id}/ocupar`             | Marcar vaga como ocupada        |
| PATCH  | `/vagas/{id}/liberar`            | Liberar vaga ocupada            |
| PATCH  | `/vagas/{id}/indisponivel`       | Desativar vaga                  |
| PATCH  | `/vagas/{id}/disponivel`         | Reativar vaga                   |
| GET    | `/vagas/status/{status}`         | Buscar por status               |
| GET    | `/vagas/resumo`                  | Resumo de disponibilidade       |
 
---
 
## 🏦 Arquitetura do sistema
 
A API de Vagas faz parte de um sistema com serviços independentes:
 
```
            ┌──────────────────┐
            │    Aplicação     │
            │  Web / Mobile    │
            └────────┬─────────┘
                     │
      ┌──────────────┼──────────────┐
      │              │              │
      ▼              ▼              ▼
┌───────────┐  ┌─────────────┐  ┌───────────┐
│ User API  │  │ Reserva API │  │ Vaga API  │
│ (futura)  │  │             │  │           │
│           │  │ Reservas    │  │ Cadastro  │
│ Usuários  │  │ Check-in    │  │ Estado    │
│ Login     │  │ Check-out   │  │ Resumo    │
│ Roles     │  │ Cancelamento│  │           │
└───────────┘  └──────┬──────┘  └─────▲─────┘
                      │               │
                      └───────────────┘
                     HTTP (RestClient)
```
 
- A **User API** será responsável por usuários, autenticação e identidade.
- A **Reserva API** é responsável pelo ciclo de vida das reservas.
- A **Vaga API** é responsável pelo gerenciamento e disponibilidade das vagas.
---
 
## ⭐ Princípios adotados
 
**Separação de responsabilidades:** cada camada possui uma função específica.
 
**Baixo acoplamento:** Controller, Service e Repository possuem responsabilidades separadas, e esta API não conhece detalhes da API de Reservas.
 
**Reutilização:** o Spring Data JPA fornece operações comuns de persistência, evitando a implementação manual de consultas básicas.
 
**Regra de negócio centralizada:** as decisões relacionadas às vagas e às suas mudanças de estado ficam no Service.
 
**Fonte única da verdade:** nome, preço e estado de uma vaga pertencem a esta API. Os demais serviços consultam esses dados aqui.
 
**Arquitetura orientada a serviços:** usuários, reservas e vagas são organizados em APIs independentes, cada uma com seu banco.
 
**Evolução incremental:** funcionalidades mais complexas, como segurança, serão adicionadas gradualmente.
 
---
 
## 🚀 Executando o projeto
 
Pré-requisitos: JDK 17+ e Maven (ou o wrapper `mvnw` do projeto).
 
```
./mvnw spring-boot:run          (Linux / macOS)
mvnw.cmd spring-boot:run        (Windows)
```
 
A API sobe em `http://localhost:8081`.
 
---
 
## 🧪 Testando com Postman
 
A API pode ser testada utilizando o Postman. Com a aplicação executando localmente: `http://localhost:8081`
 
### POST - Criar uma vaga
 
O endpoint permite que o administrador cadastre uma vaga. O `status` não deve ser enviado: toda vaga nasce `DISPONIVEL`.
 
```
http://localhost:8081/vagas/nova-vaga
```
 
Body:
 
```
{
    "nomeVaga": "A10",
    "precoHora": 10.00
}
```
 
Exemplo de resposta retornado pela API:
 
```
{
    "createdAt": "2026-10-27T03:19:16.2266731",
    "id": 1,
    "nomeVaga": "A10",
    "precoHora": 10.00,
    "status": "DISPONIVEL"
}
```
 
Se já existir uma vaga com o mesmo nome, a API responde com **409**.
 
### GET - Listar todas as vagas
 
Lista todas as vagas cadastradas.
 
```
http://localhost:8081/vagas/listar-vagas
```
 
Exemplo de resposta retornado pela API:
 
```
[
    {
        "createdAt": "2026-10-27T03:19:16.2266731",
        "id": 1,
        "nomeVaga": "A10",
        "precoHora": 10.00,
        "status": "DISPONIVEL"
    },
    {
        "createdAt": "2026-10-27T03:25:40.8812094",
        "id": 2,
        "nomeVaga": "A11",
        "precoHora": 12.50,
        "status": "OCUPADA"
    }
]
```
 
### GET - Buscar uma vaga pelo ID
 
Permite buscar uma vaga por id. É o endpoint que a API de Reservas utiliza para validar a vaga antes de criar uma reserva.
 
```
http://localhost:8081/vagas/listar-vagas/1
```
 
Se o id não existir, a API responde com **404**.
 
### PUT - Atualizar uma vaga
 
Permite alterar o nome e o preço por hora de uma vaga. Não é possível editar uma vaga `OCUPADA`. O `status` não é alterado por este endpoint.
 
```
http://localhost:8081/vagas/atualizar-vagas/1
```
 
Body:
 
```
{
    "nomeVaga": "A10",
    "precoHora": 12.00
}
```
 
> Alterar o preço da vaga **não** afeta reservas já criadas, pois cada reserva guarda o preço vigente no momento em que foi feita.
 
### DELETE
 
A exclusão depende do status da vaga. Nesse caso:
 
- DISPONIVEL ☑️ pode deletar
- INDISPONIVEL ☑️ pode deletar
- OCUPADA ✖️ não pode deletar
```
http://localhost:8081/vagas/deletar-vagas/1
```
 
Em caso de sucesso, a API responde com **204 No Content**.
 
### PATCH - Ocupar uma vaga
 
Marca a vaga como `OCUPADA`. Só funciona se a vaga estiver `DISPONIVEL`. É chamado pela API de Reservas no check-in.
 
```
PATCH http://localhost:8081/vagas/1/ocupar
```
 
Body:
 
```
Não precisa enviar JSON. O Body fica vazio.
```
 
Exemplo de resposta retornado pela API:
 
```
{
    "createdAt": "2026-10-27T03:19:16.2266731",
    "id": 1,
    "nomeVaga": "A10",
    "precoHora": 10.00,
    "status": "OCUPADA"
}
```
 
Se a vaga não estiver disponível, a API responde com **409**:
 
```
{
    "timestamp": "2026-10-27T16:55:01.123+00:00",
    "status": 409,
    "error": "Conflict",
    "message": "Vaga não está disponível (status atual: OCUPADA)",
    "path": "/vagas/1/ocupar"
}
```
 
### PATCH - Liberar uma vaga
 
Devolve a vaga ao estado `DISPONIVEL`. Só funciona se a vaga estiver `OCUPADA`. É chamado pela API de Reservas no check-out.
 
```
PATCH http://localhost:8081/vagas/1/liberar
```
 
Body:
 
```
Não precisa enviar JSON. O Body fica vazio.
```
 
### PATCH - Desativar e reativar uma vaga
 
Permite ao administrador tirar uma vaga de circulação (por exemplo, para manutenção) e colocá-la de volta. Nenhuma das duas ações é permitida em uma vaga `OCUPADA`.
 
```
PATCH http://localhost:8081/vagas/1/indisponivel
PATCH http://localhost:8081/vagas/1/disponivel
```
 
Body:
 
```
Não precisa enviar JSON. O Body fica vazio.
```
 
### GET - Buscar vagas por status
 
Lista todas as vagas com base no status. Valores aceitos: `DISPONIVEL`, `OCUPADA` e `INDISPONIVEL`.
 
Permite listar as vagas livres
 
```
http://localhost:8081/vagas/status/DISPONIVEL
```
 
Permite listar as vagas em uso
 
```
http://localhost:8081/vagas/status/OCUPADA
```
 
Permite listar as vagas desativadas
 
```
http://localhost:8081/vagas/status/INDISPONIVEL
```
 
Um status inexistente retorna **400**.
 
### GET - Resumo de disponibilidade
 
Retorna os totais de vagas por status. É o endpoint que alimenta os painéis do administrador e do cliente.
 
```
http://localhost:8081/vagas/resumo
```
 
Exemplo de resposta retornado pela API:
 
```
{
    "total": 3,
    "disponiveis": 2,
    "ocupadas": 1,
    "indisponiveis": 0
}
```
 
---
 
## 🚦 Respostas de erro
 
| Código | Quando acontece |
| ------ | --------------- |
| 400 Bad Request | Nome ausente, preço menor ou igual a zero, ou status inexistente na busca por status |
| 404 Not Found | Vaga não encontrada |
| 409 Conflict | Nome de vaga já existente, ou ação não permitida para o estado atual da vaga (editar/excluir/desativar vaga ocupada, ocupar vaga que não está disponível, liberar vaga que não está ocupada) |
 
---
 
## 🧭 Fluxo completo de teste
 
1. `POST /vagas/nova-vaga` com `A10` e `precoHora` 10.00. O status vem `DISPONIVEL`.
2. `GET /vagas/status/DISPONIVEL`. A vaga A10 aparece na lista.
3. `PATCH /vagas/1/ocupar`. O status vira `OCUPADA`.
4. `PATCH /vagas/1/ocupar` de novo. Deve retornar **409**.
5. `PUT /vagas/atualizar-vagas/1`. Deve retornar **409**, pois a vaga está ocupada.
6. `GET /vagas/resumo`. Mostra 1 vaga ocupada.
7. `PATCH /vagas/1/liberar`. A vaga volta para `DISPONIVEL`.
Para testar a integração completa com reservas, veja o fluxo descrito no README da API de Reservas.
 
---
 
## 🔮 Próximos passos e limitações conhecidas
 
- **Exclusão e desativação com reservas pendentes:** esta API não consulta a API de Reservas. Uma vaga com reservas `PENDENTE` ainda pode ser excluída ou desativada.
- **Autenticação e autorização:** ainda não implementadas. Hoje qualquer cliente pode chamar os endpoints de administração, que dependem da futura User API.
- **Banco de dados:** o H2 em memória deve ser substituído por um banco persistente (por exemplo, PostgreSQL).