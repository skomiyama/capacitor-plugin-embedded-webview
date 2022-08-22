import { Injectable } from '@angular/core';
import { isPlatform } from '@ionic/core';

import { EmbeddedContentControllerModule } from '../content-controller.module';

declare const window: Window & {
  AndroidWebView: AndroidWebViewModalFunctions;
};

export enum ModalDisplay {
  Fullscreen, Contain
}

export interface ModalOption {
  display: ModalDisplay;
}

interface AndroidWebViewModalFunctions {
  showModal: (options: string) => unknown;
  dismissModal: () => unknown;
};

@Injectable({
  providedIn: EmbeddedContentControllerModule
})
export class WebViewModal {

  private _modal?: HTMLIonModalElement;
  get modal(): HTMLIonModalElement | undefined {
    return this._modal;
  }
  set modal(modal: HTMLIonModalElement | undefined) {
    modal?.onDidDismiss().then(async () => {
      await this.dismissNativeModal();
      this.modal = undefined;
    });
    this._modal = modal;
  }

  async present(options: ModalOption = { display: ModalDisplay.Fullscreen }): Promise<void> {
    if (isPlatform('ios')) {
      await this.presentModalOnIos(options);
    }
    if (isPlatform('android')) {
      this.presentModalOnAndroid(options);
    }
  }

  async dismiss(): Promise<void> {
    await this.modal?.dismiss();
  }

  private async setNativeModalLayout(options: ModalOption) {
    const eventName = 'set_native_modal_layout';
    await new Promise<void>((resolve) => {
      window.addEventListener(
        eventName,
        (() => {
          resolve()
        }) as EventListener,
        undefined
      );

      const event = new CustomEvent('send_message_to_webview', {
        detail: {
          function: 'showOverlay',
          options: JSON.stringify(options)
        }
      });
      window.dispatchEvent(event);
    });
    window.removeEventListener(eventName, () => null, false);
  }

  private async presentModalOnIos(options: ModalOption) {
    await this.setNativeModalLayout(options);
    await this.modal?.present();
    return;
  }
  private async presentModalOnAndroid(options: ModalOption) {
    const stringifiedOptions = JSON.stringify(options);
    window.AndroidWebView.showModal(stringifiedOptions);
    await this.setNativeModalLayout(options);
    await this.modal?.present();
  }

  private async dismissNativeModal() {
    if (isPlatform('ios')) {
      this.dismissOnIos()
    }
    if (isPlatform('android')){
      this.dismissOnAndroid()
    }
    this.modal = undefined;
  }
  private dismissOnIos(): void {
    const event = new CustomEvent('send_message_to_webview', { detail: { function: 'dismissOverlay'} });
    window.dispatchEvent(event);
  }
  private dismissOnAndroid(): void {
    window.AndroidWebView.dismissModal();
  }
}
