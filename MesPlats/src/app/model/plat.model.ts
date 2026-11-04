import { Restaurant } from './restaurant.model';

export class Plat {
  referencePlat?: number;
  intitulePlat?: string;
  tarifPlat?: number;
  dateAjoutAuMenu?: Date;
  restaurant?: Restaurant;
}
