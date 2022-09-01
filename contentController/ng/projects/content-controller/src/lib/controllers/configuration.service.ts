import { Injectable } from '@angular/core';
import { isPlatform } from '@ionic/core';

import { EmbeddedWebViewControllersModule } from './controllers.module';

export enum KeyboardScrollBehaviour {
  ScrollUp,
  None
}

@Injectable({
  providedIn: EmbeddedWebViewControllersModule,
})
export class EmbeddedContentConfiguration {
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
