import { Injectable } from "@angular/core";
import { ApiService } from "./api.service";
import { map } from "rxjs";

@Injectable({ providedIn: "root" })
export class staticChatService {
    constructor(private apiService: ApiService) { }

    generateStaticChatResponse(prompt: string) {
        return this.apiService.post('ai/generate', { "prompt": prompt }).pipe(map(response => response["text"]))

    }

}
