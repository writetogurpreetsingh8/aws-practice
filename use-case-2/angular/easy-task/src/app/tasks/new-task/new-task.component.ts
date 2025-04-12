import { Component, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NewTask } from '../task/Task.model';

@Component({
  selector: 'app-new-task',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './new-task.component.html',
  styleUrl: './new-task.component.css'
})
export class NewTaskComponent {

  enteredTitle = signal('');
  enteredSummary = signal('');
  enteredDate = signal('');

  cancelOutputEvent = output();
  createTaskEvent = output<NewTask>();

  onCancel(){
      this.cancelOutputEvent.emit();
  }

  OnSubmit(){
    this.createTaskEvent.emit({
      summary: this.enteredSummary(),
      title: this.enteredTitle(),
      dueDate: this.enteredDate()
    });
    this.cancelOutputEvent.emit();
  }
}
