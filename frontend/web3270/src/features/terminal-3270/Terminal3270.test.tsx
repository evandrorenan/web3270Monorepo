import React from 'react';
import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import sessionReducer from '../../entities/session/model/slice';
import { Terminal3270 } from './Terminal3270';
import { describe, it, expect } from 'vitest';

const createTestStore = (initialState: any) => {
  return configureStore({
    reducer: {
      session: sessionReducer,
    },
    preloadedState: {
      session: initialState,
    },
  });
};

describe('Terminal3270', () => {
  it('renders fields and content', () => {
    const initialState = {
      sessionId: 'test-session',
      fields: [
        {
          fieldId: 1,
          start: 81,
          end: 90,
          row: 2,
          col: 1,
          length: 10,
          text: 'Login:',
          hidden: false,
          protected: true,
          highLight: false,
          modified: false,
        },
        {
          fieldId: 2,
          start: 92,
          end: 101,
          row: 2,
          col: 12,
          length: 10,
          text: 'User1',
          hidden: false,
          protected: false,
          highLight: true,
          modified: true,
        },
      ],
      content: '                                                                                Login:      User1                                                                               ',
      cursorPos: 92,
      status: 'connected',
      error: null,
      waitingStatus: false,
    };

    const store = createTestStore(initialState);
    render(
      <Provider store={store}>
        <Terminal3270 />
      </Provider>
    );

    // Check if fields are rendered
    // Protected field might be rendered as input but readOnly or just text?
    // TerminalField renders input for ALL fields currently.
    // 'Login:'
    expect(screen.getByDisplayValue('Login:')).toBeInTheDocument();

    // 'User1'
    expect(screen.getByDisplayValue('User1')).toBeInTheDocument();
  });

  it('shows connecting status', () => {
      const initialState = {
          status: 'connecting',
          fields: [],
          content: '',
          cursorPos: 1,
          sessionId: null,
          error: null,
          waitingStatus: false
      };

      const store = createTestStore(initialState);
      render(
          <Provider store={store}>
              <Terminal3270 />
          </Provider>
      );

      expect(screen.getByText(/Connecting to host/i)).toBeInTheDocument();
  });
});
