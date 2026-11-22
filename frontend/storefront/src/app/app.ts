import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { CartStore } from './cart/cart.store';
import { ProductsService } from './api';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { catchError, debounceTime, distinctUntilChanged, map, of, switchMap } from 'rxjs';

@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  selector: 'app-root',
  template: `
    <header class="topbar">
      <a routerLink="/" class="logo">ShopVerse</a>
      <div class="search-box">
        <input class="search" type="search" placeholder="Search products..." #box [value]="term()"
          (input)="term.set(box.value)" (keyup.enter)="search(box.value)"/>
        @if (suggestions().length) {
          <ul class="suggestions">
            @for (s of suggestions(); track s.id) {
              <li><a [routerLink]="['/products', s.id]" (click)="term.set('')">{{s.name}}</a></li>
            }
          </ul>
        }
     </div>
      <nav>
        <a routerLink="/products" routerLinkActive="active">Products</a>
        <a routerLink="/cart" routerLinkActive="active" class="cart-link">
          Cart
          @if (cart.count() > 0) { <span class="badge"> {{ cart.count() }} </span>}
        </a>
      </nav>
    </header>
    <main><router-outlet/></main>
  `
})
export class App {
  protected readonly cart = inject(CartStore);
  private readonly router = inject(Router);
  private readonly productsApi = inject(ProductsService);

  protected readonly term = signal('');

  // signal -> observable (to use RxJS timing operators) -> back to a signal (to use in the template)
  protected readonly suggestions = toSignal(
    toObservable(this.term).pipe(
      map(t => t.trim()),
      debounceTime(300),                  // wait until the user stops typing for 300 ms
      distinctUntilChanged(),             // "mou" -> "mous" -> "mou" again: no duplicate call
      switchMap(t => t.length < 2
        ? of([])
        : this.productsApi.listProducts(undefined, undefined, undefined, t, 0, 5).pipe(
          map(page => page.content),
          catchError(() => of([]))
        )
      )
    ),
    { initialValue: [] }
  );

  search(q: string) 
  {
    this.term.set('');
    this.router.navigate(['/products'], { queryParams: {q: q.trim() || null}});
  }
}
