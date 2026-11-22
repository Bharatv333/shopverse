import { Component, inject } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CartStore, MAX_QTY } from '../cart/cart.store';
import { imageFor } from '../shared/images';
import { QtyStepper } from '../shared/qty-stepper';

@Component({
  selector: 'app-cart-page',
  imports: [CurrencyPipe, RouterLink, QtyStepper],
  template: `
    <h1>Your cart</h1>
    @if (cart.isEmpty()) {
      <p class="muted">Your cart is empty. <a routerLink="/products">Start shopping</a></p>
    } @else {
      <table class="cart">
        <thead><tr><th>Product</th><th>Price</th><th>Qty</th><th>Subtotal</th><th></th></tr></thead>
        <tbody>
          @for (item of cart.items(); track item.productId) {
            <tr>
              <td class="cart-product">
                <img [src]="item.image ?? image(item.name)" alt="" width="64" height="44" />
                <a [routerLink]="['/products', item.productId]">{{ item.name }}</a>
              </td>
              <td>{{ item.price | currency: item.currency : 'symbol' : '1.0-2' }}</td>
              <td>
                <!-- One-way in, event out: the store owns the value, so we do not use [(qty)] here -->
                <app-qty-stepper [qty]="item.qty" (qtyChange)="cart.setQty(item.productId, $event)" [min]="0" [max]="maxQty" />
              </td>
              <td>{{ item.price * item.qty | currency: item.currency : 'symbol' : '1.0-2' }}</td>
              <td><button class="link" (click)="cart.remove(item.productId)">Remove</button></td>
            </tr>
          }
        </tbody>
      </table>
      <div class="cart-total">
        <button class="link" (click)="cart.clear()">Clear cart</button>
        <strong>Total ({{ cart.count() }} items): {{ cart.total() | currency: 'INR' : 'symbol' : '1.0-2' }}</strong>
        <button class="btn" disabled title="Checkout comes in a later lesson">Checkout</button>
      </div>
    }
  `,
})
export class CartPage {
  protected readonly cart = inject(CartStore);
  protected readonly maxQty = MAX_QTY;
  protected readonly image = (name: string) => imageFor(name.toLowerCase().replace(/\s+/g, '-'));
}