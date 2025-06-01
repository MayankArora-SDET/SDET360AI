import { Component } from '@angular/core';
import { TitleWithBackButtonComponent } from '../../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
import { RecordTabBarComponent } from '../../../../shared/reusableComponents/navBar/record-tab-bar/record-tab-bar.component';
import { PreviousRecordsComponent } from '../../../../shared/reusableComponents/recordComponents/previous-records/previous-records.component';

@Component({
  selector: 'app-session-library',
  standalone: true,
  imports: [TitleWithBackButtonComponent, RecordTabBarComponent, PreviousRecordsComponent],
  templateUrl: './session-library.component.html',
  styleUrl: './session-library.component.css'
})
export class SessionLibraryComponent {
  recordTabSections = [{ label: "Start Recording", linkTo: '/automationTesting/recordSession/start' }, { label: "Previous Records", linkTo: '/automationTesting/recordSession/library' }]

}
