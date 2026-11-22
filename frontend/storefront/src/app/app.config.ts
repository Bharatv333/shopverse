import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { provideApi } from './api';
import { API_URL } from './api-url';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding()),   // route params AND query params arrive as component inputs
    provideHttpClient(withFetch()),
    provideApi(API_URL)                                   // base URL for the generated services 
  ]
};
