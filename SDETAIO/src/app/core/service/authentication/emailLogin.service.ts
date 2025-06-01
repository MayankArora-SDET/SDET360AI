import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { Router } from '@angular/router';
import { ApiService } from '../api.service';
import { AlertService } from '../alert.service';
import { StorageService } from '../storage.service';
import { AuthService } from './auth.service';
@Injectable({
    providedIn: 'root'
})
export class EmailLoginService {

    // cookie service from ngx-cookie-service
    constructor(private authService: AuthService, private alertService: AlertService, private storageService: StorageService, private apiService: ApiService, private cookieService: CookieService, private router: Router) {
    }

    checkToken() {
        return this.cookieService.check('token')
    }

    getToken() {
        return this.cookieService.get('session');
    }

    loginWithUsernameAndPassword(email: string, password: string) {
        this.apiService.post(
            "auth/login",
            { email, password },
        ).subscribe({
            next: (res: any) => {
                const { userId } = res
                this.storageService.setItem('loginType', 'email'); // Store the login type in local storage
                this.router.navigate(['/']).then(success => {
                    if (success) {
                        this.authService.verifyAndLoadUser().subscribe();
                        this.authService.setUserId(userId)
                    } else {
                        console.error('Navigation failed');
                    }
                })
            }, error: ({ error }: any) => {
                this.alertService.openAlert({
                    message: error.error || 'Error occurred while logging in',
                    messageType: 'error',
                });
            },
        })
    }
}
