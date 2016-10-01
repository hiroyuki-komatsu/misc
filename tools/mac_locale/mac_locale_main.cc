#include "mac_locale.h"

#include <iostream>

int main(int argc, char* argv[]) {
  std::cout << GetPreferredLanguage() << std::endl;
  return 0;
}
