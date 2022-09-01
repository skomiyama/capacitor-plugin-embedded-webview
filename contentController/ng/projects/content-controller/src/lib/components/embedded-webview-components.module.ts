import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { KeyboardControllerModule } from '../utils/keyboard-controller.module';

import { EmbeddedWebviewFooterComponent } from './embedded-webview-footer/embedded-webview-footer.component';

@NgModule({
  imports: [CommonModule, KeyboardControllerModule],
  declarations: [EmbeddedWebviewFooterComponent],
  exports: [EmbeddedWebviewFooterComponent],
})
export class EmbeddedWebViewComponentsModule {}
