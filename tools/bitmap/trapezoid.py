#!/usr/local/bin/python3

import argparse
import cv2
import numpy as np

def edge(input):
  input = cv2.resize(input, (480, 640))
  gray = cv2.cvtColor(input, cv2.COLOR_BGR2GRAY)
  gray = cv2.GaussianBlur(gray, (11, 11), 0)
  img_dst = cv2.adaptiveThreshold(gray, 255, cv2.ADAPTIVE_THRESH_MEAN_C,
                                  cv2.THRESH_BINARY, 11, 2)
  inv = cv2.bitwise_not(img_dst)
  output = cv2.cvtColor(inv, cv2.COLOR_GRAY2BGR)

  lines = cv2.HoughLinesP(inv, 1, np.pi / 180, 70, 10, 50)
  points = []
  for line in lines:
    x1, y1, x2, y2 = line[0]
    points.append([x1, y1])
    points.append([x2, y2])
    print("(%d, %d) - (%d, %d)" % (x1, y1, x2, y2))
    cv2.line(output, (x1,y1), (x2,y2), (0,0,255), 2)

  hull = cv2.convexHull(np.array(points))
  cv2.drawContours(output, [hull], -1, (255, 0, 0), 7)
  return output

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument('--input')
  parser.add_argument('--output')
  args = parser.parse_args()

  input = cv2.imread(args.input, 1)
  input = edge(input)
  cv2.imwrite(args.output, input)

main()
