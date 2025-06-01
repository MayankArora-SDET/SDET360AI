import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from '../../../../core/service/authentication/auth.service';
import { MatIconModule } from '@angular/material/icon';
import { NavigationBarService } from '../../../../core/service/navigationBar.service';
import { NavigationEnd, Router } from '@angular/router';
import { filter, Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnInit, OnDestroy {
  showProfileBox = false;
  isMenuOpen = false;
  username = '';
  currentUrl = '';
  mainHeading = '';

  private destroy$ = new Subject<void>();

  constructor(
    public authService: AuthService,
    private navService: NavigationBarService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Track menu state
    this.navService.sideMenuExpanded$
      .pipe(takeUntil(this.destroy$))
      .subscribe(state => {
        this.isMenuOpen = state;
      });

    // Get user info
    this.authService.userDetails$
      .pipe(takeUntil(this.destroy$))
      .subscribe(data => {
        this.username = data.username;
        this.mainHeading = this.getHeaderForUrl();
      });

    // Handle initial load
    this.currentUrl = this.router.url;
    this.mainHeading = this.getHeaderForUrl();



    // Handle route changes
    this.router.events
      .pipe(
        filter((event): event is NavigationEnd => event instanceof NavigationEnd),
        takeUntil(this.destroy$)
      )
      .subscribe(event => {
        this.currentUrl = event.urlAfterRedirects;
        this.mainHeading = this.getHeaderForUrl();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  getHeaderForUrl(): string {
    if (this.currentUrl.includes('functionalTesting')) {
      return 'Functional Testing';
    } else if (this.currentUrl.includes('automationTesting')) {
      return 'Automation Testing';
    } else if (this.currentUrl.includes('productFeatures')) {
      return 'Product Features';
    } else if (this.currentUrl.includes('generalChat')) {
      return 'Open Prompt';
    } else if (this.currentUrl === '/') {
      return `Welcome back, ${this.username}`;
    } else if (this.currentUrl === '/profile') {
      return 'Profile';
    } else {
      return '';
    }
  }

  logout(): void {
    this.authService.logout();
  }

  onMenuIconClick(): void {
    this.isMenuOpen = !this.isMenuOpen;
    this.navService.toggleSideMenu();
  }

  navigateToProfile(): void {
    this.router.navigate(['/profile']);
    this.showProfileBox = false;
  }

}
