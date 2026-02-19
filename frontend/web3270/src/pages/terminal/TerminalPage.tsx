import React from 'react';
import { Terminal3270 } from '../../features/terminal-3270/Terminal3270';

const TerminalPage: React.FC = () => {
  return (
    // Terminal3270 handles its own layout and styling fully
    <Terminal3270 />
  );
};

export default TerminalPage;
