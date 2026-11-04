import { Restaurant } from './restaurant.model';

export class RestaurantWrapped {
  _embedded!: { restaurants: Restaurant[] };
}

