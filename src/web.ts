import { WebPlugin } from '@capacitor/core';

import type { EmbeddedWebviewPlugin } from './definitions';

export class EmbeddedWebviewWeb
  extends WebPlugin
  implements EmbeddedWebviewPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
