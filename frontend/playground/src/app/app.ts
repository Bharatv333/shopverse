import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CategoriesService, CategorySummary, ProductRequest, ProductControllerService, ProductSummary } from './api';

// The shape of the "errors" list our GlobalExceptionHandler sends with a 400
interface FieldProblem { field: string; message: string; }

@Component({
  selector: 'app-root',
  imports: [FormsModule],
  template: `
    <h1>ShopVerse playground</h1>

    <label>Category
      <select [(ngModel)]="categoryId" (change)="load()">
        <option [ngValue]="undefined">All</option>
        @for (c of categories(); track c.id) { <option [ngValue]="c.id">{{ c.name }} ({{ c.productCount }})</option> }
      </select>
    </label>

    <table border="1" cellpadding="6">
      <tr><th>Id</th><th>SKU</th><th>Name</th><th>Category</th><th>Price</th><th></th></tr>
      @for (p of products(); track p.id) {
        <tr>
          <td>{{ p.id }}</td><td>{{ p.sku }}</td><td>{{ p.name }}</td>
          <td>{{ p.categoryName }}</td><td>{{ p.price }} {{ p.currency }}</td>
          <td><button (click)="remove(p)">Delete</button></td>
        </tr>
      }
    </table>

    <h2>Add product</h2>
    <input placeholder="SKU, e.g. HM-LMP-007" [(ngModel)]="form.sku" />
    <input placeholder="Name" [(ngModel)]="form.name" />
    <select [(ngModel)]="form.categoryId">
      @for (c of categories(); track c.id) { <option [ngValue]="c.id">{{ c.name }}</option> }
    </select>
    <input type="number" placeholder="Price" [(ngModel)]="form.price" />
    <button (click)="add()">Add</button>

    @if (error()) { <p style="color: red">{{ error() }}</p> }
    <ul style="color: red">
      @for (e of fieldErrors(); track e.field) { <li><b>{{ e.field }}</b>: {{ e.message }}</li> }
    </ul>
  `,
})
export class App implements OnInit {
  // Generated from the backend's OpenAPI spec: no hand-written URLs or interfaces
  private productsApi = inject(ProductControllerService);
  private categoriesApi = inject(CategoriesService);

  products = signal<ProductSummary[]>([]);
  categories = signal<CategorySummary[]>([]);
  error = signal('');
  fieldErrors = signal<FieldProblem[]>([]);
  categoryId?: number;
  form: ProductRequest = { sku: '', name: '', categoryId: 1, price: 0 };   // typed: a typo here is a compile error

  ngOnInit() {
    this.categoriesApi.listCategories().subscribe(c => this.categories.set(c));
    this.load();
  }

  load() {
    this.productsApi.listProducts(this.categoryId).subscribe({
      next: page => this.products.set(page.content),
      error: e => this.showError(e),
    });
  }

  add() {
    this.productsApi.createProduct(this.form).subscribe({
      next: () => { this.clearErrors(); this.load(); },
      error: e => this.showError(e),
    });
  }

  remove(p: ProductSummary) {
    this.productsApi.deleteProduct(p.id!).subscribe(() => this.load());
  }

  // Every error from the backend is a ProblemDetail: { title, status, detail, errors? }
  private showError(e: HttpErrorResponse) {
    this.error.set(e.error?.detail ?? e.message);
    this.fieldErrors.set(e.error?.errors ?? []);
  }

  private clearErrors() {
    this.error.set('');
    this.fieldErrors.set([]);
  }
}