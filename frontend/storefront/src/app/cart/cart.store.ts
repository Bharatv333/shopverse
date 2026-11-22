import { computed } from "@angular/core";
import { patchState, signalStore, withComputed, withMethods, withState } from "@ngrx/signals";
import { withLocalStorage } from "../shared/with-local-storage";

export interface CartItem{
    productId: number;
    name: string;
    price: number;
    currency: string;
    image?: string;
    qty: number;
}

// What the cart needs from a product. Both ProductSummary and ProductDetails fit this shape.
export interface CartProduct{
    id?: number
    name?: string;
    price?: number;
    currency?: string;
    mainImage?: string;
    images?: string[];
}

export const MAX_QTY = 10;



export const CartStore = signalStore(
    { providedIn: 'root'},                                           // one cart for the whole app
    withState(() => ({ items: [] as CartItem[] })),                 // state = plain data. items is now a signal: store.items()
    withComputed(({ items }) => ({                                   // derived values, recalculated only when items change
        count: computed(() => items().reduce((n, i) => n + i.qty, 0)),
        total: computed(() => items().reduce((sum, i) => sum + (i.price * i.qty), 0)),
        isEmpty: computed(() => items().length === 0)
    })),
    withMethods(store => ({                                         // the ONLY way to change state
        add(product: CartProduct, qty = 1): void
        {
            const id = product.id!;
            const existing = store.items().find(i => i.productId === id);
            if(existing)
            {
                this.setQty(id, existing.qty + qty);
                return;
            }
            const item: CartItem = {
                productId: id,
                name: product.name ?? '',
                price: product.price ?? 0,
                currency: product.currency ?? 'INR',
                image: product.mainImage ?? product.images?.[0],
                qty: Math.min(qty, MAX_QTY),
            };
            patchState(store, { items: [...store.items(), item] });         // new array: never mutate state
        },
        setQty(productId: number, qty: number): void
        {
            if(qty <= 0)
            {
                this.remove(productId);
                return;
            }
            patchState(store, {
                items: store.items().map(i => (i.productId === productId ? {...i, qty: Math.min(qty, MAX_QTY)} : i))
            });
        },
        remove(productId: number): void
        {
            patchState(store, { items: store.items().filter(i => i.productId !== productId)});
        },
        clear(): void{
            patchState(store, { items: [] });
        }
    })),
    withLocalStorage('shopverse.cart')
);