import { inject, Injectable, signal } from '@angular/core';
import { NewTask, Response, Task } from './task/Task.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class TasksService {
  
  userTask = signal<Task[]>([]);
  private httpClient = inject(HttpClient);

  constructor() {}

  getUserTasks(userId: string) {
    return this.httpClient.get<Response>("/userId/"+userId);
  }

  completeTask(taskId:string, userId:string){
    return this.httpClient.delete<Response>("/task",{params:{
      'userId':userId,
      'taskId':taskId
    }});
  }

  addTask(newTask:NewTask, userId:string){
    const task : Task = {
      dueDate:newTask.dueDate,
      taskId:new Date().getTime().toString(),
      summary:newTask.summary,
      title:newTask.title,
      userId:userId,
      schedule:newTask.schedule
    };
    console.log('taks is ',task);
    const formData = new FormData();
    formData.append('file', newTask.taskDoc);
    formData.append('userId', userId);  
    formData.append('task',new Blob([JSON.stringify(task)], { type: 'application/json' })) 
    return this.httpClient.post<Response>("/task", formData);
  }
}