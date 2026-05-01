# Web3270 Monorepo

**TL;DR:** Esta branch (`feature/cobol-abend-analyzer-...`) contém a interface de "Cobol Abend Analyzer". É a interface web (frontend) correspondente para coletar, inspecionar e analisar erros (abends) gerados no mainframe. **Esta é a branch recomendada para você retomar**, já que se integra com a extensão `mainframe-abend-reporter`.

## Sobre esta Branch
- **Status:** Trabalho em Progresso (WIP) - **Recomendado retomar**
- **Foco:** Criação de interface rica para análise e relatório de abends.
- **Mudanças principais:**
  - Substituição da página genérica `ReportsPage` pela nova interface do **Cobol Abend Analyzer**.
  - Criação do componente `CodeLine.tsx` para apresentar trechos de código onde falhas ocorreram.
  - Criação dos componentes `VarCard.tsx` e `VarDialog.tsx` para visualização e inspeção do estado de variáveis na memória.
  - Adição de `mockData.ts` contendo exemplos de abends para fins de layout e testes.
