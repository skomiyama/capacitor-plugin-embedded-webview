## Example 

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
    const modal = await this.modalCtrl.create(options);
    modal.present();
    this.webViewModalCtrl.present();
  }
}
```

```typescript
import { Component, OnInit } from '@angular/core';
import { ModalController } from '@ionic/angular';
import { WebViewModal } from '@skomiyama/embedded-webview-controller';

@Component({
  selector: 'app-modal-content',
  templateUrl: './modal-content.page.html',
  styleUrls: ['./modal-content.page.scss'],
})
export class ModalContentPage implements OnInit {
  constructor(
    private modalCtrl: ModalController,
    private webViewModalCtrl: WebViewModal,
  ) { }

  ngOnInit() {}

  async closeModal() {
    this.webViewModalCtrl.dismiss();
    this.modalCtrl.dismiss();
  }
}

```
