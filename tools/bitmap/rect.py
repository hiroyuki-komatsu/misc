#!/usr/local/bin/python3

import argparse
import cv2

def rect(input, x, y, w, h, r=255, g=255, b=255):
  # BGR
  return cv2.rectangle(input, (x,y), (x+w,y+h), (b,g,r), cv2.FILLED)

def rect_ranking_result_player1(input):
  """Player1 in a Ranking Buttle result."""
  input = rect(input, 450, 670, 660, 78, 5, 97, 162)
  return rect(input, 450, 670, 790, 33, 5, 97, 162)

def rect_ranking_result_player2(input):
  """Player2 in a Ranking Buttle result."""
  input = rect(input, 450, 810, 660, 78, 5, 97, 162)
  return rect(input, 450, 810, 790, 33, 5, 97, 162)

def rect_ranking_result_player3(input):
  """Player2 in a Ranking Buttle result."""
  input = rect(input, 450, 950, 660, 78, 5, 97, 162)
  return rect(input, 450, 950, 790, 33, 5, 97, 162)

def rect_ranking_result_player4(input):
  """Player2 in a Ranking Buttle result."""
  input = rect(input, 450, 1088, 660, 78, 5, 97, 162)
  return rect(input, 450, 1088, 790, 33, 5, 97, 162)

def rect_ranking_result_player5(input):
  """Player2 in a Ranking Buttle result."""
  input = rect(input, 450, 1228, 660, 78, 5, 97, 162)
  return rect(input, 450, 1228, 790, 33, 5, 97, 162)

def rect_ranking_result(input):
  input = rect_ranking_result_player1(input)
  input = rect_ranking_result_player2(input)
  input = rect_ranking_result_player3(input)
  input = rect_ranking_result_player4(input)
  input = rect_ranking_result_player5(input)
  return input

def rect_ranking_result_bottom_player1(input):
  """Player1 in a Ranking Buttle result."""
  return rect(input, 450, 680, 680, 78, 5, 97, 162)

def rect_ranking_result_bottom_player2(input):
  """Player2 in a Ranking Buttle result."""
  return rect(input, 450, 820, 680, 78, 5, 97, 162)

def rect_ranking_result_bottom_player3(input):
  """Player2 in a Ranking Buttle result."""
  return rect(input, 450, 960, 680, 78, 5, 97, 162)

def rect_ranking_result_bottom_player4(input):
  """Player2 in a Ranking Buttle result."""
  return rect(input, 450, 1098, 680, 78, 5, 97, 162)

def rect_ranking_result_bottom_player5(input):
  """Player2 in a Ranking Buttle result."""
  return rect(input, 450, 1238, 680, 78, 5, 97, 162)

def rect_ranking_result_bottom(input):
  input = rect_ranking_result_bottom_player1(input)
  input = rect_ranking_result_bottom_player2(input)
  input = rect_ranking_result_bottom_player3(input)
  input = rect_ranking_result_bottom_player4(input)
  input = rect_ranking_result_bottom_player5(input)
  return input

def rect_raid_player2(input):
  """Player2 in a Raid result."""
  return rect(input, 570, 740, 560, 100)

def rect_raid_player3(input):
  """Player3 in a Raid result."""
  return rect(input, 570, 945, 560, 100)

def rect_raid_result(input):
  input = rect_raid_player2(input)
  input = rect_raid_player3(input)
  return input

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument('--input')
  parser.add_argument('--output')
  parser.add_argument('--raid', action='store_true')
  parser.add_argument('--ranking', action='store_true')
  parser.add_argument('--ranking_bottom', action='store_true')
  parser.add_argument('--x', type=int)
  parser.add_argument('--y', type=int)
  parser.add_argument('--w', type=int)
  parser.add_argument('--h', type=int)
  parser.add_argument('--r', type=int, default=255)
  parser.add_argument('--g', type=int, default=255)
  parser.add_argument('--b', type=int, default=255)
  args = parser.parse_args()

  input = cv2.imread(args.input, 1)
  if args.raid:
     input = rect_raid_result(input)

  if args.ranking:
     input = rect_ranking_result(input)

  if args.ranking_bottom:
     input = rect_ranking_result_bottom(input)

  if args.x and args.y and args.w and args.h:
    input = rect(input, args.x, args.y, args.w, args.h, args.r, args.g, args.b)

  cv2.imwrite(args.output, input)

main()
