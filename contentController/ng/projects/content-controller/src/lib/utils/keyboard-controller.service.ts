import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { KeyboardControllerModule } from './keyboard-controller.module';

export interface KeyboardShowEvent {
  keyboardHeight: {
    current: number;
    next: number;
  };
  type: 'show';
}
export interface KeyboardHideEvent {
  keyboardHeight: {
    current: number;
    next: number;
  };
  type: 'hide';
}

@Injectable({
  providedIn: KeyboardControllerModule
})
export class EmbeddedWebViewKeyboardController {

  keyboardWillShow$(): Observable<KeyboardShowEvent> {
    return new Observable(observer =>
      window.addEventListener(
        'embedded_webview_keyboard_will_show',
        (($event: CustomEvent<KeyboardShowEvent>) => {
          observer.next($event.detail)
        }) as EventListener,
        undefined
      )
    );
  }

  keyboardWillHide$(): Observable<KeyboardHideEvent> {
    console.log('keyboard_will_hide');
    return new Observable(observer =>
      window.addEventListener(
        'embedded_webview_keyboard_will_hide',
        (($event: CustomEvent<KeyboardHideEvent>) => observer.next($event.detail)) as EventListener,
        undefined
      )
    )
  }
}
