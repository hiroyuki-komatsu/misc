#!/usr/bin/python
"""
Script to resize an image to multiple resolutions for Android mipmap.

Example:
% ./multi_reso.py --input in.png --output out.png --res_dir app/src/main/res
"""

import argparse
import os
import subprocess
import sys

def parse_options():
  parser = argparse.ArgumentParser(
    description='Multi resolution images converter')
  parser.add_argument('--res_dir', dest='res_dir',
                      help='Path to res/ directory.')
  parser.add_argument('--output', dest='output', help='output file name.')
  parser.add_argument('--input', dest='input', help='input image file.')

  return parser.parse_args()

def convert(input, output, size, dir):
  command = ['convert', '-geometry', '%dx%d' % (size, size),
             input, os.path.join(dir, output)]
  print ' '.join(command)
  subprocess.check_call(command)

def mipmap(input, output, res_dir):
  mipmap_list = ((48, 'mdpi'), (72, 'hdpi'), (96, 'xhdpi'), (144, 'xxhdpi'))
  for size, name in mipmap_list:
    dir = os.path.join(res_dir, 'mipmap-' + name)
    convert(input, output, size, dir)

args = parse_options()
mipmap(args.input, args.output, args.res_dir)

