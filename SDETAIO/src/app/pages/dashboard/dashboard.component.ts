import { Component } from '@angular/core';
import { AuthService } from '../../core/service/authentication/auth.service';
import { CommonModule } from '@angular/common';
import { ProjectPopUpComponent } from '../../shared/reusableComponents/project-pop-up/project-pop-up.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ProjectPopUpComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  constructor(private authService: AuthService) {

  }
  // username = this.authService.getUserDetails()?.username;
  aboutProductList = [
    { id: "feature1", aboutFeature: "Expertly crafted software solutions meeting your business needs, enhancing efficiency and productivity.", featureHeading: "Product", icon: "assets/about/ai.svg" },
    { id: "feature2", aboutFeature: "Precision-driven manual testing services ensuring quality, reliability, and flawless user experience delivery.", featureHeading: "Manual Testing", icon: "assets/about/dashboardManual.svg" },
    { id: "feature3", aboutFeature: "Streamline processes with automated testing, reducing time-to-market and boosting cost-effectiveness significantly always.", featureHeading: "Automation Testing", icon: "assets/about/dashboardAutomation.svg" }
  ]

}
