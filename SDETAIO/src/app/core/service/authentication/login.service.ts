import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';
import { map } from 'rxjs/operators';
import { environment } from '../../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  // cookie service from ngx-cookie-service
  constructor(private http: HttpClient, private cookieService: CookieService) { }

  checkToken() {
    return this.cookieService.check('token');
  }

  getToken() {
    return this.cookieService.get('session');
  }

  loginWithUsernameAndPassword(username: string, password: string) {

    return this.http.post<any>(
      `${environment.apiUrl}/auth/login`,
      { username, password },
      {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
        withCredentials: true
      }
    )
  }


  logout() {
    this.cookieService.delete('session');
  }

  isLoggedIn() {
    return this.cookieService.check('user');

  }




}

