import { Component, computed, effect, input, signal } from '@angular/core';
import { TaskComponent } from './task/task.component';
import { NoTaskComponent } from './no-task/no-task.component';
import { NewTaskComponent } from './new-task/new-task.component';
import { NgIf } from '@angular/common';
import { TasksService } from './tasks.service';
import { NewTask} from './task/Task.model';

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [TaskComponent, NoTaskComponent, NewTaskComponent, NgIf],
  templateUrl: './tasks.component.html',
  styleUrl: './tasks.component.css'
})
export class TasksComponent {
 
  constructor(private taskService: TasksService){
    effect(() => {
      this.taskService.getUserTasks(this.userId())
      .subscribe({
          next: (result) => {
            this.taskService.userTask.set(result);
          },
          error: (error) => {
            console.log("error occured while fetch task ",error);
          },
          complete: () => {
            console.log("subscription got")
          }
      });
    });
  }
  
  
  isAddingTask = signal<boolean>(false);
  userId = input.required<string>();
  userName = input.required<string>();

  selectedUserTasks = computed(() => 
    {
      return  this.taskService.userTask();
    })

  onShowTask(){
    this.isAddingTask.set(true);
  }

  onCancelDialogBox(){
    this.isAddingTask.set(false);
  }

  onComplete(userId:string, taskId:string){
    this.taskService.completeTask(taskId, userId)
     .subscribe({
      next: (result) =>{
        this.taskService.userTask.set(result);
      },
       error:(error) => {
         console.log(error);
       }
     });
  }

  onCreateTask(newTask: NewTask){
    this.taskService.addTask(newTask,this.userId())
    .subscribe({
      next: (result) =>{
        this.taskService.userTask.set(result);
      },
       error:(error) => {
        alert(error.message);
         console.log(error);
       }
    })
  }

}
