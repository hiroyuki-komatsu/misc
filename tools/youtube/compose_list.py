#!/usr/bin/env python3
# coding: utf-8

import argparse
import os.path
import subprocess
import sys

def normalize_weapon_name(weapon):
  normalization_dict = {
    "ガロン52": ".52ガロン",
    "ガロン96": ".96ガロン",
    "ガロンデコ52": ".52ガロンデコ",
    "ガロンデコ96": ".96ガロンデコ",
  }
  return normalization_dict.get(weapon, weapon)

def get_summary(ikalog_dir, statink):
  command = os.path.join(ikalog_dir, 'tools/print_data.py')
  tsv = subprocess.check_output([command,
                                 '--tsv',
                                 '--statink=%s' % statink])
  return tsv.decode('utf-8').rstrip('\n').split('\t')

def process_file(video_file, ikalog_dir, id='', statink=''):
  # filename looks like splatoon_recording-2015-12-28-00-30-0.mp4.txt
  video = os.path.basename(video_file)
  type = ''
  date = video[19:29]
  index = ''

  type_dict = {'.52ガロン': 'スプラトゥーン',
               'ガロン52': 'スプラトゥーン',
               'スプラチャージャー': 'スプラトゥーン チャージャー'}

  data = [video, id, statink, type, date, index]

  items = get_summary(ikalog_dir, video_file + '.statink')
  (input_file, end_at, lobby, rule, stage, weapon, result, kill, death,
   rank_before, rank_after) = items
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
  parser.add_argument("--ikalog_dir")
  parser.add_argument("--output_sheet")
  args = parser.parse_args()

  ikalog_dir = args.ikalog_dir

  with open(args.output_sheet, 'w') as output:
    for item in open(args.playlist):
      video_file, video_id, title, *items = item.rstrip().split('\t')
      video_path = os.path.join(args.video_dir, video_file)
      statink = ''
      if len(items) > 0:
        statink = items[0]
      output.write('\t'.join(process_file(video_path, ikalog_dir, video_id, statink)) +
                   '\n')

if __name__ == "__main__":
  main()
