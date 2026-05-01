import React from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import { Container } from '../../shared/ui/Container';

const MainLayout: React.FC = () => {
  const location = useLocation();
  
  return (
    <div className="flex h-screen flex-col overflow-hidden">
      <header className="shrink-0 border-b bg-white shadow-sm h-16">
        <Container className="flex h-full items-center justify-between">
          <div className="flex items-center gap-6">
            <h1 className="text-xl font-bold text-gray-900">Web3270</h1>
            <nav className="flex gap-4">
              <Link to="/" className="text-sm font-medium text-gray-600 hover:text-gray-900">
                Terminal
              </Link>
              </nav>
          </div>
        </Container>
      </header>
      <main className={`flex-1 overflow-hidden bg-gray-50 py-6 overflow-y-auto`}>
        <Container>
            <Outlet />
          </Container>
      </main>
    </div>
  );
};

export default MainLayout;


