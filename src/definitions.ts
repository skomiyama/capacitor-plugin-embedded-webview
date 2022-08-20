export interface EmbeddedWebviewConfiguration {
  styles: {
    width: number;
    height: number;
  };
  global?: { [key: string]: unknown };
  enableCookie?: boolean;
  css?: string;
} 

export interface EmbeddedWebviewOptions {
  url: string;
  path?: string;
  configuration: EmbeddedWebviewConfiguration;
}

export interface EmbeddedWebviewPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  create(options: EmbeddedWebviewOptions): Promise<void>;
  hide(): Promise<EmbeddedWebviewVisibility>;
  show(): Promise<EmbeddedWebviewVisibility>;
  pushTo(options: { path: string }): Promise<void>;
  dismiss(): Promise<void>;
}


export interface EmbeddedWebviewVisibility {
  visibility: boolean;
}
