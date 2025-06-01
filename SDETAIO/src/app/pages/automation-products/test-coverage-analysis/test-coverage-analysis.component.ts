import { Component } from '@angular/core';
import { TestCaseDesignComponent } from '../../../shared/reusableComponents/testCaseGeneration/test-case-design/test-case-design.component';
import { TitleWithBackButtonComponent } from '../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
import { AnalyticsComponent } from '../../analytics/analytics.component';

@Component({
  selector: 'app-test-coverage-analysis',
  standalone: true,
  imports: [TestCaseDesignComponent, TitleWithBackButtonComponent, AnalyticsComponent],
  templateUrl: './test-coverage-analysis.component.html',
  styleUrl: './test-coverage-analysis.component.css'
})
export class TestCoverageAnalysisComponent {
  tabSections = ['epic', 'story', 'bug']

}
