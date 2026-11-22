export * from './categories.service';
import { CategoriesService } from './categories.service';
export * from './products.service';
import { ProductsService } from './products.service';
export * from './stock.service';
import { StockService } from './stock.service';
export const APIS = [CategoriesService, ProductsService, StockService];
