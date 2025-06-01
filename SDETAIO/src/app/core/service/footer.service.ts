import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})
export class VersionService {
     private httpOptions = {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
        withCredentials: true,
      };
  constructor(private http: HttpClient) {}

  getVersion(): Observable<string> {
    return this.http
      .get<any>(`${environment.apiUrl}/get_latest_version`, this.httpOptions)
      .pipe(
        map((response) => response.version || 'v1.0.0'),
        catchError((error) => {
          return of('v1.0.0');
        })
      );
  }
}
