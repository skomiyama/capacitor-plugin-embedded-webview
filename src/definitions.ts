export interface WebviewConfiguration {
  width: number;
  height: number;
  enableCookie?: boolean;
}

export interface EmbeddedWebviewOptions {
  url: string;
  webviewConfiguration: WebviewConfiguration;
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
