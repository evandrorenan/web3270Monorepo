import { screen } from '@testing-library/react';
import { renderWithProviders } from '../../../shared/lib/test-utils';
import { Screen } from './Screen';
import { describe, it, expect } from 'vitest';

describe('Screen', () => {
  it('should render loading status initially', () => {
    renderWithProviders(<Screen />);
    expect(screen.getByText(/Connecting/i)).toBeInTheDocument();
  });

  it('should render fields when connected', () => {
    const fields = [
       { fieldId: 1, start: 0, end: 5, row: 1, col: 1, length: 5, text: 'Hello', hidden: false, highLight: false, modified: false },
       { fieldId: 2, start: 6, end: 11, row: 1, col: 7, length: 5, text: 'World', hidden: false, highLight: true, modified: false }
    ];

    renderWithProviders(<Screen />, {
       preloadedState: {
          session: {
            sessionId: '123',
            fields,
            cursorPos: 1,
            status: 'connected',
            waitingStatus: false,
            error: null
          }
       }
    });

    expect(screen.getByDisplayValue('Hello')).toBeInTheDocument();
    expect(screen.getByDisplayValue('World')).toBeInTheDocument();
  });

  it('should dispatch connect on mount if disconnected', () => {
    const { store } = renderWithProviders(<Screen />, {
        preloadedState: {
            session: {
                sessionId: null,
                fields: [],
                cursorPos: 1,
                status: 'disconnected',
                waitingStatus: false,
                error: null
            }
        }
    });

    // We can spy on dispatch or check state changes if reducer handles it synchronously.
    // Since connect is async via middleware, reducer updates status to 'connecting'.
    const state = store.getState();
    expect(state.session.status).toBe('connecting');
  });
});
