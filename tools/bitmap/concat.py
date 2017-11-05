#!/usr/local/bin/python3

import argparse
import cv2

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument('--input', nargs='*')
  parser.add_argument('--output')
  parser.add_argument('--label')
  parser.add_argument('--rule')
  parser.add_argument('--vertical', action='store_true')
  args = parser.parse_args()

  inputs = []
  for input in args.input:
    inputs.append(cv2.imread(input, 1))

  if args.vertical:
    base = cv2.vconcat(inputs)
  else:
    base = cv2.hconcat(inputs)

  if args.label:
    label = cv2.imread(args.label, 1)
    lh, lw, ld = label.shape
    bh, bw, bd = base.shape
    base[bh-lh:bh, 0:lw] = label

  if args.rule:
    rule = cv2.imread(args.rule, 1)
    rh, rw, rd = rule.shape
    bh, bw, bd = base.shape
    base[bh-rh:bh, bw-rw:bw] = rule
  # elif args.label:
  #   output = trim_label(input)
  # elif args.vs2:
  #   output = trim_vs2(input)
  # else:
  #   output = trim(input, args.x, args.y, args.w, args.h)
  #
  # if args.gray:
  #   output = gray(output)

  cv2.imwrite(args.output, base)

main()
