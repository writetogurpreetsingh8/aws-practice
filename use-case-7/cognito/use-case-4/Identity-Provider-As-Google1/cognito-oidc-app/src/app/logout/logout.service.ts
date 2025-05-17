import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
//this service is useful to automatially logged-out user in case user has opened
// multiple tabs and if user logged-out from any of tab this serivce will automatically
// logged out from all the tabs with the help of BroadcastChannel
export class LogoutService {
  private logoutChannel = new BroadcastChannel('logout-channel');

  constructor() {
    this.listenForGlobalLogout();
  }

  listenForGlobalLogout() {
    this.logoutChannel.onmessage = (message) => {
        console.log('app-2 onmessage message called.......');
      if (message.data === 'logout') {
        console.log('Received global logout signal');
        this.performLogout();
      }
    };
  }

  broadcastLogout() {
    console.log('app-2 broadcastLogout() called.......');
    this.logoutChannel.postMessage('logout');
    this.performLogout(); // Local logout too
  }

  performLogout() {
    console.log('app-2 Logging out from this app… performLogout()......');
    sessionStorage.clear();
    localStorage.clear();

    // Trigger Google Logout via hidden iframe
    const iframe = document.createElement('iframe');
    iframe.style.display = 'none';
    iframe.src = 'https://accounts.google.com/logout';

    iframe.onload = () => {
        // Once Google logout iframe loads, now logout from Cognito
        window.location.href = 'https://us-east-1ljsfw43jf.auth.us-east-1.amazoncognito.com/logout?client_id=7du81oomrm3u0b5dmtc8u6c5dt&logout_uri=http://localhost:4300/logged-out/';
      };
    
    document.body.appendChild(iframe);
  }
}
