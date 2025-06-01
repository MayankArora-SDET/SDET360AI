import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../core/service/authentication/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { StorageService } from '../../core/service/storage.service';
import { ApiService } from '../../core/service/api.service';
import { forkJoin, map } from 'rxjs';

interface JiraCreds {
  jira_api_token: string;
  jira_server: string;
  jira_username: string;
}

interface Vertical {
  jira_creds: JiraCreds;
  projects: string[];
  vertical_name: string;
}

interface VerticalData {
  vertical: Vertical[];
}
interface verticalDataType {
  id: string,
  verticalName: string,
  apiKey: string,
  jiraUsername: string,
  jiraServerUrl: string,
  projects?: string[]
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatExpansionModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatSelectModule
  ],
  templateUrl: './profile.component.html',
})
export class ProfileComponent implements OnInit {
  showProfileBox: boolean = false;

  verticalListData: verticalDataType[] | [] = [];
  currentVerticalIdSelected: string = '';
  verticalData: any[] = []
  projectData: { [key: string]: any } | null = null
  profileForm: FormGroup;
  snackBar = inject(MatSnackBar);
  loading = false;
  username: string = '';
  email: string = '';

  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = '/assets/default-avatar.png';


  constructor(private apiService: ApiService, private fb: FormBuilder, private authService: AuthService, private storageService: StorageService) {
    this.profileForm = this.fb.group({
      name: [''],
      email: [''],
      activeVertical: ['', Validators.required],
      role: ['Software Engineer', Validators.required],
      department: ['Engineering', Validators.required],
      phone: ['', Validators.pattern(/^\d{10}$/)],
    });

  }
  ngOnInit(): void {
    this.authService.verticalList$.subscribe((data) => {
      this.verticalListData = data;

      const observables = this.verticalListData.map(vertical =>
        this.authService.getJiraProjectData(vertical.id).pipe(
          map(projects => ({
            ...vertical,
            projects
          }))
        )
      );

      forkJoin(observables).subscribe((finalDataWithProjects) => {
        this.verticalListData = finalDataWithProjects;
        console.log(this.verticalListData, 'final enriched vertical list');
      });
    });
    this.authService.activeVertical$.subscribe((activeVertical) => {

      this.currentVerticalIdSelected = activeVertical;
      console.log(this.currentVerticalIdSelected);
    });
    this.authService.userDetails$.subscribe((data) => {
      this.username = data?.username;
      this.email = data?.email;
      this.profileForm?.patchValue({
        name: this.username,
        email: this.email,
        activeVertical: this.currentVerticalIdSelected,
      });
    });
    // this.authService.getJiraProjectData()
    // this.authService.verticalList$.subscribe(projectData => {
    //   this.verticalData = projectData
    //   console.log(projectData, "verticalList")
    // })

  }
  onVerticalSelectionChange(event: MatSelectChange) {
    this.authService.setActiveVertical(event.value);
  }
  // logout() {
  //   this.authService.logout();
  // }


  onFileSelected(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files[0]) {
      this.selectedFile = fileInput.files[0];

      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result;
      };
      reader.readAsDataURL(this.selectedFile);

      this.snackBar.open('Profile image selected!', 'Close', {
        duration: 2000,
      });
    }
  }

  saveProfile(): void {
    if (this.profileForm?.valid) {
      this.loading = true;
      setTimeout(() => {
        this.loading = false;
      }, 1500);
    } else {
      this.snackBar.open('Please fix the errors in the form', 'Close', {
        duration: 3000,
      });
      this.profileForm?.markAllAsTouched();
    }
  }

  profileIcon() {
    this.showProfileBox = !this.showProfileBox;
  }

  editVertical(vertical: Vertical): void {
    this.snackBar.open(`Edit ${vertical.vertical_name} vertical`, 'Close', {
      duration: 2000,
    });
  }

  showToken(token: string): string {
    return token.substring(0, 50);
  }
}
