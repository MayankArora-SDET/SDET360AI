import { Component, OnInit } from '@angular/core';
import { TableComponent } from '../../../ui/table/table.component';
import { DashboardHeaderComponent } from '../dashboard-header/dashboard-header.component';
import { QualityReportService } from '../../../../core/service/qualityReport.service';
import { AlertService } from '../../../../core/service/alert.service';
import { qualityObjectiveStatusDataType, releaseTestCoverageDataType } from '../../../../core/interfaces/dashboard';
import { subscribe } from 'diagnostics_channel';
import { CommonModule } from '@angular/common';
import { error } from 'console';
interface columnDataType {
  key: string;
  label: string;
}
@Component({
  selector: 'app-quality-report-tab',
  standalone: true,
  imports: [TableComponent, DashboardHeaderComponent, CommonModule],
  templateUrl: './quality-report-tab.component.html',
  styleUrl: './quality-report-tab.component.css'
})
export class QualityReportTabComponent {
  selectedVertical: string = 'kan';
  // sampleColumns = [{ key: 'name', label: 'Name' },
  // { key: 'email', label: 'Email' },
  // { key: 'role', label: 'Role' },]
  // sampleData = [{ name: "abc", email: "xyz", role: "user" }]
  testCoverageColumns: columnDataType[] = [{ key: 'epic', label: 'Epic' }, { key: 'severity1', label: 'Severity 1' }, { key: 'severity2', label: 'Severity 2' }, { key: 'severity3', label: 'Severity 3' }, { key: 'severity4', label: 'Severity 4' }, { key: 'testCases', label: 'Test CAses' }]
  testCoverageData: releaseTestCoverageDataType[] | null = null
  qualityObjectiveData: qualityObjectiveStatusDataType[] | null = null
  qualityObjectiveColumns: columnDataType[] = [{ key: 'keyFeature', label: 'Key Feature' }, { key: 'category', label: 'Category' }, { key: 'successCriteriaLevel1', label: 'Success Criteria Level1' }, { key: 'successCriteriaLevel2', label: 'Success Criteria Level2' }, { key: 'status', label: 'Status' }]
  handleSuccess = () => {
    this.alertService.openAlert({
      message: 'Data updated successfully',
      messageType: 'success',
    });
  }
  handleError = () => {
    this.alertService.openAlert({
      message: 'Got Error while updating',
      messageType: 'error',
    });
  }


  constructor(private qualityReportService: QualityReportService, private alertService: AlertService) {
    this.qualityReportService.getReleaseTestCoverageData().subscribe();
    this.qualityReportService.testCoverageData$.subscribe(data => {
      this.testCoverageData = data

    })
    this.qualityReportService.getQualityObjectiveStatusData().subscribe();
    this.qualityReportService.qualityObjectiveStatusData$.subscribe(data => {
      this.qualityObjectiveData = data
    })
  }

  addNewTestCoverageData(newData: releaseTestCoverageDataType) {
    console.log(newData, "new data")
    this.qualityReportService.postReleaseTestCoverageData(newData).subscribe({
      next: this.handleSuccess, error: this.handleError
    })
  }
  editTestCoverageData(editedData: releaseTestCoverageDataType) {
    const idToEdit: string = editedData.id
    this.qualityReportService.putReleaseTestCoverageData(idToEdit, editedData).subscribe({
      next: this.handleSuccess, error: this.handleError
    })

  }
  deleteTestCoverageData(dataToDelete: releaseTestCoverageDataType) {
    const idToDelete = dataToDelete.id;
    this.qualityReportService.deleteReleaseTestCoverageData(idToDelete).subscribe({
      next: this.handleSuccess, error: this.handleError
    })
  }
  addQualityObjectiveData(newData: qualityObjectiveStatusDataType) {
    console.log(newData, "new data")
    this.qualityReportService.postQualityObjectiveStatusData(newData).subscribe({
      next: this.handleSuccess, error: this.handleError
    })
  }
  editQualityObjectiveData(editedData: qualityObjectiveStatusDataType) {
    const idToEdit: string = editedData.id
    this.qualityReportService.putQualityObjectiveStatusData(idToEdit, editedData).subscribe({
      next: this.handleSuccess, error: this.handleError
    })

  }
  deleteQualityObjectiveData(dataToDelete: releaseTestCoverageDataType) {
    const idToDelete = dataToDelete.id;
    this.qualityReportService.deleteQualityObjectiveStatusData(idToDelete).subscribe({
      next: this.handleSuccess, error: this.handleError
    })
  }





  onVerticalChange(value: any) {
    // this.getQualityReportDataOnVerticalChange(); // Fetch data again when vertical changes
    // this.selectedVertical = value;
    // this.updatedEpicDataSource = [...this.epicDataSource[value]]; // Update the epic data source based on the selected vertical and to render the table
    // this.updatedcriteriaDataSource = [...this.criteriaDataSource[value]]; // Update the criteria data source based on the selected vertical and to render the table

  }

  epicDataSource: any = {}

  criteriaTableColumns: string[] = ['keyFeatures', 'category', 'successCriteriaLevelone', 'successCriteriaLevelTwo', 'status'];
  criteriaColumnLabels = {
    keyFeatures: 'Key Features',
    category: 'Category',
    successCriteriaLevelone: 'Success Criteria Level 1',
    successCriteriaLevelTwo: 'Success Criteria Level 2',
    status: 'Status'
  }
  criteriaDataSource: any = {}
  updatedEpicDataSource!: any[]; // Initialize updatedEpicDataSource as an empty array
  updatedcriteriaDataSource!: any[];
  updateCriteriaData(data: any) {
    const updatedData = { ...this.criteriaDataSource, [this.selectedVertical]: data }// Create a new object with the updated data for the selected vertical
    console.log("Updated data: ", updatedData)
    // this.qualityReportService.setQualityReportData(updatedData).subscribe({
    //   next: (response) => {
    //     this.alertService.openAlert({
    //       message: 'Data updated successfully',
    //       messageType: 'success',
    //     });


    //   },
    //   error: (error) => {
    //     console.error("Error updating data: ", error);
    //   }
    // })
  }
}
