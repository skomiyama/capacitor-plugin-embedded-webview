
export interface EmbeddedWebViewUIControllerTheme {
  view: {
    background: string;
    text: string;
  };
  action: {
    cancel: {
      background: string;
      text: string;
    },
    destructive: {
      text: string;
    },
    default: {
      text: string;
    }
  }
}

export interface EmbeddedWebviewConfiguration {
  styles: {
    width: number;
    height: number;
    theme: EmbeddedWebViewUIControllerTheme
  };
  global?: { [key: string]: unknown };
  enableCookie?: boolean;
  css?: string;
} 

export interface EmbeddedWebviewOptions {
  url: string;
  configuration: EmbeddedWebviewConfiguration;
}

export interface EmbeddedWebviewPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  create(options: EmbeddedWebviewOptions): Promise<void>;
  hide(): Promise<EmbeddedWebviewVisibility>;
  show(): Promise<EmbeddedWebviewVisibility>;
  dismiss(): Promise<void>;
}


export interface EmbeddedWebviewVisibility {
  visibility: boolean;
}
