import { inject, Injectable, signal } from '@angular/core';
import { NewTask, Task } from './task/Task.model';
import { dummyTasks } from './dummy-tasks';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class TasksService {

  private tasks = signal<Task[]>(dummyTasks);
  private httpClient = inject(HttpClient);

  constructor() {}

  getUserTasks(userId: string) {
    return this.httpClient.get<Task[]>("/userId/"+userId);
  }

  completeTask(taskId:string, userId:string){
    return this.httpClient.delete<Task[]>("/task",{params:{
      'userId':userId,
      'taskId':taskId
    }});
  }

  addTask(newTask:NewTask, userId:string){
    const task : Task = {
      dueDate:newTask.dueDate,
      id:new Date().getTime().toString(),
      summary:newTask.summary,
      title:newTask.title,
      userId:userId
    };
    return this.httpClient.post<Task[]>("/task", task, {params:{
      'userId': userId
    }});
  }
}
