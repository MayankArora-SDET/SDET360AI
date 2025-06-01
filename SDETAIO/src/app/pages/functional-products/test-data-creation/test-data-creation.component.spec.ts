import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestDataCreationComponent } from './test-data-creation.component';

describe('TestDataCreationComponent', () => {
  let component: TestDataCreationComponent;
  let fixture: ComponentFixture<TestDataCreationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestDataCreationComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TestDataCreationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
