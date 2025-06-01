import { Component } from '@angular/core';
import { ChatBoxComponent } from '../../../../shared/reusableComponents/generalChat/chat-box/chat-box.component';
import { TitleWithBackButtonComponent } from '../../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
import { TabSectionHeaderComponent } from '../../../../shared/reusableComponents/locatorComponents/tab-section-header/tab-section-header.component';
@Component({
  selector: 'app-locators-creation',
  standalone: true,
  imports: [ChatBoxComponent, TitleWithBackButtonComponent, TabSectionHeaderComponent],
  templateUrl: './locators-creation.component.html',
  styleUrl: './locators-creation.component.css'
})
export class LocatorsCreationComponent {


}
