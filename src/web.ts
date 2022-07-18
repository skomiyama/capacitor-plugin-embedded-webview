import { WebPlugin } from '@capacitor/core';

import type {
  EmbeddedWebviewPlugin,
  EmbeddedWebviewOptions,
  EmbeddedWebviewVisibility,
} from './definitions';

export class EmbeddedWebviewWeb
  extends WebPlugin
  implements EmbeddedWebviewPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async create(options: EmbeddedWebviewOptions): Promise<void> {
    console.log('CREATE WEBVIEW', options);
    return;
  }

  async show(): Promise<EmbeddedWebviewVisibility> {
    console.log('SHOW WEBVIEW');
    return { visibility: true };
  }

  async hide(): Promise<EmbeddedWebviewVisibility> {
    console.log('HIDE WEBVIEW');
    return { visibility: false }
  }

  async dismiss(): Promise<void> {
    console.log('DISMISS WEBVIEW');
    return;
  }
}
  