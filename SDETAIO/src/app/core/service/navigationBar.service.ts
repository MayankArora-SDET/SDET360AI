import {
  Injectable,
  Inject,
  PLATFORM_ID,
  Renderer2,
  RendererFactory2,
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class NavigationBarService {
  private sideMenuExpanded = new BehaviorSubject<boolean>(true);
  sideMenuExpanded$ = this.sideMenuExpanded.asObservable();
  private renderer: Renderer2;

  constructor(
    @Inject(PLATFORM_ID) private platformId: any,
    rendererFactory: RendererFactory2
  ) {
    this.renderer = rendererFactory.createRenderer(null, null);

    if (isPlatformBrowser(this.platformId)) {
      this.sideMenuExpanded.next(window.innerWidth >= 760)
      this.renderer.listen(window, 'resize', () => this.onResize());
    }
  }

  getSideMenuState() {
    return this.sideMenuExpanded;
  }

  setSideMenuState(state: boolean) {
    this.sideMenuExpanded.next(state);
  }

  toggleSideMenu() {
    this.sideMenuExpanded.next(!this.sideMenuExpanded.getValue());
    console.log(this.sideMenuExpanded.getValue())

  }

  private onResize() {
    if (isPlatformBrowser(this.platformId)) {
      if (window.innerWidth < 768) {
        this.sideMenuExpanded.next(false);
      } else {
        this.sideMenuExpanded.next(true);
      }
    }
  }


}
