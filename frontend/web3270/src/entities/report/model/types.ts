export type LineType = 'division' | 'section' | 'paragraph' | 'normal' | 'blank' | 'abend';

export interface Line {
  num: number;
  text: string;
  type: LineType;
  varNames?: string[];
}

export interface VariableData {
  value: string;
  pic: string;
  offset: string;
  corrupt?: boolean;
}

export type VariablesMap = Record<string, VariableData>;

export interface Program {
  name: string;
  abendCode: string;
  abendOffset: string;
  timestamp: string;
  lines: Line[];
}

export interface VariableEntry {
  varName: string;
  varData: VariableData;
}
