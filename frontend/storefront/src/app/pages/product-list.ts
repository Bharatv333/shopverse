import { Component, computed, inject, input, numberAttribute } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';
import { CategoriesService, ProductsService, ProductSummary } from '../api';
import { CartStore } from '../cart/cart.store';
import { ProductCard } from '../shared/product-card';

// Query params arrive as strings ("3") or undefined. Turn them into numbers, keep "missing" as undefined.
const optionalNumber = (v: string | undefined) => (v ? Number(v) : undefined);

export const SORTS = [
  { value: 'id,desc', label: 'Newest' },
  { value: 'price,asc', label: 'Price: low to high' },
  { value: 'price,desc', label: 'Price: high to low' },
  { value: 'avgRating,desc', label: 'Top rated' },
  { value: 'name,asc', label: 'Name A to Z' },
];

const PAGE_SIZE = 4;   // small on purpose: we only have 6 seed products, so you can see paging work

@Component({
  selector: 'app-product-list',
  imports: [ProductCard, RouterLink],
  template: `
    <div class="list-layout">
      <aside class="filters">
        <h3>Category</h3>
        <ul class="facets">
          <li><a [routerLink]="[]" [queryParams]="{ category: null, page: null }" queryParamsHandling="merge"
                 [class.active]="!category()">All</a></li>
          @for (c of categories.value() ?? []; track c.id) {
            <li>
              <a [routerLink]="[]" [queryParams]="{ category: c.id, page: null }" queryParamsHandling="merge"
                 [class.active]="category() === c.id">{{ c.name }} <span class="muted">({{ c.productCount }})</span></a>
            </li>
          }
        </ul>

        <h3>Price</h3>
        <form class="price-form" (submit)="$event.preventDefault(); applyPrice(min.value, max.value)">
          <input #min type="number" min="0" placeholder="Min" [value]="minPrice() ?? ''" />
          <input #max type="number" min="0" placeholder="Max" [value]="maxPrice() ?? ''" />
          <button type="submit">Go</button>
        </form>
        @if (hasFilters()) {
          <button class="link" (click)="clearFilters()">Clear all filters</button>
        }
      </aside>

      <section>
        <div class="list-head">
          <h2>
            @if (q()) { Results for "{{ q() }}" } @else { All products }
            @if (products.hasValue()) { <span class="muted">({{ products.value().page.totalElements }})</span> }
          </h2>
          <select [value]="sort() ?? 'id,desc'" (change)="setParams({ sort: $any($event.target).value, page: null })"
                  aria-label="Sort">
            @for (s of sorts; track s.value) { <option [value]="s.value">{{ s.label }}</option> }
          </select>
        </div>

        @if (products.error()) {
          <p class="error">Could not load products. Is the backend running on port 8081?
            <button (click)="products.reload()">Try again</button></p>
        } @else if (products.hasValue()) {
          @let result = products.value();
          @if (result.content.length === 0) {
            <p class="muted">No products match these filters.</p>
          } @else {
            <div class="grid" [class.loading]="products.isLoading()">
              @for (p of result.content; track p.id) {
                <app-product-card [product]="p" (addToCart)="add($event)" />
              }
            </div>
            @if (result.page.totalPages > 1) {
              <nav class="pager">
                <button [disabled]="currentPage() === 0" (click)="goTo(currentPage() - 1)">Previous</button>
                <span>Page {{ currentPage() + 1 }} of {{ result.page.totalPages }}</span>
                <button [disabled]="currentPage() + 1 >= result.page.totalPages" (click)="goTo(currentPage() + 1)">Next</button>
              </nav>
            }
          }
        } @else {
          <p class="muted">Loading products...</p>
        }
      </section>
    </div>
  `,
})
export class ProductList {
  private readonly router = inject(Router);
  private readonly productsApi = inject(ProductsService);
  private readonly categoriesApi = inject(CategoriesService);
  private readonly cart = inject(CartStore);
  protected readonly sorts = SORTS;

  // withComponentInputBinding() fills these from the URL: /products?q=mouse&category=1&page=2
  // The URL is the ONLY source of truth for filters. Refresh or share the link, and you get the same page.
  readonly q = input<string>();
  readonly category = input(undefined, { transform: optionalNumber });
  readonly minPrice = input(undefined, { transform: optionalNumber });
  readonly maxPrice = input(undefined, { transform: optionalNumber });
  readonly sort = input<string>();
  readonly page = input(1, { transform: (v: string | undefined) => (v ? numberAttribute(v, 1) : 1) }); // URL is 1-based

  protected readonly currentPage = computed(() => Math.max(this.page(), 1) - 1);  // Spring is 0-based
  protected readonly hasFilters = computed(() => !!(this.q() || this.category() || this.minPrice() || this.maxPrice()));

  // Categories never change while you browse: no params, load once.
  protected readonly categories = rxResource({ stream: () => this.categoriesApi.listCategories() });

  // Re-runs automatically whenever ANY signal read in params() changes.
  // If a new request starts before the old one finishes, the old one is cancelled (like switchMap).
  protected readonly products = rxResource({
    params: () => ({
      q: this.q(),
      category: this.category(),
      minPrice: this.minPrice(),
      maxPrice: this.maxPrice(),
      sort: this.sort() ?? 'id,desc',
      page: this.currentPage(),
    }),
    stream: ({ params: p }) =>
      this.productsApi.listProducts(p.category, p.minPrice, p.maxPrice, p.q, p.page, PAGE_SIZE, [p.sort]),
  });

  // Filters never touch component state: they only change the URL. The inputs above then update by themselves.
  setParams(params: Record<string, string | number | null>) {
    this.router.navigate([], { queryParams: params, queryParamsHandling: 'merge' });
  }

  applyPrice(min: string, max: string) {
    this.setParams({ minPrice: min || null, maxPrice: max || null, page: null });
  }

  goTo(zeroBasedPage: number) {
    this.setParams({ page: zeroBasedPage === 0 ? null : zeroBasedPage + 1 });
  }

  clearFilters() {
    this.router.navigate([], { queryParams: { sort: this.sort() ?? null } });
  }

  add(p: ProductSummary) {
    this.cart.add(p);
  }
}