import { describe, it, expect } from 'vitest';
import sessionReducer, { connect, connectSuccess, updateScreen, Field } from './slice';

describe('session reducer', () => {
  const initialState = {
    sessionId: null,
    fields: [],
    content: '',
    cursorPos: 1,
    status: 'disconnected' as const,
    error: null,
    waitingStatus: false,
  };

  it('should handle initial state', () => {
    expect(sessionReducer(undefined, { type: 'unknown' })).toEqual(initialState);
  });

  it('should handle connect', () => {
    const actual = sessionReducer(initialState, connect());
    expect(actual.status).toEqual('connecting');
    expect(actual.error).toBeNull();
  });

  it('should handle connectSuccess', () => {
    const actual = sessionReducer(initialState, connectSuccess('session-123'));
    expect(actual.status).toEqual('connected');
    expect(actual.sessionId).toEqual('session-123');
  });

  it('should handle updateScreen', () => {
    const fields: Field[] = [{
       fieldId: 1, start: 0, end: 10, row: 1, col: 1, length: 10,
       text: 'Hello', hidden: false, highLight: false, modified: false, protected: false
    }];
    const content = 'Hello World';
    const actual = sessionReducer(initialState, updateScreen({ fields, content, cursorPos: 5 }));
    expect(actual.fields).toEqual(fields);
    expect(actual.content).toEqual(content);
    expect(actual.cursorPos).toEqual(5);
    expect(actual.waitingStatus).toBe(false);
  });
});
