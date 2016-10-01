#include "mac_locale.h"
#include <CoreFoundation/CoreFoundation.h>

std::string GetPreferredLanguage() {
  CFArrayRef langs = CFLocaleCopyPreferredLanguages();
  CFStringRef lang = static_cast<CFStringRef>(CFArrayGetValueAtIndex(langs, 0));
  const char* lang_chars = CFStringGetCStringPtr(lang, kCFStringEncodingUTF8);
  std::string lang_str(lang_chars ? lang_chars : "");
  CFRelease(langs);
  return lang_str;
}
