import { Injectable } from "@angular/core";
import { ApiService } from "../api.service";
import { AuthService } from "../authentication/auth.service";

@Injectable({
    providedIn: 'root'
})
export class recordService {
    activeVerticalId: string = '';
    constructor(private apiService: ApiService, private authService: AuthService) {
        this.authService.activeVertical$.subscribe(activeVertical => activeVertical)

    }
    fetchPreviousRecordData() {
        this.apiService.get(`tokens/${this.activeVerticalId}/recorded-test-cases`)
    }


}