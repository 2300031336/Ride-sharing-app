export interface Vehicle {
  model: string;
  color: string;
  number: string;
  year: number;
  fuel: string;
}

export interface Driver {
  id: number;
  name: string;
  rating: number;
  experience: string; // e.g., '5 years'
  vehicle: Vehicle;
  price: number;
  eta: string;
  rides: number;
  image: string;
}
