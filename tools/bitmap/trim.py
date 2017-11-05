#!/usr/local/bin/python3

import argparse
import cv2

# x, y, w, h

def trim_gacha_ios(input):
  return trim(input, 787, 164, 692, 1058)
  #  return trim(input, 714, 148, 626, 958)  # zoom-in mode

def trim_gacha(input):
  return trim(input, 912, 269, 802, 1230)

def trim_park10(input):
  return trim(input, 916, 200, 768, 1200)

def trim_park10_ios(input):
  return trim(input, 790, 100, 660, 980)
  # return trim(input, 714, 160, 600, 820)

def trim_landmark(input):
  return trim(input, 913, 269, 802, 1230)

def trim_label(input):
  return trim(input, 508, 567, 1263, 143)

def trim_label_ios(input):
  return trim(input, 438, 422, 1090, 120)

def trim_vs1(input):
  return trim(input, 142, 499, 470, 116)

def trim_vs2(input):
  return trim(input, 884, 499, 500, 116)

def trim_vs4(input):
  return trim(input, 1685, 499, 425, 116)

def trim_mark1(input):
  return trim(input, 567, 922, 64, 64)

def trim_mark2(input):
  return trim(input, 816, 922, 64, 64)

def trim_mark3(input):
  return trim(input, 1060, 922, 64, 64)

def trim_mark4(input):
  return trim(input, 1304, 922, 64, 64)

def trim_mark5(input):
  return trim(input, 1548, 922, 64, 64)

def trim_mark6(input):
  return trim(input, 1792, 922, 64, 64)

def trim_mark7(input):
  return trim(input, 2039, 922, 64, 64)

def trim_mark8(input):
  return trim(input, 2285, 922, 64, 64)

def trim_mark9(input):
  return trim(input, 570, 1060, 64, 64)

def trim_mark10(input):
  return trim(input, 816, 1060, 64, 64)

def trim_mark11(input):
  return trim(input, 1060, 1060, 64, 64)

def trim_mark12(input):
  return trim(input, 1304, 1060, 64, 64)

def trim_mark13(input):
  return trim(input, 1548, 1060, 64, 64)

def trim_mark14(input):
  return trim(input, 1792, 1060, 64, 64)

def trim_mark15(input):
  return trim(input, 2039, 1060, 64, 64)

def trim_mark16(input):
  return trim(input, 2285, 1060, 64, 64)

def trim_mark(input, mark):
  mark_pos = [
    # mark 1-4
    (567, 922),   (816, 922),   (1060, 922),  (1304, 922),
    # mark 5-8
    (1548, 922),  (1792, 922),  (2039, 922),  (2285, 922),
    # mark 9-12
    (570, 1060),  (816, 1060),  (1060, 1060), (1304, 1060),
    # mark 13-16
    (1548, 1060), (1792, 1060), (2039, 1060), (2285, 1060)]
  # mark is 1-origin.
  x = mark_pos[mark-1][0]
  y = mark_pos[mark-1][1]
  return trim(input, x, y, 64, 64)

def trim(input, x, y, w, h):
  return input[y:y+h, x:x+w]

def gray(input):
  gray = cv2.cvtColor(input, cv2.COLOR_BGR2GRAY)
  return cv2.cvtColor(gray, cv2.COLOR_GRAY2BGR)

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument('--input')
  parser.add_argument('--output')
  parser.add_argument('--gacha', action='store_true')
  parser.add_argument('--gacha_ios', action='store_true')
  parser.add_argument('--park10', action='store_true')
  parser.add_argument('--park10_ios', action='store_true')
  parser.add_argument('--landmark', action='store_true')
  parser.add_argument('--label', action='store_true')
  parser.add_argument('--label_ios', action='store_true')
  parser.add_argument('--vs1', action='store_true')
  parser.add_argument('--vs2', action='store_true')
  parser.add_argument('--vs4', action='store_true')
  parser.add_argument('--gray', action='store_true')
  parser.add_argument('--mark', type=int)
  parser.add_argument('--x', type=int)
  parser.add_argument('--y', type=int)
  parser.add_argument('--w', type=int)
  parser.add_argument('--h', type=int)
  args = parser.parse_args()

  input = cv2.imread(args.input, 1)
  if args.gacha:
    input = trim_gacha(input)
  elif args.gacha_ios:
    input = trim_gacha_ios(input)
  elif args.park10:
    input = trim_park10(input)
  elif args.park10_ios:
    input = trim_park10_ios(input)
  elif args.landmark:
    input = trim_landmark(input)
  elif args.label:
    input = trim_label(input)
  elif args.label_ios:
    input = trim_label_ios(input)
  elif args.vs1:
    input = trim_vs1(input)
  elif args.vs2:
    input = trim_vs2(input)
  elif args.vs4:
    input = trim_vs4(input)
  elif args.mark:
    input = trim_mark(input, args.mark)
  elif args.x and args.y and args.w and args.h:
    input = trim(input, args.x, args.y, args.w, args.h)


  if args.gray:
    input = gray(input)

  cv2.imwrite(args.output, input)

main()
