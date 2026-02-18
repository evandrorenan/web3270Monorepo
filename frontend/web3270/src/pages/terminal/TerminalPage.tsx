import React from 'react';
import { Screen } from '../../entities/session/ui/Screen';

const TerminalPage: React.FC = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-[calc(100vh-80px)] bg-gray-900 p-8">
      <h2 className="mb-4 text-xl font-bold text-gray-400 self-start w-full max-w-5xl">
        3270 Terminal Session
      </h2>
      <div className="bg-gray-800 p-4 rounded-lg shadow-xl border border-gray-700">
        <Screen />
      </div>
      <div className="mt-6 text-sm text-gray-500 max-w-5xl w-full">
         Tip: Use Tab to navigate fields. Enter to submit. F1-F12 supported.
      </div>
    </div>
  );
};

export default TerminalPage;
