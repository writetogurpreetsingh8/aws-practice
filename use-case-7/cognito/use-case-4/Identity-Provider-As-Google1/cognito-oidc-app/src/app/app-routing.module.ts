import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent }           from './home/home.component';
import { LogoutComponent } from './logout/logout.component';



const routes: Routes = [
  {
    path: 'logged-out',
    component: LogoutComponent,
  },
  {
    path: '',
    component: HomeComponent,
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
