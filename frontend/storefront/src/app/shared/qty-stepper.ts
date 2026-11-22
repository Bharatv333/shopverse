import { Component, input, model } from "@angular/core";

@Component({
    selector: 'app-qty-stepper',
    template: `
        <span class="stepper">
            <button type="button" (click)="qty.set(qty()-1)" [disabled]="qty() <= min()" aria-label="Less">-</button>
            <output>{{qty()}}</output>
            <button type="button" (click)="qty.set(qty()+1)" [disabled]="qty() >= max()" aria-label="More">+</button>
        </span>
    `
})
export class QtyStepper{
    // model(): a signal the PARENT can bind two-way with [(qty)]="someSignal"
    readonly qty = model(1);
    readonly min = input(1);
    readonly max = input(10);
}