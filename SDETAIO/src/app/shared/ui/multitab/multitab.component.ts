import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, Type } from '@angular/core';
import { MatTabsModule, MatTabChangeEvent } from '@angular/material/tabs';
import { GeneralChatComponent } from '../../../pages/general-chat/general-chat.component';
@Component({
  selector: 'app-multitab',
  standalone: true,
  imports: [MatTabsModule, CommonModule, GeneralChatComponent, CommonModule],
  templateUrl: './multitab.component.html',
  styleUrl: './multitab.component.css'
})
export class MultitabComponent {

  @Input() tabs: Array<{ label: string, content: Type<any>, id: string, data?: any, route?: string }> = [];
  @Output() onTabChangeEvent = new EventEmitter<number>();
  selectedTab: string = '';
  @Input() activeTabIndex: number = 0;
  @Input() customButtons = true
  ngOnInit() {
    this.selectedTab = this.tabs[0]?.label;
  }
  sampleInputs = {
    componentType: "sampleInputData",
    showSamplePrompts: false
  }
  onTabChange(event: MatTabChangeEvent) {
    this.selectedTab = event.tab.textLabel || '';
    this.activeTabIndex = event.index
    console.log(event)
    this.onTabChangeEvent.emit(event.index);
  }
}
