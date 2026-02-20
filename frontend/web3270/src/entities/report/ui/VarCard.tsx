import React, { useState } from 'react';
import { MOCK_VARIABLES } from '../model/mockData';
import { VariableEntry } from '../model/types';
import { toHex, truncate } from '../lib/utils';

interface VarCardProps {
  varName: string;
  pinned: boolean;
  onPin: (name: string) => void;
  onExpand: (entry: VariableEntry) => void;
}

export const VarCard: React.FC<VarCardProps> = ({ varName, pinned, onPin, onExpand }) => {
  const varData = MOCK_VARIABLES[varName];
  const [hex, setHex] = useState(false);
  if (!varData) return null;

  const displayVal = hex ? toHex(varData.value) : varData.value;
  const isTruncated = varData.value.length > 22;

  const handleDoubleClick = (e: React.MouseEvent) => {
    // Prevent default to avoid any selection behavior if that's interfering
    e.preventDefault();
    onExpand({ varName, varData });
  };

  return (
    <div
      data-testid={`var-card-${varName}`}
      className={`rounded-lg border p-2.5 font-mono transition-all select-none
        ${varData.corrupt
          ? "bg-red-950/40 border-red-700/70 shadow-[0_0_10px_rgba(239,68,68,0.12)]"
          : pinned
          ? "bg-slate-800/70 border-slate-500/70"
          : "bg-slate-900/50 border-slate-700/40 hover:border-slate-600/60"}
      `}
      onDoubleClick={handleDoubleClick}
    >
      {/* Header row */}
      <div className="flex items-center gap-1 mb-1 pointer-events-none">
        <span className={`text-[11px] font-semibold truncate flex-1 leading-none pointer-events-auto
          ${varData.corrupt ? "text-red-400" : "text-emerald-400"}`}>
          {varName}
          {varData.corrupt && " ⚠"}
        </span>

        {/* HEX toggle */}
        <button
          onClick={(e) => { e.stopPropagation(); setHex(h => !h); }}
          title="Toggle HEX / CHAR"
          className={`text-[9px] px-1 py-0.5 rounded border leading-none shrink-0 transition-colors pointer-events-auto
            ${hex
              ? "border-cyan-700 text-cyan-400 bg-cyan-950"
              : "border-slate-700 text-slate-500 hover:border-slate-500 hover:text-slate-300"}`}
        >
          {hex ? "CHR" : "HEX"}
        </button>

        {/* Expand */}
        {isTruncated && (
          <button
            onClick={(e) => { e.stopPropagation(); onExpand({ varName, varData }); }}
            title="Expand full value"
            className="text-[9px] px-1 py-0.5 rounded border border-slate-700 text-slate-500 hover:border-slate-400 hover:text-slate-200 leading-none shrink-0 pointer-events-auto"
          >⤡</button>
        )}

        {/* Pin */}
        <button
          onClick={(e) => { e.stopPropagation(); onPin(varName); }}
          title={pinned ? "Unpin" : "Pin — keep always visible"}
          className={`text-[11px] px-1 py-0.5 rounded border leading-none shrink-0 transition-colors pointer-events-auto
            ${pinned
              ? "border-amber-500 text-amber-400 bg-amber-950/60"
              : "border-slate-700 text-slate-600 hover:border-amber-600 hover:text-amber-400"}`}
        >
          {pinned ? "📌" : "📍"}
        </button>
      </div>

      {/* Meta */}
      <div className="text-[9px] text-slate-600 mb-1 leading-none pointer-events-none">
        PIC {varData.pic} · {varData.offset}
      </div>

      {/* Value */}
      <div
        className={`text-[11px] truncate leading-tight pointer-events-auto
          ${varData.corrupt ? "text-red-300" : "text-slate-200"}
          ${isTruncated ? "cursor-pointer hover:text-white" : ""}`}
        title={isTruncated ? "Click to expand full value" : displayVal}
        onClick={(e) => {
          if (isTruncated) {
            e.stopPropagation();
            onExpand({ varName, varData });
          }
        }}
      >
        {truncate(displayVal, 22)}
      </div>
    </div>
  );
};
