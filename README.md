# Web3270 Monorepo

**TL;DR:** Esta branch (`robust-3270-terminal-...`) contém trabalho em progresso focado em melhorar a interface e usabilidade do emulador de terminal 3270.

## Sobre esta Branch
- **Status:** Trabalho em Progresso (WIP)
- **Foco:** Evolução e robustez do emulador de terminal 3270 web.
- **Mudanças principais:**
  - Substituição dos componentes antigos (`Screen`, `Field`) pelo novo componente `Terminal3270`.
  - Adição de suporte a cores no terminal.
  - Padronização de tamanho de tela (tela maior).
  - Adição de serviço de mock (`mockSessionService.ts`) para possibilitar testes offline da interface.
  - Implementação de testes automatizados para a interface do terminal.
