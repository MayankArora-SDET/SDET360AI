import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { testCaseEventDataType } from '../../../../../core/interfaces/recordSession'; 

@Component({
  selector: 'app-image-dialog',
  standalone: true,
  imports: [CommonModule, MatIcon],
  templateUrl: './image-dialog.component.html',
  styleUrl: './image-dialog.component.css',
})
export class ImageDialogComponent {
  imageToDisplay: string;
  row: testCaseEventDataType;

  constructor(
    private dialogRef: MatDialogRef<ImageDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { image: string; row: testCaseEventDataType }
  ) {
    this.imageToDisplay = this.data.image;
    this.row = this.data.row;
    console.log('Dialog initialized with data:', {
      image: this.imageToDisplay,
      row: this.row,
      hasAssertion: this.row?.assertion !== undefined,
      assertionValue: this.row?.assertion,
      autoHealed: this.row?.autohealed,
    });
  }

  closeDialog(): void {
    this.dialogRef.close();
  }
}
