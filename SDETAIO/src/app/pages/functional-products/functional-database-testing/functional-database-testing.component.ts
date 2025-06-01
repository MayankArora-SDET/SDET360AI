import { Component } from '@angular/core';
import { TitleWithBackButtonComponent } from '../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
import { TestCaseDesignComponent } from '../../../shared/reusableComponents/testCaseGeneration/test-case-design/test-case-design.component';
@Component({
  selector: 'app-functional-database-testing',
  standalone: true,
  imports: [TitleWithBackButtonComponent, TestCaseDesignComponent],
  templateUrl: './functional-database-testing.component.html',
  styleUrl: './functional-database-testing.component.css'
})
export class FunctionalDatabaseTestingComponent {

}
