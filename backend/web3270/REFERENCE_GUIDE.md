# 🚀 GUIA DE REFERÊNCIA RÁPIDA

**Última Atualização:** 2026-02-10  
**Projeto:** Web3270 Transformação Corporativa  
**Status:** Pronto para Execução

---

## 📚 Documentação Gerada

```
📁 web3270/
├── ✅ plan-web3270CorporateTransformation.prompt.md  [8 PROMPTS]
├── ✅ EXECUTIVE_REPORT.md                           [STAKEHOLDERS]
├── ✅ PROMPTS_READY_FOR_EXECUTION.md               [DEVELOPERS]
├── ✅ SUMMARY.md                                    [OVERVIEW]
└── ✅ REFERENCE_GUIDE.md                            [VOCÊ ESTÁ AQUI]
```

---

## 🎯 Objetivos Principais

| # | Objetivo | Fase | Status |
|---|----------|------|--------|
| 1️⃣ | Implementar testes com 80%+ coverage | 1 | ⏳ Aguardando |
| 2️⃣ | Refatorar para hexagonal architecture | 2 | ⏳ Aguardando |
| 3️⃣ | Adicionar Spring Security + JWT | 3 | ⏳ Aguardando |
| 4️⃣ | Implementar observabilidade | 4 | ⏳ Aguardando |
| 5️⃣ | Tratamento de erros padronizado | 5 | ⏳ Aguardando |
| 6️⃣ | Docker + multi-profile configuration | 6 | ⏳ Aguardando |
| 7️⃣ | Testes de integração | 7 | ⏳ Aguardando |
| 8️⃣ | Documentação corporativa | 8 | ⏳ Aguardando |

---

## 🔥 CRÍTICO - Antes de Começar

### Verificar
- [ ] Java 21 instalado (`java -version`)
- [ ] Maven 3.8+ (`mvn -version`)
- [ ] Git configurado
- [ ] IDE com suporte a Java (IntelliJ/VS Code)

### Setup Inicial
```bash
# Clone projeto
git clone <repo>
cd web3270

# Criar branch
git checkout -b feature/corporate-transformation

# Verificar build
mvn clean install

# Verificar testes atuais
mvn test
```

---

## 📋 PROMPT 1 - Testes Unitários (Começa Aqui!)

### Objetivo Simples
```
Cobertura de testes: 15% → 80%
Status dos testes: ❌ Falha → ✅ Verde
```

### O que Fazer
1. Criar 13+ arquivos de teste
2. Testar todas as classes principais
3. Atingir 80% de cobertura
4. Todos os testes passando (GREEN)

### Arquivos a Criar
```
src/test/java/br/com/evandrorenan/web3270/
├── controller/    [4 files]
├── service/       [4 files]
├── dto/           [5 files]
├── util/          [1 file]
├── exception/     [1 file]
└── fixture/       [3 files]
```

### Validação
```bash
mvn clean test                    # Todos passando
mvn jacoco:report                 # Gerar relatório
open target/site/jacoco/index.html # Visualizar (80%+)
```

### Resultado Esperado
```
Tests run: 50+
Failures: 0
Errors: 0
Coverage: 80%+
Build Status: ✅ SUCCESS
```

---

## 📋 PROMPT 2 - Arquitetura Hexagonal

### Objetivo
```
Estrutura: Monolito → Hexagonal (Domain → App → Infra → Presentation)
Testes: 80%+ (mantém)
```

### Estrutura Criada
```
src/main/java/.../web3270/
├── domain/             (entities, value objects, ports)
├── application/        (use cases, services)
├── infrastructure/     (adapters, repositories, external libs)
└── presentation/       (controllers, DTOs, handlers)
```

### Validação
```bash
mvn clean test        # 80%+ coverage ainda
mvn compile           # Sem warnings
# Verificar package structure no IDE
```

---

## 📋 PROMPT 3 - Segurança + Validação

### Objetivo
```
Autenticação: Nenhuma → Spring Security + JWT
Validação: Nenhuma → Jakarta Validation
CORS: "*" → Restritivo
Rate Limiting: Nenhum → Bucket4j
```

### O que Adicionar
```
1. Spring Security config
2. JWT token provider
3. Auth controller (/auth/login)
4. Custom validators (@ValidIPAddress, @ValidPort)
5. Rate limiting filter
6. CORS configuration
```

