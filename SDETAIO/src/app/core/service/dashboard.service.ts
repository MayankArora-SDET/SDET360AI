import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map, catchError, of, BehaviorSubject } from 'rxjs';
import { PieChartData } from '../../core/interfaces/pieChartData';
import { environment } from '../../../environments/environment.development';

export interface BugData {
  bug_count_by_priority: {
    [key: string]: number;
  };
  total_unresolved_bugs: number;
}

export interface StoriesEpicsData {
  name: string;
  series: {
    name: string;
    value: number;
  }[];
}

export interface TestExecutionData {
  name: string;
  series: {
    name: string;
    value: number;
  }[];
}

@Injectable({
  providedIn: 'root',
})
export class DashboardDataService {
  private httpOptions = {
    headers: new HttpHeaders().set('Content-Type', 'application/json'),
    withCredentials: true,
  };
  private apiBaseUrl = environment.apiUrl;

  private testCaseStatusSubject = new BehaviorSubject<PieChartData[]>([]);
  //   private testCaseCategorySubject = new BehaviorSubject<PieChartData[]>([]);
  private verticalSelectedSubject = new BehaviorSubject<string>('kan');
  public verticalSelected$ = this.verticalSelectedSubject.asObservable();


  private testCaseProgressByLabelsSubject = new BehaviorSubject<any[]>([]);

  private defectsByPrioritySubject = new BehaviorSubject<PieChartData[]>([]);
  private storiesAndEpicsSubject = new BehaviorSubject<StoriesEpicsData[]>([]);
  private testExecutionSubject = new BehaviorSubject<TestExecutionData[]>([]);
  private testExecutionStatusSubject = new BehaviorSubject<PieChartData[]>([]);
  private bugPrioritySubject = new BehaviorSubject<PieChartData[]>([]);
  private automationTestStatusSubject = new BehaviorSubject<PieChartData[]>([]);

  private rawBugDataSubject = new BehaviorSubject<BugData>({
    bug_count_by_priority: {},
    total_unresolved_bugs: 0,
  });

  public testCaseStatus$ = this.testCaseStatusSubject.asObservable();
  //   public testCaseCategory$ = this.testCaseCategorySubject.asObservable();
  public automationTestStatus$ =
    this.automationTestStatusSubject.asObservable(); // New observable stream
  public defectsByPriority$ = this.defectsByPrioritySubject.asObservable();
  public storiesAndEpics$ = this.storiesAndEpicsSubject.asObservable();
  public testExecutionStatus$ = this.testExecutionStatusSubject.asObservable();
  public testExecution$ = this.testExecutionSubject.asObservable();
  public bugPriority$ = this.bugPrioritySubject.asObservable();
  public rawBugData$ = this.rawBugDataSubject.asObservable();
  public testCaseProgressByLabels$ =
    this.testCaseProgressByLabelsSubject.asObservable();

  constructor(private http: HttpClient) { }

  // Method to initialize all data - call this from app initialization or dashboard component


  public loadAllDashboardData(): void {
    this.fetchTestCaseStatus();
    // this.fetchTestCaseCategory();
    this.fetchDefectsByPriority();
    this.fetchStoriesAndEpics();
    this.fetchTestExecution();
    this.fetchTestExecutionStatus();
    this.fetchBugPriority();
    this.fetchAutomationTestStatus();
    this.fetchTestCaseProgressByLabels();
  }
  public setVerticalSelected(vertical: string): void {
    this.verticalSelectedSubject.next(vertical);
  }
  private fetchAutomationTestStatus(): void {
    this.http
      .get<any>(`${this.apiBaseUrl}/automation_tasks_status`, this.httpOptions)
      .pipe(
        map((response) => this.transformToPieChartDataforStatus(response)),
        catchError((error) => {
          console.error('Error fetching automation test status:', error);
          return of([
            { name: 'To Do', value: 5 },
            { name: 'In Progress', value: 3 },
            { name: 'Completed', value: 7 },
          ]);
        })
      )
      .subscribe((data) => this.automationTestStatusSubject.next(data));
  }
  private fetchTestCaseStatus(): void {
    this.http
      .get<any>(`${this.apiBaseUrl}/test_case_status`, this.httpOptions)
      .pipe(
        map((response) => this.transformToPieChartDataforStatus(response)),
        catchError((error) => {
          console.error('Error fetching test case status:', error);
          return of([
            { name: 'In Progress', value: 8 },
            { name: 'Completed', value: 8 },
            { name: 'To Do', value: 8 },
          ]);
        })
      )
      .subscribe((data) => this.testCaseStatusSubject.next(data));
  }

