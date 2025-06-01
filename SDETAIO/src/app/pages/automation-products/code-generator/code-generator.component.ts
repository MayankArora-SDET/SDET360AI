import { Component } from '@angular/core';
import { TestCaseDesignComponent } from '../../../shared/reusableComponents/testCaseGeneration/test-case-design/test-case-design.component';
import { TitleWithBackButtonComponent } from '../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';

@Component({
  selector: 'app-code-generator',
  standalone: true,
  imports: [TestCaseDesignComponent, TitleWithBackButtonComponent],
  templateUrl: './code-generator.component.html',
  styleUrl: './code-generator.component.css'
})
export class CodeGeneratorComponent {

}
