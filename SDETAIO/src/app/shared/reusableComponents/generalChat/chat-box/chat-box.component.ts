import { AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../../../../environments/environment.development';
import { BoxLayoutComponent } from '../box-layout/box-layout.component';
import { AnimationPulseComponent } from '../../animations/animation-pulse/animation-pulse.component';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AnimationPingComponent } from '../../animations/animation-ping/animation-ping.component';
import { Router } from '@angular/router';
import { CardDataService } from '../../../../core/service/cardData.service';
import { shuffleArray } from '../../../../core/utils/functions';
import { LocatorService } from '../../../../core/service/locator.service';
import { Observable } from 'rxjs';
import { EventSourceService } from '../../../../core/service/liveChatStream';
import { error } from 'console';
import { ApiService } from '../../../../core/service/api.service';
import { staticChatService } from '../../../../core/service/staticChat.service';
import { testCaseGenerationService } from '../../../../core/service/testCaseGeneration.service';


@Component({
  selector: 'app-chat-box',
  standalone: true,
  imports: [FormsModule, CommonModule, BoxLayoutComponent, AnimationPulseComponent, AnimationPingComponent],
  templateUrl: './chat-box.component.html',
  styleUrl: './chat-box.component.css',
})
export class ChatBoxComponent {
  @ViewChild('chatInputBox') chatInputBox!: ElementRef;
  @ViewChild('promptBox') promptBox!: ElementRef;


  @Input() componentType: string = "chatBox";
  @Input() showSamplePrompts = false
  promptEntered = "";
  promptSent = "";
  isLoading = false;
  chatList: { prompt: string, response: any }[] = [];
  apiUrl = environment.apiUrl;
  samplePrompts = this.cardService.samplePromptList;
  randomPrompts = shuffleArray(this.samplePrompts);
  apiCall$: Observable<any> = new Observable(); // common observable to subscribe for multiple api calls
  currentPage !: string
  displaySamplePrompts: boolean = false
  // isConnected = false


  constructor(private http: HttpClient, private testCaseService: testCaseGenerationService, private staticChatService: staticChatService, private router: Router, private cardService: CardDataService, private locatorService: LocatorService, private sseSerice: EventSourceService) {
    this.currentPage = this.router.url.split('/')[1]
    this.displaySamplePrompts = this.currentPage == 'generalChat'
    console.log(this.currentPage)

  }

  onPromptClick(prompt: any) {
    this.promptEntered = prompt
    this.getResponse(this.promptEntered)

  }
  onTextInput() {
    const textarea = this.chatInputBox.nativeElement;
    textarea.style.height = 'auto'; // Reset the height
    textarea.style.height = `${Math.max(Math.min(textarea.scrollHeight, 150), 50)}px`

  }

  getResponse(prompt: string) {
    if (prompt.trim() != "") {

      this.promptEntered = ""
      this.chatInputBox.nativeElement.style.height = "50px";
      this.promptSent = prompt
      this.isLoading = true
      const chatIndex = this.chatList.push({ prompt: prompt, response: 'Loading...' }) - 1
      setTimeout(() => {
        this.promptBox.nativeElement.scrollTop = this.promptBox.nativeElement.scrollHeight;
      }, 0);

      if (this.componentType === "locatorsCreation") {

        this.apiCall$ = this.locatorService.getLocatorDataByPrompt(prompt);
      }
      // else if (this.cuurentPage === "generalChat") {
      //   this.getLiveChatResponse(prompt, chatIndex)

      // } uncomment it for live chat response
      else if (this.componentType === "chatBox" || this.componentType === "exploratoryTesting") {
        this.apiCall$ = this.staticChatService.generateStaticChatResponse(prompt)
      }
      else if (this.componentType === 'codeGeneratorWithText') {
        console.log("codeGeneratorWithText")
        this.apiCall$ = this.testCaseService.getTestCasesWithTextForCodeGeneration(prompt);
      }
      this.apiCall$.subscribe({
        next: (response: any) => {
          console.log(response)
          this.chatList[chatIndex].response = response
          this.promptEntered = "";
          this.isLoading = false
          this.promptBox.nativeElement.scrollTop = this.promptBox.nativeElement.scrollHeight

        }, error: ({ error }) => {
          this.isLoading = false
          this.chatList[chatIndex].response = 'error';
          this.promptEntered = "";
        }

      })
    }

  }
  getLiveChatResponse(prompt: string, chatIndex: number): void {
    if (true) {
      this.sseSerice.connect(`${this.apiUrl}/ai/generate`);
      // this.isConnected = true
      console.log(chatIndex, "chat index")
      this.sseSerice.messages$.subscribe({
        next: (data) => {
          if (data) {


            if (this.chatList[chatIndex].response == 'Loading...') {
              this.isLoading = false
              this.promptEntered = "";
              this.chatList[chatIndex].response = data;
            } else {
              this.chatList[chatIndex].response += data;
            }


          }
        }, error: (error) => {
          console.error('Error in SSE:', error);
          // this.isConnected = false; // Reset connection status on error

        }, complete: () => {
          // this.isConnected = false; // Reset connection status on complete
        }
      })
    }
    // this.apiService.post(`${this.apiUrl}/ai/generate`).subscribe(response => {

    // })

  }


  onEnterClick(event: Event, inputElement: any) {
    event.preventDefault()

    if (document.activeElement == this.chatInputBox.nativeElement && !this.isLoading) {
      this.getResponse(this.promptEntered)
    }
  }

}
