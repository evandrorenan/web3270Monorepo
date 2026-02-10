# Plan: Transformar Web3270 em Aplicação Corporativa

## Resumo Executivo
Este aplicativo Spring Boot 3.4 fornece acesso a terminais 3270 via WebSocket com integração STOMP. A análise revela múltiplas lacunas críticas em segurança, tratamento de erros, logging estruturado, testes, documentação e práticas de design. Será necessário implementar melhorias em 5 categorias principais: infraestrutura de testes, padrões de design, segurança, observabilidade e qualidade de código.

## Problemas Críticos Identificados

### 1. **Problemas de Segurança**
- ❌ CORS permitido para `*` (linhas 20-24 em WebSocketConfig)
- ❌ Sem autenticação/autorização em nenhum endpoint
- ❌ Sem validação de entrada em DTOs
- ❌ Sem rate limiting ou proteção contra abuso
- ❌ Credenciais de host/port em properties públicas
- ❌ Sem HTTPS/TLS enforcement
- ❌ Sem proteção contra CSRF

### 2. **Problemas de Design e Arquitetura**
- ❌ DTOs com `@Component` (anti-pattern - SessionDto.java:5)
- ❌ WorkAreaService como DTO (misturado com lógica)
- ❌ Controllers injetam diretamente serviços (tight coupling)
- ❌ Interfaces com prefixo `I` (Java Convention contrariado)
- ❌ Sem hexagonal architecture / ports & adapters
- ❌ SessionService gerencia Map manualmente (sem cache abstração)
- ❌ Hardcoded literals em múltiplos lugares

### 3. **Problemas de Tratamento de Erro**
- ❌ `ExceptionWeb3270` não herda corretamente de Exception
- ❌ Sem GlobalExceptionHandler (@ControllerAdvice)
- ❌ `ResponseStatusException` no controller (violação SoC)
- ❌ Exceções genéricas não mapeadas corretamente
- ❌ Stack traces impressos para log (linha 20-24 em ExceptionWeb3270)

### 4. **Problemas de Logging e Observabilidade**
- ❌ Mix de `Logger` estática e `@Slf4j` (SessionService.java)
- ❌ Sem correlation IDs para rastreamento distribuído
- ❌ Sem métricas (Micrometer/Spring Boot Actuator)
- ❌ Sem structured logging (JSON)
- ❌ Sem health checks customizados
- ❌ Mensagens de log inconsistentes

### 5. **Problemas de Testes**
- ❌ Apenas 2 testes de controlador existem
- ❌ Testes não cobrem serviços
- ❌ Sem testes de integração
- ❌ Sem testes de WebSocket
- ❌ Fixtures/mocks espalhados (MySessionMock)
- ❌ Sem test containers

### 6. **Problemas de Qualidade de Código**
- ❌ Métodos muito longos (SessionService.java: 229 linhas)
- ❌ Magic strings e números espalhados
- ❌ Sem constants consolidadas
- ❌ Imports desnecessários comentados
- ❌ TODO comments sem contexto (SessionController.java:55)
- ❌ Lombok sem `@Getter/@Setter` explícito

### 7. **Problemas de Configuração e DevOps**
- ❌ `pom.xml` com dependências comentadas (poluído)
- ❌ `application.properties` hardcoded (senha do host)
- ❌ Sem perfis Spring (dev/test/prod)
- ❌ Sem Docker Compose para dependências
- ❌ Sem health checks
- ❌ `Dockerfile` sem análise de segurança

### 8. **Problemas de Documentação**
- ❌ Sem README completo
- ❌ Swagger/OpenAPI configurado mas não documentado
- ❌ Sem diagramas de arquitetura
- ❌ Sem guia de contribuição
- ❌ Javadoc inconsistente

---

## Lista de Práticas Ruins

