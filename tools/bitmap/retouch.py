#!/usr/local/bin/python3

import argparse
import cv2

def gray(input):
  gray = cv2.cvtColor(input, cv2.COLOR_BGR2GRAY)
  return cv2.cvtColor(gray, cv2.COLOR_GRAY2BGR)

def negaposi(input):
  return 255 - input

def threth(input, thresh):
  gray = cv2.cvtColor(input, cv2.COLOR_BGR2GRAY)

  max_pixel = 255
  ret, img_dst = cv2.threshold(gray, thresh, max_pixel, cv2.THRESH_BINARY)

  return cv2.cvtColor(img_dst, cv2.COLOR_GRAY2BGR)

def transparent(input):
  alpha = cv2.cvtColor(input, cv2.COLOR_BGR2BGRA)
  h, w, d = alpha.shape
  for y in range(h):
    for x in range(w):
      b, g, r, a = alpha[y, x]
      if b == 255 and g == 255 and r == 255:
        alpha[y, x] = [b, g, r, 0]
  return alpha

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument('--input')
  parser.add_argument('--output')
  parser.add_argument('--gray', action='store_true')
  parser.add_argument('--negaposi', action='store_true')
  parser.add_argument('--threth', type=int)
  parser.add_argument('--transparent', action='store_true')
  args = parser.parse_args()

  input = cv2.imread(args.input, 1)
  if args.gray:
    input = gray(input)
  if args.negaposi:
    input = negaposi(input)
  if args.threth:
    input = threth(input, args.threth)
  if args.transparent:
    input = transparent(input)

  cv2.imwrite(args.output, input)

main()
