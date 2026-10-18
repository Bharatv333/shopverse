import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';

interface Product{
  id: number;
  name: String;
  category: String;
  price: number;
  rating: number;
  stock: number; 
}

const API = "http://localhost:8081/api";

@Component({
  selector: 'app-root',
  imports: [FormsModule],
  template: `
     <h1>ShopVerse playground</h1>

    <label>Category
      <select [(ngModel)]="filter" (change)="load()">
        <option value="">All</option>
        @for (c of categories(); track c) { <option [value]="c">{{ c }}</option> }
      </select>
    </label>

    <table border="1" cellpadding="6">
      <tr><th>Id</th><th>Name</th><th>Category</th><th>Price</th><th>Stock</th><th></th></tr>
      @for (p of products(); track p.id) {
        <tr>
          <td>{{ p.id }}</td><td>{{ p.name }}</td><td>{{ p.category }}</td>
          <td>{{ p.price }}</td><td>{{ p.stock }}</td>
          <td>
            <button (click)="addTen(p)">+10 stock</button>
            <button (click)="remove(p)">Delete</button>
          </td>
        </tr>
      }
    </table>

    <h2>Add product</h2>
    <input placeholder="Name" [(ngModel)]="form.name" />
    <select [(ngModel)]="form.category">
      @for (c of categories(); track c) { <option [value]="c">{{ c }}</option> }
    </select>
    <input type="number" placeholder="Price" [(ngModel)]="form.price" />
    <input type="number" placeholder="Stock" [(ngModel)]="form.stock" />
    <button (click)="add()">Add</button>

    @if (error()) { <p style="color: red">{{ error() }}</p> }
  `,
  styles: [],
})
export class App implements OnInit{
  private http = inject(HttpClient);

  products = signal<Product[]>([]);
  categories = signal<string[]>([]);
  error = signal('');
  filter = '';
  form = { name: '', category: 'HOME', price: 0, stock: 0 };

  ngOnInit() {
    this.http.get<string[]>(`${API}/categories`).subscribe(c => this.categories.set(c));
    this.load();
  }

  load() {
    const params: Record<string, string> = this.filter ? { category: this.filter } : {};
    this.http.get<Product[]>(`${API}/products`, { params }).subscribe({
      next: list => this.products.set(list),
      error: e => this.error.set(e.message),
    });
  }

  add() {   // POST
    this.http.post<Product>(`${API}/products`, this.form).subscribe({
      next: () => { this.error.set(''); this.load(); },
      error: e => this.error.set(e.error?.detail ?? e.message),
    });
  }

  addTen(p: Product) {   // PATCH: send only the field that changes
    this.http.patch<Product>(`${API}/products/${p.id}`, { stock: p.stock + 10 })
      .subscribe(() => this.load());
  }

  remove(p: Product) {   // DELETE
    this.http.delete(`${API}/products/${p.id}`).subscribe(() => this.load());
  }
}
