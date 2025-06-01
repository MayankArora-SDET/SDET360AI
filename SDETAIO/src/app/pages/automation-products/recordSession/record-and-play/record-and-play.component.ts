import { Component, OnInit } from '@angular/core';
import { MultitabComponent } from '../../../../shared/ui/multitab/multitab.component';
import { RecordPlayComponent } from '../../../../shared/reusableComponents/recordComponents/record-play/record-play.component';
import { PreviousRecordsComponent } from '../../../../shared/reusableComponents/recordComponents/previous-records/previous-records.component';
import { TitleWithBackButtonComponent } from '../../../../shared/reusableComponents/title-with-back-button/title-with-back-button.component';
import { ActivatedRoute, NavigationEnd, Route, Router } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-record-and-play',
  standalone: true,
  imports: [MultitabComponent, TitleWithBackButtonComponent],
  templateUrl: './record-and-play.component.html',
  styleUrl: './record-and-play.component.css'
})
export class RecordAndPlayComponent implements OnInit {
  tabSections = [
    {
      label: 'Start recording',
      content: RecordPlayComponent,
      id: 'startRecording',
      data: {},
      route: '/automationTesting/recordSession/start'
    },
    {
      label: 'Previous Records',
      content: PreviousRecordsComponent,
      id: 'previousRecords',
      data: {},
      route: '/automationTesting/recordSession/library'
    }
  ];

  activeTabIndex: number = 0;

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        const currentUrl = this.router.url;
        const index = this.tabSections.findIndex(tab => currentUrl.startsWith(tab.route));
        this.activeTabIndex = index !== -1 ? index : 0;
      });

    // Set tab on init (in case router.events doesn't fire on first load)
    const currentUrl = this.router.url;
    const index = this.tabSections.findIndex(tab => currentUrl.startsWith(tab.route));
    this.activeTabIndex = index !== -1 ? index : 0;
  }

  onTabChange(selectedTab: number) {
    this.activeTabIndex = selectedTab;
    this.router.navigate([this.tabSections[selectedTab].route]);
  }

}
