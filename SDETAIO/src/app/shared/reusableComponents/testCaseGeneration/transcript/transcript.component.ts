import { CommonModule } from '@angular/common';
import { Component, ElementRef, Inject, inject, Input, OnDestroy, ViewChild } from '@angular/core';
import { TranscriptService } from '../../../../core/service/transcipt.service';
import { Subscription } from 'rxjs';
import { MarkdownModule } from 'ngx-markdown';
import { AnimationPingComponent } from '../../animations/animation-ping/animation-ping.component';
import { TitleWithBackButtonComponent } from '../../title-with-back-button/title-with-back-button.component';
import { AlertService } from '../../../../core/service/alert.service';

@Component({
  selector: 'app-transcript',
  standalone: true,
  providers: [AlertService],
  imports: [
    CommonModule,
    MarkdownModule,
    AnimationPingComponent,
    TitleWithBackButtonComponent,
  ],
  templateUrl: './transcript.component.html',
  styleUrl: './transcript.component.css',
})
export class TranscriptComponent implements OnDestroy {
  constructor(
    private transcriptService: TranscriptService,
    private alertService: AlertService,
  ) {
    console.log(this.activeTab, "active tab from multi tab click")
  }
  @Input() activeTab = 'Transcript';
  selectedFile: File | null = null;
  selectedFileName: string = '';
  currentSubscription!: Subscription;
  transcriptData: string = '';
  tabHeadings: {
    [key: string]: string;
  } = {
      'srs/brs': 'Creating user story for SRS/BRS',
      'transcript': 'Creating user story for Transcript',
    };

  isLoading: boolean = false;
  errorResponse: string | null = null;
  @ViewChild('fileUpload') fileInput!: ElementRef;
  ngOnDestroy(): void {
    this.currentSubscription && this.currentSubscription.unsubscribe()
  }
  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    this.selectedFileName = file.name;

    this.selectedFile = event.target.files[0];
    this.errorResponse = null;
    this.isLoading = false;
  }
  removeFile() {
    this.selectedFileName = '';
    this.selectedFile = null
    this.fileInput.nativeElement.value = ''; // Reset file input
  }
  generateData() {
    this.errorResponse = null;
    this.transcriptData = '';
    if (this.selectedFile != null) {
      this.isLoading = true;
      const formData = new FormData();
      formData.append('file', this.selectedFile);

      this.currentSubscription = this.transcriptService
        .getResponse(this.activeTab, formData)
        .subscribe({
          next: (response) => {
            this.transcriptData = response;
            this.isLoading = false;
            console.log('response', response);
          },
          error: ({ error }) => {
            this.isLoading = false;
            this.alertService.openAlert({
              message: 'Got error',
              messageType: 'error',
            });
            this.errorResponse = error?.error || 'Unknown error occurred';
          },
        });
    } else {
      this.alertService.openAlert({
        message: 'Select a file to generate data',
        messageType: 'warning',
      });
    }
  }
}
