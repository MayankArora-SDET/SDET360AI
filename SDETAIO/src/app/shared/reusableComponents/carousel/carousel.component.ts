import { CommonModule } from '@angular/common';
import {  Component, Input, OnInit } from '@angular/core';
import { SlickCarouselModule } from 'ngx-slick-carousel';
import { slideType } from '../../../core/interfaces/loginInterface';

@Component({
  selector: 'app-carousel',
  standalone: true,
  imports: [SlickCarouselModule,CommonModule],
  templateUrl: './carousel.component.html',
  styleUrl: './carousel.component.css'
})
export class CarouselComponent implements OnInit {
  
  @Input() slides!: slideType[];
  @Input() sliderConfig!:any;
  render=false;
 
 ngOnInit(): void {
  this.render=true //to avoid initial rendering issue of carousel 
 }

 
}

