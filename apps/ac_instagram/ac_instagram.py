import json
import os
import re
import urllib.request

import userdata

INSTAGRAM_USERNAME = userdata.INSTAGRAM_USERNAME
SLACK_WEBHOOK = userdata.SLACK_WEBHOOK
SHORTCODE_FILE = userdata.SHORTCODE_FILE

class AcInstagram():
    @staticmethod
    def fetchContent(username):
        url = f'https://www.instagram.com/{username}/'
        request = urllib.request.Request(url)
        with urllib.request.urlopen(request) as response:
            return response.read().decode('utf-8')


    @staticmethod
    def getJson(username):
        content = AcInstagram.fetchContent(username)
        query_begin = 'window._sharedData = '
        index_begin = content.find(query_begin) + len(query_begin)
        query_end = ';</script>'
        index_end = content.find(query_end, index_begin)
        return json.loads(content[index_begin:index_end])


    @staticmethod
    def getEntry(edge):
        entry = {}
        entry['image'] = edge['node']['display_url']
        entry['caption'] = edge['node']['edge_media_to_caption']['edges'][0]['node']['text']
        entry['timestamp'] = edge['node']['taken_at_timestamp']
        entry['shortcode'] = edge['node']['shortcode']
        return entry


    @staticmethod
    def getEntries(username):
        data = AcInstagram.getJson(username)
        user = data['entry_data']['ProfilePage'][0]['graphql']['user']
        timelines = user['edge_owner_to_timeline_media']['edges']
        entries = []
        for timeline in timelines:
            entries.append(AcInstagram.getEntry(timeline))
        return entries


def getPayload(entry):
    text = f'<https://www.instagram.com/p/{entry["shortcode"]}>\\n{entry["caption"]}'
    payload = f'''{{
  "username": "どうぶつの森 公式インスタグラム",
  "text": "{text}",
  "attachments": [
      {{ "image_url": "{entry['image']}" }}
   ]
}}
'''
    return payload


def readCachedShortcode():
    shortcode = ''
    if os.path.exists(SHORTCODE_FILE):
        with open(SHORTCODE_FILE, 'r') as file:
            shortcode = file.read()
    return shortcode


def writeCachedShortcode(shortcode):
    with open(SHORTCODE_FILE, 'w') as file:
        file.write(shortcode)


def sendMessageToSlack(entry):
    payload = getPayload(entry)
    print(payload)
    urllib.request.urlopen(SLACK_WEBHOOK, payload.encode('utf-8'))


def main():
    entries = AcInstagram.getEntries(INSTAGRAM_USERNAME)
    cached_shortcode = readCachedShortcode()
    for entry in entries:
        if entry['shortcode'] == cached_shortcode:
            break
        sendMessageToSlack(entry)

    if cached_shortcode != entries[0]['shortcode']:
        writeCachedShortcode(entries[0]['shortcode'])


main()
