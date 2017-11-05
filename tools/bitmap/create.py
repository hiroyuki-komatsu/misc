#!/usr/local/bin/python3

import argparse
import cv2
import numpy as np

def create(w, h, r=255, g=255, b=255):
  image = np.zeros((h, w, 3), np.uint8)
  # BGR
  image[:,:] = [b,g,r]
  return image

def create_card():
  return create(802, 1230, 0, 0, 0)

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument('--input')
  parser.add_argument('--output')
  parser.add_argument('--card', action='store_true')
  parser.add_argument('--w', type=int)
  parser.add_argument('--h', type=int)
  parser.add_argument('--r', type=int, default=255)
  parser.add_argument('--g', type=int, default=255)
  parser.add_argument('--b', type=int, default=255)
  args = parser.parse_args()

  image = None
  if args.w and args.h:
    image = create(args.w, args.h, args.r, args.g, args.b)
  elif args.card:
    image = create_card()

  if image is not None:
    cv2.imwrite(args.output, image)

main()
