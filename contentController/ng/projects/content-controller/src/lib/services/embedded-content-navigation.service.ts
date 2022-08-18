import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { EmbeddedContentModule } from '../content-controller.module';

export interface EmbeddedContentNavigationEvent {
  path: string;
}

@Injectable({
  providedIn: EmbeddedContentModule
})
export class EmbeddedWebViewRouterService {
  navigationEventListener(): Observable<string> {
    return new Observable(subscriber => {
      window.addEventListener(
        'embedded_content_navigation',
        (($event: CustomEvent<EmbeddedContentNavigationEvent>) => {
          const path = $event.detail.path;
          subscriber.next(path)
        }) as EventListener,
        undefined,
      )
    });
  }
}

