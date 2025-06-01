import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpErrorResponse,
  HttpClient
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../service/authentication/auth.service';
import { Router } from '@angular/router';
import { StorageService } from '../service/storage.service';
import { environment } from '../../../environments/environment.development';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private router: Router, private http: HttpClient, private storageService: StorageService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Unauthorized — likely token expired
          this.http.post(`${environment.apiUrl}/auth/logout`, '', { withCredentials: true, responseType: 'text' })
            .subscribe(msg => {
              this.storageService.clear();
              this.router.navigate(['/login']);
            });
        }
        return throwError(() => error);
      })
    );
  }
}
