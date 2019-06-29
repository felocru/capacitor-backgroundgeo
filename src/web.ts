import { WebPlugin } from '@capacitor/core';
import { BackgroundGeoPlugin } from './definitions';

export class BackgroundGeoWeb extends WebPlugin implements BackgroundGeoPlugin {
  constructor() {
    super({
      name: 'BackgroundGeo',
      platforms: ['web']
    });
  }

  async echo(options: { value: string }): Promise<{value: string}> {
    console.log('ECHO', options);
    return options;
  }
}

const BackgroundGeo = new BackgroundGeoWeb();

export { BackgroundGeo };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(BackgroundGeo);
