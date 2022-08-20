import { EmbeddedWebView, EmbeddedWebviewConfiguration,EmbeddedWebviewOptions } from '@skomiyama/embedded-webview';

export class EmbeddedTabService {
    public initialized = false;
    private options: EmbeddedWebviewOptions;
    constructor(width: number, height: number) {
        const configuration: EmbeddedWebviewConfiguration = {
            styles: {
              width,
              height
            },
            global: {
              parent: {
                pet: 'dog',
                children: ['boy1', 'girl1']   
              }
            }
        }
        this.options = {
            url: 'http://localhost:3000',
            configuration
        }
    }

    public async create() {
        await EmbeddedWebView.create(this.options)
        this.initialized = true;
        window.addEventListener('show_embedded_view', console.log);
    }
}
