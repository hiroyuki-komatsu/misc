#!/usr/bin/python
# -*- coding: utf-8 -*-

import httplib2
import os
import os.path
import re
import sys

import youtube_auth

from oauth2client.tools import argparser

def get_default_title(filename):
  filename = re.compile(".mp4|.ts|\\(|\\)").sub("", filename)
  filename = re.compile("[\\.\\-_]").sub(" ", filename)
  return filename

def get_video_dict(video_dir):
  video_dict = {}
  for filename in os.listdir(video_dir):
    if filename.endswith('mp4') or filename.endswith('ts'):
      video_dict[get_default_title(filename)] = filename
  return video_dict

def get_playlist(youtube, num=None):
  max_results = 50
  if num and num < 50:
    max_results = num

  # Retrieve the list of videos uploaded to the authenticated user's channel.
  playlist = []

  # Retrieve the contentDetails part of the channel resource for the
  # authenticated user's channel.
  channels_response = youtube.channels().list(
    mine=True,
    part="contentDetails"
  ).execute()

  for channel in channels_response["items"]:
    # From the API response, extract the playlist ID that identifies the list
    # of videos uploaded to the authenticated user's channel.
    uploads_list_id = channel["contentDetails"]["relatedPlaylists"]["uploads"]

    print "Videos in list %s" % uploads_list_id
    playlistitems_list_request = youtube.playlistItems().list(
      playlistId=uploads_list_id,
      part="snippet",
      maxResults=max_results
    )

    while (num is None) or (num > 0):
      if not playlistitems_list_request:
        break

      playlistitems_list_response = playlistitems_list_request.execute()

      # Print information about each video.
      for playlist_item in playlistitems_list_response["items"]:
        title = playlist_item["snippet"]["title"]
        video_id = playlist_item["snippet"]["resourceId"]["videoId"]

        if not title.startswith('splatoon'):
          print('\t'.join([video_id, title]))
          return playlist

        playlist.append([title, video_id])
        #print data.encode("utf-8")

        if num:
          num -= 1
          if num <= 0:
            return playlist

      playlistitems_list_request = youtube.playlistItems().list_next(
        playlistitems_list_request, playlistitems_list_response)

  return playlist

def compose_data(playlist, video_dict, output):
  with open(output, 'w') as file:
    for item in reversed(playlist):
      title, video_id = item
      video_file = video_dict.get(title, '')
      line = u'\t'.join([video_file, video_id, title]).encode('utf-8')
      file.write(line + '\n')

def main():
  argparser.add_argument("--num", "-n", dest="num",
                         help="num of videos", type=int)
  argparser.add_argument("--video_dir")
  argparser.add_argument("--output_playlist")
  args = argparser.parse_args()

  video_dict = get_video_dict(args.video_dir)

  youtube = youtube_auth.get_authenticated_service(args)

  playlist = get_playlist(youtube, args.num)

  compose_data(playlist, video_dict, args.output_playlist)

if __name__ == "__main__":
  main()
