import { Component } from '@angular/core';
import { CarouselComponent } from '../../shared/reusableComponents/carousel/carousel.component';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from "../../../environments/environment.development";
import { EmailLoginService } from '../../core/service/authentication/emailLogin.service';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { UserDetailService } from '../../core/service/authentication/userDetails.service';
import { event } from 'jquery';
import { AlertService } from '../../core/service/alert.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CarouselComponent, FormsModule, HttpClientModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  providers: [EmailLoginService, AlertService],
})
export class LoginComponent {
  constructor(
    private router: Router,
    private loginService: EmailLoginService,
    private userService: UserDetailService,
    private alertService: AlertService
  ) { }
  private apiUrl = `${environment.apiUrl}/chat_login`;
  slides = [
    {
      img: '/assets/loginSlider/sliderImg1.jpg',
      title: ' Title 1',
      description:
        ' AI can automatically generate test cases using machine learning algorithms, reducing manual effort.',
    },
    {
      img: '/assets/loginSlider/sliderImg2.jpg',
      title: 'Title 2',
      description:
        ' AI drives automation, performs faster to identify errors and causes, suggests fixes and connect a set of related tests.',
    },
    {
      img: '/assets/loginSlider/sliderImg3.jpg',
      title: 'Title 3',
      description:
        ' AI can generate concise summaries of Jira tickets by analyzing their content.',
    },
  ];
  sliderConfig = {
    slidesToShow: 1,
    slidesToScroll: 1,
    dots: true,
    infinite: true,
    autoplay: true,
    autoplaySpeed: 2000,
    prevArrow: false,
    nextArrow: false,
    fade: true,
  };
  onInputChange(event: Event) {
    const target = event.target as HTMLInputElement;
    target.value = target.value.trimStart();
  }
  onSubmit(event: Event, formValue: { email: string; password: string }) {
    event.preventDefault();
    
    if (formValue.email.length > 0 && formValue.password.length > 0) {
      this.loginService
        .loginWithUsernameAndPassword(
          formValue.email.trim(),
          formValue.password.trim()
        )

    } else {
      this.alertService.openAlert({
        message: 'Email or password cannot be empty',
        messageType: 'error',
      });
    }
  }

}