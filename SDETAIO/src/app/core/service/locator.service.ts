import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ApiService } from "./api.service";
import { AuthService } from "./authentication/auth.service";
import { BehaviorSubject } from "rxjs";
@Injectable({
    providedIn: 'root'
})
export class LocatorService {
    constructor(private http: HttpClient, private apiService: ApiService, private authService: AuthService) {
        this.authService.activeVertical$.subscribe((verticalId) => {
            this.verticalId = verticalId
        })
    };
    private toolSelected = new BehaviorSubject<string>("selenium-java")
    toolSelected$ = this.toolSelected.asObservable();
    private toolList = [{ label: "Selenium-Java", value: "selenium-java" }, { label: "Selenium-Python", value: "selenium-python" }, { label: "Robot Framework", value: "robotframework" }, { label: "Cypress", value: "cypress" }]
    private verticalId: string = ""
    setToolSelected(toolSelected: string): void {
        this.toolSelected.next(toolSelected)
    }

    getToolList(): { label: string; value: string }[] {
        return this.toolList;
    }
    getLocatorDataByPrompt(prompt: string) {
        const activeTool = this.toolSelected.getValue()

        return this.apiService.postWithHtmlContent(`locators/${this.verticalId}/generate_locators?tool=${activeTool}`, prompt)
    }

    getLocatorDataByUrl(urlEntered: string) {
        const activeTool = this.toolSelected.getValue()
        const params = { url: urlEntered, tool: this.toolSelected }
        return this.apiService.get(`locators/${this.verticalId}/generate_url_locators?tool=${activeTool}&url=${urlEntered}`)
    }



}
