import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DefaultLocatorCreationComponent } from './default-locator-creation.component';

describe('DefaultLocatorCreationComponent', () => {
  let component: DefaultLocatorCreationComponent;
  let fixture: ComponentFixture<DefaultLocatorCreationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DefaultLocatorCreationComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DefaultLocatorCreationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
