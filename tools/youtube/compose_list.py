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

def process_file(video_file, ikalog_dir, video_id='', statink='',
                 prev_date='', prev_index=0):
  # filename looks like splatoon_recording-2015-12-28-00-30-0.mp4.txt
  video = os.path.basename(video_file)
  type = ''
  date = video[19:29]
  index = 1
  if date == prev_date:
    index = prev_index + 1

  items = get_summary(ikalog_dir, video_file + '.statink')
  (end_at, lobby, rule, stage, weapon, result, kill, death,
   rank_before, rank_after) = items

  type_dict = {'.52ガロン': 'スプラトゥーン',
               'ガロン52': 'スプラトゥーン',
               'スプラチャージャー': 'スプラトゥーン チャージャー'}
  lobby_to_type = {'タッグマッチ': 'タッグマッチ',
                   'プライベートマッチ': 'プライベートマッチ'}

  # type = type_dict.get(weapon, '')  # update type
  type = lobby_to_type.get(lobby, 'スプラトゥーン')

  data = [video, video_id, statink, type, date, str(index)]

  weapon = normalize_weapon_name(weapon)
  rule = rule.replace('ガチホコバトル', 'ガチホコ')
  comment = ''
  valid = '0'
  if type and rule and stage:
    valid = '1'

  rank_before = rank_before.upper()
  rank_after = rank_after.upper()
  rank_trajectory = ''
  if rank_before or rank_after:
    rank_trajectory = '→'.join([rank_before, rank_after])
  kill_text = ''
  if kill:
    kill_text = kill + 'k'
  death_text = ''
  if death:
    death_text = death + 'd'

  result = result.capitalize()

  title = '%s %s#%02d %s %s %s %s %s%s %s' % (
    type, date, index, weapon, rule, stage, result, kill_text, death_text, rank_trajectory)

  data += [rule, stage, result, kill, death, rank_before, rank_after, weapon, comment, valid, title]

  if type is not 'スプラトゥーン':
    data.append('unlisted')
  return data


def main():
  parser = argparse.ArgumentParser()
  parser.add_argument("--playlist")
  parser.add_argument("--video_dir")
  parser.add_argument("--ikalog_dir")
  parser.add_argument("--output_sheet")
  parser.add_argument("--prev_data")
  parser.add_argument("--prev_date", help='e.g. 2015-05-28#01')
  args = parser.parse_args()

  ikalog_dir = args.ikalog_dir
  if args.prev_data:
    with open(args.prev_data) as prev_data:
      prev_info = prev_data.read().split(' ')[1]
  elif args.prev_date:
    prev_info = args.prev_date
  else:
    prev_info = '2015-05-28#01'
  prev_date, prev_index = prev_info.split('#')
  prev_index = int(prev_index)

  with open(args.output_sheet, 'w') as output:
    for item in open(args.playlist):
      video_file, video_id, title, *items = item.rstrip().split('\t')
      video_path = os.path.join(args.video_dir, video_file)
      statink = ''
      if len(items) > 0:
        statink = items[0]
      data = process_file(video_path, ikalog_dir, video_id, statink, prev_date, prev_index)
      prev_date = data[4]
      prev_index = int(data[5])
      output.write('\t'.join(data) + '\n')

if __name__ == "__main__":
  main()
