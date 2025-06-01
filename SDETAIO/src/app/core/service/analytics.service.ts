import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment.development";
import { error } from "console";
import { tap } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class AnalyticsService {
    statusCounts: any = {};

    testCaseResultCounts = {
        "Passed": 0,
        "Failed": 0,
        "Blocked": 0
    };
    bugCounts = {
        "Run_creation": 0,
        "Play_Simulations": 0,
        "Login_Functionality": 0,
        "Reports": 0,
        "Profile_Settings": 0
    };

    executionStatusCounts = {
        "Automated": 0,
        "Pending": 0
    };

    constructor(private http: HttpClient) { }
    getAnalyticsData() {
        return this.http.get<any>(`${environment.apiUrl}/jira-data`, {
            withCredentials: true
        }).pipe(tap(data => {
            // Parse Jira data to extract statuses and their counts
            data.issues.forEach((issue: any) => {
                const status = issue.fields.status.name;
                this.statusCounts[status] = (this.statusCounts[status] || 0) + 1;
            })


            data.issues.forEach((issue: any) => {
                const labels = issue.fields.labels;
                if (labels.includes("Passed")) this.testCaseResultCounts["Passed"]++;
                if (labels.includes("Failed")) this.testCaseResultCounts["Failed"]++;
                if (labels.includes("Blocked")) this.testCaseResultCounts["Blocked"]++;
            });

            // Create the CanvasJS Pie Chart



            data.issues.forEach((issue: any) => {
                const labels = issue.fields.labels;
                if (labels.includes("Run_creation")) this.bugCounts["Run_creation"]++;
                if (labels.includes("Play_Simulations")) this.bugCounts["Play_Simulations"]++;
                if (labels.includes("Login_Functionality")) this.bugCounts["Login_Functionality"]++;
                if (labels.includes("Reports")) this.bugCounts["Reports"]++;
                if (labels.includes("Profile_Settings")) this.bugCounts["Profile_Settings"]++;
            });


            data.issues.forEach((issue: any) => {
                const automationStatus = issue.fields["customfield_10037"];
                if (automationStatus && automationStatus[0] === "Automated") this.executionStatusCounts["Automated"]++;
                if (automationStatus && automationStatus[0] === "Pending") this.executionStatusCounts["Pending"]++;
            });

        }
        ))



    }
}


