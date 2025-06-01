import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-automated-events-display',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './automated-events-display.component.html',
  styleUrl: './automated-events-display.component.css'
})
export class AutomatedEventsDisplayComponent {
  gridItems = [
    { image: 'https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0', text: 'Beautiful Beach' },
    { image: 'https://images.unsplash.com/photo-1521747116042-5a810fda9664', text: 'Mountain View' },
    { image: 'https://images.unsplash.com/photo-1519681393784-d120267933ba', text: 'Sunset Over City' },
    { image: 'https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0', text: 'Dense Forest' },
    { image: 'https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0', text: 'Starry Night' },
    { image: 'https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0', text: 'Ocean Waves' },
    { image: 'https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0', text: 'Golden Desert' },
    { image: 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f', text: 'Snowy Mountains' }
  ];
}
