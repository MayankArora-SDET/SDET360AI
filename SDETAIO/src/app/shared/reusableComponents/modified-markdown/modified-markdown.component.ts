import { AfterViewInit, Component, ElementRef, Inject, Input, Renderer2 } from '@angular/core';
import { MarkdownModule } from 'ngx-markdown';
import { exportToExcel } from '../../../core/utils/exportToExcel';
import { AlertService } from '../../../core/service/alert.service';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-modified-markdown',
  standalone: true,
  imports: [MarkdownModule],
  providers: [AlertService],
  templateUrl: './modified-markdown.component.html',
  styleUrl: './modified-markdown.component.css',
})
export class ModifiedMarkdownComponent implements AfterViewInit {
  @Input() data!: string;
  constructor(
    private renderer: Renderer2,
    private el: ElementRef,
    private alertService: AlertService,
  ) { }
  ngAfterViewInit(): void {
    setTimeout(() => {
      console.log('rendered');
      this.insertCheckBoxesUsingRenderer();
    }, 0);
  }
  insertCheckBoxesUsingRenderer() {
    const markdownElements = Array.from(
      this.el.nativeElement.querySelectorAll('markdown ul, markdown ol,markdown table')
    );

    markdownElements
      .filter((element: any) => {
        const textContent = element.previousElementSibling.textContent.trim();
        console.log(textContent)

        if (/^Test Case \d+/.test(textContent) || /^\d/.test(textContent) || /^Test Case ID\b/.test(textContent)) {
          return true;
        } else return false;
      })
      .forEach((element: any) => {
        const titleBox = element.previousElementSibling; //p tag
        const stepsBox = element; //ul tag
        const checkBox = this.createCheckBox();
        //wrap  title,checkbox and ul in one div element
        const wrapper = this.renderer.createElement('div');
        this.renderer.setAttribute(wrapper, 'class', 'wrapper');
        this.renderer.appendChild(wrapper, checkBox);
        this.renderer.setAttribute(titleBox, 'class', 'testCaseTitle');
        this.renderer.setAttribute(stepsBox, 'class', 'testCaseSteps');
        this.renderer.appendChild(wrapper, element.previousElementSibling);

        this.renderer.appendChild(wrapper, element);

        this.el.nativeElement
          .getElementsByTagName('markdown')[0]
          .appendChild(wrapper);
      });
  }
  createCheckBox() {
    const checkBox = this.renderer.createElement('input');
    this.renderer.setAttribute(checkBox, 'type', 'checkbox');

    this.renderer.setAttribute(checkBox, 'class', 'testCaseCheckBox');
    return checkBox;
  }



}
