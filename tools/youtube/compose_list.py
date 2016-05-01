#!/usr/bin/python
# coding: utf-8

import argparse
import os.path
import sys

def normalize_weapon_name(weapon):
  normalization_dict = {
    "ガロン52": ".52ガロン",
    "ガロン96": ".96ガロン",
    "ガロンデコ52": ".52ガロンデコ",
    "ガロンデコ96": ".96ガロンデコ",
  }
  return normalization_dict.get(weapon, weapon)

def process_file(video_file, id='', statink=''):
  # filename looks like splatoon_recording-2015-12-28-00-30-0.mp4.txt
  video = os.path.basename(video_file)
  type = ''
  date = video[19:29]
  index = ''

  type_dict = {'.52ガロン': 'スプラトゥーン',
               'ガロン52': 'スプラトゥーン',
               'スプラチャージャー': 'スプラトゥーン チャージャー'}

  data = [video, id, statink, type, date, index]
  with open(video_file + '.txt') as file:
    # line looks like:
    # "IKA ガチエリア	モンガラキャンプ場	lose	6	7	a+62  a+52	ガロン52"
    line = file.readline()
    if line.startswith('IKA'):
      items = line.strip('\n').split('\t')[1:]
      rule, stage, result, kill, death, rank_before, rank_after, weapon = items
      data[3] = type_dict.get(weapon, '')  # update type
      weapon = normalize_weapon_name(weapon)
      rule = rule.replace('ガチホコバトル', 'ガチホコ')
      data += [rule, stage, result.capitalize(), kill, death,
               rank_before.upper(), rank_after.upper(), weapon]
  return data

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument("--playlist")
  parser.add_argument("--video_dir")
  parser.add_argument("--output_sheet")
  args = parser.parse_args()

  with open(args.output_sheet, 'w') as output:
    for item in open(args.playlist):
      video_file, video_id, title, statink = item.rstrip().split('\t')
      video_path = os.path.join(args.video_dir, video_file)
      output.write('\t'.join(process_file(video_path, video_id, statink)) +
                   '\n')

if __name__ == "__main__":
  main()
