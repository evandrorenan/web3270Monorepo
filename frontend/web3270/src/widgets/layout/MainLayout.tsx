import React from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import { Container } from '../../shared/ui/Container';

const MainLayout: React.FC = () => {
  const location = useLocation();
  const isReportsPage = location.pathname === '/reports';

  return (
    <div className="flex h-screen flex-col">
      <header className="border-b bg-white shadow-sm">
        <Container className="flex h-16 items-center justify-between">
          <div className="flex items-center gap-6">
            <h1 className="text-xl font-bold text-gray-900">Web3270</h1>
            <nav className="flex gap-4">
              <Link to="/" className="text-sm font-medium text-gray-600 hover:text-gray-900">
                Terminal
              </Link>
              <Link to="/reports" className="text-sm font-medium text-gray-600 hover:text-gray-900">
                Reports
              </Link>
              <Link to="/upload" className="text-sm font-medium text-gray-600 hover:text-gray-900">
                Upload
              </Link>
            </nav>
          </div>
        </Container>
      </header>
      <main className={`flex-1 bg-gray-50 ${isReportsPage ? '' : 'py-6'}`}>
        {isReportsPage ? (
          <Outlet />
        ) : (
          <Container>
            <Outlet />
          </Container>
        )}
      </main>
    </div>
  );
};

export default MainLayout;
