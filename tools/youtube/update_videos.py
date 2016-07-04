#!/usr/bin/python
# coding: utf-8

import httplib2
import json
import os
import os.path
import sys

import youtube_auth

from apiclient.discovery import build
from apiclient.errors import HttpError
from oauth2client.client import flow_from_clientsecrets
from oauth2client.file import Storage
from oauth2client.tools import argparser, run_flow

NEXT_VIDEO_PLACEHOLDER = u"次: 準備中"

def update_prev_id(youtube, video_id, next_id):
  # Call the API's videos.list method to retrieve the video resource.
  videos_list_response = youtube.videos().list(
    id=video_id,
    part='snippet'
  ).execute()

  # If the response does not contain an array of "items" then the video was
  # not found.
  if not videos_list_response["items"]:
    print "Video '%s' was not found." % options.video_id
    return False

  # Since the request specified a video ID, the response only contains one
  # video resource. This code extracts the snippet from that resource.
  videos_list_snippet = videos_list_response["items"][0]["snippet"]

  videos_list_snippet["description"] = (
      videos_list_snippet["description"].replace(
          NEXT_VIDEO_PLACEHOLDER,
          u"次: https://youtu.be/%s" % next_id))

  # Update the video resource by calling the videos.update() method.
  videos_update_response = youtube.videos().update(
    part='snippet',
    body=dict(
      snippet=videos_list_snippet,
      id=video_id
    )).execute()
  return True

def update_video(youtube, video_id, options):
  # Call the API's videos.list method to retrieve the video resource.
  videos_list_response = youtube.videos().list(
    id=video_id,
    part='snippet,status'
  ).execute()

  # If the response does not contain an array of "items" then the video was
  # not found.
  if not videos_list_response["items"]:
    print "Video '%s' was not found." % options.video_id
    return False

  # Since the request specified a video ID, the response only contains one
  # video resource. This code extracts the snippet from that resource.
  videos_list_snippet = videos_list_response["items"][0]["snippet"]
  videos_list_status = videos_list_response["items"][0]["status"]

  # Preserve any tags already associated with the video. If the video does
  # not have any tags, create a new array. Append the provided tag to the
  # list of tags associated with the video.
  if options.get("tags", None):
    videos_list_snippet["tags"] = options["tags"]

  if options.has_key("title"):
    videos_list_snippet["title"] = options["title"]

  if options.has_key("description"):
    videos_list_snippet["description"] = options["description"]

  if options.has_key("privacyStatus"):
    videos_list_status["privacyStatus"] = options["privacyStatus"]

  # Update the video resource by calling the videos.update() method.
  videos_update_response = youtube.videos().update(
    part='snippet,status',
    body=dict(
      snippet=videos_list_snippet,
      status=videos_list_status,
      id=video_id
    )).execute()
  return True

def json_dumps(json_data):
  return json.dumps(json_data, sort_keys=True, ensure_ascii=False,
                    indent=2, separators=(',', ': '))

def read_description(description_file):
  descritpion = u""
  with open(description_file, "r") as data:
    for line in data:
      if line.startswith("IKA"):
        continue
      descritpion += unicode(line, "utf-8")
  return descritpion

def parse_file(filename, video_dir, prev_id = None, unlisted = False):
  update_list = []
  with open(filename, "r") as data:
    for line in data:
      items = unicode(line, "utf-8").rstrip().split("\t")
      if len(items) < 1:
        continue
      options = {}
      options["video_file"] = items[0]
      options["video_id"] = items[1]
      options["statink"] = items[2]
      options["title"] = items[16]
      if len(items) > 17:
        options["privacyStatus"] = items[17]
      elif unlisted:
        options["privacyStatus"] = "unlisted"
      else:
        options["privacyStatus"] = "public"
      options["tags"] = ["splatoon", u"スプラトゥーン"]
      update_list.append(options)

  for index, value in enumerate(update_list):
    if index != 0:
      prev_id = update_list[index-1]["video_id"]
    next_id = None
    if index + 1 != len(update_list):
      next_id = update_list[index+1]["video_id"]

    description = ""
    if next_id:
      description += u"次: https://youtu.be/%s\n" % next_id
    else:
      description += u"%s\n" % NEXT_VIDEO_PLACEHOLDER
    if prev_id:
      description += u"前: https://youtu.be/%s\n" % prev_id
    if value.get("statink"):
      description += u"stat.ink: %s\n" % value["statink"]

    description_file = os.path.join(video_dir, value["video_file"] + ".txt")
    video_description = read_description(description_file)
    value["description"] = u"\n".join([description, video_description])
  return update_list


if __name__ == "__main__":
  argparser.add_argument("--data", help="Data file.", required=True)
  argparser.add_argument("--prev_id", help="Previous ID")
  argparser.add_argument("--prev_data")
  argparser.add_argument("--video_dir")
  argparser.add_argument("--unlisted", action="store_true")
  args = argparser.parse_args()

  update_list = parse_file(args.data, args.video_dir,
                           prev_id=args.prev_id,
                           unlisted=args.unlisted)

  youtube = youtube_auth.get_authenticated_service(args)

  number = 0
  prev_id = None
  if args.prev_id:
    prev_id = args.prev_id
  elif args.prev_data:
    with open(args.prev_data) as prev_data:
      prev_id = prev_data.read().split('\t')[0]

  if prev_id:
    update_prev_id(youtube, prev_id, update_list[0]["video_id"])
    number += 1

  try:
    for update in update_list:
      video_id = update["video_id"]
      if update_video(youtube, video_id, update):
        print("Updated https://youtu.be/%s" % video_id)
        print(json_dumps(update))
        print('')
      else:
        print("Error on updating https://youtu.be/%s" % video_id)
      number += 1
  except HttpError, e:
    print("An HTTP error %d occurred:" % e.resp.status)
    print(json_dumps(e.content))
  else:
    print("Updated %d videos." % number)
