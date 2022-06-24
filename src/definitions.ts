export interface EmbeddedWebviewPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
