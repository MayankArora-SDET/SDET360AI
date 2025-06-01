import { DOCUMENT } from "@angular/common";
import { Inject, Injectable } from "@angular/core";
import { exportToExcel } from "../utils/exportToExcel";
import { AlertService } from "./alert.service";
@Injectable({
    providedIn: 'root',
})
export class DOMService {
    constructor(@Inject(DOCUMENT) private document: Document, private alertService: AlertService) { }
    getSelectedTestCaseList() {
        let selectedTestCases: string[] = [];
        Array.from(
            this.document.querySelectorAll('.testCaseCheckBox')
        ).forEach((element: any) => {
            if (element.checked) {
                let testCaseVal = element.parentElement.textContent
                    .replace(/\n/g, '')
                    .replace(/\s+/g, ' ');
                selectedTestCases.push(testCaseVal);
            }
        });
        return selectedTestCases;
    }
    extractDataAndSendToExcel() {
        if (this.getSelectedTestCaseList().length == 0) {
            this.alertService.openAlert({
                message: 'Please select atleast one test case',
                messageType: 'warning',
            });
            return;
        }
        let jsonData: any[] = [];
        Array.from(
            this.document.querySelectorAll('.testCaseCheckBox')
        ).forEach((element: any) => {
            if (element.checked) {
                const obj: any = {};
                element.parentElement.childNodes.forEach((child: any) => {
                    if (child.className === 'testCaseTitle') {
                        obj['title'] = child.textContent;
                    }
                    if (child.className === 'testCaseSteps') {
                        child.childNodes.forEach((step: any) => {
                            if (step.tagName === 'LI') {
                                if (step.childNodes[0].tagName == 'STRONG') {
                                    let key = step.childNodes[0].textContent;
                                    let val = step.textContent
                                        .replace(/.*?:/, '')
                                        .replace(/\n/g, '')
                                        .trim();

                                    obj[key] = val;
                                }
                            }
                        });
                    }
                });
                jsonData.push(obj);
            }
        });
        console.log(jsonData)
        exportToExcel(jsonData);
    }

}