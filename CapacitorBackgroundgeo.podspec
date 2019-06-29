
  Pod::Spec.new do |s|
    s.name = 'CapacitorBackgroundgeo'
    s.version = '0.0.1'
    s.summary = 'Background geolocalization'
    s.license = 'MIT'
    s.homepage = 'https://github.com/felocru/capacitor-backgroundgeo.git'
    s.author = 'Felocru'
    s.source = { :git => 'https://github.com/felocru/capacitor-backgroundgeo.git', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end