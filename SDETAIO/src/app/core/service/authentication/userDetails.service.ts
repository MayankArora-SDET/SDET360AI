import { Injectable } from "@angular/core";
import { LoginService } from "./login.service";
import { CookieService } from "ngx-cookie-service";

@Injectable({
    providedIn: 'root'
})
export class UserDetailService {
    username: string = ""
    projectAssigned: string = ""
    constructor(private loginService: LoginService, private cookieService: CookieService) {

    }
    setUsername(name: string) {
        this.username = name

    }
    setProject(name: string) {
        this.projectAssigned = name
    }
    getUsername() {
        return this.username
    }
    getUserDetails() {
        this.cookieService.get('')
    }



}