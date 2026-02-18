import React from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import MainLayout from '../../../widgets/layout/MainLayout';
import TerminalPage from '../../../pages/terminal/TerminalPage';
import UploadPage from '../../../pages/upload/UploadPage';
import ReportsPage from '../../../pages/reports/ReportsPage';

const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    children: [
      {
        index: true,
        element: <TerminalPage />,
      },
      {
        path: 'reports',
        element: <ReportsPage />,
      },
      {
        path: 'upload',
        element: <UploadPage />,
      },
    ],
  },
]);

export const AppRouter: React.FC = () => {
  return <RouterProvider router={router} />;
};
