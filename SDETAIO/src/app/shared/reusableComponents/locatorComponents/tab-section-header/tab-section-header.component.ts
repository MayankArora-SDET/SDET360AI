import { Component } from '@angular/core';
import { TitleWithBackButtonComponent } from '../../title-with-back-button/title-with-back-button.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LocatorService } from '../../../../core/service/locator.service';
import { MatSelectModule } from '@angular/material/select';
interface toolType {
  label: string,
  value: string
}


@Component({
  selector: 'app-tab-section-header',
  standalone: true,
  imports: [TitleWithBackButtonComponent, FormsModule, CommonModule, MatSelectModule],
  templateUrl: './tab-section-header.component.html',
  styleUrl: './tab-section-header.component.css'
})
export class TabSectionHeaderComponent {
  constructor(private locatorService: LocatorService) {
    this.locatorService.toolSelected$.subscribe(tool => {
      this.selectedTool = tool

    })
  }
  selectedTool!: string;
  toolList: toolType[] = this.locatorService.getToolList()
  // @Output() toolEmitter: EventEmitter<string> = new EventEmitter<string>()

  onFrameworkChange(event: any) {
    // this.toolEmitter.emit(value)
    this.locatorService.setToolSelected(event.value)

  }
}
