import { Component, ElementRef, ViewChild } from '@angular/core';
import { urlPatternCheck } from '../../../../core/utils/functions';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../../core/service/authentication/auth.service';
import { ApiService } from '../../../../core/service/api.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AlertService } from '../../../../core/service/alert.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { error } from 'console';

@Component({
  selector: 'app-record-play',
  standalone: true,
  providers: [AlertService],
  imports: [
    FormsModule,
    CommonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatIconModule,
  ],
  templateUrl: './record-play.component.html',
  styleUrl: './record-play.component.css',
})
export class RecordPlayComponent {
  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private apiService: ApiService,
    private alertService: AlertService
  ) {
    this.authService.activeVertical$.subscribe(verticalId => {
      this.activeVerticalId = verticalId
    })
  }
  @ViewChild('infoTag') infoTag!: ElementRef;

  @ViewChild('tokenText') tokenText!: ElementRef;
  sessionUrl = '';
  isUrlOpen = false;
  webTokenForUrl = '';
  testCaseId = '';
  description = '';
  category = '';
  categories: string[] = ['Smoke', 'Sanity', 'Regression', 'Functional'];
  activeVerticalId = '';
  recordSession() {
    if (urlPatternCheck(this.sessionUrl)) {
      this.isUrlOpen = true;
      // window.open(this.sessionUrl, '_blank');

      const tokenRequest = {
        url: this.sessionUrl,
        description: this.description,
        testCaseId: this.testCaseId,
        category: this.category,
      };

      this.apiService.post(`tokens/${this.activeVerticalId}/generate`, tokenRequest)
        .subscribe({
          next: (response) => {
            console.log('Token generated successfully:', response);
            // Extract token from response
            if (response && response.token) {
              this.webTokenForUrl = response.token;
            } else {
              console.error('Token not found in response:', response);
              this.alertService.openAlert({
                message: 'Token not found in response',
                messageType: 'error',
              });
            }
          },
          error: (e) => {
            console.error('Error generating token:', e);
            let errorMessage = 'An error occurred while generating token';
            if (e.error) {
              if (typeof e.error === 'object' && e.error.error) {
                errorMessage = e.error.error;
              } else if (typeof e.error === 'string') {
                errorMessage = e.error;
              }
            }
            this.alertService.openAlert({
              message: errorMessage,
              messageType: 'error',
            });
          },
        });
    } else {
      this.alertService.openAlert({
        message: 'Enter valid url',
        messageType: 'error',
      });
    }
  }
  copyToClipBoard() {
    navigator.clipboard.writeText(this.webTokenForUrl);
    this.tokenText.nativeElement.textContent = 'Token Copied';
  }
  outFunc() {
    this.tokenText.nativeElement.textContent = 'Copy token';
  }
}





