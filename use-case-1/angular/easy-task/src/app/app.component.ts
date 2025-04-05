import { Component, computed, signal } from '@angular/core';
import { HeaderComponent } from './header/Header.component';
import { TasksComponent } from "./tasks/tasks.component";
import { UserComponent } from "./user/user.component";
import { NgFor, NgIf } from '@angular/common';
import { UserService } from './user/service.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HeaderComponent, UserComponent, TasksComponent, NgFor, NgIf],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  
 constructor(private userService: UserService){}

  selectedUserSignal = signal<string>('');
  
  onSelectUser(id:string){
    this.selectedUserSignal.set(id);
  }

  computeUser = computed(() => { 
    return this.userService.user.find(user => user.id === this.selectedUserSignal())!
  });

  get users(){
     return this.userService.user;
   }
}
