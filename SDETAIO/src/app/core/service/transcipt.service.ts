import { Injectable } from "@angular/core";
import { ApiService } from "./api.service";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../environments/environment.development";
import { map } from "rxjs";
import { response } from "express";

@Injectable({
    providedIn: 'root'
})
export class TranscriptService {


    constructor(private apiService: ApiService, private http: HttpClient) { }
    private apiUrl = environment.apiUrl;
    getResponse(selectedTab: string, formData: FormData) {
        // this.endpoint = selectedTab === "Transcript" ? "upload_transcript_file" : "upload_srs_brs_file";
        console.log("formData", formData);
        return this.http.post(`${this.apiUrl}/srs/generate-jira-stories`, formData, { withCredentials: true }).pipe(map((response: any) => response["jiraStories"]))
    }
}