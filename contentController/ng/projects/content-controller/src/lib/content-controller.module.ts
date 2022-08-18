import { ModuleWithProviders, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

interface ListenerOptions {
  navigatingFunction: (path: string) => unknown;
}

@NgModule({
  imports: [RouterModule]
})
export class EmbeddedContentModule {
  listen(): ModuleWithProviders<EmbeddedContentModule> {
    return {
      ngModule: EmbeddedContentModule,
      providers: []
    };
  }
}
