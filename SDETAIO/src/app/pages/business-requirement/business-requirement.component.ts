import { Component } from '@angular/core';
import { TranscriptComponent } from '../../shared/reusableComponents/testCaseGeneration/transcript/transcript.component';
import { TitleWithBackButtonComponent } from '../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
import { MultitabComponent } from '../../shared/ui/multitab/multitab.component';

@Component({
  selector: 'app-business-requirement',
  standalone: true,
  imports: [TranscriptComponent, TitleWithBackButtonComponent, MultitabComponent],
  templateUrl: './business-requirement.component.html',
  styleUrl: './business-requirement.component.css'
})
export class BusinessRequirementComponent {
  businessReqtabs = [{ label: 'Transcript', content: TranscriptComponent, id: 'transcript', data: { activeTab: "transcript" } }, { label: 'SRS/BRS', content: TranscriptComponent, id: 'srs/brs', data: { activeTab: "srs/brs" } }];
  currentTab: string = this.businessReqtabs[0]?.label || '';
  onTabChange(selectedTab: string) {
    this.currentTab = selectedTab;
  }
  tabHeadings: {
    [key: string]: string;
  } = {
      'SRS/BRS': 'Creating user story for SRS/BRS',
      'Transcript': 'Creating user story for Transcript',
    };
}
