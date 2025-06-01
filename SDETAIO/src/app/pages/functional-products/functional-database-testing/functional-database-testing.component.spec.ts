import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FunctionalDatabaseTestingComponent } from './functional-database-testing.component';

describe('FunctionalDatabaseTestingComponent', () => {
  let component: FunctionalDatabaseTestingComponent;
  let fixture: ComponentFixture<FunctionalDatabaseTestingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FunctionalDatabaseTestingComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FunctionalDatabaseTestingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
