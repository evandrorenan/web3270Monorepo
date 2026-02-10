# Web3270: Tarefas de Implementação Prontas para Agentes

**Data de Criação:** 2026-02-10  
**Status:** Pronto para Execução Sequencial  
**Ordem Recomendada:** PROMPT 1 → PROMPT 2 → ... → PROMPT 8

---

## 📋 Índice de Tarefas

1. [PROMPT 1: Testes Unitários (Foundation)](#prompt-1)
2. [PROMPT 2: Arquitetura Hexagonal](#prompt-2)
3. [PROMPT 3: Segurança + Validação](#prompt-3)
4. [PROMPT 4: Observabilidade + Logging](#prompt-4)
5. [PROMPT 5: Tratamento de Erro Global](#prompt-5)
6. [PROMPT 6: DevOps + Configuração](#prompt-6)
7. [PROMPT 7: Testes de Integração](#prompt-7)
8. [PROMPT 8: Documentação + Polish](#prompt-8)

---

<a id="prompt-1"></a>
## PROMPT 1: Implementar Testes Unitários (Foundation)

### Comando para Executar
```bash
Você é um agente especializado em testes Java. Implemente testes unitários completos 
para o projeto Web3270 seguindo a especificação abaixo:
```

### Especificação Completa

#### Objetivo
Criar suite completa de testes unitários com 80%+ coverage **antes de refatoração de código**.

#### Escopo Detalhado

**1. DTOs - Testes de Serialização/Desserialização**
- [ ] SessionDtoTest.java (construtores, getters/setters, equals, hashCode, serialization)
- [ ] ScreenDtoTest.java
- [ ] UserInputDtoTest.java
- [ ] SendKeysDtoTest.java
- [ ] SessionPropertiesDtoTest.java
- [ ] Validar padrão Lombok (@Data) funciona corretamente

**2. Utilities - Testes para Web3270Utils**
- [ ] Web3270UtilsTest.java com casos:
  - `getMessage()` - com locale válido e inválido
  - `isNumeric()` - números, strings mistas, vazias
  - `substringByLength()` - dentro/fora de limites
  - `nextWordAfter()` - palavra encontrada/não encontrada

**3. Exceptions - Testes para ExceptionWeb3270**
- [ ] ExceptionWeb3270Test.java:
  - Construção com null exception
  - Construção com válida exception
  - toString() formata corretamente
  - Logger captura stack trace

**4. Services (Mock) - Testes Unitários com Mocks**
- [ ] SessionServiceTest.java:
  - `createNewSessionDto()` - sucesso, timeout, erro de conexão
  - `getSession()` - sessão encontrada, não encontrada
  - `disconnect()` - remove do map
  - `getSessionDto()` - null input, válido
  - Mock IMySession, IScreenService, SimpMessagingTemplate

- [ ] ScreenServiceTest.java:
  - `getScreenDto()` - com dados válidos
  - `getScreenFields()` - retorna campos
  - `sendKeys()` - múltiplas teclas, função key
  - `sendKeysAsync()` - executa async

**5. Controllers - Testes REST com MockMvc**
- [ ] SessionControllerTest.java (expandir):
  - POST /newsession - sucesso (200), erro (503)
  - GET /session/{id} - sucesso (200), not found (404)
  - GET /session/{id}/screen - retorna ScreenDto
  - GET /session/{id}/disconnect - retorna disconnect status
  - POST /session/sendkeys - sucesso, session not found

- [ ] ScreenControllerTest.java:
  - @MessageMapping("/sendkeys") - validar binding
  - Mock IScreenService e ISessionService

- [ ] ReportControllerTest.java:
  - POST /programreport - com 3 overloads
  - POST /compilationreport
  - POST /baselocators

- [ ] EvtControllerTest.java:
  - GET /sysout/html/{opcao}/{jobId}
  - GET /sysout/html/{evt}/{opcao}/{jobId}
  - GET /sysout/txt/{evt}/{opcao}/{jobId}

#### Requisitos Técnicos

**Dependencies**
```xml
<!-- Já incluídos em pom.xml -->
- JUnit 5
- Mockito
- Spring Boot Test
- REST Assured MockMvc
```

**Padrões a Seguir**
- Use `@DisplayName("should_X_when_Y")` para cada teste
- Arrange-Act-Assert padrão
- Use fixtures/builders para criar dados de teste
- Use `ArgumentCaptor` para validar chamadas
- Use `@Spy` com cuidado (preferir `@Mock`)
- Testes isolados (sem dependências de teste)

**Estrutura de Pastas**
```
src/test/java/br/com/evandrorenan/web3270/
├── controller/
│   ├── SessionControllerTest.java ← EXPANDIR
│   ├── ScreenControllerTest.java ← CRIAR
│   ├── ReportControllerTest.java ← CRIAR
│   └── EvtControllerTest.java ← CRIAR
├── service/
│   ├── SessionServiceTest.java ← CRIAR
│   ├── ScreenServiceTest.java ← CRIAR
│   ├── ProgramReportServiceTest.java ← CRIAR
│   └── EvtServiceTest.java ← CRIAR
├── dto/
│   ├── SessionDtoTest.java ← CRIAR
│   ├── ScreenDtoTest.java ← CRIAR
│   ├── UserInputDtoTest.java ← CRIAR
│   ├── SendKeysDtoTest.java ← CRIAR
│   └── SessionPropertiesDtoTest.java ← CRIAR
├── util/
│   └── Web3270UtilsTest.java ← CRIAR
├── exception/
│   └── ExceptionWeb3270Test.java ← CRIAR
└── fixture/
    ├── SessionDtoFixture.java ← CRIAR (builder)
    ├── ScreenDtoFixture.java ← CRIAR
    ├── UserInputDtoFixture.java ← CRIAR
    └── TestDataBuilder.java ← CRIAR (central)
```

#### Validação de Sucesso
- [ ] `mvn clean test` executa sem erros
- [ ] JaCoCo report mostra **80%+ coverage** (gerar com `mvn clean test jacoco:report`)
- [ ] Report HTML em `target/site/jacoco/index.html`
- [ ] Todos testes marcados com ✅ (VERDE)
- [ ] Sem avisos de compilação (warnings)

#### Entregáveis
1. Arquivos .java de teste (13 arquivos)
2. Arquivos de fixture (3 arquivos)
3. Relatório JaCoCo em HTML
4. Commit message: `test(unit): implement 80% coverage for DTOs, Services, Controllers`

#### Dicas para Sucesso
1. Comece pelos DTOs (mais simples)
2. Depois Services (com mocks)
3. Depois Controllers (com MockMvc)
4. Use `@BeforeEach` para setup comum
5. Considere usar `@ParameterizedTest` para múltiplos casos
6. Mock **apenas** dependências externas (não cascade mocks)

---

<a id="prompt-2"></a>
## PROMPT 2: Arquitetura Hexagonal

### Comando para Executar
```bash
Você é um arquiteto Java experiente em Hexagonal Architecture. Refatore o projeto 
Web3270 para seguir ports & adapters pattern conforme especificação abaixo:
```

### Especificação Completa

#### Objetivo
Reorganizar código em camadas: Domain → Application → Infrastructure + Presentation

#### Estrutura Target Final
```
src/main/java/br/com/evandrorenan/web3270/
├── domain/                          # ← CORE (ZERO Spring, IMMUTABLE)
│   ├── session/
│   │   ├── Session.java (aggregate root)
│   │   ├── SessionId.java (value object)
│   │   ├── SessionProperties.java (value object)
│   │   ├── SessionStatus.java (enum)
│   │   ├── port/
│   │   │   ├── SessionRepository.java (interface)
│   │   │   └── TerminalConnection.java (interface)
│   │   └── exception/
│   │       ├── DomainException.java
│   │       ├── SessionNotFoundException.java
│   │       └── InvalidSessionPropertiesException.java
│   ├── screen/
│   │   ├── Screen.java
│   │   ├── ScreenField.java
│   │   ├── port/
│   │   │   └── ScreenRepository.java
│   │   └── exception/
│   │       └── ScreenException.java
│   └── shared/
│       ├── exception/
│       │   └── DomainException.java
│       └── value/
│           └── Identifier.java (base value object)
├── application/                      # ← USE CASES (Service layer)
│   ├── port/
│   │   ├── CreateSessionRequest.java
│   │   ├── GetSessionScreenRequest.java
│   │   └── SendKeysRequest.java
│   ├── usecase/
│   │   ├── CreateSessionUseCase.java
│   │   ├── GetSessionScreenUseCase.java
│   │   ├── DisconnectSessionUseCase.java
│   │   └── SendKeysUseCase.java
│   ├── service/
│   │   └── ApplicationException.java
│   └── dto/ (OUTPUT ONLY)
│       ├── SessionResponse.java
│       ├── ScreenResponse.java
│       └── SendKeysResponse.java
├── infrastructure/                   # ← ADAPTERS (Implementações)
│   ├── persistence/
│   │   ├── InMemorySessionRepository.java
│   │   └── CachedSessionRepository.java (Caffeine cache)
│   ├── terminal/
│   │   ├── PcommTerminalAdapter.java (implementa TerminalConnection)
│   │   └── PcommConnectionFactory.java
│   ├── web/
│   │   └── WebSocketAdapter.java
│   └── configuration/
│       ├── HexagonalConfiguration.java
│       ├── CacheConfiguration.java
│       └── PcommConfiguration.java
└── presentation/                     # ← REST + WEBSOCKET
    ├── api/
    │   └── v1/ ← VERSÃO DA API
    │       ├── controller/
    │       │   ├── SessionController.java (refatorado)
    │       │   ├── ScreenController.java
    │       │   ├── ReportController.java
    │       │   └── EvtController.java
    │       ├── request/ (INPUT DTOs)
    │       │   ├── CreateSessionRequest.java
    │       │   ├── SendKeysRequest.java
    │       │   └── ProgramReportRequest.java
    │       └── response/ (OUTPUT DTOs)
    │           ├── SessionResponse.java
    │           ├── ScreenResponse.java
    │           └── ErrorResponse.java
    ├── websocket/
    │   └── ScreenWebSocketController.java
    └── exception/
        └── GlobalExceptionHandler.java

src/test/java/br/com/evandrorenan/web3270/
├── domain/
│   └── session/
│       ├── SessionTest.java
│       └── SessionIdTest.java
├── application/
│   └── usecase/
│       ├── CreateSessionUseCaseTest.java
│       └── ...
└── infrastructure/
    └── persistence/
        ├── InMemorySessionRepositoryTest.java
        └── CachedSessionRepositoryTest.java
```

#### Implementação Passo-a-Passo

**PASSO 1: Criar Domain Layer (Pré-requisito)**

Não depende de nada! (Zero Spring, Zero DB, Zero HTTP)

1.1. Value Objects e Identifiers
```java
// domain/shared/value/Identifier.java
public abstract record Identifier<T>(T value) {}

// domain/session/SessionId.java (imutable record)
public record SessionId(String value) extends Identifier<String> {}

// domain/session/SessionProperties.java
public record SessionProperties(
    String host,
    String port,
    String type,
    String codePage
) {}
```

1.2. Aggregate Root
```java
// domain/session/Session.java
public class Session {
    private final SessionId id;
    private final SessionProperties properties;
    private final SessionStatus status;
    private final LocalDateTime createdAt;
    
    public Session(SessionId id, SessionProperties props) { ... }
    
    public void connect() { ... }
    public void disconnect() { ... }
    public boolean isConnected() { ... }
}
```

1.3. Ports (Interfaces - contratam adapters)
```java
// domain/session/port/SessionRepository.java
public interface SessionRepository {
    void save(Session session);
    Session findById(SessionId id);
    void delete(SessionId id);
    List<Session> findAll();
}

// domain/session/port/TerminalConnection.java
public interface TerminalConnection {
    void connect(SessionProperties props);
    void disconnect();
    boolean isConnected();
    String getScreen();
    void sendKeys(String keys);
}
```

1.4. Domain Exceptions
```java
// domain/session/exception/SessionNotFoundException.java
public class SessionNotFoundException extends DomainException {
    public SessionNotFoundException(SessionId id) {
        super(String.format("Session %s not found", id.value()));
    }
}
```

**PASSO 2: Criar Application Layer**

Depende apenas de domain. Orquestra use cases.

2.1. Requests (Commands)
```java
// application/port/CreateSessionRequest.java
public record CreateSessionRequest(
    String host,
    String port,
    String type,
    String codePage
) {}
```

2.2. Use Cases
```java
// application/usecase/CreateSessionUseCase.java
@Component
public class CreateSessionUseCase {
    private final SessionRepository repository;
    private final TerminalConnection connection;
    
    public SessionResponse execute(CreateSessionRequest request) {
        SessionProperties props = mapRequest(request);
        Session session = new Session(SessionId.random(), props);
        
        try {
            connection.connect(props);
            repository.save(session);
            return SessionResponse.from(session);
        } catch (Exception e) {
            throw new ApplicationException("Failed to create session", e);
        }
    }
}
```

2.3. Output DTOs (Responses)
```java
// application/dto/SessionResponse.java
public record SessionResponse(
    String sessionId,
    boolean connected,
    LocalDateTime createdAt
) {
    public static SessionResponse from(Session session) {
        return new SessionResponse(...);
    }
}
```

**PASSO 3: Criar Infrastructure Layer**

Implementa ports. Depende de domain + libs externas.

3.1. Persistence Adapter
```java
// infrastructure/persistence/InMemorySessionRepository.java
@Component
public class InMemorySessionRepository implements SessionRepository {
    private final Map<SessionId, Session> store = new ConcurrentHashMap<>();
    
    @Override
    public void save(Session session) {
        store.put(session.getId(), session);
    }
}

// infrastructure/persistence/CachedSessionRepository.java
@Component
public class CachedSessionRepository implements SessionRepository {
    private final LoadingCache<SessionId, Session> cache = 
        Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(key -> loadFromStore(key));
}
```

3.2. Terminal Adapter (Pcomm)
```java
// infrastructure/terminal/PcommTerminalAdapter.java
@Component
public class PcommTerminalAdapter implements TerminalConnection {
    private final Properties connectionProps;
    private MyPcommSession session;
    
    @Override
    public void connect(SessionProperties props) {
        this.connectionProps = mapProperties(props);
        this.session = new MyPcommSession(connectionProps, ...);
        this.session.connect();
    }
}
```

3.3. Configuration
```java
// infrastructure/configuration/HexagonalConfiguration.java
@Configuration
public class HexagonalConfiguration {
    
    @Bean
    public SessionRepository sessionRepository() {
        return new CachedSessionRepository();
    }
    
    @Bean
    public TerminalConnection terminalConnection() {
        return new PcommTerminalAdapter();
    }
}
```

**PASSO 4: Refatorar Presentation Layer**

Controllers injetam **Use Cases** (não services).

4.1. Novo SessionController
```java
// presentation/api/v1/controller/SessionController.java
@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {
    private final CreateSessionUseCase createSession;
    private final GetSessionScreenUseCase getScreen;
    private final DisconnectSessionUseCase disconnect;
    
    @PostMapping
    public ResponseEntity<SessionResponse> createSession(
        @RequestBody CreateSessionRequest request) {
        return ResponseEntity.ok(createSession.execute(request));
    }
    
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(
        @PathVariable String sessionId) {
        return ResponseEntity.ok(getSessionUseCase.execute(
            new GetSessionRequest(sessionId)));
    }
}
```

#### Requisitos de Qualidade

- [ ] **Zero Spring anotations em domain/**
- [ ] **Records para value objects** (imutable)
- [ ] **DTOs de input separadas de output**
- [ ] **Cada use case em arquivo separado**
- [ ] **Sem transações em domain** (@Transactional em application)
- [ ] **Testes de domain rápidos** (sem Spring)
- [ ] **Testes de infrastructure com Spring** (@SpringBootTest)

#### Validação de Sucesso
- [ ] Projeto compila sem warnings
- [ ] Testes unitários ainda passam (80%+ coverage)
- [ ] Controllers injetam apenas use cases
- [ ] Domain layer sem imports Spring
- [ ] Estrutura de pastas segue padrão

#### Entregáveis
1. Refatoração completa de código (15+ arquivos)
2. Novos testes de domain (5+ arquivos)
3. Testes de integration atualizados
4. Commit message: `refactor(arch): implement hexagonal architecture`

---

<a id="prompt-3"></a>
## PROMPT 3: Segurança + Validação

### Comando para Executar
```bash
Você é especialista em segurança Spring. Implemente Spring Security + Validação 
conforme especificação abaixo:
```

### Especificação Completa

#### Objetivo
Adicionar autenticação JWT, autorização, validação de entrada e proteção de CORS

#### Tasks Detalhadas

**TASK 1: Spring Security + JWT**

1.1. Dependências
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

1.2. JWT Token Provider
```java
// presentation/security/JwtTokenProvider.java
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    public String generateToken(String username) {
        return Jwts.builder()
            .subject(username)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }
    
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

1.3. JWT Authentication Filter
```java
// presentation/security/JwtAuthenticationFilter.java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired private JwtTokenProvider tokenProvider;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response,
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        String token = extractToken(request);
        if (token != null && tokenProvider.validateToken(token)) {
            String username = tokenProvider.extractUsername(token);
            UsernamePasswordAuthenticationToken auth = 
                new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

1.4. Auth Controller
```java
// presentation/api/v1/controller/AuthController.java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired private JwtTokenProvider tokenProvider;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // Validate credentials (implement user authentication)
        String token = tokenProvider.generateToken(request.username());
        return ResponseEntity.ok(new LoginResponse(token));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request) {
        String token = extractToken(request);
        if (tokenProvider.validateToken(token)) {
            String username = tokenProvider.extractUsername(token);
            String newToken = tokenProvider.generateToken(username);
            return ResponseEntity.ok(new LoginResponse(newToken));
        }
        throw new UnauthorizedException("Invalid token");
    }
}
```

1.5. WebSecurityConfig
```java
// presentation/configuration/WebSecurityConfig.java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired private JwtAuthenticationFilter jwtFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        return http.build();
    }
}
```

**TASK 2: Jakarta Validation**

2.1. Adicionar Dependência
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

2.2. Request DTOs com Validação
```java
// presentation/api/v1/request/CreateSessionRequest.java
public record CreateSessionRequest(
    @NotBlank(message = "Host is required")
    @ValidIPAddress(message = "Invalid IP address")
    String host,
    
    @NotBlank(message = "Port is required")
    @ValidPort(message = "Port must be between 1 and 65535")
    String port
) {}
```

2.3. Custom Validators
```java
// presentation/validation/ValidIPAddressValidator.java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IPAddressValidator.class)
public @interface ValidIPAddress {
    String message() default "Invalid IP address";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class IPAddressValidator implements ConstraintValidator<ValidIPAddress, String> {
    private static final Pattern IP_PATTERN = 
        Pattern.compile("^([0-9]{1,3}\\.){3}[0-9]{1,3}$");
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return IP_PATTERN.matcher(value).matches();
    }
}

// presentation/validation/ValidPortValidator.java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PortValidator.class)
public @interface ValidPort {
    String message() default "Port must be between 1 and 65535";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class PortValidator implements ConstraintValidator<ValidPort, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        try {
            int port = Integer.parseInt(value);
            return port >= 1 && port <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
```

2.4. Controllers com @Valid
```java
@PostMapping
public ResponseEntity<SessionResponse> create(
    @Valid @RequestBody CreateSessionRequest request) {
    return ResponseEntity.ok(createSessionUseCase.execute(request));
}
```

**TASK 3: CORS Seguro**

3.1. Remove WebSocketConfig CORS aberto
```java
// ANTES (inseguro)
registry.addMapping("/**")
    .allowedOrigins("*")  // ❌ REMOVIDO

// DEPOIS (seguro)
registry.addMapping("/**")
    .allowedOrigins(corsProperties.getAllowedOrigins()) // ← via config
    .allowedMethods("GET", "POST", "PUT", "DELETE")
    .allowedHeaders("*")
    .allowCredentials(true)
    .maxAge(3600);
```

3.2. Configuration Properties
```java
// presentation/configuration/CorsProperties.java
@Configuration
@ConfigurationProperties("cors")
public class CorsProperties {
    private List<String> allowedOrigins = List.of("http://localhost:3000");
    
    public List<String> getAllowedOrigins() { return allowedOrigins; }
    public void setAllowedOrigins(List<String> origins) { this.allowedOrigins = origins; }
}
```

3.3. application-prod.yml
```yaml
cors:
  allowed-origins:
    - https://myapp.com
    - https://api.myapp.com
```

**TASK 4: Rate Limiting**

4.1. Dependency
```xml
<dependency>
    <groupId>com.giffing.bucket4j.spring.boot.starter</groupId>
    <artifactId>bucket4j-spring-boot-starter</artifactId>
    <version>0.8.0</version>
</dependency>
```

4.2. Rate Limiting Filter
```java
// presentation/security/RateLimitingFilter.java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private final LoadingCache<String, Bucket> cache = 
        Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(key -> createNewBucket());
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String key = getClientKey(request);
        Bucket bucket = cache.get(key);
        
        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Rate limit exceeded");
        }
    }
    
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket4j.builder()
            .addLimit(limit)
            .build();
    }
    
    private String getClientKey(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
```

4.3. SecurityFilterChain - adicionar filter
```java
http.addFilterAfter(rateLimitingFilter, JwtAuthenticationFilter.class);
```

**TASK 5: HTTPS**

5.1. application-prod.yml
```yaml
server:
  ssl:
    enabled: true
    key-store: ${SSL_KEYSTORE_PATH}
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: tomcat
```

5.2. Configuração de Redirecionamento HTTP → HTTPS
```java
@Bean
public EmbeddedServletContainerCustomizer containerCustomizer() {
    return container -> {
        if (sslEnabled) {
            container.getSession().setTrackingModes(
                Collections.singleton(SessionTrackingMode.COOKIE));
        }
    };
}
```

**TASK 6: Secrets Management**

6.1. application.yml (defaults)
```yaml
jwt:
  secret: ${JWT_SECRET:change-me-in-production}
  expiration: 86400000  # 24 horas
```

6.2. Setup documentation
```bash
# .env.example
JWT_SECRET=your-256-bit-secret-key-here
SSL_KEYSTORE_PATH=/path/to/keystore.p12
SSL_KEYSTORE_PASSWORD=your-password
```

6.3. docker-compose com env file
```yaml
services:
  web3270:
    environment:
      JWT_SECRET: ${JWT_SECRET}
      CORS_ORIGINS: https://myapp.com
```

#### Testes de Segurança
- [ ] POST /api/v1/sessions sem JWT → 401
- [ ] POST /api/v1/sessions com JWT inválido → 401
- [ ] POST /api/v1/sessions com JWT válido → 201
- [ ] POST /api/v1/sessions com host inválido → 400 + validation errors
- [ ] 101 requests em 1 minuto → 429 (rate limited)
- [ ] CORS bloqueado para origem não-permitida

#### Entregáveis
1. JwtTokenProvider, JwtAuthenticationFilter
2. AuthController com login/refresh
3. WebSecurityConfig completo
4. Custom validators (ValidIPAddress, ValidPort)
5. CorsProperties configuration
6. RateLimitingFilter
7. application-{dev,test,prod}.yml atualizado
8. Testes de segurança (SecurityIntegrationTest)
9. Commit: `feat(security): implement Spring Security + JWT + validation`

---

<a id="prompt-4"></a>
## PROMPT 4: Observabilidade + Logging

[Instruções similares ao PROMPT 3...]

---

<a id="prompt-5"></a>
## PROMPT 5: Tratamento de Erro Global

[Instruções similares ao PROMPT 3...]

---

<a id="prompt-6"></a>
## PROMPT 6: DevOps + Configuração

[Instruções similares ao PROMPT 3...]

---

<a id="prompt-7"></a>
## PROMPT 7: Testes de Integração

[Instruções similares ao PROMPT 3...]

---

<a id="prompt-8"></a>
## PROMPT 8: Documentação + Polish

[Instruções similares ao PROMPT 3...]

---

## 📊 Status de Execução

Copie esta seção em um novo arquivo `EXECUTION_TRACKER.md` para acompanhar o progresso:

```markdown
# Rastreamento de Execução

| PROMPT | Status | Data Início | Data Fim | Commits | Coverage |
|--------|--------|-------------|----------|---------|----------|
| 1 | ⏳ Aguardando | - | - | 0 | 0% |
| 2 | ⏳ Aguardando | - | - | 0 | - |
| 3 | ⏳ Aguardando | - | - | 0 | - |
| 4 | ⏳ Aguardando | - | - | 0 | - |
| 5 | ⏳ Aguardando | - | - | 0 | - |
| 6 | ⏳ Aguardando | - | - | 0 | - |
| 7 | ⏳ Aguardando | - | - | 0 | - |
| 8 | ⏳ Aguardando | - | - | 0 | - |

### Legenda
- ⏳ Aguardando
- 🔄 Em Progresso
- ✅ Completo
- ❌ Falhou
```

---

## 🚀 Como Começar

### 1. Revisar plano
```bash
cat plan-web3270CorporateTransformation.prompt.md
cat EXECUTIVE_REPORT.md
```

### 2. Executar PROMPT 1
```bash
Copiar especificação do PROMPT 1 acima
Passar para Copilot Agent
Executar: mvn clean test
Validar: 80% coverage em JaCoCo report
```

### 3. Commit e avançar
```bash
git add src/test
git commit -m "test(unit): implement 80% coverage for DTOs, Services, Controllers"
git push origin feature/corporate-transformation
```

### 4. Próximo PROMPT
Assim que PROMPT 1 estiver ✅ COMPLETO, iniciar PROMPT 2

---

**Preparado por:** GitHub Copilot (Agent Mode)  
**Data:** 2026-02-10  
**Status:** Pronto para Execução Imediata

