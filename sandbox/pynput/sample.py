# Sample code for pynput
# https://pypi.org/project/pynput/
#
# pip3 install pynput
# python3 sample.py

import pynput
import time

mouse = pynput.mouse.Controller()

mouse.position = (131, 44)

while True:
  print(mouse.position)
  time.sleep(1)
