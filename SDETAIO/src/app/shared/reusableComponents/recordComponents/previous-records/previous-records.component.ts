import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../../../core/service/authentication/auth.service';
import { AfterViewInit, Component, ElementRef, OnInit, QueryList, ViewChildren } from '@angular/core';
import { environment } from '../../../../../environments/environment.development';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { AlertService } from '../../../../core/service/alert.service';
import { MatIcon } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { ApiService } from '../../../../core/service/api.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-previous-records',
  standalone: true,
  providers: [AlertService],
  imports: [
    CommonModule,
    RouterOutlet,
    FormsModule,
    MatIcon,
    MatMenuModule,
    MatButtonModule,
    MatDividerModule,
  ],
  templateUrl: './previous-records.component.html',
  styleUrl: './previous-records.component.css',
})
export class PreviousRecordsComponent implements OnInit {
  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute,
    private alertService: AlertService,
    private authService: AuthService,
    private apiService: ApiService
  ) {

  }

  categories = ['All', 'Smoke', 'Sanity', 'Regression', 'Functional'];
  currentCategory = 'All';
  private destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.authService.activeVertical$
      .pipe(takeUntil(this.destroy$))
      .subscribe(verticalId => {
        this.activeVerticalId = verticalId;
        this.fetchRecords();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();     // Triggers unsubscription
    this.destroy$.complete(); // Cleanly finishes the subject
  }

  recordingData = [];
  groupedRecordingData: { [key: string]: any[] } = {};
  displayCategories: string[] = [];
  isExecuting = false;
  activeVerticalId = ''
  @ViewChildren('checkbox') checkboxes!: QueryList<
    ElementRef<HTMLInputElement>
  >;
  fetchRecords() {
    const selectedCategory = this.currentCategory;

    let url = `tokens/${this.activeVerticalId}/recorded-test-cases`;
    if (selectedCategory.toLowerCase() !== 'all') {
      url = `${url}?category=${selectedCategory.toLowerCase()}`;
    }
    this.apiService.get(url)
      .subscribe({
        next: (response) => {
          this.recordingData = response.test_cases;
          if (this.currentCategory === 'All') {
            this.groupRecordsByCategory();
          }
        },
        error: ({ error }) => {
          console.log(error);
        },
      });
  }
  onCardClick(record: any) {
    console.log(record, 'record');
    this.router.navigate([record['test_case_id']], { relativeTo: this.route });
  }
  get filteredCategories(): string[] {
    return this.categories.filter(
      (category) => category !== this.currentCategory
    );
  }
  groupRecordsByCategory() {
    this.groupedRecordingData = {};
    this.displayCategories = [];

    this.categories.forEach((category) => {
      if (category !== 'All') {
        this.groupedRecordingData[category] = [];
      }
    });

    this.recordingData.forEach((record) => {
      const categoryValue = record['category'] || '';
      const category = this.capitalizeFirstLetter(categoryValue);
      if (!this.groupedRecordingData[category]) {
        this.groupedRecordingData[category] = [];
      }
      this.groupedRecordingData[category].push(record);
    });

    this.displayCategories = Object.keys(this.groupedRecordingData).filter(
      (category) => this.groupedRecordingData[category].length > 0
    );
  }

  capitalizeFirstLetter(str: string): string {
    if (!str) return '';
    return str.charAt(0).toUpperCase() + str.slice(1);
  }
  onCheckBoxClick(event: Event, testCaseId: string) {
    event.stopPropagation();
  }
  selectCategory(category: string) {
    this.currentCategory = category;
    this.fetchRecords();
  }

  toggleSelectionButton() {
    console.log('Button selected');
  }
  executeTestCases() {
    const checkedItems = this.checkboxes
      .filter((box) => box.nativeElement.checked)
      .map((box) => box.nativeElement.value);
    if (checkedItems.length < 1) {
      // alert("Select atleast one test case")
      this.alertService.openAlert({
        message: 'Select atleast one test case',
        messageType: 'warning',
      });
    } else {
      this.isExecuting = true;
      this.http
        .post<any>(
          `${environment.apiUrl}/tokens/${this.activeVerticalId}/run-multiple-test-cases`,
          {
            testCaseIds: checkedItems,
          },
          {
            headers: new HttpHeaders().set('Content-Type', 'application/json'),
            withCredentials: true,
          }
        )
        .subscribe({
          next: (response) => {
            this.isExecuting = false;
            this.alertService.openAlert({
              message: 'All Test cases executed successfully',
              messageType: 'success',
            });
          },
          error: ({ error }) => {
            this.isExecuting = false;
            this.alertService.openAlert({
              message: 'Got Error while executing test cases',
              messageType: 'error',
            });
            console.error(error);
          },
        });
    }
  }
  onDeleteClick(event: Event, idSelected: string) {
    event.stopPropagation();
    this.apiService
      .post(
        `tokens/delete_test_case`,
        { testCaseId: idSelected }
      )
      .subscribe({
        next: (response) => {
          this.alertService.openAlert({
            message: 'Deleted test case successfully',
            messageType: 'success',
          });
          this.fetchRecords();
        },
        error: ({ error }) => {
          console.log(error);
          this.alertService.openAlert({
            message: 'Deletion Failed',
            messageType: 'error',
          });
        },
      });
  }
  // openAlertDialog() {

  //   // const formDialogRef = this.matdialog.open(AlertPopUpComponent, {
  //   //   width: "60vw",
  //   //   hasBackdrop: true,
  //   //   minWidth: "300px",
  //   //   height: "400px",
  //   //   panelClass: ['md:w-3/5'],

  //   // })

  // }
}
