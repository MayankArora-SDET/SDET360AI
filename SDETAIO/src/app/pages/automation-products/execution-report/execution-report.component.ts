import { Component } from '@angular/core';
import { TestCaseDesignComponent } from '../../../shared/reusableComponents/testCaseGeneration/test-case-design/test-case-design.component';
import { TitleWithBackButtonComponent } from '../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
import { MatIcon } from '@angular/material/icon';
@Component({
  selector: 'app-execution-report',
  standalone: true,
  imports: [
    TestCaseDesignComponent,

    TitleWithBackButtonComponent,
    MatIcon,
  ],
  templateUrl: './execution-report.component.html',
  styleUrl: './execution-report.component.css',
})
export class ExecutionReportComponent {
  tabSections = ['epic', 'story', 'bug'];
}
