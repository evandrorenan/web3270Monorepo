import React, { useState, useRef, useEffect, useCallback } from "react";
import { MOCK_PROGRAM, MOCK_VARIABLES } from "../../entities/report/model/mockData";
import { VariableEntry } from "../../entities/report/model/types";
import { CodeLine } from "../../entities/report/ui/CodeLine";
import { VarCard } from "../../entities/report/ui/VarCard";
import { VarDialog } from "../../entities/report/ui/VarDialog";

export default function ReportsPage() {
  const [dialogEntry, setDialogEntry] = useState<VariableEntry | null>(null);
  const [pinnedVars,  setPinnedVars]  = useState<Set<string>>(new Set(["WS-TAX-RATE"]));
  const [visibleVars, setVisibleVars] = useState<Set<string>>(new Set());
  const [searchInput, setSearchInput] = useState("");
  const [searchError, setSearchError] = useState("");

  const codeScrollRef = useRef<HTMLDivElement>(null);
  // Store refs for ALL lines (indexed by line.num) so observer can watch every row
  const lineEls = useRef<Record<number, HTMLDivElement | null>>({});

  const prog = MOCK_PROGRAM;

  // ── IntersectionObserver — track visible line numbers, derive vars from them ──
  // Key insight: track a persistent Set of visible line *numbers* (in a ref so it
  // survives between observer callbacks). After each batch of entries, recompute
  // the full set of visible variables from scratch — no counters that can drift.
  const visibleLineNums = useRef(new Set<number>());

  useEffect(() => {
    const root = codeScrollRef.current;
    if (!root) return;

    const recompute = () => {
      const vars = new Set<string>();
      for (const lineNum of visibleLineNums.current) {
        const line = MOCK_PROGRAM.lines.find(l => l.num === lineNum);
        if (line && line.varNames) line.varNames.forEach(v => vars.add(v));
      }
      setVisibleVars(vars);
    };

    const observer = new IntersectionObserver(
      (entries) => {
        for (const entry of entries) {
          const lineNum = Number((entry.target as HTMLElement).dataset.linenum);
          if (!lineNum) continue;
          if (entry.isIntersecting) visibleLineNums.current.add(lineNum);
          else visibleLineNums.current.delete(lineNum);
        }
        recompute();
      },
      { root, threshold: 0.01 }
    );

    Object.values(lineEls.current).forEach(el => el && observer.observe(el));
    return () => {
      observer.disconnect();
      visibleLineNums.current.clear();
    };
  }, []);

  // ── Pinning ────────────────────────────────────────────────────────────────
  const togglePin = useCallback((varName: string) => {
    setPinnedVars(prev => {
      const next = new Set(prev);
      next.has(varName) ? next.delete(varName) : next.add(varName);
      return next;
    });
  }, []);

  // ── Search pin ────────────────────────────────────────────────────────────
  const handleSearchPin = () => {
    const name = searchInput.trim().toUpperCase();
    if (!name) return;
    if (!MOCK_VARIABLES[name]) {
      setSearchError(`"${name}" not found`);
      return;
    }
    setPinnedVars(prev => new Set([...prev, name]));
    setSearchInput("");
    setSearchError("");
  };

  // ── Compute right panel lists ─────────────────────────────────────────────
  const pinnedList  = [...pinnedVars].filter(v => MOCK_VARIABLES[v]);
  // "in view" = visible in code pane AND not already pinned
  const inViewList  = [...visibleVars].filter(v => !pinnedVars.has(v) && MOCK_VARIABLES[v]);
  const totalShown  = pinnedList.length + inViewList.length;

  return (
    <div
      className="h-full flex flex-col overflow-hidden"
      style={{ background: "#080c10", fontFamily: "'IBM Plex Mono','Fira Code','Courier New',monospace" }}
    >
      {/* ── HEADER ── */}
      <header className="shrink-0 border-b border-slate-800 bg-[#0c1118] px-5 py-2.5 flex items-center gap-4 flex-wrap z-10">
        <div>
          <div className="text-[9px] text-slate-500 uppercase tracking-[0.2em]">COBOL Abend Analyzer</div>
          <div className="text-white font-semibold text-sm leading-tight mt-0.5">{prog.name}</div>
        </div>

        <div className="flex items-center gap-2">
          <span className="px-2 py-1 bg-red-950 border border-red-600 rounded text-red-300 text-xs font-mono font-bold animate-pulse">
            {prog.abendCode}
          </span>
          <span className="text-slate-500 text-xs font-mono">
            offset <span className="text-orange-400">{prog.abendOffset}</span>
          </span>
        </div>

        <span className="text-slate-600 text-xs font-mono">{prog.timestamp}</span>

        <div className="ml-auto text-slate-600 text-[10px] leading-tight text-right hidden md:block font-mono">
          <div>Right panel tracks your scroll position</div>
          <div>📍 pin · 📌 pinned · double-click = detail</div>
        </div>
      </header>

      {/* ── BODY ── */}
      <div className="flex-1 flex overflow-hidden min-h-0">

        {/* ── CODE PANE ── */}
        <div ref={codeScrollRef} className="shrink-0 overflow-auto border-r border-slate-800" style={{ width: '84ch', maxWidth: '60%' }}>
          <div className="py-3 px-2 min-w-max">

            {/* Legend */}
            <div className="flex items-center gap-5 mb-3 px-2 text-[10px] text-slate-600 font-mono">
              <span className="flex items-center gap-1.5">
                <span className="w-3 h-0.5 bg-yellow-400 inline-block rounded"></span>DIVISION
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-3 h-0.5 bg-sky-400 inline-block rounded"></span>PARAGRAPH
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-3 h-0.5 bg-red-500 inline-block rounded"></span>ABEND LINE
              </span>
            </div>

            {/* Code block */}
            <div className="rounded-lg border border-slate-800 overflow-hidden bg-[#0d1117]">
              <div className="px-3 py-1.5 border-b border-slate-800 bg-slate-900/70 text-[10px] text-slate-500 font-mono flex gap-3">
                <span>{prog.name}.cbl</span>
                <span className="text-slate-700">· SYS001.DUMP.D240315</span>
              </div>
              <div className="py-1">
                {prog.lines.map(line => (
                  <CodeLine
                    key={line.num}
                    line={line}
                    lineRef={(el) => { lineEls.current[line.num] = el; }}
                  />
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* ── RIGHT PANEL ── */}
        <aside className="flex-1 border-l border-slate-800 bg-[#090e15] flex flex-col overflow-hidden min-w-0">

          {/* Panel header + search */}
          <div className="shrink-0 px-2.5 py-2.5 border-b border-slate-800 bg-[#0b1119]/80 space-y-2">
            <div className="text-[9px] text-slate-500 uppercase tracking-[0.18em] font-mono">
              Working Storage
            </div>

            {/* Search input */}
            <div className="flex gap-1">
              <input
                value={searchInput}
                onChange={e => {
                  setSearchInput(e.target.value.toUpperCase());
                  setSearchError("");
                }}
                onKeyDown={e => e.key === "Enter" && handleSearchPin()}
                placeholder="WS-VAR-NAME…"
                spellCheck={false}
                className="flex-1 min-w-0 bg-slate-800/80 border border-slate-700 rounded px-2 py-1
                  text-[11px] font-mono text-slate-200 placeholder-slate-600
                  focus:outline-none focus:border-emerald-700 focus:bg-slate-800 transition-colors"
              />
              <button
                onClick={handleSearchPin}
                title="Pin this variable"
                className="px-2 py-1 bg-slate-800/80 border border-slate-700 rounded
                  text-amber-400 hover:border-amber-600 hover:bg-amber-950/40
                  transition-colors text-[13px] shrink-0"
              >📌</button>
            </div>

            {searchError && (
              <div className="text-red-400 text-[9px] font-mono">{searchError}</div>
            )}
          </div>

          {/* Card list */}
          <div className="flex-1 overflow-y-auto p-2 space-y-1.5 min-h-0">

            {/* Pinned section */}
            {pinnedList.length > 0 && (
              <>
                <div className="flex items-center gap-1.5 px-0.5 pt-0.5 pb-1">
                  <span className="text-[9px] text-amber-600 uppercase tracking-widest font-mono">📌 Pinned</span>
                  <span className="text-[9px] text-slate-700 font-mono">({pinnedList.length})</span>
                </div>
                {pinnedList.map(name => (
                  <VarCard
                    key={name}
                    varName={name}
                    pinned
                    onPin={togglePin}
                    onExpand={setDialogEntry}
                  />
                ))}
              </>
            )}

            {/* Divider between sections */}
            {pinnedList.length > 0 && inViewList.length > 0 && (
              <div className="border-t border-slate-800/80 my-1" />
            )}

            {/* In-view section */}
            {inViewList.length > 0 ? (
              <>
                <div className="flex items-center gap-1.5 px-0.5 pt-0.5 pb-1">
                  <span className="text-[9px] text-slate-500 uppercase tracking-widest font-mono">👁 In view</span>
                  <span className="text-[9px] text-slate-700 font-mono">({inViewList.length})</span>
                </div>
                {inViewList.map(name => (
                  <VarCard
                    key={name}
                    varName={name}
                    pinned={false}
                    onPin={togglePin}
                    onExpand={setDialogEntry}
                  />
                ))}
              </>
            ) : (
              pinnedList.length === 0 && (
                <div className="text-slate-700 text-[10px] text-center pt-10 px-3 leading-relaxed font-mono">
                  Scroll the code pane — variables in view will appear here
                </div>
              )
            )}
          </div>

          {/* Footer stats */}
          <div className="shrink-0 border-t border-slate-800 px-3 py-1.5 flex justify-between text-[9px] text-slate-700 font-mono">
            <span>{totalShown} shown</span>
            <span>{Object.keys(MOCK_VARIABLES).length} total vars</span>
          </div>
        </aside>
      </div>

      {/* ── DIALOG ── */}
      {dialogEntry && (
        <VarDialog entry={dialogEntry} onClose={() => setDialogEntry(null)} />
      )}
    </div>
  );
}
