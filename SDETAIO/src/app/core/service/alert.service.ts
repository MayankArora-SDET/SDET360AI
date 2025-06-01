import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AlertPopUpComponent } from '../../shared/reusableComponents/popUps/alert-pop-up/alert-pop-up.component';
@Injectable({
  providedIn: 'root',
})
export class AlertService {
  constructor(private dialog: MatDialog) { }

  openAlert({
    message,
    messageType,
    toRefresh = false, // Default value for toRefresh
  }: {
    message: string;
    messageType: string;
    toRefresh?: boolean;
  }): void {
    this.dialog.open(AlertPopUpComponent, {
      width: '400px',
      data: { message, messageType, toRefresh }, // toRefresh included in data
      panelClass: 'custom-dialog-container',
      disableClose: true,
    });
  }
}
