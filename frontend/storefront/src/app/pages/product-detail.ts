import { Component, computed, effect, inject, input, linkedSignal, numberAttribute, untracked } from '@angular/core';
import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { httpResource } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { ProductDetails } from '../api';
import { API_URL } from '../api-url';
import { CartStore, MAX_QTY } from '../cart/cart.store';
import { imageFor } from '../shared/images';
import { RelatedProducts } from '../shared/related-products';
import { QtyStepper } from '../shared/qty-stepper';
import { RecentlyViewedStore } from '../shared/recently-viewed.store';

@Component({
  selector: 'app-product-detail',
  imports: [CurrencyPipe, DecimalPipe, RouterLink, QtyStepper, RelatedProducts],
  template: `
    @if (product.hasValue()) {
      @let p = product.value();
      <a routerLink="/products" [queryParams]="{ category: p.categoryId }" class="muted">← {{ p.categoryName }}</a>
      <div class="detail">
        <div class="gallery">
          <img [src]="selectedImage()" [alt]="p.name" width="600" height="400" />
          @if (images().length > 1) {
            <div class="thumbs">
              @for (img of images(); track img) {
                <button type="button" (click)="selectedImage.set(img)" [class.active]="img === selectedImage()">
                  <img [src]="img" alt="" width="90" height="60" />
                </button>
              }
            </div>
          }
        </div>
        <div class="info">
          <h1>{{ p.name }}</h1>
          <p class="muted">@if (p.brand) { {{ p.brand }} · } ★ {{ p.rating | number: '1.1-1' }} ({{ p.reviewCount ?? 0 }} reviews)</p>
          <p class="price big">{{ p.price | currency: p.currency : 'symbol' : '1.0-2' }}</p>
          <p>{{ p.description }}</p>
          <div class="buy">
            <app-qty-stepper [(qty)]="qty" [max]="maxQty" />
            <button class="btn" (click)="addToCart(p)">Add to cart</button>
          </div>
          @if (added()) { <p class="ok">Added to cart. <a routerLink="/cart">View cart</a></p> }
        </div>
      </div>

      <h2>More in {{ p.categoryName }}</h2>
      <!-- @defer: RelatedProducts' code and its HTTP call wait until the user scrolls near it -->
      @defer (on viewport) {
        <app-related-products [categoryId]="p.categoryId" [excludeId]="p.id" />
      } @placeholder {
        <p class="muted">Related products will load when you scroll here.</p>
      }
    } @else if (product.error()) {
      <p class="error">Product not found. <a routerLink="/products">Back to products</a></p>
    } @else {
      <p class="muted">Loading product...</p>
    }
  `,
})
export class ProductDetail {
  private readonly cart = inject(CartStore);
  protected readonly maxQty = MAX_QTY;

  // From the route path /products/:id (withComponentInputBinding). "7" becomes 7.
  readonly id = input.required({ transform: numberAttribute });

  // httpResource: a GET request as a signal. When id() changes, it fetches again by itself.
  // We call the URL directly (not the generated client) because getProduct returns ProductDetails | ProductDetailsV2.
  protected readonly product = httpResource<ProductDetails>(() => `${API_URL}/api/products/${this.id()}`);

  protected readonly images = computed(() => {
    const p = this.product.value();
    return p?.images?.length ? p.images : [imageFor(p?.slug)];
  });

  // linkedSignal: a writable signal that RESETS when its source changes.
  // Click a thumbnail -> it changes. Open another product -> it resets to that product's first image.
  protected readonly selectedImage = linkedSignal(() => this.images()[0]);

  protected readonly qty = linkedSignal({ source: this.id, computation: () => 1 });  // back to 1 on a new product
  protected readonly added = linkedSignal({ source: this.id, computation: () => false }); // hide the message on a new product

  constructor() {
    const recent = inject(RecentlyViewedStore);
    effect(() => {
      const p = this.product.value();
      // untracked: track() READS recent.products(). Without untracked, this effect would also
      // depend on products, then write products, then run again... forever (the page freezes).
      if (p) untracked(() => recent.track({ ...p, mainImage: p.images?.[0] }));
    });
  }

  addToCart(p: ProductDetails) {
    this.cart.add(p, this.qty());
    this.added.set(true);
  }
}