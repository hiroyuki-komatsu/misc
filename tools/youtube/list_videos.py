#!/usr/bin/env python3
# coding: utf-8

import argparse
import os
import os.path
import subprocess
import sys
import time

import os

def get_videos(video_dir, last_time=0):
  video_list = []
  for filename in os.listdir(video_dir):
    if filename.endswith('mp4') or filename.endswith('ts'):
      path = os.path.join(video_dir, filename)
      mtime = os.path.getmtime(path)
      if mtime > last_time:
        video_list.append((path, mtime))
  video_list.sort(key=lambda x: x[1])
  return video_list


def get_tsv(ikalog_dir, video_path):
  command = [os.path.join(ikalog_dir, 'tools/print_data.py'),
             '--tsv', '--tsv_format=TEXT', '--statink=%s.statink' % video_path]
  tsv = subprocess.check_output(command)
  return tsv.decode('utf-8').rstrip('\n').split('\t')


def main():
  parser = argparse.ArgumentParser()
  parser.add_argument('--video_dir')
  parser.add_argument('--ikalog_dir')
  parser.add_argument('--last_time', default='')
  parser.add_argument('--list_file')
  # parser.add_argument('--statink_summary')
  args = parser.parse_args()

  last_time = 0
  if args.last_time:
    last_time = time.mktime(time.strptime(args.last_time, "%Y-%m-%d_%H:%M:%S"))

  lines = []
  for video_pair in get_videos(os.path.expanduser(args.video_dir), last_time):
    video_path = video_pair[0]
    if os.path.basename(video_path).startswith('._'):
      continue

    tsv = get_tsv(os.path.expanduser(args.ikalog_dir), video_path)
    valid = 0
    if '' not in tsv:
      valid = 1
    if ((tsv[1] == 'プライベートマッチ' or tsv[2] == 'ナワバリバトル') and
        ('' not in tsv[0:8])):
      valid = 1

    line = '\t'.join([str(valid), video_path] + tsv)
    with open(video_path + '.tsv', 'w') as f:
      print(line, file=f)
    print(line)
    lines.append(line)

  with open(os.path.expanduser(args.list_file), 'w') as f:
    print('\n'.join(lines), file=f)


if __name__ == "__main__":
  main()
