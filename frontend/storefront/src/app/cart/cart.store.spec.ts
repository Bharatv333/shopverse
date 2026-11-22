import { TestBed } from '@angular/core/testing';
import { CartStore } from './cart.store';

describe('CartStore', () => {
  const laptop = { id: 1, name: 'Laptop', price: 1000, currency: 'INR' };
  const mouse = { id: 2, name: 'Mouse', price: 500, currency: 'INR' };

  beforeEach(() => localStorage.clear());

  function createStore() {
    TestBed.resetTestingModule();
    return TestBed.inject(CartStore);
  }

  it('adds products and adds up count and total', () => {
    const cart = createStore();
    cart.add(laptop);
    cart.add(laptop, 2);       // same product again: qty goes up, no second row
    cart.add(mouse);
    expect(cart.items().length).toBe(2);
    expect(cart.count()).toBe(4);
    expect(cart.total()).toBe(3500);
  });

  it('caps qty at 10 and removes the item at 0', () => {
    const cart = createStore();
    cart.add(laptop, 50);
    expect(cart.count()).toBe(10);
    cart.setQty(1, 0);
    expect(cart.isEmpty()).toBe(true);
  });

  it('saves to localStorage and loads it back (the refresh test)', () => {
    const cart = createStore();
    cart.add(mouse, 3);
    TestBed.tick();            // let the effect run, like Angular does after a change
    const afterRefresh = createStore();   // a new store = what happens when you press F5
    expect(afterRefresh.count()).toBe(3);
  });
});