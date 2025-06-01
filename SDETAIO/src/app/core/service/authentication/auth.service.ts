import { Injectable, signal } from "@angular/core";
import { CookieService } from 'ngx-cookie-service';
import { LoginService } from "./login.service";
import { Router } from "@angular/router";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { environment } from "../../../../environments/environment";
import { ApiService } from "../api.service";
import { BehaviorSubject, catchError, map, of, tap } from "rxjs";
import { StorageService } from "../storage.service";



interface userDetails {
  username: string;
  email: string;
}
interface verticalList {
  id: string;
  verticalName: string;
  apiKey: string;
  jiraUsername: string;
  jiraServerUrl: string;
}

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  cookieValue: any;

  constructor(private storageService: StorageService, private cookieService: CookieService, private apiService: ApiService, private loginService: LoginService, private router: Router, private http: HttpClient) {
    // this.getUserDetails()
    // this.getVerticalList()
    this.verifyAndLoadUser()
  }
  private userId = new BehaviorSubject<string>(this.storageService.getItem('userId') || '');
  userId$ = this.userId.asObservable();

  private userDetails = new BehaviorSubject<userDetails>({ username: '', email: '' });
  userDetails$ = this.userDetails.asObservable();
  private verticalList = new BehaviorSubject<verticalList[]>([]);
  verticalList$ = this.verticalList.asObservable();
  private activeVertical = new BehaviorSubject<string>(this.storageService.getItem("activeVertical") || '');
  activeVertical$ = this.activeVertical.asObservable();
  private isLoggedIn = new BehaviorSubject<boolean>(this.storageService.getItem("userId") ? true : false);
  isLoggedIn$ = this.isLoggedIn.asObservable();
  private jiraProjectsList = new BehaviorSubject<any>([])
  jiraProjectList$ = this.jiraProjectsList.asObservable();



  // loginStateFunction(val: false) {
  //   this.apiService.get('auth/check-session').subscribe(response=>{
  //     this.isLoggedIn$
  //   })
  // }
  verifyAndLoadUser() {
    return this.apiService.get('auth/check-session').pipe(
      tap(response => {
        if (response.session) {
          this.getVerticalList()
          this.getUserDetails()
          this.isLoggedIn.next(true)

        }

      }), catchError(() => of()))
  }
  getUserDetails() {
    const userIdVal = this.userId.getValue()
    this.apiService.get(`users/${userIdVal}`).pipe(
      map(res => {
        const { username, email } = res;
        return { username, email };
      })
    ).subscribe({
      next: (res: any) => {
        this.userDetails.next(res);

      },
      error: (error) => {
        console.error('Error fetching user details:', error);
      }
    })
  }

  // decodeToken(token: string) {
  //   return token ? jwtDecode(token) : null
  // }
  // getSessionToken() {

  //   return (this.cookieService.get('session')) || null
  // }

  getVerticalList() {

    this.apiService.get('verticals').pipe(
      map((res: any) => {
        return res.map((item: any) => {
          return {
            id: item.id,
            verticalName: item.name,
            apiKey: item.apiKey,
            jiraUsername: item.jiraUsername,
            jiraServerUrl: item.jiraServerUrl,
          }
        })
      })
    ).subscribe({
      next: (res: verticalList[]) => {
        this.verticalList.next(res);
        this.activeVertical.next(this.storageService.getItem('activeVertical') || res[0].id);
        this.storageService.setItem("activeVertical", this.activeVertical.getValue())
        // this.setJiraServer.UrlsList()
      }
    })

  }
  logout() {
    console.log("oout")
    this.http.post(`${environment.apiUrl}/auth/logout`, '', { withCredentials: true, responseType: 'text' })
      .subscribe(msg => {
        this.storageService.clear();
        this.router.navigate(['/login']);
      });

  }

  // setJiraServerUrlsList() {
  //   this.jiraVerticalnames = this.verticalList.getValue().map(item => item.name)

  // }

  refreshUserDetails() {
    this.getUserDetails()
    this.getVerticalList()
  }

  setUserId(userId: string) {
    this.userId.next(userId);
    this.storageService.setItem('userId', userId);
  }
  setActiveVertical(verticalId: string) {
    this.storageService.setItem('activeVertical', verticalId);
    this.activeVertical.next(verticalId);
    console.log(this.activeVertical.getValue(), "activevertical")
  }
  setJiraProjectsList(projectData: any[]) {
    this.jiraProjectsList.next(projectData)
  }


  getJiraProjectData(verticalId: string) {
    return this.apiService.post(`jira-projects/fetch/${verticalId}`, {}).pipe(map(response => {
      return response.map((project: any) => {
        return project["projectName"]
      })
    }))
  }



}   