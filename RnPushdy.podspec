require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "RnPushdy"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/mobiletechvn/rn-pushdy.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm,cpp,swift}"
  # s.public_header_files = 'ios/Sources/RNPushdy.h'
   s.private_header_files = "ios/**/*.h"

  # === Swift support ===
  s.requires_arc = true
  s.swift_version = '5.0'
  s.pod_target_xcconfig = {
    # 'SWIFT_OBJC_BRIDGING_HEADER' => '$(PODS_TARGET_SRCROOT)/ios/Sources/RNPushdySDK-Bridging-Header.h',
    'SWIFT_INCLUDE_PATHS' => '$(SRCROOT)/ios',
    'DEFINES_MODULE' => 'YES'
  }

  # === React Native headers ===
  s.header_mappings_dir = 'ios'
  s.preserve_paths = 'package.json', 'ios'
  s.dependency 'React-Core'  # Required for RN modules
  s.dependency 'PushdySDK', '0.6.3'

 install_modules_dependencies(s)
end
