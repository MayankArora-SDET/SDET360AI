import { ApplicationConfig } from '@angular/core';
import { provideRouter, withRouterConfig, withHashLocation } from '@angular/router';
import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptors, withInterceptorsFromDi } from '@angular/common/http';
import { provideMarkdown } from 'ngx-markdown';
import { provideAnimations } from '@angular/platform-browser/animations';

import { environment } from '../environments/environment.development';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';

const isIE =
  typeof window !== 'undefined' &&
  (window.navigator.userAgent.indexOf('MSIE ') > -1 ||
    window.navigator.userAgent.indexOf('Trident/') > -1);


export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(
      routes, 
      withRouterConfig({ onSameUrlNavigation: 'reload' }),
      withHashLocation()  
    ),
    provideHttpClient(withInterceptorsFromDi()), // ✅ Removed duplicate
    provideAnimations(),
    provideClientHydration(),
    provideMarkdown(),
    provideAnimations(),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  ],
};