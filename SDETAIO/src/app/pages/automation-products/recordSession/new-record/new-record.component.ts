import { Component } from '@angular/core';
import { RecordPlayComponent } from '../../../../shared/reusableComponents/recordComponents/record-play/record-play.component';
import { TitleWithBackButtonComponent } from '../../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
import { RecordTabBarComponent } from '../../../../shared/reusableComponents/navBar/record-tab-bar/record-tab-bar.component';

@Component({
  selector: 'app-new-record',
  standalone: true,
  imports: [RecordPlayComponent, TitleWithBackButtonComponent, RecordTabBarComponent],
  templateUrl: './new-record.component.html',
  styleUrl: './new-record.component.css'
})
export class NewRecordComponent {
  recordTabSections = [{ label: "Start Recording", linkTo: '/automationTesting/recordSession/start' }, { label: "Previous Records", linkTo: '/automationTesting/recordSession/library' }]
}
