#!/usr/bin/python
# coding: utf-8

import httplib2
import json
import os
import sys

from apiclient.discovery import build
from apiclient.errors import HttpError
from oauth2client.client import flow_from_clientsecrets
from oauth2client.file import Storage
from oauth2client.tools import argparser, run_flow


# The CLIENT_SECRETS_FILE variable specifies the name of a file that contains
# the OAuth 2.0 information for this application, including its client_id and
# client_secret. You can acquire an OAuth 2.0 client ID and client secret from
# the Google Developers Console at
# https://console.developers.google.com/.
# Please ensure that you have enabled the YouTube Data API for your project.
# For more information about using OAuth2 to access the YouTube Data API, see:
#   https://developers.google.com/youtube/v3/guides/authentication
# For more information about the client_secrets.json file format, see:
#   https://developers.google.com/api-client-library/python/guide/aaa_client_secrets
CLIENT_SECRETS_FILE = "client_secrets.json"

# This OAuth 2.0 access scope allows for full read/write access to the
# authenticated user's account.
YOUTUBE_READ_WRITE_SCOPE = "https://www.googleapis.com/auth/youtube"
YOUTUBE_API_SERVICE_NAME = "youtube"
YOUTUBE_API_VERSION = "v3"

# This variable defines a message to display if the CLIENT_SECRETS_FILE is
# missing.
MISSING_CLIENT_SECRETS_MESSAGE = """
WARNING: Please configure OAuth 2.0

To make this sample run you will need to populate the client_secrets.json file
found at:

   %s

with information from the Developers Console
https://console.developers.google.com/

For more information about the client_secrets.json file format, please visit:
https://developers.google.com/api-client-library/python/guide/aaa_client_secrets
""" % os.path.abspath(os.path.join(os.path.dirname(__file__),
                                   CLIENT_SECRETS_FILE))

def get_authenticated_service(args):
  flow = flow_from_clientsecrets(CLIENT_SECRETS_FILE,
    scope=YOUTUBE_READ_WRITE_SCOPE,
    message=MISSING_CLIENT_SECRETS_MESSAGE)

  storage = Storage("%s-oauth2.json" % sys.argv[0])
  credentials = storage.get()

  if credentials is None or credentials.invalid:
    credentials = run_flow(flow, storage, args)

  return build(YOUTUBE_API_SERVICE_NAME, YOUTUBE_API_VERSION,
    http=credentials.authorize(httplib2.Http()))

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

  print json_dumps(videos_list_status)
  sys.exit(0)

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
                    indent=2, separators=(',', ': ')).decode('utf-8')

def parse_file(filename):
  update_list = []
  with open(filename, "r") as data:
    for line in data:
      items = line.rstrip().split("\t")
      if len(items) < 1:
        continue
      options = {}
      options["video_id"] = items[0]
      options["title"] = items[1]
      options["privacyStatus"] = items[2]
      options["tags"] = ["splatoon", "スプラトゥーン"]
      update_list.append(options)

  for index, value in enumerate(update_list):
    prev_id = None
    if index != 0:
      prev_id = update_list[index-1]["video_id"]
    next_id = None
    if index + 1 != len(update_list):
      next_id = update_list[index+1]["video_id"]

    description = ""
    if prev_id:
      description += "Prev: https://youtu.be/%s\n" % prev_id
    if next_id:
      description += "Next: https://youtu.be/%s\n" % next_id
    value["description"] = description
  return update_list


if __name__ == "__main__":
  argparser.add_argument("--data", help="Data file.", required=True)
  args = argparser.parse_args()

  update_list = parse_file(args.data)

  youtube = get_authenticated_service(args)
  number = 0
  try:
      for update in update_list:
        video_id = update["video_id"]
        if update_video(youtube, video_id, update):
          print "Updated https://youtu.be/%s" % video_id
          print json_dumps(update)
          print
        else:
          print "Error on updating https://youtu.be/%s" % video_id
        number += 1
  except HttpError, e:
    print "An HTTP error %d occurred:" % e.resp.status
    print json_dumps(e.content)
  else:
    print "Updated %d videos." % number
