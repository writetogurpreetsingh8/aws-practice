import { Component, EventEmitter, Input, Output, computed, input, output } from '@angular/core';
import { User } from './user.model';
import { CardUiComponent } from '../shared/card-ui/card-ui.component';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CardUiComponent],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent {


  @Input({required:true}) user!:User;
  selected = input.required<boolean>();

  @Output() select = new EventEmitter<string>();
  
  onSelectImage(){
    this.select.emit(this.user.userId);
  }

  get imagePath(){
    return "assets/users/"+this.user.userAvatar;
  }
}