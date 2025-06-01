import { Component } from '@angular/core';
import { TestCaseDesignComponent } from '../../../shared/reusableComponents/testCaseGeneration/test-case-design/test-case-design.component';
import { TitleWithBackButtonComponent } from '../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
import { ChatBoxComponent } from '../../../shared/reusableComponents/generalChat/chat-box/chat-box.component';
import { MultitabComponent } from '../../../shared/ui/multitab/multitab.component';
@Component({
  selector: 'app-log-bug',
  standalone: true,
  imports: [TestCaseDesignComponent, TitleWithBackButtonComponent, ChatBoxComponent, MultitabComponent],
  templateUrl: './log-bug.component.html',
  styleUrl: './log-bug.component.css'
})
export class LogBugComponent {
  // tabSections = ['Test Case', 'Exploratory Testing']
  tabSections = [{ label: 'Test Case', content: TestCaseDesignComponent, id: 'testCase', data: { isExportServiceRequired: true, activeTab: 'testCase' } }, { label: 'Exploratory Testing', content: ChatBoxComponent, id: 'exploratoryTesting', data: { componentType: "exploratoryTesting", showSamplePrompts: false } }]
  currentTab: string = ""
  onTabChange(selectedTab: string) {
    this.currentTab = selectedTab;
  }

}
