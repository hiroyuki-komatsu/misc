cc_binary(
  name = "mac_locale_main",
  srcs = ["mac_locale_main.cc"],
  deps = ["mac_locale"],
)

cc_library(
  name = "mac_locale",
  hdrs = ["mac_locale.h"],
  srcs = ["mac_locale.cc"],
  linkopts = [
    "-framework CoreFoundation",
  ],
)

# This objc_library doesn't work on macOS, because objc_library is supported
# only on iOS.
#
# objc_library(
#   name = "mac_locale",
#   srcs = ["mac_locale.mm"],
# )
