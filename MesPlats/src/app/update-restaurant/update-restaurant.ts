import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Restaurant } from '../model/restaurant.model';

@Component({
  selector: 'app-update-restaurant',
  imports: [FormsModule, CommonModule],
  templateUrl: './update-restaurant.html',
  styles: ``
})
export class UpdateRestaurant implements OnInit {
  @Input() restaurant!: Restaurant;
  @Input() ajout: boolean = true;

  @Output() restaurantUpdated = new EventEmitter<Restaurant>();
  @Output() categorieUpdated = new EventEmitter<Restaurant>();

  ngOnInit(): void {
    console.log('ngOnInit du composant UpdateRestaurant', this.restaurant);
  }

  saveRestaurant(): void {
    this.restaurantUpdated.emit(this.restaurant);
    this.categorieUpdated.emit(this.restaurant);
  }

  saveCategorie(): void {
    this.saveRestaurant();
  }
}
