import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-alert-pop-up',
  standalone: true,
  imports: [MatIconModule, CommonModule],
  templateUrl: './alert-pop-up.component.html',
  styleUrls: ['./alert-pop-up.component.css'],
})
export class AlertPopUpComponent {
  message: string;
  messageType: string;
  toRefresh: boolean;

  constructor(
    public dialogRef: MatDialogRef<AlertPopUpComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { message: string; messageType: string, toRefresh?: boolean }
  ) {
    this.message = data.message;
    this.messageType = data.messageType;
    this.toRefresh = data.toRefresh || false; // Default to false if not provided
  }

  closeDialog(): void {
    this.dialogRef.close();
  }

  onOkClick(): void {
    this.dialogRef.close();
    this.toRefresh && window.location.reload();
  }
}
