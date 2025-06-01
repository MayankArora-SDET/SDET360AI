import { Component } from '@angular/core';
import { ApiAutomationComponent } from '../../../shared/reusableComponents/api-automation-components/api-automation/api-automation.component';
import { TitleWithBackButtonComponent } from '../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';

@Component({
  selector: 'app-api-testing',
  standalone: true,
  imports: [ApiAutomationComponent, TitleWithBackButtonComponent],
  templateUrl: './api-testing.component.html',
  styleUrl: './api-testing.component.css'
})
export class ApiTestingComponent {

}
