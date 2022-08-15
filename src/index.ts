import { registerPlugin } from '@capacitor/core';

import type { EmbeddedWebviewPlugin } from './definitions';

const EmbeddedWebView = registerPlugin<EmbeddedWebviewPlugin>(
  'EmbeddedWebView',
  {
    web: () => import('./web').then(m => new m.EmbeddedWebviewWeb()),
  },
);

export * from './definitions';
export { EmbeddedWebView };
