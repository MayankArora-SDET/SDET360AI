import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { dialogFormService } from '../../../../../core/service/recordSession/dialogFormData';
import { testCaseEventDataType } from '../../../../../core/interfaces/recordSession';

@Component({
  selector: 'app-form-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './form-dialog.component.html',
  styleUrl: './form-dialog.component.css'
})
export class FormDialogComponent {
  constructor(private dialogRef: MatDialogRef<FormDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any, private formService: dialogFormService) { }
  savedFormData = this.data.eventsData;
  actionOptions = this.formService.gethtmlActions();
  inputTypeOptions = this.formService.gethtmlInputTypes();
  // currentIndexOfEdit = this.data.currentIndexOfEdit;

  closeDialog(): void {
    this.dialogRef.close({ message: "closed" });
  }
  onDataSubmit(event: Event, editedData: testCaseEventDataType) {
    event.preventDefault();
    editedData.eventId = this.savedFormData.eventId;
    this.dialogRef.close({ message: "edited", editedDataInDialog: editedData });
  }
}
