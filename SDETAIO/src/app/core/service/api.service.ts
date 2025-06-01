import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { AuthService } from './authentication/auth.service';

@Injectable({ providedIn: 'root' })
export class ApiService {
    private http = inject(HttpClient);
    private apiUrl = environment.apiUrl; // Use the environment variable for the API URL

    getHttpOptions() {
        return {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
            }),
            withCredentials: true
        };
    }
    private getHttpOptionsWithHtmlContent() {
        return {
            headers: new HttpHeaders({
                'Content-Type': 'text/html',
            }),
            withCredentials: true
        };
    }



    get(endpoint: string): Observable<any> {
        return this.http.get(`${this.apiUrl}/${endpoint}`, this.getHttpOptions());
    }

    post(endpoint: string, data: any = {}): Observable<any> {
        return this.http.post(`${this.apiUrl}/${endpoint}`, data, this.getHttpOptions());
    }
    postWithHtmlContent(endpoint: string, data: string): Observable<any> {
        return this.http.post(`${this.apiUrl}/${endpoint}`, data, this.getHttpOptionsWithHtmlContent());
    }
    put(endpoint: string, data: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/${endpoint}`, data, this.getHttpOptions());
    }

    delete(endpoint: string): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${endpoint}`, this.getHttpOptions());
    }

    // getTestCasesApi(endpoint: string, data: string[]): Observable<any> {
    //     return this.http.request('GET', `${this.apiUrl}/${endpoint}`, { ...this.getHttpOptions(), body: data });
    // }
}