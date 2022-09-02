## Example 


### Installation
```
$ ng add @skomiyama/embedded-webview-controller
```

### overlay components
```typescript
import { Component } from '@angular/core';
import { ModalController, ModalOptions } from '@ionic/angular';
import {
  WebViewAlert,
  WebViewActionSheet,
  WebViewModal,
  OverlayActionStyle,
  EmbeddedWebViewContentAlertOptions
} from '@skomiyama/embedded-webview-controller';

import { ModalContentPage } from '../modal-content/modal-content.page';


@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage {

  actionSheetSelected?: string;
  alertSelected?: string;

  constructor(  
    private modalCtrl: ModalController,
    private webViewAlertCtrl: WebViewAlert,
    private webViewActionSheetCtrl: WebViewActionSheet,
    private webViewModalCtrl: WebViewModal,
  ) {}

  async openActionSheet() {
    const alertOptions: EmbeddedWebViewContentAlertOptions = {
      title: 'Alert from embedded content',
      message: 'message',
      actions: [
        {
          title: 'Button 1',
          value: 'button1',
          role: OverlayActionStyle.Default
        },
        {
          title: 'Button 2',
          value: 'button2',
          role: OverlayActionStyle.Default
        },
        {
          title: 'Delete',
          value: 'delete',
          role: OverlayActionStyle.Destructive,
        },
        {
          title: 'Cancel',
          value: 'cancel',
          role: OverlayActionStyle.Cancel
        }
      ]
    };
    this.webViewActionSheetCtrl.present(alertOptions);
    this.actionSheetSelected = await this.webViewActionSheetCtrl.onDidDismiss();
  }

  async openAlert() {
    const alertOptions: EmbeddedWebViewContentAlertOptions = {
      title: 'Alert from embedded content',
      message: 'message',
      // until two elements.
      actions: [
        {
          title: 'Yes',
          value: 'yes',
          role: OverlayActionStyle.Default
        },
        {
          title: 'No',
          value: 'no',
          role: OverlayActionStyle.Destructive
        },
      ]
    };
    this.webViewAlertCtrl.present(alertOptions);
    this.alertSelected = await this.webViewAlertCtrl.onDidDismiss();
  }

  async openModal() {
    const options: ModalOptions = {
      component: ModalContentPage
    };
    this.webViewModalCtrl.modal = await this.modalCtrl.create(options);
    this.webViewModalCtrl.present();
  }
}
```

### routing

```typescript
export class AppComponent {
  constructor(
    private embeddedWebViewNavigationService: EmbeddedContentNavigationService,
    private router: Router,
  ) {
    const resolvePath = ($event: EmbeddedContentNavigationEvent) => this.router.navigate([$event.path]);
    this.embeddedWebViewNavigationService.navigationEventListener(resolvePath).subscribe();
  }
}
```

### keyboard scroll behaviour
```html
<!-- footer -->
<ion-footer>
  <ion-toolbar>
    <embedded-webview-footer-inner>
      <ion-input placeholder="input..."></ion-input>
    </embedded-webview-footer-inner>
  </ion-toolbar>
</ion-footer>
```

```typescript
// KeyboardScrollBehaviour is supported only iOS
  constructor(
    private embeddedContentConfiguration: EmbeddedContentConfiguration,
  ) {}

  setScrollBehaviour() {
    const behaviour = KeyboardScrollBehaviour.None;
    this.embeddedContentConfiguration.setKeyboardScrollBehaviour({ behaviour });
  }
```
| KeyboardScrollBehaviour  | Value Type |
| --- | --------- |
| ScrollUp  | when textfield was focused, scroll webview up |
| none | no scrolling when focused |