### Validação
```bash
curl -X POST http://localhost:8080/api/v1/sessions \
  -H "Content-Type: application/json" \
  -d '{"host":"192.168.1.1","port":"51004"}'
# Deve retornar 401 (sem JWT)

curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
# Deve retornar JWT token
```

---

## 📋 PROMPT 4 - Observabilidade + Logging

### Objetivo
```
Logs: Manuais → Structured (JSON)
Tracing: Nenhum → Correlation IDs (MDC)
Métricas: Nenhuma → Micrometer
Health: Nenhum → Custom Health Indicators
```

### O que Adicionar
```
1. Structured logging (SLF4J + Logback JSON)
2. MDC Filter (correlation ID)
3. Custom metrics
4. Health check indicators
5. /actuator endpoints
```

### Validação
```bash
# Logs estruturados em JSON
tail -f logs/app.log | jq .

# Métricas disponíveis
curl http://localhost:8080/actuator/metrics

# Health check
curl http://localhost:8080/actuator/health
```

---

## 📋 PROMPT 5 - Tratamento de Erro Global

### Objetivo
```
Erros: Espalhados → Centralizado em @ControllerAdvice
Formato: Variado → RFC 7807 (Problem Details)
```

### O que Adicionar
```
1. GlobalExceptionHandler (@ControllerAdvice)
2. ErrorCode enum (E001, E002, ...)
3. ProblemResponse class
4. Domain exceptions (SessionNotFoundException, ...)
5. Teste: cada erro com status HTTP correto
```

### Validação
```bash
curl -X POST http://localhost:8080/api/v1/sessions \
  -H "Content-Type: application/json" \
  -d '{"host":"invalid","port":"99999"}'
# Deve retornar JSON com type, status, detail, errorCode
```

---

## 📋 PROMPT 6 - DevOps + Configuração

### Objetivo
```
Configuration: Hardcoded → Environment-based
Deployment: Manual → Docker ready
Profiles: Nenhum → dev/test/prod
```

### O que Adicionar
```
1. application-dev.yml
2. application-test.yml
3. application-prod.yml
4. Dockerfile (multi-stage, non-root)
5. docker-compose.yml
6. ConfigurationProperties classes
7. setup.md (como rodar)
```

### Validação
```bash
# Dev
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Docker
docker build -t web3270:latest .
docker run -p 8080:8080 -e JWT_SECRET=xyz web3270:latest

# docker-compose
docker-compose up
```

---

## 📋 PROMPT 7 - Testes de Integração

### Objetivo
```
Testes: Unit only → Unit + Integration + E2E
Coverage: 80% unit → 95%+ overall
```

### O que Adicionar
```
1. BaseIntegrationTest (abstract)
2. SessionIntegrationTest
3. SecurityIntegrationTest
4. ValidationIntegrationTest
5. PcommMockServer
6. WebSocket integration tests
```

### Validação
```bash
mvn clean test -Dgroups=integration
# Todos testes passando
# Coverage > 95%
```

---

## 📋 PROMPT 8 - Documentação + Polish

### Objetivo
```
Docs: Mínima → Corporativa
Swagger: Incompleto → Completo com exemplos
Code Quality: SonarQube warnings → 0 blockers
```

### O que Adicionar
```
1. README.md completo
2. /docs/ARCHITECTURE.md (C4 diagrams)
3. /docs/CONTRIBUTING.md
4. /docs/API.md (endpoints + curl)
5. /docs/RUNBOOK.md (operacional)
6. Swagger @Operation + @Schema
7. Remove comentários poluídos
8. SonarQube: 0 blockers
```

### Validação
```bash
mvn sonar:sonar  # 0 blockers
open http://localhost:8080/swagger-ui.html
# README está no raiz
```

---

## 🎬 Fluxo de Execução

```
[INÍCIO]
   ↓
[PROMPT 1: Testes] ← START HERE
   ↓ (mvn clean test ✅)
[PROMPT 2: Arquitetura]
   ↓ (coverage 80%+ ✅)
[PROMPT 3: Segurança]
   ↓ (spring-security ✅)
[PROMPT 4: Observabilidade]
   ↓ (logs JSON ✅)
[PROMPT 5: Erro Global]
   ↓ (@ControllerAdvice ✅)
[PROMPT 6: DevOps]
   ↓ (Docker ✅)
[PROMPT 7: Integração]
   ↓ (E2E tests ✅)
[PROMPT 8: Documentação]
   ↓ (Readme ✅)
[FIM - PRODUÇÃO READY]
```

---

