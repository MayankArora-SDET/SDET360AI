import { Inject, Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router, RouterStateSnapshot, CanActivateFn, CanActivateChild, GuardResult, MaybeAsync } from '@angular/router';
import { ApiService } from '../service/api.service';
import { Observable, map, catchError, of } from 'rxjs';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate, CanActivateChild {

  constructor(
    private router: Router,
    private apiService: ApiService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    const currentUrl = state.url;

    if (!isPlatformBrowser(this.platformId)) {
      return of(true); // SSR-safe
    }

    return this.apiService.get('auth/check-session').pipe(
      map((response: any) => {
        //  Case: Already logged in and trying to access login page
        if (currentUrl.includes('/login')) {
          this.router.navigateByUrl('/');
          return false;
        }

        //  Allow access
        return true;
      }),
      catchError(() => {
        //  Case: Not logged in
        console.log(currentUrl.includes('/login'), currentUrl, "on error")
        if (!currentUrl.includes('/login')) {
          this.router.navigateByUrl('/login');
        }

        return of(true);
      })
    );
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): MaybeAsync<GuardResult> {
    return this.canActivate(childRoute, state);
  }
}
