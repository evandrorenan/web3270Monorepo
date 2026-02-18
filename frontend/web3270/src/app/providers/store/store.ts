import { configureStore } from '@reduxjs/toolkit';
import sessionReducer from '../../../entities/session/model/slice';
import { sessionMiddleware } from '../../../entities/session/api/sessionMiddleware';

export const store = configureStore({
  reducer: {
    session: sessionReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(sessionMiddleware),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
