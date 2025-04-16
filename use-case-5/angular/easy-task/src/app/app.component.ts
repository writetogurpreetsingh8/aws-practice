import { Component, computed, OnInit, signal } from '@angular/core';
import { HeaderComponent } from './header/Header.component';
import { TasksComponent } from "./tasks/tasks.component";
import { UserComponent } from "./user/user.component";
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { UserService } from './user/service.service';
import { LoaderComponent } from "./loader-component/loader/loader.component";
import { NewUserComponent } from "./user/new-user/new-user.component";
import { NewUser } from './user/new-user/new-user.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NewUserComponent, HeaderComponent, UserComponent, TasksComponent, NgFor, NgIf, LoaderComponent, AsyncPipe, NewUserComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit{
  
  selectedUserSignal = signal<string>('');
  isNewUserAdded = signal(false);
  

 constructor(private userService: UserService){}
  
  ngOnInit(): void {
    this.userService.users
     .subscribe({
      next: (result) => {
        this.userService.usersData.set(result);
      },
      error: (error) => {
        console.log("error occured while fetch users ",error);
      },
      complete: () => {
        console.log("subscription got")
      }
  });
  }

  onSelectUser(id:string){
    this.selectedUserSignal.set(id);
  }
  
  computeUser = computed(() => {
    return this.userService.usersData().find(user => user.userId === this.selectedUserSignal())!
  });


  get users(){
    return this.userService.usersData
   }

   onAddUser(){
      this.isNewUserAdded.set(true);
   }

   onCancel(){
    this.isNewUserAdded.set(false);
   }

   onAddNewUser(newUser: NewUser){
      this.userService.postNewUser(newUser);
   }
}
