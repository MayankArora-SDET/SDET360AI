import { Component } from '@angular/core';
import { TestCaseDesignComponent } from '../../../shared/reusableComponents/testCaseGeneration/test-case-design/test-case-design.component';
import { TitleWithBackButtonComponent } from '../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
import { MultitabComponent } from '../../../shared/ui/multitab/multitab.component';
@Component({
  selector: 'app-edge-case-analysis',
  standalone: true,
  imports: [TestCaseDesignComponent, TitleWithBackButtonComponent, MultitabComponent],
  templateUrl: './edge-case-analysis.component.html',
  styleUrl: './edge-case-analysis.component.css'
})
export class EdgeCaseAnalysisComponent {
  tabSections = [{ label: 'Epic', content: TestCaseDesignComponent, id: 'epic', data: { isExportServiceRequired: true, activeTab: 'epic' } }, { label: 'Story', content: TestCaseDesignComponent, id: 'story', data: { isExportServiceRequired: true, activeTab: 'story' } }, { label: 'Bug', content: TestCaseDesignComponent, id: 'bug', data: { isExportServiceRequired: true, activeTab: 'bug' } }]
  currentTab: string = ""
  onTabChange(selectedTab: string) {
    this.currentTab = selectedTab;
  }
}