| # | Prática | Arquivo | Severidade |
|---|---------|---------|-----------|
| 1 | DTOs anotadas com `@Component` | SessionDto.java | CRÍTICA |
| 2 | Mix de Logger estática + @Slf4j | SessionService.java | ALTA |
| 3 | CORS permitido para `*` | WebSocketConfig.java | CRÍTICA |
| 4 | Sem autenticação/autorização | Controllers | CRÍTICA |
| 5 | Hardcoded literals | Múltiplos | ALTA |
| 6 | Exceção não herda corretamente | ExceptionWeb3270.java | ALTA |
| 7 | Controllers lançam ResponseStatusException | SessionController.java | ALTA |
| 8 | Map manual de sessões | SessionService.java | MÉDIA |
| 9 | Métodos sem Javadoc | Todos | MÉDIA |
| 10 | Dependências comentadas em pom.xml | pom.xml | MÉDIA |
| 11 | TODO comments contextualizados | SessionController.java | BAIXA |
| 12 | Web3270Utils com static methods | Web3270Utils.java | BAIXA |
| 13 | Testes espalhados sem padrão | test/ | ALTA |
| 14 | Sem validation em DTOs | Todos DTOs | ALTA |
| 15 | Lombok sem configuração explícita | Services | BAIXA |

---

## Erros de System Design

### 1. **Camadas Mal Definidas**
```
Problema: Controllers → Services → Pcomm Library
          (sem abstração clara)
Resultado: Código acoplado a bibliotecas IBM
```

### 2. **Estado Compartilhado Inseguro**
```
Problema: Map<String, IMySession> em memória sem sincronização
Resultado: Race conditions em ambiente multi-thread
```

### 3. **Falta de Padrão de Resiliência**
```
Problema: Sleep hardcoded (2 segundos) após conexão
Resultado: Sem retry logic, circuit breaker ou timeout estruturado
```

### 4. **Versionamento de API Ausente**
```
Problema: Endpoints /newsession, /session/{id} sem /v1
Resultado: Quebra de retrocompatibilidade ao modificar
```

### 5. **Não-separação de Concerns**
```
Problema: ScreenService faz sendKeys + getScreen
Resultado: Viola Single Responsibility Principle
```

### 6. **Cache e Gerenciamento de Recursos**
```
Problema: IMySession alocadas manualmente
Resultado: Sem lógica de eviction ou TTL
```

---

## Lista de Melhorias Necessárias

### **Fase 1: Foundation (Testes + Infraestrutura)**
1. ✅ Criar estrutura de testes unitários completa (Target: 80% coverage)
2. ✅ Implementar teste de integração base
3. ✅ Setup de TestContainers para Pcomm mock
4. ✅ Criar fixtures e builders para testes

### **Fase 2: Arquitetura**
5. ✅ Implementar Hexagonal Architecture (ports & adapters)
6. ✅ Criar camada de Domain (entities, value objects)
7. ✅ Implementar camada de Application (use cases)
8. ✅ Criar adapters para externa (Pcomm, HTTP)
9. ✅ Remover anotações incorretas de DTOs
10. ✅ Criar constants consolidadas

### **Fase 3: Segurança**
11. ✅ Implementar Spring Security com JWT
12. ✅ Validação de entrada (Jakarta Validation)
13. ✅ Proteção CORS restritiva
14. ✅ Rate limiting (Bucket4j)
15. ✅ HTTPS enforcement
16. ✅ Criptografia de credenciais em properties

### **Fase 4: Tratamento de Erro**
17. ✅ Implementar GlobalExceptionHandler
18. ✅ Criar enums de ErrorCode estruturados
19. ✅ Restruturar ExceptionWeb3270
20. ✅ Padronizar respostas de erro (RFC 7807)

### **Fase 5: Observabilidade**
21. ✅ Implementar structured logging (SLF4J + Logback JSON)
22. ✅ Adicionar correlation IDs (MDC)
23. ✅ Implementar métricas (Micrometer)
24. ✅ Health checks customizados
25. ✅ Distributed tracing (Sleuth)

### **Fase 6: Qualidade de Código**
26. ✅ Refatorar métodos longos (SonarQube threshold)
27. ✅ Implementar lombok corretamente
28. ✅ Remover comentários poluídos
29. ✅ Adicionar Javadoc completo
30. ✅ Code formatação + checkstyle

### **Fase 7: DevOps e Configuração**
31. ✅ Multi-profile configuration (application-{profile}.yml)
32. ✅ Docker + Docker Compose
33. ✅ Kubernetes manifests (opcional)
34. ✅ Actuator endpoints seguros
35. ✅ Environment validation

