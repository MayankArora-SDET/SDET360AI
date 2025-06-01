import { Component, ElementRef, Output, ViewChild } from '@angular/core';
import { testCaseEventDataType, formElementsType } from '../../../../../core/interfaces/recordSession';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { dialogFormService } from '../../../../../core/service/recordSession/dialogFormData';
import { EventEmitter } from '@angular/core';
@Component({
  selector: 'app-edit-dialog-box',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './edit-dialog-box.component.html',
  styleUrl: './edit-dialog-box.component.css'
})
export class EditDialogBoxComponent {
  constructor(private formService: dialogFormService) { }
  @ViewChild('myModal', { static: false }) myModal!: ElementRef;
  @Output() dialogBoxEmitter: EventEmitter<any> = new EventEmitter<any>()
  dialogBox!: HTMLElement;
  savedFormData: formElementsType = {
    "eventId": '',
    "relativeXPath": '',
    "action": '',
    "type": '',
    "value": '',
    "absoluteXPath": '',
    "relationalXPath": '',
    "assertion": false
  };
  indexToSave: number = 1;
  displayAction = "";
  actionOptions = this.formService.gethtmlActions();
  inputTypeOptions = this.formService.gethtmlInputTypes();
  ngAfterViewInit(): void {
    this.dialogBox = this.myModal.nativeElement as HTMLElement;
  }
  resetToDefault() {
    this.savedFormData = {
      "eventId": '',
      "relativeXPath": '',
      "action": '',
      "type": '',
      "value": '',
      "absoluteXPath": '',
      "relationalXPath": '',
      "assertion": false
    };
    this.indexToSave = -1;
  }
  close(): void {
    this.dialogBox.classList.remove('show');
    this.displayAction = ""
    this.resetToDefault()

  }
  openForm(eventsData: testCaseEventDataType, index: number): void {
    this.displayAction = "form"
    this.dialogBox.classList.add('show');
    this.savedFormData = { ...eventsData }
    this.indexToSave = index


  }
  openImage(): void {
    this.displayAction = "image"
    this.dialogBox.classList.add('show');
  }
  onDataSubmit(event: Event, modifiedData: testCaseEventDataType) {
    event.preventDefault();
    console.log(modifiedData, "modifiedData")
    this.dialogBoxEmitter.emit({ modifiedData, index: this.indexToSave })
    this.close()
  }


}
