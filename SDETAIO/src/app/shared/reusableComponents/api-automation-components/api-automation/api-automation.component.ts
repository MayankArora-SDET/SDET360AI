import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { urlPatternCheck } from '../../../../core/utils/functions';
import { environment } from '../../../../../environments/environment.development';
import { HttpHeaders } from '@angular/common/http';
import { MarkdownModule } from 'ngx-markdown';
import { AnimationPingComponent } from '../../animations/animation-ping/animation-ping.component';
import { ApiTableComponent } from '../api-table/api-table.component';
import { AlertService } from '../../../../core/service/alert.service';
import { ApiService } from '../../../../core/service/api.service';
import { AuthService } from '../../../../core/service/authentication/auth.service';

@Component({
  selector: 'app-api-automation',
  standalone: true,
  providers: [AlertService],
  imports: [
    CommonModule,
    FormsModule,
    MarkdownModule,
    AnimationPingComponent,
    ApiTableComponent,
  ],
  templateUrl: './api-automation.component.html',
  styleUrl: './api-automation.component.css',
})
export class ApiAutomationComponent {
  constructor(private http: HttpClient, private alertService: AlertService, private apiService: ApiService, private authService: AuthService) {
    this.authService.activeVertical$.subscribe((activeVerticalId) => {
      this.activeVerticalId = activeVerticalId;
    });
  }
  @ViewChild(ApiTableComponent) urlDataComponent!: ApiTableComponent;
  apiUrl = environment.apiUrl;
  activeVerticalId = "";
  selectedRequestType: string = 'GET';
  requestTypes = ['GET', 'POST', 'DELETE'];
  dataEntered: { [key: string]: string } = {
    params: '',
    headers: '',
    body: '',
  };
  urlEntered = '';
  responseData = '';
  errorResponse = '';
  isLoading = false;
  bgColors: { [key: string]: string } = {
    GET: '#acd1a1',
    POST: '#e89556',
    DELETE: '#e37964',
  };
  requestInfoTypes = ['params', 'headers', 'body'];
  selectedInfoType: string = 'params';
  onTabClick(tab: string) {
    this.selectedInfoType = tab;
  }

  stringToObject(data: string) {
    const resultObj: { [key: string]: string } = {};
    data.split('\n').forEach((item) => {
      const [key, value] = item.split(':');
      resultObj[key] = value;
    });

    return resultObj;
  }

  GenerateData() {
    if (urlPatternCheck(this.urlEntered)) {
      this.isLoading = true;
      this.responseData = '';
      const dataToSend = this.urlDataComponent.dataToSendApi();
      this.apiService
        .post(
          `api-testing/generate/${this.activeVerticalId}`,
          {
            url: this.urlEntered,
            method: this.selectedRequestType,
            params: dataToSend.params,
            headers: dataToSend.headers,
            body: dataToSend.body,
          }
        )
        .subscribe({
          next: (response) => {
            this.isLoading = false;
            this.responseData = response['testScenarios'];
          },
          error: ({ error }) => {
            this.isLoading = false;
            this.alertService.openAlert({
              message: error?.error || 'Unknown error occurred',
              messageType: 'error',
            });
            this.errorResponse = error?.error || 'Unknown error occurred';
          },
        });
      console.log(this.urlDataComponent.dataToSendApi());
    } else {
      this.alertService.openAlert({
        message: 'Enter valid url',
        messageType: 'error',
      });
    }
  }
}

