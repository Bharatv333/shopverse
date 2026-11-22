import { CurrencyPipe } from "@angular/common";
import { Component, input, output } from "@angular/core";
import { RouterLink } from "@angular/router";
import { ProductSummary } from "../api";
import { imageFor } from "./images";

@Component({
    selector: 'app-product-card',
    imports: [RouterLink, CurrencyPipe],
    template: `
        @let p = product();
        <article class="card">
            <a [routerLink]="['/products', p.id]">
                <img [src]="image(p)" [alt]="p.name" width="300" height="200" loading="lazy"/>
                <h3>{{p.name}}</h3>
            </a>
            <p class="muted">{{p.categoryName}} @if (p.brand) { · {{p.brand}} }</p>
            <p class="price">{{p.price | currency: p.currency : 'symbol' : '1.0-2'}}</p>
            <button (click)="addToCart.emit(p)">Add to cart</button>
        </article>
    `
})
export class ProductCard{
    // Signal input: the parent MUST pass it ([product]="..."), and we read it as product()
    readonly product = input.required<ProductSummary>();
    // Signal-era output: the parent listens with (addToCart)="..."
    readonly addToCart = output<ProductSummary>();

    protected readonly image = (p: ProductSummary) => imageFor(p.slug, p.mainImage);
}