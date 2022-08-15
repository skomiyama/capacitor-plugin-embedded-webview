import { Injectable } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/consistent-type-imports
import { isPlatform } from '@ionic/core';

import { EmbeddedContentControllerModule } from '../lib/content-controller.module';

interface AndroidWebViewAlertFunctions {
  showAlert: (data: unknown) => unknown;
  showActionSheet: (data: string) => unknown;
};
declare const window: Window & {
  AndroidWebView: AndroidWebViewAlertFunctions;
};

export enum OverlayStyle {
  ActionSheet,
  Alert,
}
export enum OverlayActionStyle {
  Default,
  Cancel,
  Destructive
}
export interface EmbeddedWebViewContentAlertAction {
  title: string;
  value: string;
  role: OverlayActionStyle;
}
export interface EmbeddedWebViewContentAlertOptions {
  title: string;
  message: string;
  actions: EmbeddedWebViewContentAlertAction[];
}

class WebViewOverlayService {
  private readonly name: string;
  private readonly style: OverlayStyle;
  private readonly functionName: keyof AndroidWebViewAlertFunctions;

  constructor(
    name: string,
    style: OverlayStyle,
    functionName: keyof AndroidWebViewAlertFunctions,
  ) {
    this.name = name;
    this.style = style;
    this.functionName = functionName;
  }

  present(alertOptions: EmbeddedWebViewContentAlertOptions) {
    if (isPlatform('ios')) {
      this.presentOnIOS(alertOptions);
    }
    if (isPlatform('android')) {
      this.presentOnAndroid(alertOptions);
    }
  };

  async onDidDismiss(): Promise<string> {
    const eventName = `on_did_dismiss_${this.name}`;
    const v = await new Promise<string>((resolve) => {
      window.addEventListener(
        eventName,
        (($event: CustomEvent<string>) => {
          resolve($event.detail);
        }) as EventListener,
        undefined
      );
    });
    window.removeEventListener(eventName, () => null, false);
    return v;
  };

  private presentOnIOS(alertOptions: EmbeddedWebViewContentAlertOptions) {
    const options = JSON.stringify({
      ...alertOptions,
      style: this.style,
      name: this.name,
    });
    const event = new CustomEvent('send_message_to_webview', {
      detail: {
        function: this.functionName,
        options
      }
    });
    window.dispatchEvent(event);
  }

  private presentOnAndroid(alertOptions: EmbeddedWebViewContentAlertOptions) {
    const options = JSON.stringify({ ...alertOptions,  name: this.name});
    window.AndroidWebView[this.functionName](options);
  }
}

@Injectable({
  providedIn: EmbeddedContentControllerModule
})
export class WebViewActionSheet extends WebViewOverlayService {
  constructor() {
    super('action_sheet', OverlayStyle.ActionSheet,'showActionSheet');
  }
}

@Injectable({
  providedIn: EmbeddedContentControllerModule
})
export class WebViewAlert extends WebViewOverlayService {
  constructor() {
    super('alert', OverlayStyle.Alert, 'showAlert');
  }
}
