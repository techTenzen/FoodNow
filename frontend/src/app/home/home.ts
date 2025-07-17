import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatGridListModule } from '@angular/material/grid-list';
import { NavbarComponent } from '../shared/navbar/navbar';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatGridListModule,
    NavbarComponent
  ],
  templateUrl: './home.html',
  styleUrls: ['./home.scss']
})
export class HomeComponent implements OnInit {
  userName: string = '';
  featuredRestaurants = [
    {
      id: 1,
      name: 'Pizza Palace',
      image: 'assets/images/pizza-palace.jpg',
      rating: 4.5,
      cuisine: 'Italian',
      deliveryTime: '30-45 min',
      deliveryFee: '$2.99'
    },
    {
      id: 2,
      name: 'Burger Barn',
      image: 'assets/images/burger-barn.jpg',
      rating: 4.3,
      cuisine: 'American',
      deliveryTime: '25-40 min',
      deliveryFee: '$1.99'
    },
    {
      id: 3,
      name: 'Sushi Supreme',
      image: 'assets/images/sushi-supreme.jpg',
      rating: 4.7,
      cuisine: 'Japanese',
      deliveryTime: '35-50 min',
      deliveryFee: '$3.99'
    },
    {
      id: 4,
      name: 'Taco Fiesta',
      image: 'assets/images/taco-fiesta.jpg',
      rating: 4.2,
      cuisine: 'Mexican',
      deliveryTime: '20-35 min',
      deliveryFee: '$2.49'
    }
  ];

  constructor(private authService: AuthService) {}

  ngOnInit() {
    // Get user info or set default
    this.userName = 'User'; // You can get this from token or user service
  }

  orderFood(restaurantId: number) {
    console.log('Ordering from restaurant:', restaurantId);
    // Implement order logic here
  }
}