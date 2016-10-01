#import "mac_locale.h"
#import <Foundation/Foundation.h>

std::string GetPreferredLanguage() {
  NSString* prefLang = [NSLocale preferredLanguages][0];
  return std::string([prefLang UTF8String]);
}
