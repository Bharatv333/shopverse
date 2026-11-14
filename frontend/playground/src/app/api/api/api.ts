export * from './categories.service';
import { CategoriesService } from './categories.service';
export * from './productController.service';
import { ProductControllerService } from './productController.service';
export * from './stockController.service';
import { StockControllerService } from './stockController.service';
export const APIS = [CategoriesService, ProductControllerService, StockControllerService];
