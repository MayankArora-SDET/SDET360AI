import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, Inject, Input, OnInit, QueryList, Renderer2, ViewChild, ViewChildren } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { testCaseGenerationService } from '../../../../core/service/testCaseGeneration.service';
import { MarkdownModule } from 'ngx-markdown';
import { Subscription } from 'rxjs';
import { ChatBoxComponent } from '../../generalChat/chat-box/chat-box.component';
import { MultipleInputComponent } from '../../multiple-input/multiple-input.component';
import { AnimationPingComponent } from '../../animations/animation-ping/animation-ping.component';
import { ModifiedMarkdownComponent } from '../../modified-markdown/modified-markdown.component';
import { AlertService } from '../../../../core/service/alert.service';
import { testCaseHeadings } from '../../../../core/constants/testCaseDesignheadings';
import { epicTicketDataType, ticketDataType } from '../../../../core/interfaces/testCaseDesign';
import { DOMService } from '../../../../core/service/dom.service';

@Component({
  selector: 'app-test-case-design',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MarkdownModule,
    ChatBoxComponent,
    MultipleInputComponent,
    AnimationPingComponent,
    ModifiedMarkdownComponent,
  ],
  templateUrl: './test-case-design.component.html',
  styleUrl: './test-case-design.component.css',
  providers: [testCaseGenerationService, AlertService],
})

export class TestCaseDesignComponent implements OnInit {
  constructor(
    private testCaseService: testCaseGenerationService,
    private el: ElementRef,
    private alertService: AlertService,
    private domService: DOMService //to access dom tree
  ) { }
  @Input() activeTab: string = 'epic';
  @Input() isExportServiceRequired: boolean = true;


  ngOnInit(): void {
    console.log(this.activeTab, "active tab", this.isExportServiceRequired)
  }
  ngOnDestroy() {
    if (this.currentSubsciption) {
      this.currentSubsciption.unsubscribe();
    }
  }

  @ViewChild('modifiedMarkdown') modifiedMarkdown!: ModifiedMarkdownComponent;
  @ViewChild(MultipleInputComponent) multipleInputComponent!: MultipleInputComponent;
  tabSelected!: string;
  ticketList: string[] = [];
  ticketEntered = '';
  currentSubsciption!: Subscription;
  isLoading: boolean = false;
  errorResponse: string | null = null;
  ticketsSentForResponse: string[] = [];
  ticketData: ticketDataType | null = null;
  epicTicketData: epicTicketDataType | null = null
  testCaseHeadings = testCaseHeadings;
  setToDefault() {
    this.epicTicketData = null
    this.ticketData = null
    this.errorResponse = null

  }


  handleticketList(ticketListEntered: string[]) {
    this.ticketList = ticketListEntered;
  }

  GenerateData() {
    this.setToDefault()
    if (this.ticketList.length > 0) {
      this.ticketsSentForResponse = [...this.ticketList];
      this.isLoading = true;
      this.currentSubsciption = this.testCaseService
        .getTestCaseResponse(this.activeTab, this.ticketList)
        .subscribe({
          next: (response: any) => {
            this.isLoading = false;
            if (this.activeTab == "epic") {
              this.epicTicketData = response
              console.log(this.epicTicketData)
            } else {
              this.ticketData = response
            }
          },
          error: ({ error }: any) => {
            this.isLoading = false;
            this.errorResponse = error?.error || 'Error occured while generating data';
          },
        });
    } else {
      this.alertService.openAlert({
        message: 'Enter atleast one ticket',
        messageType: 'warning',
      });
    }
  }
  onExportClick() {
    this.domService.extractDataAndSendToExcel();
  }
  getObjectKeys<T extends object>(obj: T | null) {
    return obj ? Object.keys(obj) : [];
  }

  selectAllTestCases(event: Event) {
    const buttonText = (event.target as HTMLElement).textContent; //  innerText
    if (buttonText === 'Select All') {
      Array.from(
        this.el.nativeElement.querySelectorAll('.testCaseCheckBox')
      ).forEach((element: any) => {
        element.checked = true;
      });
      (event.target as HTMLElement).textContent = 'Deselect All';
    } else {
      Array.from(
        this.el.nativeElement.querySelectorAll('.testCaseCheckBox')
      ).forEach((element: any) => {
        element.checked = false;
      });
      (event.target as HTMLElement).textContent = 'Select All';
    }
  }
}


