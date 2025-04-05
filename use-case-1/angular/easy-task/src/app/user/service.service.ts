import { Injectable } from '@angular/core';
import { DUMMY_USERS } from './dummy-users';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private users = DUMMY_USERS;

  constructor() { }

  get user(){
    return this.users;
  }
}
