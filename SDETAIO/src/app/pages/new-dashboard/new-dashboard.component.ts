import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { MultitabComponent } from '../../shared/ui/multitab/multitab.component';
import { ProjectReportTabComponent } from '../../shared/reusableComponents/dashboard/project-report-tab/project-report-tab.component';
import { QualityReportTabComponent } from '../../shared/reusableComponents/dashboard/quality-report-tab/quality-report-tab.component';
import { isPlatformBrowser } from '@angular/common';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-new-dashboard',
  standalone: true,
  imports: [MultitabComponent, CommonModule],
  templateUrl: './new-dashboard.component.html',
  styleUrl: './new-dashboard.component.css'
})
export class NewDashboardComponent {
  toDisplay = false
  tabs = [
    { label: 'Project Report', content: ProjectReportTabComponent, id: 'projectReport' },
    { label: 'Quality Report', content: QualityReportTabComponent, id: 'qualityReport' },

  ];
  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    if (isPlatformBrowser(platformId)) {
      this.toDisplay = true
    }

  }


}