### **Fase 8: Documentação**
36. ✅ README completo
37. ✅ Arquitetura (C4 diagrams)
38. ✅ Swagger/OpenAPI com exemplos
39. ✅ Guia de contribuição
40. ✅ Runbook operacional

---

## Plano de Execução (Roadmap)

```
SEMANA 1-2: Testes (Foundation)
├── Unit tests para DTOs
├── Unit tests para Services  
├── Unit tests para Controllers
├── Integration tests base
└── ✅ Target: 80% coverage + all green

SEMANA 3-4: Arquitetura
├── Criar estrutura hexagonal
├── Implementar domain layer
├── Implementar application layer
├── Refatorar controllers
└── ✅ Manter 80% coverage

SEMANA 5: Segurança
├── Spring Security + JWT
├── Validação com Jakarta
├── CORS configuration
└── ✅ Testes de segurança passando

SEMANA 6: Erro & Observabilidade
├── GlobalExceptionHandler
├── Structured logging
├── Métricas
└── ✅ Logs em produção claros

SEMANA 7: DevOps
├── Docker + Compose
├── Multi-profile config
└── ✅ Deployment pronto

SEMANA 8: Documentação
├── README + Arquitetura
└── ✅ Projeto documentado
```

---

## Prompts para Agentes

### **PROMPT 1: Implementar Testes Unitários (Foundation)**

```markdown
# Agent: Implementar Testes Unitários Completos

## Objetivo
Criar suite completa de testes unitários com 80%+ coverage antes de refatoração de código.

## Escopo
1. **DTOs**: Criar testes para serialização/desserialização
   - SessionDto, ScreenDto, UserInputDto, SendKeysDto
   - Validar constructores, getters, setters, equals/hashCode

2. **Utilities**: Testes para Web3270Utils
   - getMessage, isNumeric, substringByLength, nextWordAfter

3. **Exceptions**: Testes para ExceptionWeb3270
   - Construção, mensagem, stack trace

4. **Services (Mock)**: Testes unitários com mocks
   - SessionService.createNewSessionDto (com 3 cenários)
   - SessionService.getSession (encontrado/não encontrado)
   - ScreenService.getScreenDto (com dados válidos)
   - Sem dependências do Pcomm (mockar IMySession)

5. **Controllers**: Testes REST com MockMvc
   - POST /newsession (sucesso/erro)
   - GET /session/{id} (sucesso/not found)
   - GET /session/{id}/screen
   - POST /session/sendkeys
   - Validar HTTP status codes

## Requisitos
- Use JUnit 5 + Mockito + AssertJ
- Fixtures testFixtures()/builder pattern para criar dados
- Arquivo separado por classe testada (CamelCase + Test suffix)
- Mínimo 3 casos por método (happy path + 2 errors)
- Cobertura mínima: 80% (verificar com JaCoCo report)
- Todos os testes devem passar (GREEN)

## Estrutura de Pastas
```
src/test/java/br/com/evandrorenan/web3270/
├── controller/
│   ├── SessionControllerTest.java (expandir)
│   ├── ScreenControllerTest.java
│   ├── ReportControllerTest.java
│   └── EvtControllerTest.java
├── service/
│   ├── SessionServiceTest.java
│   ├── ScreenServiceTest.java
│   ├── ProgramReportServiceTest.java
│   └── EvtServiceTest.java
├── dto/
│   ├── SessionDtoTest.java
│   ├── ScreenDtoTest.java
│   └── ...
├── util/
│   └── Web3270UtilsTest.java
├── exception/
│   └── ExceptionWeb3270Test.java
└── fixture/
    ├── SessionDtoFixture.java
    ├── ScreenDtoFixture.java
    └── TestDataBuilder.java
```

## Entregáveis
- [ ] Todos .java test files implementados
- [ ] Relatório JaCoCo mostrando 80%+
- [ ] Todos testes VERDE (mvn clean test)
- [ ] Commits ordenados por file/component

## Notas
- Use @Spy para ServiceMock se houver lógica mínima
- Utilize ArgumentCaptor para validar chamadas
- Crie fixtures reutilizáveis
- Documente testes com @DisplayName("should_do_X_when_Y")
```

---

### **PROMPT 2: Arquitetura Hexagonal**

