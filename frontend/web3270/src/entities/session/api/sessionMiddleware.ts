import { Middleware } from '@reduxjs/toolkit';
import { Client, IMessage } from '@stomp/stompjs';
import {
  connect,
  disconnect,
  connectSuccess,
  connectFailure,
  updateScreen,
  sendKeystroke,
  submitScreen,
  SessionState,
} from '../model/slice';

let stompClient: Client | null = null;

export const sessionMiddleware: Middleware = (store) => (next) => (action: unknown) => {
  // 1. Handle Connect
  if (connect.match(action)) {
    if (stompClient?.active) {
      return next(action);
    }

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
      // We assume the initial connection provides a session ID or we subscribe generally
      // Based on old code: send message to /subscribe/{sessionId}
      // Here we might need a handshake first.

      // Subscribe to screen updates
      stompClient?.subscribe('/topic/screens', (message: IMessage) => {
        if (message.body) {
          try {
            const data = JSON.parse(message.body);
            // Dispatch updateScreen with the fields and cursor
            store.dispatch(updateScreen(data));

            // If sessionId is present, confirm connection success
            if (data.sessionId) {
               store.dispatch(connectSuccess(data.sessionId));
            }
          } catch (e) {
            console.error('Failed to parse screen update', e);
          }
        }
      });
    };

    stompClient.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      store.dispatch(connectFailure(frame.headers['message']));
    };

    stompClient.activate();
  }

  // 2. Handle Disconnect
  if (disconnect.match(action)) {
    if (stompClient) {
      stompClient.deactivate();
      stompClient = null;
    }
  }

  // 3. Handle Send Keystroke (AID keys like Enter, F1-F12)
  if (sendKeystroke.match(action)) {
    if (stompClient?.active) {
      const state = store.getState() as { session: SessionState };
      const sessionId = state.session.sessionId;

      if (sessionId) {
        stompClient.publish({
          destination: `/app/keystroke/${sessionId}`,
          body: JSON.stringify(action.payload), // { key: 'ENTER', cursor: 123 }
        });
      }
    }
  }

  // 4. Handle Submit Screen (Send modified fields)
  if (submitScreen.match(action)) {
    if (stompClient?.active) {
      const state = store.getState() as { session: SessionState };
      const { sessionId, fields, cursorPos } = state.session;

      // Filter only modified fields to send
      const modifiedFields = fields.filter(f => f.modified).map(f => ({
        fieldId: f.fieldId,
        text: f.text
      }));

      if (sessionId) {
        stompClient.publish({
          destination: `/app/submit/${sessionId}`,
          body: JSON.stringify({
            fields: modifiedFields,
            cursorPos: cursorPos
          }),
        });
      }
    }
  }

  return next(action);
};
