import { Component } from '@angular/core';
import { ChatBoxComponent } from '../../../../shared/reusableComponents/generalChat/chat-box/chat-box.component';
import { LocatorsCreationByUrlComponent } from '../locators-creation-by-url/locators-creation-by-url.component';
import { MultitabComponent } from '../../../../shared/ui/multitab/multitab.component';
import { TabSectionHeaderComponent } from '../../../../shared/reusableComponents/locatorComponents/tab-section-header/tab-section-header.component';
@Component({
  selector: 'app-default-locator-creation',
  standalone: true,
  imports: [MultitabComponent, TabSectionHeaderComponent],
  templateUrl: './default-locator-creation.component.html',
  styleUrl: './default-locator-creation.component.css'
})
export class DefaultLocatorCreationComponent {
  tabSections = [{ label: 'By DOM', content: ChatBoxComponent, id: 'bydom', data: { componentType: "locatorsCreation", showSamplePrompts: false } }, { label: 'By Url', content: LocatorsCreationByUrlComponent, id: 'byUrl' }]

}
