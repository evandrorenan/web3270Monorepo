import { Middleware } from '@reduxjs/toolkit';
import { Client, IMessage } from '@stomp/stompjs';
import axios from 'axios';
import {
  connect,
  disconnect,
  connectSuccess,
  connectFailure,
  updateScreen,
  sendKeystroke,
  SessionState,
} from '../model/slice';

let stompClient: Client | null = null;

// Helper to convert backend DTOs to our state format
const adaptScreenData = (data: any) => {
  const mapField = (f: any, index: number) => {
    // Determine start/end/length
    const start = f.start || 0;
    const end = f.end || 0;
    const length = end - start + 1; // Simplification, assuming linear

    // Determine row/col (1-based)
    // start is 1-based index
    const row = Math.ceil(start / 80);
    const col = ((start - 1) % 80) + 1;

    return {
      fieldId: f.fieldId || index,
      start,
      end,
      row,
      col,
      length: length > 0 ? length : 0,
      text: f.text || '',
      hidden: f.isHidden || f.hidden || false,
      protected: f.isProtected || f.protected || false,
      highLight: f.isHighIntensity || f.highIntensity || f.isHighLight || f.highLight || false,
      modified: false
    };
  };

  let fields = [];
  let content = '';
  let cursorPos = 1;

  // If it's ScreenResponse (from Controller)
  if (data.content !== undefined) {
    fields = (data.fields || []).map(mapField);
    content = data.content;
    cursorPos = data.cursorPosition;
  }
  // If it's ScreenDto (from Listener)
  else if (data.positions) {
    // Reconstruct content string from positions
    content = data.positions.map((p: any) => p.text).join('');
    fields = (data.fields || []).map(mapField);
    cursorPos = data.cursorPos || 1;
  }

  return {
    fields,
    content,
    cursorPos
  };
};

export const sessionMiddleware: Middleware = (store) => (next) => async (action: unknown) => {
  // 1. Handle Connect
  if (connect.match(action)) {
    if (stompClient?.active) {
      return next(action);
    }

    try {
      // 1. Create Session via REST
      // Use defaults as per backend config or common values
      const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
      const sessionResponse = await axios.post(`${apiUrl}/api/v1/sessions`, {
        host: '192.168.240.1', // Default from backend config
        port: '51004',
        type: '1',
        codePage: '037'
      });

      const sessionId = sessionResponse.data.sessionId;
      store.dispatch(connectSuccess(sessionId));

      // 2. Connect WebSocket
      const wsUrl = import.meta.env.VITE_WS_URL || 'ws://localhost:8080/web3270-websocket';

      stompClient = new Client({
        brokerURL: wsUrl,
        reconnectDelay: 5000,
        debug: (str) => {
          console.log(str);
        },
      });

      stompClient.onConnect = (frame) => {
        console.log('Connected: ' + frame);

        // Subscribe to Controller updates (Response/Request pattern)
        stompClient?.subscribe(`/topic/session/${sessionId}/screen`, (message: IMessage) => {
          if (message.body) {
            try {
              const data = JSON.parse(message.body);
              const screenData = adaptScreenData(data);
              store.dispatch(updateScreen(screenData));
            } catch (e) {
              console.error('Failed to parse screen update', e);
            }
          }
        });

        // Subscribe to Listener updates (Push pattern)
        stompClient?.subscribe(`/queue/session/${sessionId}`, (message: IMessage) => {
           if (message.body) {
             try {
               const data = JSON.parse(message.body);
               const screenData = adaptScreenData(data);
               store.dispatch(updateScreen(screenData));
             } catch (e) {
               console.error('Failed to parse listener update', e);
             }
           }
        });

        // Request initial screen
        stompClient?.publish({
            destination: `/app/session/${sessionId}/screen`,
            body: '{}'
        });
      };

      stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        store.dispatch(connectFailure(frame.headers['message']));
      };

      stompClient.activate();

    } catch (error: any) {
      console.error('Failed to create session', error);
      store.dispatch(connectFailure(error.message || 'Failed to create session'));
    }
  }

  // 2. Handle Disconnect
  if (disconnect.match(action)) {
    if (stompClient) {
      stompClient.deactivate();
      stompClient = null;
      // Should also call DELETE /api/v1/sessions/{sessionId}
      const state = store.getState() as { session: SessionState };
      const sessionId = state.session.sessionId;
      if (sessionId) {
          const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
          axios.delete(`${apiUrl}/api/v1/sessions/${sessionId}`).catch(console.error);
      }
    }
  }

  // 3. Handle Send Keystroke
  if (sendKeystroke.match(action)) {
    if (stompClient?.active) {
      const state = store.getState() as { session: SessionState };
      const { sessionId, fields } = state.session;
      const { key } = action.payload; // key is e.g. '[enter]', '[pf1]'

      if (sessionId) {
        const sendKeysList = [];

        // 1. Add modified fields
        // We need to know where to type. row/col from field.
        fields.forEach(f => {
            if (f.modified) {
                // Send text at field position
                sendKeysList.push({
                    row: f.row,
                    col: f.col,
                    text: f.text,
                    functionKey: null
                });
            }
        });

        // 2. Add the Action Key (at cursor position or just global?)
        sendKeysList.push({
            row: 0,
            col: 0,
            text: key,
            functionKey: null
        });

        const payload = {
            sessionId,
            sendKeys: sendKeysList
        };

        stompClient.publish({
          destination: `/app/sendkeys`,
          body: JSON.stringify(payload),
        });

        // Optimistically clear modified flags? Or wait for screen update?
        // Redux state will be replaced by screen update anyway.
      }
    }
  }

  return next(action);
};
