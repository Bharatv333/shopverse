import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { CategoriesService, ProductsService } from '../api';
import { CartStore } from '../cart/cart.store';
import { ProductCard } from '../shared/product-card';
import { RecentlyViewedStore } from '../shared/recently-viewed.store';

@Component({
  selector: 'app-home',
  imports: [RouterLink, ProductCard],
  template: `
    <section class="hero">
      <h1>Welcome to ShopVerse</h1>
      <p>Laptops, books, fashion and more, delivered across India.</p>
      <a routerLink="/products" class="btn">Shop all products</a>
    </section>

    <h2>Shop by category</h2>
    <div class="chips">
      @for (c of categories.value() ?? []; track c.id) {
        <a class="chip" routerLink="/products" [queryParams]="{ category: c.id }">{{ c.name }}</a>
      }
    </div>
    @if (recent.products().length) {
      <h2>Recently viewed</h2>
      <div class="grid">
        @for (p of recent.products(); track p.id) {
          <app-product-card [product]="p" (addToCart)="cart.add($event)" />
        }
      </div>
    }
    <h2>Top rated</h2>
    @if (topRated.hasValue()) {
      <div class="grid">
        @for (p of topRated.value().content; track p.id) {
          <app-product-card [product]="p" (addToCart)="cart.add($event)" />
        }
      </div>
    } @else if (topRated.error()) {
      <p class="error">Could not load products.</p>
    } @else {
      <p class="muted">Loading...</p>
    }
  `,
})
export class Home {
  private readonly productsApi = inject(ProductsService);
  private readonly categoriesApi = inject(CategoriesService);
  protected readonly cart = inject(CartStore);

  protected readonly categories = rxResource({ stream: () => this.categoriesApi.listCategories() });
  protected readonly topRated = rxResource({
    stream: () => this.productsApi.listProducts(undefined, undefined, undefined, undefined, 0, 4, ['avgRating,desc']),
  });

  private readonly recent = inject(RecentlyViewedStore);
}