```markdown
# Agent: Refatorar para Arquitetura Hexagonal

## Objetivo
Reorganizar código em camadas: Domain → Application → Infrastructure + Presentation

## Estrutura Target
```
src/main/java/br/com/evandrorenan/web3270/
├── domain/                          # CORE - IMMUTABLE
│   ├── session/
│   │   ├── Session.java (entity)
│   │   ├── SessionId.java (value object)
│   │   ├── SessionProperties.java
│   │   └── SessionRepository.java (interface)
│   ├── screen/
│   │   ├── Screen.java
│   │   └── ScreenRepository.java
│   └── exception/
│       ├── DomainException.java
│       └── SessionNotFoundException.java
├── application/                      # USE CASES
│   ├── service/
│   │   ├── CreateSessionUseCase.java
│   │   ├── GetSessionScreenUseCase.java
│   │   ├── DisconnectSessionUseCase.java
│   │   └── SendKeysUseCase.java
│   └── dto/  (OUTPUT ONLY)
│       ├── SessionResponse.java
│       └── ScreenResponse.java
├── infrastructure/                   # ADAPTERS
│   ├── persistence/
│   │   ├── InMemorySessionRepository.java
│   │   └── CachedSessionRepository.java (Caffeine)
│   ├── terminal/  (Pcomm adapter)
│   │   ├── PcommTerminalAdapter.java
│   │   └── PcommConnectionFactory.java
│   └── configuration/
│       ├── HexagonalConfiguration.java
│       └── CacheConfiguration.java
└── presentation/                     # REST + WEBSOCKET
    ├── controller/
    │   ├── SessionController.java (refatorado)
    │   ├── ScreenController.java
    │   └── ReportController.java
    ├── request/  (INPUT DTOs)
    │   ├── CreateSessionRequest.java
    │   └── SendKeysRequest.java
    └── exception/
        └── GlobalExceptionHandler.java
```

## Implementação Step-by-Step

### 1. Criar Domain Layer (não depende de nada)
- [ ] SessionId (value object, imutable)
- [ ] SessionProperties (value object)
- [ ] Session (aggregate root)
- [ ] SessionRepository interface
- [ ] Exceções de domínio (DomainException)

### 2. Criar Application Layer (depende apenas de domain)
- [ ] CreateSessionUseCase
- [ ] GetSessionScreenUseCase
- [ ] DisconnectSessionUseCase
- [ ] SendKeysUseCase
- [ ] ApplicationException, command objects

### 3. Criar Infrastructure (implementa interfaces de domain)
- [ ] InMemorySessionRepository → CachedSessionRepository (Caffeine)
- [ ] PcommTerminalAdapter (implementa Terminal interface)
- [ ] Configuration beans

### 4. Refatorar Presentation
- [ ] SessionController injeta use cases (não services)
- [ ] Request/Response DTOs separadas
- [ ] GlobalExceptionHandler mapeia exceções

## Requisitos
- Nenhuma anotação Spring em domain/
- Use records para value objects
- DTO → Domain mapping explícito
- Sem transações em domain
- Use @Autowired apenas em adapters

## Validação
- Testes continuam VERDE (80%+)
- Compilação limpa (sem warnings)
- Estrutura segue padrão
```

---

### **PROMPT 3: Segurança + Validação**

