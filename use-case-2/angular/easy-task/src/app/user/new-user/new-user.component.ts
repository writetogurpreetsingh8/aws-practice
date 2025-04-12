import { Component, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NewUser } from './new-user.model';

@Component({
  selector: 'app-new-user',
  standalone: true,
   imports: [FormsModule],
  templateUrl: './new-user.component.html',
  styleUrl: './new-user.component.css'
})
export class NewUserComponent {
  
  selectedFile!: File;
  enteredUserName = signal('');

  cancelDialogBoxEvent = output();

  newUserDataEvent = output<NewUser>();

  onCancel(){
      this.cancelDialogBoxEvent.emit();
  }

  OnSubmit(){
    this.newUserDataEvent.emit({
      userName : this.enteredUserName(),
      userAvatar: this.selectedFile
    })
    this.cancelDialogBoxEvent.emit();
  }

  onFileSelected(event: Event) {
  const input = event.target as HTMLInputElement;
  if (input.files?.length) {
    this.selectedFile = input.files[0];
  }
}
}
