import React, { useRef } from 'react';
import { Field as FieldType } from '../model/slice';
import { cn } from '../../../shared/lib/utils';

interface FieldProps {
  field: FieldType;
  index: number;
  onUpdate: (index: number, text: string) => void;
  onFocus: (index: number) => void;
  onSubmit: () => void;
  onFunctionKey: (key: string) => void;
}

export const Field: React.FC<FieldProps> = React.memo(({
  field,
  index,
  onUpdate,
  onFocus,
  onSubmit,
  onFunctionKey
}) => {
  const inputRef = useRef<HTMLInputElement>(null);

  // Focus handling could be done via parent effect or imperative handle,
  // but for now let's rely on standard focus events.

  // Map 3270 colors to Tailwind classes
  // This is a simplified mapping. Real 3270 has complex attribute bytes.
  // Assuming 'highLight' means intensified.
  const getColorClass = () => {
      if (field.highLight) return 'text-white font-bold';
      if (field.hidden) return 'text-transparent';
      // Default green for terminal
      return 'text-green-500';
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    // Handle Function Keys (F1-F12)
    if (e.key.startsWith('F') && !isNaN(Number(e.key.substring(1)))) {
        e.preventDefault();
        onFunctionKey(e.key);
        return;
    }

    // Handle Enter
    if (e.key === 'Enter') {
        e.preventDefault();
        onSubmit();
        return;
    }

    // Handle Tab/Arrow navigation (native browser behavior handles some, but we might need custom logic)
    // For now, let's rely on native tab index order which follows DOM order.
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      onUpdate(index, e.target.value);
  };

  return (
    <input
      ref={inputRef}
      id={`field-${index}`}
      type={field.hidden ? 'password' : 'text'}
      value={field.text}
      maxLength={field.length}
      onChange={handleChange}
      onFocus={() => onFocus(index)}
      onKeyDown={handleKeyDown}
      className={cn(
        'font-mono bg-black border-none outline-none p-0 m-0 leading-none block',
        getColorClass(),
        {
           'underline': field.modified,
           'cursor-text': !field.hidden, // Simplified logic
        }
      )}
      style={{
        gridColumn: `span ${field.length}`,
        width: '100%' // Fill the grid cell
      }}
      autoComplete="off"
      spellCheck={false}
    />
  );
}, (prev, next) => {
    // Custom memo comparison for performance
    return (
        prev.field.text === next.field.text &&
        prev.field.highLight === next.field.highLight &&
        prev.field.hidden === next.field.hidden &&
        prev.field.modified === next.field.modified &&
        prev.field.start === next.field.start
    );
});

Field.displayName = 'Field';
