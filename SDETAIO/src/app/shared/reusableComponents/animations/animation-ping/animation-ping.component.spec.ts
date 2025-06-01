import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnimationPingComponent } from './animation-ping.component';

describe('AnimationPingComponent', () => {
  let component: AnimationPingComponent;
  let fixture: ComponentFixture<AnimationPingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnimationPingComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AnimationPingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
