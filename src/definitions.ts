declare module "@capacitor/core" {
  interface PluginRegistry {
    BackgroundGeoPlugin: BackgroundGeoPlugin;
  }
}

export interface BackgroundGeoPlugin {
  echo(options: { value: string }): Promise<{value: string}>;
}
