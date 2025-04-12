import { Component, computed, OnInit, signal } from '@angular/core';
import { HeaderComponent } from './header/Header.component';
import { TasksComponent } from "./tasks/tasks.component";
import { UserComponent } from "./user/user.component";
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { UserService } from './user/service.service';
import { LoaderComponent } from "./loader-component/loader/loader.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HeaderComponent, UserComponent, TasksComponent, NgFor, NgIf, LoaderComponent, AsyncPipe],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit{
  
 constructor(private userService: UserService){}
  
  ngOnInit(): void {
    this.userService.users
     .subscribe({
      next: (result) => {
        this.userService.usersData = result;
      },
      error: (error) => {
        console.log("error occured while fetch users ",error);
      },
      complete: () => {
        console.log("subscription got")
      }
  });
  }

  selectedUserSignal = signal<string>('');
  
  onSelectUser(id:string){
    this.selectedUserSignal.set(id);
  }

  computeUser = computed(() => {
    return this.userService.usersData.find(user => user.userId === this.selectedUserSignal())!
  });

  get users(){
    return this.userService.usersData
   }
}
