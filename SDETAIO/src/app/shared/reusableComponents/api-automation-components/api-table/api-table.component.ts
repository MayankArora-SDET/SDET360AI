
import { Component, Input, OnInit } from '@angular/core';
import { apiTableDataType, urlDataType } from '../../../../core/interfaces/apiAutomation';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-api-table',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './api-table.component.html',
  styleUrl: './api-table.component.css'
})
export class ApiTableComponent {
  tableData: apiTableDataType[] = [{ key: "", value: "", description: "" }];
  urlData: urlDataType = { params: [{ key: "", value: "", description: "" }], headers: [{ key: "", value: "", description: "" }], body: "" }
  @Input() selectedInfoType!: string;
  consoleData() {
    console.log(this.urlData[this.selectedInfoType])

  }
  onInputChange(rowNumber: number) {
    if (rowNumber == this.urlData[this.selectedInfoType].length - 1) {
      this.urlData[this.selectedInfoType].push({ key: "", value: "", description: "" })
    }
  }
  onDeleteIconClick(rowNumber: number) {
    (this.urlData[this.selectedInfoType].splice(rowNumber, 1))
  }
  dataToSendApi() {
    let dataToSend = { params: this.urlData['params'].filter((item: any) => item.key != ''), headers: this.urlData['headers'].filter((item: any) => item.key != ''), body: this.urlData["body"] }
    return dataToSend;
  }


}
