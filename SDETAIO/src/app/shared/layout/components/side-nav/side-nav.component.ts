import { CommonModule } from '@angular/common';
import { Component, Input, OnChanges } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CardDataService } from '../../../../core/service/cardData.service';
import { MatIconModule } from '@angular/material/icon';
import { NavigationBarService } from '../../../../core/service/navigationBar.service';
import { expand } from 'rxjs';
import { AuthService } from '../../../../core/service/authentication/auth.service';
@Component({
  selector: 'app-side-nav',
  standalone: true,
  imports: [RouterModule, CommonModule, MatIconModule],
  templateUrl: './side-nav.component.html',
  styleUrl: './side-nav.component.css',
})
export class SideNavComponent {
  constructor(
    private cardDataService: CardDataService,
    private router: Router,
    private navService: NavigationBarService,
    private authService: AuthService
  ) {
    this.navService.sideMenuExpanded$.subscribe(state => {
      this.sideNavExpanded = state
    })
    this.authService.userDetails$.subscribe(data => {
      this.userdetails = { ...data }
    })
  }
  userdetails!: { username: string, email: string }

  sideNavExpanded!: boolean
  onMenuIconClick() {
    this.navService.toggleSideMenu();

  }

  // openPrompt
  // businessReq
  // testing
  // setting
  // accessibility
  // performance
  menuItems = [
    {
      id: 'dashboard',
      label: 'Quality Dashboard',
      icon: '/assets/icons/trending.png',
      linkTo: '/',
    },
    {
      id: 'genaralChat',
      label: 'Open Prompt',
      icon: '/assets/icons/note.png',
      linkTo: '/generalChat',
    },
    {
      id: 'product',
      label: 'Business Requirements',
      icon: '/assets/icons/briefcase.png',
      linkTo: '/productFeatures',
    },

    /** Functional Testing Section */
    {
      id: 'functionalTesting',
      label: 'Functional Testing',
      icon: '/assets/icons/list.png',
      linkTo: '/functionalTesting',
      expanded: false,
      subMenuItems: [
        {
          category: 'Web Testing',
          expanded: false,
          items: [
            {
              id: 'testCaseGeneration',
              label: 'Test Case Generator',
              linkTo: 'functionalTesting/testCaseGeneration',
              icon: 'functionalTesting/testCaseGeneration.png',
            },
            {
              id: 'edgeCaseAnalysis',
              label: 'Edge Case Generator',
              linkTo: 'functionalTesting/edgeCaseAnalysis',
              icon: 'functionalTesting/edgeCaseAnalysis.png',
            },
            {
              id: 'testDataCreation',
              label: 'Test Data Generator',
              linkTo: 'functionalTesting/testDataCreation',
              icon: 'functionalTesting/testDataCreation1.png',
            },
            {
              id: 'predictBug',
              label: 'Predict a Bug',
              linkTo: 'functionalTesting/predictBug',
              icon: 'functionalTesting/logBug.png',
            },
          ],
        },
        {
          category: 'Database Testing',
          expanded: false,
          items: [
            {
              id: 'databaseTesting',
              label: 'Database Test Case Generator',
              linkTo: '/functionalTesting/databaseTesting',
              icon: 'database.png',
            },
          ],
        },
        {
          category: 'API Testing',
          expanded: false,
          items: [
            {
              id: 'apiTesting',
              label: 'API Test Case Generator',
              linkTo: '/functionalTesting/apiTesting',
              icon: 'api.png',
            },
          ],
        },
      ],
    },

    /** Automation Testing Section */
    {
      id: 'automationTesting',
      label: 'Automation Testing',
      icon: '/assets/icons/gear.png',
      linkTo: '/automationTesting',
      expanded: false,
      subMenuItems: [
        {
          category: 'Web Automation',
          expanded: false,
          items: [
            {
              id: 'locatorsCreation',
              label: 'Locator Generator',
              linkTo: '/automationTesting/locatorsCreation',
              icon: 'automationTesting/locatorsCreation.png',
            },
            {
              id: 'customFunctions',
              label: 'Code Generator',
              linkTo: '/automationTesting/codeGenerator',
              icon: 'automationTesting/customFunctions.png',
            },
            {
              id: 'codeGeneratorWithText',
              label: 'Code Generator With Text',
              linkTo: '/automationTesting/codeGeneratorWithText',
              icon: 'automationTesting/customFunctions.png',
            },
            {
              id: 'testExecution',
              label: 'Test Execution',
              linkTo: 'https://github.com/MayankArora-SDET/SDETAIO/actions',
              icon: 'automationTesting/testExecution.png',
              isExternal: true,
            },
            {
              id: 'executionReport',
              label: 'Execution Report',
              linkTo: '/automationTesting/executionReport',
              icon: 'automationTesting/executionReport.png',
            },
            {
              id: 'codelessAutomation',
              label: 'Record and Play',
              linkTo: '/automationTesting/recordSession/start',
            },
          ],
        },
        {
          category: 'API Automation Testing',
          expanded: false,
          items: [
            {
              id: 'apiTesting',
              label: 'API Testing',
              linkTo: '/automationTesting/apiTesting',
              icon: 'api.png',
              isExternal: false,
            },
          ],
        },
        {
          category: 'Database Automation Testing',
          expanded: false,
          items: [
            {
              id: 'databaseTesting',
              label: 'Database Testing',
              linkTo: '/automationTesting/databaseTesting',
              icon: 'database.png',
              isExternal: false,
            },
          ],
        },
      ],
    },
    {
      id: 'accessibilityTesting',
      label: 'Accessibility Testing',
      linkTo: '/accessibility',
      icon: '/assets/icons/view.png',
    },
    {
      id: 'performanceTesting',
      label: 'Performance Testing',
      linkTo: '/performance',
      icon: '/assets/icons/star.png',
    },
    {
      id: 'profile',
      label: 'Profile',
      linkTo: '/profile',
      icon: '/assets/icons/user.png',
    },
  ];


  /** Toggle Main Submenu */
  toggleSubMenu(index: number) {
    this.menuItems[index].expanded = !this.menuItems[index].expanded;
  }

  /** Toggle Submenu Categories */
  toggleCategorySubMenu(menuIndex: number, categoryIndex: number) {
    const menu = this.menuItems[menuIndex];
    if (menu && menu.subMenuItems && menu.subMenuItems[categoryIndex]) {
      menu.subMenuItems[categoryIndex].expanded =
        !menu.subMenuItems[categoryIndex].expanded;
    }
  }

  // onMenuIconClick() {
  //   this.navService.toggleSideMenu();

  // }
}
