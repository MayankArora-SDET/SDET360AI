import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { SideNavComponent } from '../side-nav/side-nav.component';
import { NavigationBarService } from '../../../../core/service/navigationBar.service';
import { HeaderComponent } from '../header/header.component';
import { AsyncPipe } from '@angular/common';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-navigation-panel',
  standalone: true,
  imports: [SideNavComponent, AsyncPipe, CommonModule, HeaderComponent],
  templateUrl: './navigation-panel.component.html',
  styleUrl: './navigation-panel.component.css'
})
export class NavigationPanelComponent implements OnChanges {
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['userdetails']) {
      console.log('Child received new userdetails:', changes['userdetails'].currentValue);
      this.userdetails = changes['userdetails'].currentValue
    }
  }

  constructor(public navService: NavigationBarService) {
    this.navService.sideMenuExpanded$.subscribe(state => {
      this.sideNavExpanded = state
    })
  }
  ngOnInit(): void {
    console.log(this.userdetails, "user details in navigation panel")
  }
  @Input() userdetails!: { username: string, email: string }
  sideNavExpanded!: boolean;
}
