import { effect } from '@angular/core';
import { getState, patchState, signalStoreFeature, withHooks } from '@ngrx/signals';

// A reusable store feature: add it to ANY SignalStore and its whole state is saved and restored.
export function withLocalStorage(key: string) {
  return signalStoreFeature(
    withHooks({
      onInit(store) {
        try {
          const saved = localStorage.getItem(key);
          if (saved) patchState(store, JSON.parse(saved));
        } catch {
          localStorage.removeItem(key);           // broken data: forget it
        }
        effect(() => localStorage.setItem(key, JSON.stringify(getState(store))));
      },
    }),
  );
}