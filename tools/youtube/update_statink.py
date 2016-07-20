#!/usr/bin/python
# coding: utf-8

import argparse
import os
import os.path
import subprocess
import sys
import time

def parse_file(filename, video_dir):
  update_list = []
  with open(filename, "r") as data:
    for line in data:
      items = unicode(line, "utf-8").rstrip().split("\t")
      if len(items) < 1:
        continue
      options = {}
      options["video_file"] = items[0]
      options["video_id"] = items[1]
      options["title"] = items[2]

      statink = os.path.join(video_dir, options["video_file"] + ".statink")
      if os.path.exists(statink):
        options["statink"] = statink
      update_list.append(options)

  return update_list


def UploadToStatInk(ikalog_dir, statink, video_id):
  command = os.path.join(ikalog_dir, 'tools/statink/upload_payload.py')
  url = subprocess.check_output([command, statink, '--video_id=%s' % video_id])
  return url.rstrip()


if __name__ == "__main__":
  parser = argparse.ArgumentParser()
  parser.add_argument("--data", help="Data file.", required=True)
  parser.add_argument("--ikalog_dir")
  parser.add_argument("--video_dir")

  args = parser.parse_args()
#  sys.path.append(args.ikalog_dir)
#  import ikalog.outputs.statink_uploader # import UploadToStatInk

  update_list = parse_file(args.data, args.video_dir)

  for update in update_list:
    video_file = update['video_file']
    video_id = update["video_id"]
    title = update['title']
    if 'statink' in update:
      statink = update['statink']
      url = UploadToStatInk(args.ikalog_dir, statink, video_id)
      if not url.startswith('http'):
          sys.exit(1)
      time.sleep(2)
    else:
      url = ''
    print(u'\t'.join([video_file, video_id, title, url]).encode('utf-8'))
