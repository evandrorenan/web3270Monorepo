# Frontend Assessment Report: Web3270

## 1. Current State Analysis
The current application `frontend/web3270` is a Single Page Application (SPA) built with an older version of React (v16.13.1) using `create-react-app` (react-scripts 3.4.3). It functions as a 3270 terminal emulator, communicating with a backend via WebSockets (STOMP).

### Key Characteristics:
- **Framework:** React 16 (Class Components).
- **State Management:** Redux (Legacy pattern: `connect`, `actions`, `reducers`).
- **Routing:** Custom manual routing (conditional rendering based on state).
- **Styling:** Standard CSS files imported per component.
- **Network:** `react-stomp` for WebSockets, `axios` for HTTP.
- **Build Tool:** Webpack (via `react-scripts`).

## 2. Identified Issues & Bad Practices

### A. Architecture & Structure
- **Manual Routing:** The application uses a custom `activeTab` state in `Layout.js` to switch views. This prevents deep linking, browser history navigation (back/forward buttons), and lazy loading of routes.
- **Flat Directory Structure:** The `src/components` and `src/store` organization is simple but doesn't scale well for enterprise applications. It mixes UI components with business logic.
- **Hardcoded Configuration:** URLs like `http://localhost:8080/web3270-websocket` are hardcoded in components (`ScreenWebSocket.js`), making environment promotion (Dev -> QA -> Prod) impossible without code changes.

### B. Code Quality & Patterns
- **Legacy React Patterns:** Heavy reliance on Class Components (`Component`, `constructor`, `componentDidMount`). Modern React encourages Functional Components and Hooks (`useEffect`, `useState`) for better readability and performance.
- **Mutable State Operations:** In `Layout.js`, `Object.assign` is used, which is fine, but modern Redux Toolkit or Immer guarantees immutability more safely.
- **Direct DOM/CSS Manipulation:** The `Screen.js` component manually calculates rows and fields in the render method, which can be computationally expensive on every render.
- **Prop Drilling & Complexity:** The `Screen.js` component is tightly coupled to Redux via `connect`, making it harder to test in isolation.
- **Testing:** Only the default `App.test.js` exists. There is no comprehensive unit or integration testing strategy.

### C. Dependencies
- **Outdated Libraries:** `react-scripts` is deprecated in favor of modern bundlers. `react-stomp` v5 and `axios` v0.20 are old.
- **Security:** Older dependencies often have known vulnerabilities.

## 3. Target Architecture & Modernization Plan

To transform this into a "Corporate-Grade" application, we will adopt the following stack and practices:

### A. Technology Stack
- **Build Tool:** **Vite** (Faster builds, modern HMR).
- **Language:** **TypeScript** (Static typing for safety and better developer experience).
- **Framework:** **React 18** (Functional Components, Hooks).
- **Styling:** **Tailwind CSS** (Utility-first, consistent design system).
- **State Management:** **Redux Toolkit (RTK)** (Standard, less boilerplate, built-in Thunks/Middleware).
- **Routing:** **React Router v6** (Standard declarative routing).
- **Testing:** **Vitest** (Unit) + **React Testing Library** (Component).

### B. Architecture: Feature-Sliced Design (FSD)
We will reorganize the codebase to separate concerns clearly:
- `src/app`: Global setup (Store, Router, Styles).
- `src/pages`: Composition of widgets into full pages (e.g., `TerminalPage`).
- `src/widgets`: Major UI blocks (e.g., `TerminalScreen`, `Sidebar`).
- `src/features`: User interactions (e.g., `ConnectSession`, `UploadFile`).
- `src/entities`: Business logic & data (e.g., `Session` model, `Row` component).
- `src/shared`: Reusable primitives (UI Kit, helpers).

### C. Specific Improvements
1.  **WebSocket Management:** Move `ScreenWebSocket` logic into a custom **Redux Middleware**. This decouples the connection logic from the UI view, allowing the connection to persist even if the user navigates away from the screen.
2.  **Performance:** Use `React.memo` for Terminal Rows/Fields to prevent re-rendering the entire 80x24 grid when only one character changes.
3.  **Forms:** Use **React Hook Form** for the "Manual Upload" and "Sysout Download" forms to handle validation and submission efficiently.

## 4. Execution Roadmap
1.  **Setup:** Initialize Vite + TypeScript + Tailwind.
2.  **Foundation:** Port shared UI components and utilities.
3.  **Core Logic:** Implement Redux Toolkit slices and WebSocket Middleware.
4.  **Migration:** Refactor features one by one (Terminal, Forms, Reports).
5.  **Integration:** Assemble features into Pages and Layouts using React Router.
6.  **Verification:** comprehensive testing and Dockerization.
