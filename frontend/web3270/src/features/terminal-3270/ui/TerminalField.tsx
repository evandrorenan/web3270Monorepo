import React, { useRef, useEffect } from 'react';
import { Field } from '../../../entities/session/model/slice';
import { cn } from '../../../shared/lib/utils';

interface TerminalFieldProps {
  field: Field;
  index: number;
  isActive: boolean;
  onUpdate: (index: number, text: string) => void;
  onFocus: (index: number) => void;
  onSubmit: () => void;
  onFunctionKey: (key: string) => void;
  onAutoSkip: (currentIndex: number) => void;
  onMoveToPrevious: (currentIndex: number) => void;
  onMoveToNext: (currentIndex: number) => void;
}

export const TerminalField: React.FC<TerminalFieldProps> = React.memo(({
  field,
  index,
  isActive,
  onUpdate,
  onFocus,
  onSubmit,
  onFunctionKey,
  onAutoSkip,
  onMoveToPrevious,
  onMoveToNext
}) => {
  const inputRef = useRef<HTMLInputElement>(null);

  // Focus management
  useEffect(() => {
    if (isActive && inputRef.current && !field.protected) {
      inputRef.current.focus();
    }
  }, [isActive, field.protected]);

  const getColorClass = () => {
    if (field.highLight) return 'text-white font-bold';
    if (field.hidden) return 'text-transparent';
    if (field.text === '' && field.highLight) return 'bg-gray-700';
    return 'text-green-500';
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    // If protected, should not be receiving events ideally, but just in case
    if (field.protected) return;

    if (e.key.startsWith('F') && !isNaN(Number(e.key.substring(1)))) {
      e.preventDefault();
      onFunctionKey(e.key);
      return;
    }

    if (e.key === 'Enter') {
      e.preventDefault();
      onSubmit();
      return;
    }

    if (e.key === 'Tab') {
      e.preventDefault();
      if (e.shiftKey) {
        onMoveToPrevious(index);
      } else {
        onMoveToNext(index);
      }
      return;
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (field.protected) return;

    const newValue = e.target.value;
    if (newValue.length <= field.length) {
      onUpdate(index, newValue);
      if (newValue.length === field.length) {
         onAutoSkip(index);
      }
    }
  };

  return (
    <input
      ref={inputRef}
      id={`field-${index}`}
      type={field.hidden ? 'password' : 'text'}
      value={field.text}
      maxLength={field.length}
      onChange={handleChange}
      onFocus={() => {
          if (!field.protected) {
             onFocus(index);
          }
      }}
      onKeyDown={handleKeyDown}
      readOnly={field.protected}
      tabIndex={field.protected ? -1 : 0}
      className={cn(
        'font-mono border-none outline-none p-0 m-0 leading-none block absolute bg-black',
        getColorClass(),
        {
           'underline': field.modified,
           'cursor-text': !field.hidden && !field.protected,
        }
      )}
      style={{
        top: `calc(${field.row - 1} * 1.2em)`,
        left: `${field.col - 1}ch`,
        width: `${field.length}ch`,
        height: '1.2em',
        zIndex: field.protected ? 5 : 10, // Editable fields on top
      }}
      autoComplete="off"
      spellCheck={false}
    />
  );
}, (prev, next) => {
  return (
    prev.field === next.field &&
    prev.isActive === next.isActive &&
    prev.index === next.index
  );
});

TerminalField.displayName = 'TerminalField';
