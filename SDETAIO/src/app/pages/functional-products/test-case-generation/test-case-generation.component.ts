import { Component } from '@angular/core';
import { TestCaseDesignComponent } from '../../../shared/reusableComponents/testCaseGeneration/test-case-design/test-case-design.component';
import { TitleWithBackButtonComponent } from '../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
import { MultitabComponent } from '../../../shared/ui/multitab/multitab.component';
@Component({
  selector: 'app-test-case-generation',
  standalone: true,
  imports: [TestCaseDesignComponent, TitleWithBackButtonComponent, MultitabComponent],
  templateUrl: './test-case-generation.component.html',
  styleUrl: './test-case-generation.component.css'
})
export class TestCaseGenerationComponent {
  tabSections = [{ label: 'Epic', content: TestCaseDesignComponent, id: 'epic', data: { isExportServiceRequired: true, activeTab: 'epic' } }, { label: 'Story', content: TestCaseDesignComponent, id: 'story', data: { isExportServiceRequired: true, activeTab: 'story' } }, { label: 'Bug', content: TestCaseDesignComponent, id: 'bug', data: { isExportServiceRequired: true, activeTab: 'bug' } }]
  currentTab: string = ""
  onTabChange(selectedTab: string) {
    this.currentTab = selectedTab;
  }
}
