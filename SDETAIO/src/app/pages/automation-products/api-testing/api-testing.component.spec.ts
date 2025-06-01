import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApiTestingComponent } from './api-testing.component';

describe('ApiTestingComponent', () => {
  let component: ApiTestingComponent;
  let fixture: ComponentFixture<ApiTestingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApiTestingComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ApiTestingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
