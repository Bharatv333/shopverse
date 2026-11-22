import { Component, inject, input } from "@angular/core";
import { ProductsService } from "../api";
import { CartStore } from "../cart/cart.store";
import { rxResource } from "@angular/core/rxjs-interop";
import { map } from "rxjs";
import { ProductCard } from "./product-card";

@Component({
    selector: 'app-related-products',
    imports: [ProductCard],
    template: `
        <div class="grid">
            @for(r of related.value() ?? []; track r.id){
                <app-product-card [product]="r" (addToCart)="cart.add($event)" />
            }
            @empty {
                @if(!related.isLoading()){
                    <p class="muted">No other products in this category yet.</p>                        
                }
            }
        </div>
    `
})
export class RelatedProducts{
    private readonly productsApi = inject(ProductsService);
    protected readonly cart = inject(CartStore);

    readonly categoryId = input.required<number | undefined>();
    readonly excludeId = input.required<number | undefined>();

    protected readonly related = rxResource({
        params: () => ({categoryId: this.categoryId(), excludeId: this.excludeId()}),
        stream: ({params: p}) => 
            this.productsApi.listProducts(p.categoryId, undefined, undefined, undefined, 0, 5)
            .pipe(map(page => page.content.filter(r => r.id !== p.excludeId).slice(0,4)))
    });
}