import { Component, Input } from '@angular/core';
import { Router } from "@angular/router";
import { Location } from '@angular/common';

@Component({
  selector: 'app-title-with-back-button',
  standalone: true,
  imports: [],
  templateUrl: './title-with-back-button.component.html',
  styleUrl: './title-with-back-button.component.css'
})
export class TitleWithBackButtonComponent {
  constructor(private location: Location) { }
  @Input() title = ""
  goBack() {
    // if (this.router.url.includes("functionalTesting")) {
    //   this.router.navigate(["/functionalTesting"])
    // }

    // else if (this.router.url.includes("automationTesting")) {
    //   this.router.navigate(["/automationTesting"])
    // } else if (this.router.url.includes("productFeatures")) {
    //   this.router.navigate(["/productFeatures"])
    // }
    this.location.back();
  }
}