## 💾 Commits Esperados

### PROMPT 1
```bash
git commit -m "test(unit): implement 80% coverage for DTOs, Services, Controllers"
```

### PROMPT 2
```bash
git commit -m "refactor(arch): implement hexagonal architecture (domain-app-infra)"
```

### PROMPT 3
```bash
git commit -m "feat(security): add Spring Security + JWT + validation"
```

### PROMPT 4
```bash
git commit -m "feat(observability): add structured logging + metrics + MDC"
```

### PROMPT 5
```bash
git commit -m "feat(error): implement GlobalExceptionHandler + RFC 7807"
```

### PROMPT 6
```bash
git commit -m "feat(devops): add Docker + multi-profile config"
```

### PROMPT 7
```bash
git commit -m "test(integration): add E2E tests with TestContainers"
```

### PROMPT 8
```bash
git commit -m "docs: add README + architecture + contributing guide"
```

---

## 🛠️ Troubleshooting Rápido

### Erro: Tests falhando
```bash
# Limpar cache
mvn clean

# Rebuild
mvn install

# Rodar testes com verbose
mvn test -X
```

### Erro: Compiler warnings
```bash
# Verificar warnings
mvn compile -Wall

# Maven pode ter opções de warning
```

### Erro: JaCoCo não gera report
```bash
mvn clean test jacoco:report

# Report em:
target/site/jacoco/index.html
```

### Erro: Spring Boot não starta
```bash
# Verificar properties
cat src/main/resources/application.yml

# Logs
mvn spring-boot:run > app.log 2>&1

# Debug
mvn spring-boot:run -Ddebug
```

---

## 📞 Referências Rápidas

### Documentação Oficial
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- Jakarta Validation: https://jakarta.ee/specifications/validation/
- JUnit 5: https://junit.org/junit5/

### Padrões
- Hexagonal Architecture: https://alistair.cockburn.us/hexagonal-architecture/
- Clean Code: Robert C. Martin
- SOLID: https://en.wikipedia.org/wiki/SOLID

### Ferramentas
- Maven: https://maven.apache.org/
- JaCoCo: https://www.jacoco.org/
- SonarQube: https://www.sonarqube.org/

---

## ✅ Checklist Final (Após Todos PROMPTs)

- [ ] 80%+ test coverage
- [ ] Hexagonal architecture implementada
- [ ] Spring Security + JWT funcionando
- [ ] Structured logging com JSON
- [ ] GlobalExceptionHandler centralizado
- [ ] Docker image compilando
- [ ] Testes de integração passando
- [ ] README completo
- [ ] Swagger funcional
- [ ] SonarQube: 0 blockers
- [ ] Todos os 8 commits no git
- [ ] CI/CD pipeline funcionando

---

## 🎓 Aprendizados Esperados

Ao final da transformação, a equipe terá experiência em:

1. **Testes:** JUnit 5, Mockito, TestContainers
2. **Arquitetura:** Hexagonal, ports & adapters, DDD
3. **Segurança:** Spring Security, JWT, validação
4. **Observabilidade:** Structured logging, metrics, tracing
5. **DevOps:** Docker, profiles, configuration externalization
6. **Qualidade:** Code review, SonarQube, coverage

---

## 🏆 Sucesso Medido Por

```
Métrica                    Antes    Depois    ✅ Target
─────────────────────────────────────────────────────
Test Coverage              15%      80%+      ✅
Security Issues            7        0         ✅
Uptime                     ?        99.9%     ✅
MTTR                       ∞        <30min    ✅
SonarQube Blockers         ?        0         ✅
API Documentation          0%       100%      ✅
Logs Estruturados          Não      Sim       ✅
Métricas Disponíveis       Não      Sim       ✅
```

---

## 🚀 Próximo Passo

### Agora mesmo:
1. Ler EXECUTIVE_REPORT.md (15 min)
2. Ler SUMMARY.md (10 min)
3. Ler seção PROMPT 1 em PROMPTS_READY_FOR_EXECUTION.md (15 min)

### Depois:
1. Executar PROMPT 1 com Copilot Agent
2. Atingir 80% coverage
3. Commit ao git
4. Próximo PROMPT

---

**Preparado por:** GitHub Copilot (Agent Mode)  
**Data:** 2026-02-10  
**Status:** ✅ PRONTO PARA COMEÇAR  
**Tempo Estimado:** 8 semanas | **Investimento:** $60k | **ROI:** $200k/ano

