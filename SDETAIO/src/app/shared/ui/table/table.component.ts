import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, output, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatIcon } from '@angular/material/icon';
interface columnDataType {
  key: string;
  label: string;
}

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [MatTableModule, CommonModule, FormsModule, MatIcon],
  templateUrl: './table.component.html',
  styleUrl: './table.component.css'
})
export class TableComponent {
  @Input() columnsToDisplay!: columnDataType[];

  @Input()
  set rowdata(value: any) {
    if (value) {
      this.data = value.map((row: any) => ({
        ...row,
        __isEditing: false,
        __isNewRow: false
      }));
    } else {
      this.data = [];
    }
  }

  columns!: columnDataType[];
  data: any[] | null = null

  @Output() addRowEvent = new EventEmitter();
  @Output() editRowEvent = new EventEmitter();
  @Output() deleteRowEvent = new EventEmitter();

  ngOnChanges() {
    this.columns = [
      ...(this.columnsToDisplay || []),
      { key: 'actions', label: 'Actions' }
    ];
  }
  // ngOnInit(): void {
  //   this.data = [...this.rowdata]
  //   console.log(this.rowdata)
  // }

  isEditing(row: any): boolean {
    return row.__isEditing;
  }

  isNewRow(row: any): boolean {
    return row.__isNewRow;
  }

  addNewRow() {
    if (this.data) {
      const newRow: any = {};
      for (const col of this.columnsToDisplay) {
        newRow[col.key] = '';
      }

      newRow.__isEditing = true;
      newRow.__isNewRow = true;

      this.data.push(newRow);
    }
  }

  editRow(row: any) {
    row.__isEditing = true;
  }

  saveRow(row: any) {
    const { __isNewRow, __isEditing, ...cleanedRow } = row;

    if (__isNewRow) {
      const hasValues = Object.values(cleanedRow).some((v) => v !== '');
      if (hasValues) {
        this.addRowEvent.emit(cleanedRow);
      }
    } else if (__isEditing) {
      this.editRowEvent.emit(cleanedRow);
    }

    row.__isEditing = false;
    row.__isNewRow = false;
  }

  deleteRow(index: number) {
    if (this.data) {
      const row = this.data[index];
      row.__isEditing = false;
      row.__isNewRow = false;

      this.deleteRowEvent.emit(row);
      this.data.splice(index, 1);
    }
  }
}
