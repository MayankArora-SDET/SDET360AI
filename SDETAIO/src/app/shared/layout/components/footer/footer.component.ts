// src/app/shared/reusableComponents/footer/footer.component.ts
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { VersionService } from '../../../../core/service/footer.service';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css',
})
export class FooterComponent implements OnInit {
  versionNumber: string = '';
  currentYear: number = new Date().getFullYear();
  isLoading: boolean = true;

  constructor(private versionService: VersionService) {}

  ngOnInit(): void {
    this.fetchVersionNumber();
  }

  fetchVersionNumber(): void {
    this.versionService.getVersion().subscribe({
      next: (version) => {
        this.versionNumber = version;
        this.isLoading = false;
      },
      error: () => {
        this.versionNumber = 'v1.0.0';
        this.isLoading = false;
      },
    });
  }
}
