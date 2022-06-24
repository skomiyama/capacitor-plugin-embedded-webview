import { registerPlugin } from '@capacitor/core';

import type { EmbeddedWebviewPlugin } from './definitions';

const EmbeddedWebview = registerPlugin<EmbeddedWebviewPlugin>(
  'EmbeddedWebview',
  {
    web: () => import('./web').then(m => new m.EmbeddedWebviewWeb()),
  },
);

export * from './definitions';
export { EmbeddedWebview };
