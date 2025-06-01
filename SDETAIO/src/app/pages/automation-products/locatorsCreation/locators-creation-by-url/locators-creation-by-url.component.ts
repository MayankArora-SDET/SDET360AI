import { Component } from '@angular/core';
import { TitleWithBackButtonComponent } from '../../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
import { RecordTabBarComponent } from '../../../../shared/reusableComponents/navBar/record-tab-bar/record-tab-bar.component';
import { FormsModule } from '@angular/forms';
import { TabSectionHeaderComponent } from '../../../../shared/reusableComponents/locatorComponents/tab-section-header/tab-section-header.component';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AnimationPingComponent } from '../../../../shared/reusableComponents/animations/animation-ping/animation-ping.component';
import { LocatorService } from '../../../../core/service/locator.service';
import { PrettyJsonPipe } from '../../../../shared/pipes/prettierJson';
import { LocatorTableComponent } from '../../../../shared/reusableComponents/locatorComponents/locator-table/locator-table.component';
import { urlPatternCheck } from '../../../../core/utils/functions';
import { AlertService } from '../../../../core/service/alert.service';
@Component({
  selector: 'app-locators-creation-by-url',
  standalone: true,
  providers: [AlertService],
  imports: [
    TitleWithBackButtonComponent,
    AnimationPingComponent,
    RecordTabBarComponent,
    FormsModule,
    CommonModule,
    TabSectionHeaderComponent,
    FormsModule,
    PrettyJsonPipe,
    LocatorTableComponent,
  ],
  templateUrl: './locators-creation-by-url.component.html',
  styleUrl: './locators-creation-by-url.component.css',
})
export class LocatorsCreationByUrlComponent {
  constructor(
    private http: HttpClient,
    private locatorService: LocatorService,
    private alertService: AlertService
  ) {
    this.locatorService.toolSelected$.subscribe(tool => {
      this.selectedTool = tool
    })
  }
  locatorsTabSections = [
    {
      label: 'By DOM',
      linkTo: '/automationTesting/locatorsCreation',
    },
    { label: 'By url', linkTo: '/automationTesting/locatorsCreation/url' },
  ];
  selectedTool: string = "";
  isLoading = false;
  urlEntered = '';
  locatorUrlResponse?: {};
  errorMessage: string | undefined = undefined;

  onSubmitClick() {
    if (!this.urlEntered.trim()) {
      this.alertService.openAlert({
        message: 'Please enter the URL',
        messageType: 'error',
      });
      return;
    }
    if (this.urlEntered) {
      if (urlPatternCheck(this.urlEntered)) {
        this.errorMessage = undefined;
        this.isLoading = true;
        this.locatorService.getLocatorDataByUrl(this.urlEntered).subscribe({
          next: (response: any) => {
            this.isLoading = false;
            this.locatorUrlResponse = response;
          },
          error: ({ error }: any) => {
            this.isLoading = false;
            console.log(error, 'error');
            this.errorMessage = error.error;
            if (this.errorMessage) {
              this.alertService.openAlert({
                message: this.errorMessage || 'Error while Generating data',
                messageType: 'error',
              });
              return;
            }
            this.alertService.openAlert({
              message: error.message || 'Error while fetching data',
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
  }
}
