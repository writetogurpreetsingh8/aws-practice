import { NgModule } from '@angular/core';
import { AuthModule } from 'angular-auth-oidc-client';


@NgModule({
    imports: [AuthModule.forRoot({
        config: {
              authority: 'https://cognito-idp.us-east-1.amazonaws.com/us-east-1_lJsfW43JF',
              redirectUrl: 'http://localhost:4300/',
              postLogoutRedirectUri: 'http://localhost:4300/',
              clientId: '7du81oomrm3u0b5dmtc8u6c5dt',
              scope: 'email openid phone aws.cognito.signin.user.admin', // 'openid profile offline_access ' + your scopes
              responseType: 'code',
              silentRenew: true,
              useRefreshToken: true,
              renewTimeBeforeTokenExpiresInSeconds: 30,
              secureRoutes:['/data']
          }
      })],
    exports: [AuthModule],
})
export class AuthConfigModule {}
