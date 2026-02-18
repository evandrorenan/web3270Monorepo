import { PropsWithChildren } from 'react';
import { render } from '@testing-library/react';
import type { RenderOptions } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';

import sessionReducer from '../../entities/session/model/slice';
import type { RootState } from '../../app/providers/store/store';

// This type interface extends the default options for render from RTL, as well
// as allows the user to specify other things such as initialState, store.
interface ExtendedRenderOptions extends Omit<RenderOptions, 'queries'> {
  preloadedState?: Partial<RootState>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  store?: any;
}

export function renderWithProviders(
  ui: React.ReactElement,
  {
    preloadedState = {},
    store,
    ...renderOptions
  }: ExtendedRenderOptions = {}
) {
  const testStore = store ?? configureStore({
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    reducer: { session: sessionReducer } as any,
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    preloadedState: preloadedState as any
  });

  function Wrapper({ children }: PropsWithChildren): JSX.Element {
    return <Provider store={testStore}>{children}</Provider>;
  }

  return { store: testStore, ...render(ui, { wrapper: Wrapper, ...renderOptions }) };
}
