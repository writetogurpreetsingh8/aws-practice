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
  isInvalidFile = signal(false);

  cancelDialogBoxEvent = output();

  newUserDataEvent = output<NewUser>();

  onCancel(){
      this.cancelDialogBoxEvent.emit();
      this.isInvalidFile.set(false);
  }

  OnSubmit(){
    this.newUserDataEvent.emit({
      userName : this.enteredUserName(),
      userAvatar: this.selectedFile
    })
    this.isInvalidFile.set(false);
    this.cancelDialogBoxEvent.emit();
  }

  onFileSelected(event: Event) {
  const input = event.target as HTMLInputElement;
  if (input.files?.length) {
    this.selectedFile = input.files[0];
    const extension = this.selectedFile.name.substring(this.selectedFile.name.lastIndexOf('.'),
    this.selectedFile.name.length);
    if( extension.toLowerCase() !== '.png' 
      && extension.toLowerCase() !== '.jpg'
     && extension.toLowerCase() != '.jpeg' ){
        this.isInvalidFile.set(true);
    }else{
      this.isInvalidFile.set(false);
      
    }
  }
}
}