```markdown
# Agent: Implementar Segurança e Validação

## Objetivo
Adicionar autenticação, autorização, validação de entrada e proteção de CORS

## Tasks

### 1. Spring Security + JWT
- [ ] Configurar WebSecurityConfig
- [ ] Criar JwtAuthenticationFilter
- [ ] Implementar JwtTokenProvider (generate/validate)
- [ ] AuthController (/auth/login, /auth/refresh)
- [ ] Proteger endpoints com @PreAuthorize
- [ ] Teste: login retorna JWT válido

### 2. Validação de Entrada
- [ ] Adicionar Jakarta Validation em DTOs
  - SessionPropertiesDto: @NotBlank @ValidHost @ValidPort
  - UserInputDto: @NotNull @NotEmpty
  - ProgramReportRequestDto: @NotBlank JobId
- [ ] Criar custom validators
  - @ValidIPAddress
  - @ValidPort (1-65535)
- [ ] GlobalExceptionHandler trata MethodArgumentNotValidException
- [ ] Teste: POST /newsession com dados inválidos → 400

### 3. CORS Seguro
- [ ] Remover .allowedOrigins("*")
- [ ] Configurar via application-{profile}.yml
  - DEV: http://localhost:3000
  - PROD: https://myapp.com
- [ ] Teste: cross-origin requests bloqueados

### 4. Rate Limiting (Bucket4j)
- [ ] Dependency: bucket4j + spring-boot-starter
- [ ] RateLimitingFilter
- [ ] 100 requests/min por IP
- [ ] Teste: 429 Too Many Requests

### 5. HTTPS
- [ ] application-prod.yml: server.ssl.enabled=true
- [ ] PEM files em environment
- [ ] Teste: HTTP redirect para HTTPS

### 6. Secrets Management
- [ ] Remover credenciais de application.properties
- [ ] Usar environment variables
- [ ] Documento: setup.md como setá-las

## Entregáveis
- [ ] pom.xml com dependências (spring-security-jwt, bucket4j)
- [ ] Todos .java files para security
- [ ] Testes de segurança passando
- [ ] application-{profile}.yml com defaults seguros
```

---

### **PROMPT 4: Observabilidade + Logging**

```markdown
# Agent: Implementar Observabilidade e Logging Estruturado

## Objetivo
Adicionar structured logging, correlation IDs, métricas e health checks

## Tasks

### 1. Structured Logging (SLF4J + Logback JSON)
- [ ] Dependency: logback-contrib + jackson
- [ ] logback-spring.xml com JSON encoder
- [ ] Remover System.out.println, printStackTrace
- [ ] Refatorar Logger duplos em SessionService
- [ ] Padrão log: logger.info("Action completed", new KV("sessionId", id), new KV("duration", ms))
- [ ] Teste: logs aparecem em JSON format

### 2. Correlation ID (MDC)
- [ ] MDCFilter adiciona UUID a cada request
- [ ] MDC.put("correlationId", uuid)
- [ ] Logback inclui correlationId em todos os logs
- [ ] WebSocket: passar correlation ID em StompSession
- [ ] Teste: GET /session/{id} logs contêm correlationId

### 3. Métricas (Micrometer)
- [ ] Dependencies: micrometer-core
- [ ] Habilitar /actuator/metrics
- [ ] Custom metrics:
  - counter: active_sessions
  - timer: request_duration
  - gauge: memory_usage
- [ ] SessionService.createNewSessionDto → @Timed
- [ ] Teste: /actuator/metrics/request.duration mostra dados

### 4. Health Checks
- [ ] /actuator/health endpoint
- [ ] PcommHealthIndicator (testa conexão com host)
- [ ] SessionRepositoryHealthIndicator (memory usage)
- [ ] Teste: /actuator/health → UP/DOWN status

### 5. Distributed Tracing (opcional: Spring Cloud Sleuth)
- [ ] Adicionar headers X-Trace-ID
- [ ] Auto-propagate em WebSocket
- [ ] Teste: rastreamento ponta-a-ponta

## Logback Configuration
```xml
<!-- logback-spring.xml com JSON encoder -->
<pattern>
{
  "timestamp": "%d{ISO_8601}",
  "level": "%level",
  "logger": "%logger",
  "thread": "%thread",
  "correlationId": "%X{correlationId}",
  "message": "%message",
  "exception": "%exception{short}"
}
</pattern>
```

## Entregáveis
- [ ] logback-spring.xml
- [ ] MDCFilter.java
- [ ] CustomHealthIndicators
- [ ] application-{profile}.yml com logging levels
```

---

### **PROMPT 5: Tratamento de Erro Global**

