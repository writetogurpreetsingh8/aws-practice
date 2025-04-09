import { inject, Injectable, OnInit } from '@angular/core';
import { TasksService } from '../tasks/tasks.service';
import { HttpClient } from '@angular/common/http';
import { User } from './user.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  usersData: User[] = [];
  private httpClient = inject(HttpClient);

  get users() : Observable<User[]>{
    return this.httpClient.get<User[]>("/users")
  }
}
