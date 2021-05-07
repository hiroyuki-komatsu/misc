import argparse
import asyncio
import concurrent.futures
import subprocess
import sys
import threading
from asyncio.subprocess import PIPE


is_terminate = threading.Event()


def parse_args():
  parser = argparse.ArgumentParser()
  parser.add_argument('--file', default='events.txt')
  parser.add_argument('--device', default='/dev/input/event1')
  return parser.parse_args()


async def check_interuption(loop):
  reader = asyncio.StreamReader()
  protocol = asyncio.StreamReaderProtocol(reader)
  await loop.connect_read_pipe(lambda: protocol, sys.stdin)

  line = await reader.readline()
  print('done.')
  is_terminate.set()


async def run_command(args):
  process = await asyncio.create_subprocess_exec(*args, stdout=PIPE)
  output = []

  while not is_terminate.is_set():
    try:
      line = await asyncio.wait_for(process.stdout.readline(), timeout=0.1)
    except asyncio.TimeoutError:
      continue

    line = line.decode('utf-8')
    output.append(line)
    print(line, end='')

  return ''.join(output)


def get_event_loop():
  if sys.platform == 'win32':
    loop = asyncio.ProactorEventLoop()
    asyncio.set_event_loop(loop)
  else:
    loop = asyncio.get_event_loop()
  return loop


def main():
  args = parse_args()
  loop = get_event_loop()

  command = ['/usr/local/bin/adb', 'shell', 'getevent', '-lt', args.device]
  futures = asyncio.gather(run_command(command), check_interuption(loop))
  output = loop.run_until_complete(futures)[0]

  loop.close()

  with open(args.file, 'w') as file:
    file.write(output)


main()