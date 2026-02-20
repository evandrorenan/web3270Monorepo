import React, { useState } from 'react';
import { VariableEntry } from '../model/types';
import { toHex } from '../lib/utils';

interface VarDialogProps {
  entry: VariableEntry | null;
  onClose: () => void;
}

export const VarDialog: React.FC<VarDialogProps> = ({ entry, onClose }) => {
  const [hex, setHex] = useState(false);
  if (!entry) return null;
  const { varName, varData } = entry;
  const displayVal = hex ? toHex(varData.value) : varData.value;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-sm"
      onClick={onClose}
    >
      <div
        className="relative bg-[#0d1117] border border-slate-700 rounded-xl shadow-2xl w-[700px] max-w-[96vw] max-h-[80vh] flex flex-col overflow-hidden"
        onClick={e => e.stopPropagation()}
      >
        <div className="flex items-center justify-between px-5 py-3 border-b border-slate-700 bg-slate-900/80">
          <div className="flex items-center gap-3 flex-wrap">
            <span className="text-emerald-400 font-mono font-bold">{varName}</span>
            <span className="text-slate-500 font-mono text-xs">PIC {varData.pic}</span>
            <span className="text-slate-600 font-mono text-xs">@ {varData.offset}</span>
            {varData.corrupt && (
              <span className="text-red-400 text-xs border border-red-700 bg-red-950 px-2 py-0.5 rounded font-mono">
                ⚠ CORRUPT — probable S0C7 cause
              </span>
            )}
          </div>
          <div className="flex items-center gap-2 ml-4">
            <button
              onClick={() => setHex(h => !h)}
              className={`text-xs px-3 py-1 rounded border font-mono transition-colors
                ${hex ? "bg-cyan-900 border-cyan-600 text-cyan-300" : "bg-slate-800 border-slate-600 text-slate-300 hover:border-slate-400"}`}
            >
              {hex ? "CHAR" : "HEX"}
            </button>
            <button onClick={onClose} className="text-slate-500 hover:text-white text-xl w-7 h-7 flex items-center justify-center rounded hover:bg-slate-800 transition">✕</button>
          </div>
        </div>
        <div className="p-5 overflow-auto">
          <pre className="font-mono text-sm text-emerald-300 whitespace-pre-wrap break-all leading-relaxed bg-slate-950 p-4 rounded-lg border border-slate-800">
            {displayVal}
          </pre>
          <div className="mt-3 flex gap-5 text-xs text-slate-500 font-mono">
            <span>Length: {varData.value.length}</span>
            <span>Offset: {varData.offset}</span>
            <span>Format: {hex ? "Hexadecimal" : "Character"}</span>
          </div>
        </div>
      </div>
    </div>
  );
};
