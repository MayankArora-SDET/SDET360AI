import { Component, OnInit, Output } from '@angular/core';
import { ChatBoxComponent } from '../../shared/reusableComponents/generalChat/chat-box/chat-box.component';
import { CookieService } from 'ngx-cookie-service';
import { CardDataService } from '../../core/service/cardData.service';
import { shuffleArray } from '../../core/utils/functions';
import { CommonModule } from '@angular/common';
import { EventEmitter } from '@angular/core';
@Component({
  selector: 'app-general-chat',
  standalone: true,
  imports: [ChatBoxComponent, CommonModule],
  templateUrl: './general-chat.component.html',
  styleUrl: './general-chat.component.css'
})
export class GeneralChatComponent {
  constructor(private cardService: CardDataService) { }
  samplePrompts = this.cardService.samplePromptList;
  @Output() emitPrompt: EventEmitter<string> = new EventEmitter();
  randomPrompts = shuffleArray(this.samplePrompts);

  onPromptClick(prompt: string) {
    this.emitPrompt.emit(prompt)
    console.log(prompt)

  }


}

