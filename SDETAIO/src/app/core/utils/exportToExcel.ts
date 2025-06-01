import * as XLSX from 'xlsx';
export function exportToExcel(data: any) {
    const ws: XLSX.WorkSheet = XLSX.utils.json_to_sheet(data);

    // Create a new workbook and append the worksheet
    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');

    // Write the workbook and trigger the file download
    XLSX.writeFile(wb, 'users.xlsx');
}