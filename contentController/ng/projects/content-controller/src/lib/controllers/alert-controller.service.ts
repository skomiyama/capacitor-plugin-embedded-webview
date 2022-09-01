import { Injectable } from '@angular/core';
import { isPlatform } from '@ionic/core';

import { EmbeddedWebViewControllersModule } from './controllers.module';

export interface EmbeddedWebViewUIControllerTheme {
  view: {
    background: string;
    text: string;
  };
  action: {
    cancel: {
      background: string;
      text: string;
    },
    destructive: {
      text: string;
    },
    default: {
      text: string;
    }
  }
}

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
export interface EmbeddedWebViewContentAlertOption {
  title: string;
  message: string;
  theme: EmbeddedWebViewUIControllerTheme;
  actions: EmbeddedWebViewContentAlertAction[];
}
export interface EmbeddedWebViewContentActionSheetOption {
  theme: EmbeddedWebViewUIControllerTheme;
  actions: EmbeddedWebViewContentAlertAction[];
}

class WebViewOverlayService<Option> {
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

  present(alertOption: Option) {
    if (isPlatform('ios')) {
      this.presentOnIOS(alertOption);
    }
    if (isPlatform('android')) {
      this.presentOnAndroid(alertOption);
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

  private presentOnIOS(alertOption: Option) {
    const options = JSON.stringify({
      ...alertOption,
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

  private presentOnAndroid(alertOption: Option) {
    const option = JSON.stringify({ ...alertOption,  name: this.name});
    window.AndroidWebView[this.functionName](option);
  }
}

@Injectable({
  providedIn: EmbeddedWebViewControllersModule
})
export class WebViewActionSheet extends WebViewOverlayService<EmbeddedWebViewContentActionSheetOption> {
  constructor() {
    super('action_sheet', OverlayStyle.ActionSheet,'showActionSheet');
  }
}

@Injectable({
  providedIn: EmbeddedWebViewControllersModule
})
export class WebViewAlert extends WebViewOverlayService<EmbeddedWebViewContentAlertOption> {
  constructor() {
    super('alert', OverlayStyle.Alert, 'showAlert');
  }
}
