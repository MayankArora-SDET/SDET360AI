import { Component } from '@angular/core';
import { TestCaseDesignComponent } from '../../../shared/reusableComponents/testCaseGeneration/test-case-design/test-case-design.component';
import { TitleWithBackButtonComponent } from '../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';

@Component({
  selector: 'app-code-snippets',
  standalone: true,
  imports: [TestCaseDesignComponent, TitleWithBackButtonComponent],
  templateUrl: './code-snippets.component.html',
  styleUrl: './code-snippets.component.css'
})
export class CodeSnippetsComponent {
  tabSections = ['epic', 'story', 'bug']

}
