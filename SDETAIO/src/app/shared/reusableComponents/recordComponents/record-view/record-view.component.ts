import { ApiService } from '../../../../core/service/api.service';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../../../environments/environment.development';
import { CommonModule } from '@angular/common';
import { EditDialogBoxComponent } from '../dialogs/edit-dialog-box/edit-dialog-box.component';
import { testCaseEventDataType, formElementsType } from '../../../../core/interfaces/recordSession';
import { AutomatedEventsDisplayComponent } from '../automated-events-display/automated-events-display.component';
import { MatDialog } from '@angular/material/dialog';
import { ImageDialogComponent } from '../dialogs/image-dialog/image-dialog.component';
import { FormDialogComponent } from '../dialogs/form-dialog/form-dialog.component';
import { AlertService } from '../../../../core/service/alert.service';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../../../../core/service/authentication/auth.service';
import { Subject, takeUntil } from 'rxjs';
@Component({
  selector: 'app-record-view',
  standalone: true,
  providers: [AlertService],
  imports: [
    RouterOutlet,
    MatMenuModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    CommonModule,
    AutomatedEventsDisplayComponent,
    MatIcon,
  ],
  templateUrl: './record-view.component.html',
  styleUrls: ['./record-view.component.css'],
})
export class RecordViewComponent implements OnInit {
  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private apiService: ApiService,
    private http: HttpClient,
    public matdialog: MatDialog,
    private alertService: AlertService,
    private authService: AuthService,
  ) {

  }

  testCaseId = '';
  tcId = '';
  description = '';
  recordedData: testCaseEventDataType[] = [];
  editedData: testCaseEventDataType[] = [];
  tableHeaders = ['xpath', 'action / Type', 'value', 'edit'];
  isRevertable = false;
  eventScreenshots: string[] | undefined = undefined;
  backendUrl = environment.apiUrl;
  isExecuting = false;
  categories = ['Smoke', 'Sanity', 'Regression', 'Functional'];
  currentCategory = 'Functional';
  isCategoryDropdownOpen = false;
  activeVerticalId = ''

  @ViewChild('DialogBoxComponent') dialog!: EditDialogBoxComponent;


  private destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.activatedRoute.params.subscribe((params: any) => {
      this.testCaseId = params.tickedId;
    });
    this.authService.activeVertical$
      .pipe(takeUntil(this.destroy$))
      .subscribe(verticalId => {
        this.activeVerticalId = verticalId;
        this.fetchRecordData();
        this.fetchEventScreenshots();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();     // Triggers unsubscription
    this.destroy$.complete(); // Cleanly finishes the subject
  }

  fetchRecordData() {
    console.log(this.activeVerticalId, "vertical Id")
    this.apiService
      .get(`tokens/${this.activeVerticalId}/test-case-events/${this.testCaseId}`)
      .subscribe({
        next: (response) => {
          console.log(response, 'in recordview');

          const eventsWithReadableDates = response.events.map((event: testCaseEventDataType) => {
            if (event.createdAt && Array.isArray(event.createdAt) && event.createdAt.length >= 7) {
              const [year, month, day, hour, minute, second, nano] = event.createdAt;

              const date = new Date(year, month - 1, day, hour, minute, second, nano / 1000000);
              return {
                ...event,
                readableCreatedAt: date.toLocaleString(),
                createdAt: event.createdAt
              };
            }
            return event;
          });

          const sortedEvents = eventsWithReadableDates.sort((a: testCaseEventDataType, b: testCaseEventDataType) => {
            if (a.sequenceNumber !== undefined && b.sequenceNumber !== undefined) {
              return a.sequenceNumber - b.sequenceNumber;
            }

            if (!a.createdAt || !b.createdAt) return 0;

            for (let i = 0; i < 7; i++) {
              const valueA = a.createdAt[i] || 0;
              const valueB = b.createdAt[i] || 0;
              if (valueA !== valueB) {
                return valueA - valueB;
              }
            }

            return a.eventId.localeCompare(b.eventId);
          });

          this.recordedData = [...sortedEvents];
          this.editedData = [...sortedEvents];
          this.description = response.description;
          this.tcId = response.tcId;
          if (response.category) {
            this.currentCategory = response.category;
          }
        },
        error: ({ error }) => {
          console.error(error);
        },
      });
  }

  fetchEventScreenshots() {
    this.apiService
      .get(`tokens/captured_screenshots/${this.testCaseId}`)
      .subscribe({
        next: (response) => {
          this.eventScreenshots = [...response['screenshots']];
        },
        error: ({ error }) => {
          console.error(error, '12334');
        },
      });
  }

  openEditDialog({
    eventsData,
    index,
  }: {
    eventsData: testCaseEventDataType;
    index: number;
  }) {
    this.dialog.openForm(eventsData, index);
  }

  toggleCategoryDropdown() {
    this.isCategoryDropdownOpen = !this.isCategoryDropdownOpen;
  }

  updateCategory(category: string) {
    const requestBody = {
      testCaseId: this.testCaseId,
      category: category,
    };

    this.apiService
      .put(`tokens/${this.activeVerticalId}/update_test_case_category`, requestBody)
      .subscribe({
        next: (response) => {
          this.currentCategory = category;
          this.isCategoryDropdownOpen = false;
          this.alertService.openAlert({
            message: 'Category updated successfully',
            messageType: 'success',
          });
        },
        error: (error) => {
          this.alertService.openAlert({
            message: 'Error updating category',
            messageType: 'error',
          });
          console.error(error);
        },
      });
  }

  saveEditedData({
    modifiedData,
    index,
  }: {
    modifiedData: testCaseEventDataType;
    index: number;
  }) {
    this.editedData[index] = modifiedData;
    console.log(modifiedData, 'modifiedData');
  }

  get filteredCategories(): string[] {
    return this.categories.filter(
      (category) => category !== this.currentCategory
    );
  }

  openImageDialog(imageUrl: string, rowData: testCaseEventDataType): void {
    console.log('Opening dialog with row data:', rowData);
    if (!imageUrl) {
      this.alertService.openAlert({
        message: 'No screenshot available for this event',
        messageType: 'warning',
      });
      return;
    }

    const imageDialogRef = this.matdialog.open(ImageDialogComponent, {
      data: {
        image: `${this.backendUrl}/tokens${imageUrl}`,
        row: rowData,
      },
      panelClass: 'image-dialog-container',
    });
  }

  openFormDialog({
    eventsData,
    index,
  }: {
    eventsData: testCaseEventDataType;
    index: number;
  }) {
    console.log(eventsData, "data while openeing form")
    const formDialogRef = this.matdialog.open(FormDialogComponent, {
      width: '60vw',
      hasBackdrop: true,
      minWidth: '300px',
      height: '400px',
      panelClass: ['md:w-3/5'],
      data: {
        eventsData: { ...eventsData },
      },
    });
    formDialogRef.afterClosed().subscribe((result) => {
      console.log(result, "result")
      if (result?.message == 'edited') {
        this.editedData[index] = result.editedDataInDialog;
        console.log(this.editedData[index], 'editedData');
      }
    });
  }

  onRevertButtonClick() {
    this.editedData = [...this.recordedData];
  }

  onUpdateButtonClick() {
    console.log(this.editedData, 'update');
    this.apiService
      .put(
        `tokens/${this.activeVerticalId}/update_event`,
        { testCaseId: this.testCaseId, events: this.editedData }
      )
      .subscribe({
        next: (response) => {
          this.alertService.openAlert({
            message: response.message,
            messageType: 'success',
            toRefresh: false,
          });
          this.recordedData = this.editedData;
        },
        error: (error) => {
          this.alertService.openAlert({
            message: 'Got error while updating data',
            messageType: 'error',
          });
        },
      });
  }

  onExecuteButtonClick() {
    this.isExecuting = true;
    this.http
      .post<any>(
        `${environment.apiUrl}/tokens/${this.activeVerticalId}/run-multiple-test-cases`,
        {
          testCaseIds: [this.testCaseId],
        },
        {
          headers: new HttpHeaders().set('Content-Type', 'application/json'),
          withCredentials: true,
        }
      ).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.isExecuting = false;
          this.fetchEventScreenshots();
          this.fetchRecordData();

          this.alertService.openAlert({
            message: 'Test case executed successfully',
            messageType: 'success',
            toRefresh: false,
          });
        },
        error: ({ error }) => {
          this.isExecuting = false;
          this.alertService.openAlert({
            message: 'Got Error while executing',
            messageType: 'error',
          });
          console.error(error);
        },
      });
  }
}