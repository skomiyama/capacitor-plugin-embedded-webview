import { Injectable } from '@angular/core';
import { isPlatform } from '@ionic/core';
import { Observable } from 'rxjs';

import { EmbeddedWebViewControllersModule } from './controllers.module';

export enum KeyboardScrollBehaviour {
  ScrollUp,
  None
}

@Injectable({
  providedIn: EmbeddedWebViewControllersModule,
})
export class EmbeddedContentConfiguration {
  ready$(): Observable<void> {
    return new Observable((observer) => {
      window.addEventListener(
        'initialized_global_variables',
        () => {
          console.log('initialized global variables')
          observer.next();
          observer.complete();
        },
        undefined
      )
    })
  }

  /*
    Supported only iOS
  */
  setKeyboardScrollBehaviour({ behaviour }: { behaviour: KeyboardScrollBehaviour }): void {
    if (isPlatform('ios')) {
      this.setKeyboardScrollBehaviourForIOS(behaviour);
    }
    if (isPlatform('android')) {
      return;
    }
  }

  private setKeyboardScrollBehaviourForIOS(behaviour: KeyboardScrollBehaviour) {
    const event = new CustomEvent('send_message_to_webview', {
      detail: {
        function: 'setContentConfiguration',
        options: JSON.stringify({ behaviour })
      }
    });
    window.dispatchEvent(event);
  }
}
