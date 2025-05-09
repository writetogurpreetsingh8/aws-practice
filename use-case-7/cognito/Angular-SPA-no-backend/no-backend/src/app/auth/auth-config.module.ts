import { NgModule } from '@angular/core';
import { AuthModule } from 'angular-auth-oidc-client';


@NgModule({
    imports: [AuthModule.forRoot({
        config: {
              authority: 'https://cognito-idp.us-east-1.amazonaws.com/us-east-1_QMlDKndwa',
              redirectUrl: 'http://localhost:4200',
              clientId: '4aul8lfp411crcsnc70ff42kgh',
              scope: 'email openid phone', // 'openid profile offline_access ' + your scopes
              responseType: 'code'
          }
      })],
    exports: [AuthModule],
})
export class AuthConfigModule {}
