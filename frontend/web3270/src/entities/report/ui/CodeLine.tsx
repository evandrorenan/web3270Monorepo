import React from 'react';
import { Line } from '../model/types';

interface CodeLineProps {
  line: Line;
  lineRef?: (el: HTMLDivElement | null) => void;
}

export const CodeLine: React.FC<CodeLineProps> = ({ line, lineRef }) => {
  const isAbend     = line.type === "abend";
  const isDivision  = line.type === "division";
  const isSection   = line.type === "section";
  const isParagraph = line.type === "paragraph";

  const textColor = isAbend     ? "text-red-300"
    : isDivision  ? "text-yellow-400"
    : isSection   ? "text-yellow-200/80"
    : isParagraph ? "text-sky-300"
    : "text-slate-300";

  return (
    <div
      ref={lineRef}
      data-linenum={line.num}
      className={`flex items-center group relative
        ${isAbend
          ? "bg-red-950/40 border-l-2 border-red-500"
          : "border-l-2 border-transparent hover:bg-slate-800/25"}
        transition-colors
      `}
    >
      {/* Line number */}
      <span className="select-none text-slate-700 text-xs font-mono w-10 shrink-0 text-right pr-3 py-[3px] leading-5">
        {line.num}
      </span>

      {/* Code text */}
      <span className={`flex-1 font-mono text-[13px] leading-5 whitespace-pre py-[3px] ${textColor}`}>
        {line.text || " "}
      </span>

      {/* Abend badge */}
      {isAbend && (
        <span className="shrink-0 mr-2 text-[10px] font-bold text-red-400 bg-red-950 border border-red-600 rounded px-1.5 py-0.5 font-mono animate-pulse">
          ✦ ABEND
        </span>
      )}
    </div>
  );
};
