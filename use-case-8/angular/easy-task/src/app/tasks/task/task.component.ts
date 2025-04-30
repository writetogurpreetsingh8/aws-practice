import { Component, input, output, } from '@angular/core';
import { Task } from './Task.model';
import { CardUiComponent } from '../../shared/card-ui/card-ui.component';
import { TasksService } from '../tasks.service';

@Component({
  selector: 'app-task',
  standalone: true,
  imports: [CardUiComponent],
  templateUrl: './task.component.html',
  styleUrl: './task.component.css'
})
export class TaskComponent {

  task = input.required<Task>();
  complete = output();

   onComplete(){
    this.complete.emit();
  }
}