  private transformToPieChartDataforStatus(
    response: any[]
  ): { name: string; value: number }[] {
    return response.map((item) => ({
      name: item.name,
      value: item.series[0]?.value || 0,
    }));
  }
  private fetchTestExecutionStatus(): void {
    this.http
      .get<any>(
        `${this.apiBaseUrl}/test_case_execution_status`,
        this.httpOptions
      )
      .pipe(
        map((response) => this.transformToPieChartDataforStatus(response)),
        catchError((error) => {
          console.error('Error fetching test execution status:', error);
          return of([
            { name: 'Failed', value: 2 },
            { name: 'Passed', value: 1 },
            { name: 'In Progress', value: 2 },
          ]);
        })
      )
      .subscribe((data) => this.testExecutionStatusSubject.next(data));
  }
  private fetchTestCaseProgressByLabels(): void {
    this.http
      .get<any>(
        `${this.apiBaseUrl}/test_case_progress_by_labels`,
        this.httpOptions
      )
      .pipe(
        map((response) => this.transformTestCaseProgressData(response)),
        catchError((error) => {
          console.error('Error fetching test case progress by labels:', error);
          return of(error);
        })
      )
      .subscribe((data) => this.testCaseProgressByLabelsSubject.next(data));
  }

  private transformTestCaseProgressData(response: any): any[] {
    console.log('API Response Keys:', Object.keys(response));

    const statusFields = [
      "random" //if any Label need to block we need to write it here 
    ];
    const labelKeys = Object.keys(response).filter(
      (key) => !statusFields.includes(key)
    );

    console.log('Filtered Label Keys:', labelKeys);

    return labelKeys.map((key) => ({
      name: key.replace(/_/g, ' '),
      value: response[key],
    }));
  }

  private fetchDefectsByPriority(): void {
    this.http
      .get<BugData>(`${this.apiBaseUrl}/defects_by_priority`, this.httpOptions)
      .pipe(
        catchError((error) => {
          console.error('Error fetching defects by priority:', error);
          return of({
            bug_count_by_priority: {},
            total_unresolved_bugs: 0,
          } as BugData);
        })
      )
      .subscribe((data) => {
        this.rawBugDataSubject.next(data);

        const transformedData = Object.entries(data.bug_count_by_priority).map(
          ([name, value]) => ({ name, value })
        );

        this.defectsByPrioritySubject.next(transformedData);
      });
  }

  private fetchStoriesAndEpics(): void {
    this.http
      .get<StoriesEpicsData[]>(
        `${this.apiBaseUrl}/stories_and_epics_progress`,
        this.httpOptions
      )
      .pipe(
        catchError((error) => {
          console.error('Error fetching stories and epics:', error);
          return of([error]);
        })
      )
      .subscribe((data) => this.storiesAndEpicsSubject.next(data));
  }

  private fetchTestExecution(): void {
    this.http
      .get<TestExecutionData[]>(
        `${this.apiBaseUrl}/test_results_pass_fail_blocked`, this.httpOptions
      )
      .pipe(
        catchError((error) => {
          console.error('Error fetching test execution data:', error);
          return of([
            {
              name: 'Passed',
              series: [
                { name: '1', value: 2 },
                { name: '2', value: 5 },
              ],
            },
          ]);
        })
      )
      .subscribe((data) => this.testExecutionSubject.next(data));
  }

  private fetchBugPriority(): void {
    this.http
      .get<any>(`${this.apiBaseUrl}/unresolved_bugs`, this.httpOptions)
      .pipe(
        map((response) =>
          this.transformToPieChartDataForUnResolveBug(response)
        ),
        catchError((error) => {
          console.error('Error fetching bug priority data:', error);
          return of([
            { name: 'Low', value: 14 },
            { name: 'Medium', value: 20 },
            { name: 'High', value: 4 },
            { name: 'Critical', value: 14 },
          ]);
        })
      )
      .subscribe((data) => this.bugPrioritySubject.next(data));
  }

  private transformToPieChartDataForUnResolveBug(
    response: any[]
  ): { name: string; value: number }[] {
    return response.map((item) => ({
      name: item.name,
      value: item.series[0]?.value || 0,
    }));
  }

  //   // Helper method to transform API response to PieChartData format
  //   // Adjust this based on your actual API response structure
  //   private transformToPieChartData(apiResponse: any): PieChartData[] {
  //     // This is a placeholder transformation - modify based on your API structure
  //     if (Array.isArray(apiResponse)) {
  //       return apiResponse.map((item) => ({
  //         name: item.name || item.category || item.status || item.priority,
  //         value: item.value || item.count || 0,
  //       }));
  //     } else if (typeof apiResponse === 'object') {
  //       return Object.entries(apiResponse).map(([key, value]) => ({
  //         name: key,
  //         value: typeof value === 'number' ? value : 0,
  //       }));
  //     }
  //     return [];
  //   }

  public refreshTestCaseStatus(): void {
    this.fetchTestCaseStatus();
  }

  //   public refreshTestCaseCategory(): void {
  //     this.fetchTestCaseCategory();
  //   }

  public refreshAllData(): void {
    this.loadAllDashboardData();
  }
}
