import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { ProductSummary } from '../api';
import { withLocalStorage } from './with-local-storage';

export const RecentlyViewedStore = signalStore(
  { providedIn: 'root' },
  withState({ products: [] as ProductSummary[] }),
  withMethods(store => ({
    track(p: ProductSummary): void {
      const others = store.products().filter(x => x.id !== p.id);   // no duplicates
      patchState(store, { products: [p, ...others].slice(0, 4) });   // newest first, keep 4
    },
  })),
  withLocalStorage('shopverse.recent'),
);