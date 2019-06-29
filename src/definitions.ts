declare module "@capacitor/core" {
  interface PluginRegistry {
    BackgroundGeo: BackgroundGeoPlugin;
  }
}

export interface BackgroundGeoPlugin {
  echo(options: { value: string }): Promise<{value: string}>;
}
