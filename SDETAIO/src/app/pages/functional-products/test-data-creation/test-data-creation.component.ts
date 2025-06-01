import { Component } from '@angular/core';
import { TestCaseDesignComponent } from '../../../shared/reusableComponents/testCaseGeneration/test-case-design/test-case-design.component';
import { TitleWithBackButtonComponent } from '../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
import { MultitabComponent } from '../../../shared/ui/multitab/multitab.component';
@Component({
  selector: 'app-test-data-creation',
  standalone: true,
  imports: [TestCaseDesignComponent, TitleWithBackButtonComponent, MultitabComponent],
  templateUrl: './test-data-creation.component.html',
  styleUrl: './test-data-creation.component.css'
})
export class TestDataCreationComponent {
  tabSections = [{ label: 'Epic', content: TestCaseDesignComponent, id: 'epic', data: { isExportServiceRequired: true, activeTab: 'epic' } }, { label: 'Story', content: TestCaseDesignComponent, id: 'story', data: { isExportServiceRequired: true, activeTab: 'story' } }, { label: 'Bug', content: TestCaseDesignComponent, id: 'bug', data: { isExportServiceRequired: true, activeTab: 'bug' } }]

}