```markdown
# Agent: Implementar GlobalExceptionHandler e Error Responses

## Objetivo
Padronizar tratamento de erros com RFC 7807 (Problem Details)

## Structure

### 1. Error Code Enum
```java
public enum ErrorCode {
    SESSION_NOT_FOUND("S001", "Session not found"),
    INVALID_HOST("S002", "Invalid host or port"),
    CONNECTION_TIMEOUT("S003", "Connection timeout"),
    ...
}
```

### 2. Domain Exception Hierarchy
- DomainException (base)
  - SessionNotFoundException
  - InvalidSessionPropertiesException
  - TerminalConnectionException

### 3. Problem Response (RFC 7807)
```java
{
  "type": "about:blank#session_not_found",
  "title": "Session Not Found",
  "status": 404,
  "detail": "Session 'abc123' does not exist",
  "instance": "/session/abc123",
  "errorCode": "S001",
  "timestamp": "2025-02-10T15:30:00Z",
  "correlationId": "uuid-xxxx"
}
```

### 4. GlobalExceptionHandler
```java
@ControllerAdvice
class GlobalExceptionHandler {
    
    @ExceptionHandler(SessionNotFoundException.class)
    ResponseEntity<ProblemResponse> handleSessionNotFound(...) {
        // status 404, errorCode S001
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ValidationErrorResponse> handleValidation(...) {
        // status 400, lista de field errors
    }
}
```

## Tasks
- [ ] Criar ErrorCode enum com todos os erros
- [ ] Remover ExceptionWeb3270, criar exceções específicas
- [ ] Implementar GlobalExceptionHandler
- [ ] Remover ResponseStatusException dos controllers
- [ ] ProblemResponse + ValidationErrorResponse classes
- [ ] Testes: cada erro retorna HTTP status correto
- [ ] Documentar error codes em API docs

## Entregáveis
- [ ] ErrorCode.java enum
- [ ] GlobalExceptionHandler.java
- [ ] Exception classes em domain/
- [ ] ProblemResponse.java
- [ ] Testes de cada erro code
```

---

### **PROMPT 6: DevOps + Configuração**

```markdown
# Agent: Setup DevOps - Docker, Profiles, Config

## Objetivo
Preparar aplicação para múltiplos ambientes (dev/test/prod)

## Tasks

### 1. Multi-Profile Configuration
- [ ] application.yml (defaults)
- [ ] application-dev.yml (localhost, debug logging)
- [ ] application-test.yml (in-memory, mock Pcomm)
- [ ] application-prod.yml (https, reduced logging, metrics)
- [ ] Environment variable substitution para secrets

### 2. Docker
- [ ] Dockerfile otimizado
  - Multi-stage build
  - Java 21 base image
  - Non-root user
  - Health check
- [ ] .dockerignore
- [ ] docker-compose.yml (incluir redis para cache, se usado)

### 3. Configuration Externalization
- [ ] ConfigurationProperties para groups:
  - SessionConfig (host defaults, timeout)
  - SecurityConfig (jwt secret, cors origins)
  - LoggingConfig (levels)
- [ ] Validação de required properties ao startup

### 4. Actuator Security
- [ ] /actuator endpoints requer ROLE_ADMIN
- [ ] GET /health público (sem detalhes)
- [ ] POST endpoints (shutdown) disabled

## application-{profile}.yml Structure
```yaml
# application-dev.yml
logging.level.br.com.evandrorenan: DEBUG
spring.jpa.hibernate.ddl-auto: create-drop
cors.allowed-origins: http://localhost:3000
pcomm.connection-timeout-ms: 5000

# application-prod.yml
logging.level.br.com.evandrorenan: WARN
server.ssl.enabled: true
cors.allowed-origins: ${CORS_ORIGINS}
pcomm.connection-timeout-ms: 30000
```

## Entregáveis
- [ ] Dockerfile + .dockerignore
- [ ] docker-compose.yml
- [ ] application-{dev,test,prod}.yml
- [ ] ConfigurationProperties classes
- [ ] setup.md (como rodar em cada ambiente)
```

---

### **PROMPT 7: Testes de Integração**

```markdown
# Agent: Implementar Testes de Integração

## Objetivo
Criar testes de integração ponta-a-ponta com TestContainers/Mocks

## Structure

### 1. Base Test Class
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
abstract class BaseIntegrationTest {
    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;
    @Autowired MockMvc mockMvc;
}
```

### 2. Integration Tests por Feature
- [ ] SessionIntegrationTest
  - POST /newsession → criar session
  - GET /session/{id} → recuperar
  - GET /session/{id}/disconnect → desconectar
  - Validar ordem de chamadas ao Pcomm mock

- [ ] ScreenIntegrationTest
  - GET /session/{id}/screen → retorna ScreenDto
  - POST /session/sendkeys → atualiza tela
  - WebSocket /ws/sendkeys → message mapping

- [ ] SecurityIntegrationTest
  - POST /newsession sem JWT → 401
  - POST /newsession com JWT expirado → 401
  - com JWT válido → sucesso

- [ ] ValidationIntegrationTest
  - POST /newsession com host inválido → 400 + validation errors
  - POST /session/sendkeys com sessionId null → 400

### 3. Test Fixtures
- [ ] PcommMockServer (simula terminal 3270)
  - Response em camadas (login → menu → dados)
- [ ] JwtTestTokenProvider
- [ ] TestData builders

### 4. WebSocket Integration Test
```java
@SpringBootTest
class WebSocketIntegrationTest {
    void testSendKeysViaWebSocket() {
        // Conectar ao /web3270-websocket
        // Enviar message para /ws/sendkeys
        // Validar response em /queue/session
    }
}
```

## Requisitos
- Use testcontainers-junit-jupiter (opcional, para real DB se integrar)
- Mock Pcomm completamente (não chamar servidor real)
- Validar performance (requests < 1s)
- Cobertura: critical paths (sessão, tela, segurança)

## Entregáveis
- [ ] *IntegrationTest.java files
- [ ] BaseIntegrationTest abstrato
- [ ] PcommMockServer
- [ ] Todos testes VERDE
```

---

### **PROMPT 8: Documentação + Polish**

```markdown
# Agent: Documentação e Polish Final

## Objetivo
Documentação completa, Swagger/OpenAPI, README e arquitetura

## Tasks

### 1. README.md
- [ ] Como fazer build (mvn clean install)
- [ ] Como rodar localmente (docker-compose up)
- [ ] Como rodar testes (mvn test)
- [ ] Endpoints principais com curl examples
- [ ] Autores e license

### 2. Architecture Documentation
- [ ] Diagrama C4 (Context, Container, Component)
- [ ] Hexagonal Architecture diagram
- [ ] Sequence diagram: create session → send keys
- [ ] Data flow: WebSocket → Service → Pcomm

### 3. Swagger/OpenAPI
- [ ] @Operation, @Parameter em controllers
- [ ] @Schema em DTOs
- [ ] Examples para cada endpoint
- [ ] Error responses documentadas
- [ ] /swagger-ui.html funcional

### 4. API Documentation
- [ ] /docs/api.md (endpoints + curl)
- [ ] /docs/error-codes.md (todos os ErrorCodes)
- [ ] /docs/authentication.md (JWT flow)
- [ ] /docs/websocket.md (STOMP endpoints)

### 5. Developer Guide
- [ ] /docs/CONTRIBUTING.md
  - Como submeter PR
  - Code style (checkstyle)
  - Testing guidelines (80%+ coverage)
- [ ] /docs/ARCHITECTURE.md (detalhado)

### 6. Operational Runbook
- [ ] /docs/RUNBOOK.md
  - Como fazer deploy em Kubernetes
  - Health checks esperados
  - Escalabilidade (sessions em memória)
  - Monitoring (metrics/logs)

### 7. Code Cleanup
- [ ] Remover comentários poluídos de pom.xml
- [ ] Adicionar Javadoc a todas as classes públicas
- [ ] Checkstyle pass (80 char limit, etc)
- [ ] SonarQube scan (0 blocker issues)

## Entregáveis
- [ ] README.md com 1000+ caracteres
- [ ] /docs/ com 5+ arquivos
- [ ] Swagger funcional
- [ ] 0 SonarQube blockers
- [ ] 100% javadoc classes públicas
```

---

## Próximos Passos

1. **Aguardando aprovação do plano** - Revisar estrutura e prioridades
2. **Validar Requisitos** - Confirmar versão de Java, Spring, compatibilidade
3. **Setup Inicial** - Criar branches, configurar CI/CD básico
4. **Executar PROMPT 1** - Testes unitários com 80% coverage (primeira barreira)

Todos os 8 prompts seguem padrão consistente:
- **Objetivo claro**
- **Requisitos específicos**
- **Estrutura de pastas**
- **Entregáveis mensuráveis**
- **Validação de sucesso**

