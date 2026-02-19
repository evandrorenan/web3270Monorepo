import React, { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from '../../shared/model/hooks';
import { connect, sendKeystroke, submitScreen, updateFieldText } from '../../entities/session/model/slice';
import { TerminalField } from './ui/TerminalField';

export const Terminal3270: React.FC = () => {
  const dispatch = useAppDispatch();
  const { fields, content, cursorPos, status, waitingStatus, sessionId } = useAppSelector((state) => state.session);
  const [focusedIndex, setFocusedIndex] = useState<number>(0);

  // Connect on mount
  useEffect(() => {
    if (status === 'disconnected') {
      dispatch(connect());
    }
  }, [dispatch, status]);

  // Determine initial focus based on cursorPos
  useEffect(() => {
    if (fields.length > 0) {
      // Find unprotected field containing or after cursor
      let initialIndex = fields.findIndex(f => !f.protected && ((cursorPos >= f.start && cursorPos <= f.end) || f.start >= cursorPos));

      // If not found, find ANY first unprotected field
      if (initialIndex === -1) {
         initialIndex = fields.findIndex(f => !f.protected);
      }

      if (initialIndex !== -1) {
        setFocusedIndex(initialIndex);
      }
    }
  }, [fields, cursorPos]);

  const findNextUnprotectedIndex = (startIndex: number): number => {
    let nextIndex = (startIndex + 1) % fields.length;
    let loopCount = 0;
    while (fields[nextIndex].protected && loopCount < fields.length) {
      nextIndex = (nextIndex + 1) % fields.length;
      loopCount++;
    }
    return fields[nextIndex].protected ? startIndex : nextIndex;
  };

  const findPrevUnprotectedIndex = (startIndex: number): number => {
    let prevIndex = (startIndex - 1 + fields.length) % fields.length;
    let loopCount = 0;
    while (fields[prevIndex].protected && loopCount < fields.length) {
      prevIndex = (prevIndex - 1 + fields.length) % fields.length;
      loopCount++;
    }
    return fields[prevIndex].protected ? startIndex : prevIndex;
  };

  const handleUpdate = (index: number, text: string) => {
    dispatch(updateFieldText({ index, text }));
  };

  const handleAutoSkip = (index: number) => {
     const nextIndex = findNextUnprotectedIndex(index);
     if (nextIndex !== index) {
        setFocusedIndex(nextIndex);
     }
  };

  const handleMoveNext = (currentIndex: number) => {
     const nextIndex = findNextUnprotectedIndex(currentIndex);
     if (nextIndex !== currentIndex) {
        setFocusedIndex(nextIndex);
     }
  };

  const handleMovePrev = (currentIndex: number) => {
     const prevIndex = findPrevUnprotectedIndex(currentIndex);
     if (prevIndex !== currentIndex) {
        setFocusedIndex(prevIndex);
     }
  };

  const handleFunctionKey = (key: string) => {
      const currentField = fields[focusedIndex];
      const cursor = currentField ? currentField.start : 1;
      dispatch(sendKeystroke({ key, cursor }));
  };

  const handleSubmit = () => {
      dispatch(submitScreen());
  };

  const rows = [];
  // Ensure content has 1920 chars (24 * 80)
  const paddedContent = content ? content.padEnd(1920, ' ') : ' '.repeat(1920);

  for (let i = 0; i < 24; i++) {
     rows.push(paddedContent.substring(i * 80, (i + 1) * 80));
  }

  if (status === 'connecting') {
     return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-900 p-8">
            <div className="text-white text-xl font-mono p-8">Connecting to host...</div>
        </div>
     );
  }

  if (status === 'error') {
     return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-900 p-8">
            <div className="text-red-500 text-xl font-mono p-8">Connection Error. Please refresh.</div>
            <div className="text-gray-500 text-sm">{sessionId ? `Session: ${sessionId}` : ''}</div>
        </div>
     );
  }

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-900 p-8">
      <h2 className="mb-4 text-xl font-bold text-gray-400 self-start w-full max-w-5xl">
        3270 Terminal Session (New)
      </h2>
      <div className="bg-gray-800 p-4 rounded-lg shadow-xl border border-gray-700 overflow-hidden relative">
        <div className="relative bg-black text-green-500 font-mono shadow-2xl" style={{
            width: '80ch',
            height: '28.8em', // 24 rows * 1.2em
            fontSize: '16px',
            lineHeight: '1.2em',
            userSelect: 'text',
            whiteSpace: 'pre'
        }}>
            {/* Background Layer */}
            <div className="absolute inset-0 z-0" style={{ letterSpacing: '0' }}>
                {rows.map((row, i) => (
                    <div key={i} style={{ height: '1.2em' }}>{row}</div>
                ))}
            </div>

            {/* Fields Layer */}
            {fields.map((field, index) => (
                <TerminalField
                    key={`${sessionId}-${field.fieldId}`}
                    index={index}
                    field={field}
                    isActive={index === focusedIndex}
                    onUpdate={handleUpdate}
                    onFocus={setFocusedIndex}
                    onSubmit={handleSubmit}
                    onFunctionKey={handleFunctionKey}
                    onAutoSkip={() => handleAutoSkip(index)}
                    onMoveToNext={() => handleMoveNext(index)}
                    onMoveToPrevious={() => handleMovePrev(index)}
                />
            ))}

            {/* Wait Indicator */}
            {waitingStatus && (
                 <div className="absolute bottom-2 right-2 px-2 py-1 text-xs font-bold text-black bg-yellow-400 z-50 rounded shadow animate-pulse">
                    WAIT
                 </div>
            )}
        </div>
      </div>
       <div className="mt-6 text-sm text-gray-500 max-w-5xl w-full flex justify-between">
            <span>Session: {sessionId || 'N/A'}</span>
            <span>Cursor: {cursorPos}</span>
            <span>Status: {status}</span>
        </div>
    </div>
  );
};
