import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export interface Field {
  fieldId: number;
  start: number;
  end: number;
  row: number;
  col: number;
  length: number;
  text: string;
  hidden: boolean;
  highLight: boolean;
  modified: boolean;
  // ref removed as it's non-serializable and shouldn't be in Redux
}

export interface SessionState {
  sessionId: string | null;
  fields: Field[];
  cursorPos: number;
  status: 'disconnected' | 'connecting' | 'connected' | 'error';
  error: string | null;
  waitingStatus: boolean;
}

const initialState: SessionState = {
  sessionId: null,
  fields: [],
  cursorPos: 1,
  status: 'disconnected',
  error: null,
  waitingStatus: false,
};

export const sessionSlice = createSlice({
  name: 'session',
  initialState,
  reducers: {
    connect: (state) => {
      state.status = 'connecting';
      state.error = null;
    },
    connectSuccess: (state, action: PayloadAction<string>) => {
      state.status = 'connected';
      state.sessionId = action.payload;
    },
    connectFailure: (state, action: PayloadAction<string>) => {
      state.status = 'error';
      state.error = action.payload;
    },
    disconnect: (state) => {
      state.status = 'disconnected';
      state.sessionId = null;
      state.fields = [];
    },
    updateScreen: (state, action: PayloadAction<{ fields: Field[]; cursorPos: number }>) => {
      state.fields = action.payload.fields;
      state.cursorPos = action.payload.cursorPos;
      state.waitingStatus = false;
    },
    updateFieldText: (state, action: PayloadAction<{ index: number; text: string }>) => {
      if (state.fields[action.payload.index]) {
        state.fields[action.payload.index].text = action.payload.text;
        state.fields[action.payload.index].modified = true;
      }
    },
    setWaiting: (state, action: PayloadAction<boolean>) => {
      state.waitingStatus = action.payload;
    },
    // Action to be dispatched by UI to send keystrokes (intercepted by middleware)
    sendKeystroke: (_state, _action: PayloadAction<{ key: string; cursor: number }>) => {
      // Logic handled in middleware
    },
    submitScreen: (state) => {
      state.waitingStatus = true;
    }
  },
});

export const {
  connect,
  connectSuccess,
  connectFailure,
  disconnect,
  updateScreen,
  updateFieldText,
  setWaiting,
  sendKeystroke,
  submitScreen
} = sessionSlice.actions;

export default sessionSlice.reducer;
