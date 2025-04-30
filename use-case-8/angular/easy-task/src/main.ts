import { bootstrapApplication } from '@angular/platform-browser';

import { AppComponent } from './app/app.component';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { baseInterceptor } from './app/baseInterceptor';

bootstrapApplication(AppComponent,{
    providers:[provideHttpClient(withInterceptors([baseInterceptor]))]
}).catch((err) => console.error(err));
