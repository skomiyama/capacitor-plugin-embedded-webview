import { Injectable } from '@angular/core';
import { isPlatform } from '@ionic/core';
import { Observable } from 'rxjs';

import { EmbeddedContentControllerModule } from '../content-controller.module';

export interface EmbeddedContentNavigationEvent {
  path: string;
  options: {
    animation: boolean;
  }
}

interface AndroidWebViewAlertFunctions {
  completedEvent: () => unknown;
};
declare const window: Window & {
  AndroidWebView: AndroidWebViewAlertFunctions;
};

@Injectable({
  providedIn: EmbeddedContentControllerModule
})
export class EmbeddedContentNavigationService {

  async notifyEvent(functionName: string, options?: unknown): Promise<void> {
    new Promise<void>((resolve) => {
      const event = new CustomEvent('send_message_to_webview', {
        detail: {
          function: functionName,
          options
        }
      });
      window.dispatchEvent(event);
      resolve();
    });
  }

  navigationEventListener<T>(resolver: (options: EmbeddedContentNavigationEvent) => Promise<T>): Observable<EmbeddedContentNavigationEvent> {
    return new Observable(subscriber => {
      window.addEventListener(
        'embedded_content_navigation',
        (($event: CustomEvent<EmbeddedContentNavigationEvent>) => {
          resolver($event.detail).then(() => {
            if (isPlatform('ios')) {
              this.notifyEvent('navigationEnd');
            }
            if (isPlatform('android')) {
              window.AndroidWebView.completedEvent()
            }
            subscriber.next($event.detail)
          });
        }) as EventListener,
        undefined,
      )
    })
  }
}

