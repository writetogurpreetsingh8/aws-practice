import { Component, output, signal, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
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
  selectedDoc!: File;
  cancelOutputEvent = output();
  createTaskEvent = output<NewTask>();
  useTimePicker = signal(false);
  isSchedule:boolean = false;

  @ViewChild('taskForm') taskForm!: NgForm;

  togglePicker(event: Event) {
     this.isSchedule = (event.target as HTMLInputElement).checked;
    this.useTimePicker.set(this.isSchedule);

    if (this.isSchedule) {
      this.enteredDate.set(''); 
    }

    setTimeout(() => {
      const dueDateControl = this.taskForm.controls['due-date'];
      if (dueDateControl) {
        dueDateControl.updateValueAndValidity();
      }
    });

  }

  onCancel(){
      this.cancelOutputEvent.emit();
  }

  OnSubmit(){
    this.createTaskEvent.emit({
      summary: this.enteredSummary(),
      title: this.enteredTitle(),
      dueDate: this.enteredDate(),
      taskDoc:this.selectedDoc,
      schedule: this.useTimePicker()
    });
    this.cancelOutputEvent.emit();
  }

  onDocSelected(event: Event){
    const input = event.target as HTMLInputElement;
  if (input.files?.length) {
      this.selectedDoc = input.files[0];
    }
  }
}
