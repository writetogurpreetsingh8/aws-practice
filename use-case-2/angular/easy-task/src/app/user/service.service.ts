import { inject, Injectable, OnInit, signal } from '@angular/core';
import { TasksService } from '../tasks/tasks.service';
import { HttpClient } from '@angular/common/http';
import { User } from './user.model';
import { Observable } from 'rxjs';
import { NewUser } from './new-user/new-user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  
  usersData = signal<User[]>([]);
  private httpClient = inject(HttpClient);

  get users() : Observable<User[]>{
    return this.httpClient.get<User[]>("/users")
  }

  postNewUser(newUser:NewUser){
    const formData = new FormData();
  formData.append('file', newUser.userAvatar); 
  formData.append('userName', newUser.userName); 

  this.httpClient.post<User[]>('/user', formData).subscribe({
    next: response => this.usersData.set(response),
    error: err => console.error('Upload failed', err)
  });
  }
}
