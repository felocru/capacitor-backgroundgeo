declare module "@capacitor/core" {
  interface PluginRegistry {
    BackgroundGeo: BackgroundGeoPlugin;
  }
}

export interface BackgroundGeoPlugin {
  echo(options: { value: string }): Promise<{value: string}>;
  startBackground(): void;
  stopBackground(force?:boolean): void;
}
export interface FusedLocationPosition {
    coords: {
        latitude: number;
        longitude: number;
        accuracy: number;
        altitude?: number;
        speed?: number;
        heading?: number;
    };
}
export interface FusedLocationOptions {
  enableHighAccuracy?: boolean;
  timeout?: number;
  maximumAge?: number;
  requireAltitude?: boolean;
}