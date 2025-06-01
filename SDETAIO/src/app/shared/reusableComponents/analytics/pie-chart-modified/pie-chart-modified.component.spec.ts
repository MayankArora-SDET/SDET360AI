// src/app/shared/reusableComponents/analytics/pie-chart-modified/pie-chart-modified.component.spec.ts
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { PieChartModifiedComponent } from './pie-chart-modified.component';
import { ScaleType } from '@swimlane/ngx-charts';

describe('PieChartModifiedComponent', () => {
  let component: PieChartModifiedComponent;
  let fixture: ComponentFixture<PieChartModifiedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PieChartModifiedComponent, NgxChartsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(PieChartModifiedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate total value correctly', () => {
    component.data = [
      { name: 'Test 1', value: 10 },
      { name: 'Test 2', value: 20 },
    ];
    expect(component.getTotalValue()).toBe(30);
  });

  it('should calculate percentage correctly', () => {
    component.data = [
      { name: 'Test 1', value: 10 },
      { name: 'Test 2', value: 20 },
    ];
    expect(component.getPercentage({ name: 'Test 1', value: 10 })).toBe(33);
  });
});
