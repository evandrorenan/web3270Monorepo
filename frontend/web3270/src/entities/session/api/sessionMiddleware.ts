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

import { MockSessionService } from './mockSessionService';

let stompClient: Client | null = null;

const adaptScreenData = (data: any) => {
  const mapField = (f: any, index: number) => {
    const start = f.start || 0;
    const end = f.end || 0;
    const length = end - start + 1;

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
      color: f.color || undefined,
      modified: false
    };
  };

  let fields = [];
  let content = '';
  let cursorPos = 1;

  if (data.content !== undefined) {
    fields = (data.fields || []).map(mapField);
    content = data.content;
    cursorPos = data.cursorPos;
  }
  else if (data.positions) {
    content = data.positions.map((p: any) => p.text).join('');
    fields = (data.fields || []).map(mapField);
    cursorPos = data.cursorPos || 1;
  }
  else if (data.fields && data.content) {
      fields = (data.fields || []).map(mapField);
      content = data.content;
      cursorPos = data.cursorPos || 1;
  }

  return {
    fields,
    content,
    cursorPos
  };
};

export const sessionMiddleware: Middleware = (store) => (next) => async (action: unknown) => {
  const useMock = import.meta.env.VITE_USE_MOCK === 'true';

  if (connect.match(action)) {
    if (stompClient?.active) {
      return next(action);
    }

    if (useMock) {
        try {
            const session = await MockSessionService.connect();
            store.dispatch(connectSuccess(session.sessionId));

            const screen = await MockSessionService.getScreen();
            store.dispatch(updateScreen(adaptScreenData(screen)));
        } catch (e: any) {
            store.dispatch(connectFailure(e.message));
        }
        return next(action);
    }

    // ... Real implementation ...
    try {
      const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
      const sessionResponse = await axios.post(`${apiUrl}/api/v1/sessions`, {
        host: '192.168.240.1',
        port: '51004',
        type: '1',
        codePage: '037'
      });

      const sessionId = sessionResponse.data.sessionId;
      store.dispatch(connectSuccess(sessionId));

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

  if (disconnect.match(action)) {
    if (useMock) {
        return next(action);
    }
    if (stompClient) {
      stompClient.deactivate();
      stompClient = null;
      const state = store.getState() as { session: SessionState };
      const sessionId = state.session.sessionId;
      if (sessionId) {
          const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
          axios.delete(`${apiUrl}/api/v1/sessions/${sessionId}`).catch(console.error);
      }
    }
  }

  if (sendKeystroke.match(action)) {
    const state = store.getState() as { session: SessionState };
    const sessionId = state.session.sessionId;
    const { key } = action.payload;

    if (useMock && sessionId) {
        MockSessionService.sendKeys(sessionId, key).then((newScreen) => {
             store.dispatch(updateScreen(adaptScreenData(newScreen)));
        });

        return next(action);
    }

    if (stompClient?.active && sessionId) {
        const { fields } = state.session;
        const sendKeysList = [];

        fields.forEach(f => {
            if (f.modified) {
                sendKeysList.push({
                    row: f.row,
                    col: f.col,
                    text: f.text,
                    functionKey: null
                });
            }
        });

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
    }
  }

  return next(action);
};
