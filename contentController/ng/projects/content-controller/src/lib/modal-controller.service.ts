import { Injectable } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/consistent-type-imports
import { isPlatform } from '@ionic/core';

import { EmbeddedContentControllerModule } from './content-controller.module';

declare const window: Window & {
  AndroidWebView: AndroidWebViewModalFunctions;
};

interface AndroidWebViewModalFunctions {
  showModal: () => unknown;
  dismissModal: () => unknown;
};

@Injectable({
  providedIn: EmbeddedContentControllerModule
})
export class WebViewModal {
  present(): void {
    if (isPlatform('ios')) {
      this.openModalOnIos();
    }
    if (isPlatform('android')) {
      this.openModalOnAndroid();
    }
  }

  dismiss(): void {
    if (isPlatform('ios')) {
      this.dismissOnIos();
    }
    if (isPlatform('android')) {
      this.dismissOnAndroid();
    }
  }

  private openModalOnIos() {
    const event = new CustomEvent('send_message_to_webview', { detail: { function: 'showOverlay' }});
    window.dispatchEvent(event);
  }
  private openModalOnAndroid() {
    window.AndroidWebView.showModal();
  }
  private dismissOnIos() {
    const event = new CustomEvent('send_message_to_webview', { detail: { function: 'dismissOverlay'} });
    window.dispatchEvent(event);
  }
  private dismissOnAndroid() {
    window.AndroidWebView.dismissModal();
  }
}
