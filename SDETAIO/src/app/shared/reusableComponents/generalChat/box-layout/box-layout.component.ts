import { CommonModule } from '@angular/common';
import { Component, Input, OnInit, SecurityContext } from '@angular/core';
import { MarkdownModule, MarkdownService, SECURITY_CONTEXT } from 'ngx-markdown';
import { PrettyJsonPipe } from '../../../pipes/prettierJson';
import { LocatorTableComponent } from '../../locatorComponents/locator-table/locator-table.component';
@Component({
  selector: 'app-box-layout',
  standalone: true,
  imports: [CommonModule, MarkdownModule, PrettyJsonPipe, LocatorTableComponent],
  templateUrl: './box-layout.component.html',
  styleUrl: './box-layout.component.css',

})
export class BoxLayoutComponent implements OnInit {
  @Input() boxType = ""
  @Input() data: any;
  @Input() componentType!: string;
  ngOnInit(): void {
  }
}
