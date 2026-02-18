import React, { useEffect, useRef } from 'react';
import { useAppDispatch, useAppSelector } from '../../../shared/model/hooks';
import { connect, sendKeystroke, submitScreen, updateFieldText } from '../model/slice';
import { Field as FieldComponent } from './Field';

export const Screen: React.FC = () => {
  const dispatch = useAppDispatch();
  const { fields, cursorPos, status, waitingStatus, sessionId } = useAppSelector((state) => state.session);
  const screenRef = useRef<HTMLDivElement>(null);

  // Initial connection
  useEffect(() => {
    // Only connect if not already connected or connecting
    if (status === 'disconnected') {
      dispatch(connect());
    }
  }, [dispatch, status]);

  const handleUpdate = (index: number, text: string) => {
    dispatch(updateFieldText({ index, text }));
  };

  const handleFocus = (_index: number) => {
    // Optionally update focusedField in Redux if needed
  };

  const handleSubmit = () => {
     dispatch(submitScreen());
  };

  const handleFunctionKey = (key: string) => {
     dispatch(sendKeystroke({ key, cursor: cursorPos }));
  };

  if (status === 'connecting') {
     return <div className="text-white">Connecting to terminal...</div>;
  }

  if (status === 'error') {
     return <div className="text-red-500">Connection Error. Please refresh.</div>;
  }

  return (
    <div className="relative inline-block border-8 border-gray-800 rounded bg-black p-2 shadow-2xl">
        <div
          className="bg-black text-green-500 font-mono"
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(80, 1ch)',
            gridAutoRows: '1.2em', // slightly taller for readability
            gap: 0,
            width: '80ch', // Fixed width for 80 chars
            height: '24.2em', // 24 rows approx
            overflow: 'hidden',
            userSelect: 'text',
          }}
          ref={screenRef}
        >
          {fields.map((field, index) => (
            <FieldComponent
              key={`${sessionId}-${field.fieldId}-${index}`} // Re-render on session change
              index={index}
              field={field}
              onUpdate={handleUpdate}
              onFocus={handleFocus}
              onSubmit={handleSubmit}
              onFunctionKey={handleFunctionKey}
            />
          ))}
        </div>

        {waitingStatus && (
             <div className="absolute top-2 right-2 px-2 py-1 text-xs font-bold text-black bg-yellow-400 rounded animate-pulse">
                WAIT
             </div>
        )}

        <div className="mt-2 text-xs text-gray-500 flex justify-between">
            <span>Session: {sessionId || 'N/A'}</span>
            <span>Cursor: {cursorPos}</span>
            <span>Status: {status}</span>
        </div>
    </div>
  );
};
