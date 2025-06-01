import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment.development";
import { ApiService } from "./api.service";
import { AuthService } from "./authentication/auth.service";
import { BehaviorSubject, Observable, tap } from "rxjs";
import { qualityObjectiveStatusDataType, releaseTestCoverageDataType } from "../interfaces/dashboard"


@Injectable({
    providedIn: 'root',
})
export class QualityReportService {
    activeVerticalId!: string
    testCoverageData = new BehaviorSubject<releaseTestCoverageDataType[]>([])
    testCoverageData$ = this.testCoverageData.asObservable();
    qualityObjectiveStatusData = new BehaviorSubject<qualityObjectiveStatusDataType[]>([])
    qualityObjectiveStatusData$ = this.qualityObjectiveStatusData.asObservable()

    constructor(private apiService: ApiService, private authService: AuthService) {
        this.authService.activeVertical$.subscribe(activeVertical => {
            this.activeVerticalId = activeVertical

        })
        // this.getReleaseTestCoverageData().subscribe();

        // this.getQualityObjectiveStatusData().subscribe();


    }


    getReleaseTestCoverageData(): Observable<any> {
        return this.apiService.get(`release/${this.activeVerticalId}/release-test-coverage`).pipe(tap((response) => {
            this.testCoverageData.next(response)
        }))
    }
    postReleaseTestCoverageData(data: releaseTestCoverageDataType): Observable<any> {
        return this.apiService.post(`release/${this.activeVerticalId}/release-test-coverage`, data).pipe(tap((updatedData) => {
            const current = this.testCoverageData.getValue();
            let updatedList = [...current, updatedData]
            this.testCoverageData.next(updatedList);
            console.log(current, updatedData)
        }))
    }
    putReleaseTestCoverageData(id: string, data: releaseTestCoverageDataType): Observable<any> {
        return this.apiService.put(`release/${this.activeVerticalId}/release-test-coverage/${id}`, data).pipe(tap((updatedData) => {
            let current = this.testCoverageData.getValue();
            let updatedList = current.map((item) => {
                return item.id == id ? updatedData : item
            })
            this.testCoverageData.next(updatedList)
        }))
    }
    deleteReleaseTestCoverageData(id: string): Observable<any> {
        return this.apiService.delete(`release/${this.activeVerticalId}/release-test-coverage/${id}`).pipe(tap((updatedData) => {
            let current = this.testCoverageData.getValue();
            let updatedList = current.filter((item) => item.id != id)
            this.testCoverageData.next(updatedList)
        }))
    }
    getQualityObjectiveStatusData(): Observable<any> {
        return this.apiService.get(`quality-status/${this.activeVerticalId}/quality-objective-status  `).pipe(tap((response) => {
            this.qualityObjectiveStatusData.next(response)
        }))

    }
    postQualityObjectiveStatusData(data: qualityObjectiveStatusDataType): Observable<any> {
        return this.apiService.post(`quality-status/${this.activeVerticalId}/quality-objective-status`, data).pipe(tap((updatedData) => {
            const current = this.qualityObjectiveStatusData.getValue();
            this.qualityObjectiveStatusData.next([...current, updatedData]);
        }))

    }
    putQualityObjectiveStatusData(id: string, data: qualityObjectiveStatusDataType): Observable<any> {
        return this.apiService.put(`quality-status/${this.activeVerticalId}/quality-objective-status/${id}`, data).pipe(tap((updatedData) => {
            let current = this.qualityObjectiveStatusData.getValue();
            let updatedList = current.map((item) => {
                return item.id == id ? updatedData : item
            })
            this.qualityObjectiveStatusData.next(updatedList)
        }))

    }
    deleteQualityObjectiveStatusData(id: string): Observable<any> {
        return this.apiService.delete(`quality-status/${this.activeVerticalId}/quality-objective-status/${id}`).pipe(tap((updatedData) => {
            let current = this.qualityObjectiveStatusData.getValue();
            let updatedList = current.filter((item) => item.id != id)
            this.qualityObjectiveStatusData.next(updatedList)
        }))

    }





}