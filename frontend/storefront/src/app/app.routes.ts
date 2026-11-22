import { Routes } from '@angular/router';

// loadComponent = lazy loading: each page is its own JS chunk, downloaded on first visit
export const routes: Routes = [
    {path: '', loadComponent: () => import('./pages/home').then(m => m.Home), title: 'ShopVerse'},
    {path: 'products', loadComponent: () => import('./pages/product-list').then(m => m.ProductList), title: 'Products | ShopVerse'},
    {path: 'products/:id', loadComponent: () => import('./pages/product-detail').then(m => m.ProductDetail), title: 'Product | ShopVerse'},
    {path: 'cart', loadComponent: () => import('./pages/cart-page').then(m => m.CartPage), title: 'Your cart | ShopVerse'},
    {path: '**', redirectTo: ''},
];
