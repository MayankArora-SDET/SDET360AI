import { Component } from '@angular/core';
import { ChatBoxComponent } from '../../../shared/reusableComponents/generalChat/chat-box/chat-box.component';
import { TitleWithBackButtonComponent } from '../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
@Component({
  selector: 'app-code-generator-with-text',
  standalone: true,
  imports: [ChatBoxComponent, TitleWithBackButtonComponent],
  templateUrl: './code-generator-with-text.component.html',
  styleUrl: './code-generator-with-text.component.css'
})
export class CodeGeneratorWithTextComponent {

}
