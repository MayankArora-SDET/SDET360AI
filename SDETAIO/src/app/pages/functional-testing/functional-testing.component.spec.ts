import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FunctionalTestingComponent } from './functional-testing.component';

describe('FunctionalTestingComponent', () => {
  let component: FunctionalTestingComponent;
  let fixture: ComponentFixture<FunctionalTestingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FunctionalTestingComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FunctionalTestingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
