import { AfterViewInit, Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { NavigationEnd, RouterModule, RouterOutlet } from '@angular/router';
import { HeaderComponent } from './shared/layout/components/header/header.component';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavigationPanelComponent } from './shared/layout/components/navigation-panel/navigation-panel.component';
import { HttpClient } from '@angular/common/http';
import { FooterComponent } from './shared/layout/components/footer/footer.component';
import { AuthService } from './core/service/authentication/auth.service';
import { isPlatformBrowser } from '@angular/common';
import { Inject, PLATFORM_ID } from '@angular/core';

declare var $: any;
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    HeaderComponent,
    CommonModule,
    RouterModule,
    NavigationPanelComponent,
    FooterComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  constructor(
    public router: Router,
    public http: HttpClient,
    private authService: AuthService,

    @Inject(PLATFORM_ID) private platformId: Object,

  ) {

    if (isPlatformBrowser(this.platformId)) {
      // This block runs only in the browser
      this.authService.verifyAndLoadUser().subscribe()
      this.authService.userDetails$.subscribe(data => {
        this.userDetails = data
      });
      this.authService.userDetails$.subscribe(data => {
        // Important: spread to ensure a new reference so Angular detects changes
        this.userDetails = { ...data };

        console.log(this.userDetails, "userDetails")

      })
    }
  }
  userDetails!: { username: string, email: string };
  ngOnInit(): void {
    // if (isPlatformBrowser(this.platformId)) {
    //   // This block runs only in the browser
    //   this.authService.verifyAndLoadUser().subscribe();
    //   this.authService.userDetails$.subscribe(data => {
    //     this.userDetails = data

    //   })
    // }

  }




}
