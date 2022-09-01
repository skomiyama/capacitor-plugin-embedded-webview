// eslint-disable-next-line @typescript-eslint/consistent-type-imports
import { AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, EventEmitter, HostBinding, HostListener, Input, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

// eslint-disable-next-line @typescript-eslint/consistent-type-imports
import { KeyboardHideEvent, KeyboardShowEvent, EmbeddedWebViewKeyboardController } from '../../utils/keyboard-controller.service';

@Component({
  selector: 'embedded-webview-footer-inner',
  templateUrl: './embedded-webview-footer.component.html',
  styleUrls: ['./embedded-webview-footer.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EmbeddedWebviewFooterComponent implements OnDestroy, OnInit {
  @Input()
  withSafeArea = false;

  @HostListener('document:focusin', ['$event'])
  onFocus($event: Event): void {
    console.log($event);
    const target = $event.target;
    if (target instanceof Node) {
      const element  = this.el.nativeElement as HTMLElement
      this.isTarget = element.contains(target);
    }
  }

  private _isTarget = false;
  get isTarget(): boolean {
    return this._isTarget;
  }
  set isTarget(isTarget: boolean) {
    this._isTarget = isTarget;
  }

  @HostBinding('style.--footer-inner-padding-bottom')
  private paddingBottom = '0px';
  private safeArea = '0px';

  private readonly onDestroy$: EventEmitter<void>;
  keyboardOffset$: BehaviorSubject<number>;

  constructor(
    private el: ElementRef,
    private keyboardController: EmbeddedWebViewKeyboardController,
  ) {
    this.onDestroy$ = new EventEmitter<void>();
    this.keyboardOffset$ = new BehaviorSubject<number>(0);
  }

  ngOnInit(): void {
    // window.addEventListener('window:focus', ($event: Event) => console.log('evet', $event));

    if(this.withSafeArea) {
      this.safeArea = 'env(safe-area-inset-bottom)';
    }
    this.paddingBottom = this.safeArea;

    this.keyboardOffset$.pipe(takeUntil(this.onDestroy$)).subscribe({
      next: v => {
        if (this.isTarget) {
          this.paddingBottom = `${v}px`;
        }
      }
    });
    this.keyboardController.keyboardWillShow$().pipe(takeUntil(this.onDestroy$)).subscribe({
      next: (info: KeyboardShowEvent) => {
        this.keyboardOffset$.next(info.keyboardHeight.next)
      }
    });
    this.keyboardController.keyboardWillHide$().pipe(takeUntil(this.onDestroy$)).subscribe({
      next: (info: KeyboardHideEvent) => {
        this.keyboardOffset$.next(info.keyboardHeight.next);
        this.isTarget = false;
        this.paddingBottom = this.safeArea;
      }
    });
  }

  ngOnDestroy(): void {
    this.onDestroy$.emit();
  }
}
