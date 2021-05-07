import argparse
import re
import subprocess
import time


def parse_args():
  parser = argparse.ArgumentParser()
  parser.add_argument('--file', default='events.txt')
  parser.add_argument('--interval', type=int, default=2)
  return parser.parse_args()


def set_event(event, timestamp, event_id, key, value):
  if event_id == 'EV_SYN':
    event['__time__'] = float(timestamp)
    return False

  if event_id == 'EV_ABS':
    event[key] = int(value, 16)
    return True

  if event_id == 'EV_KEY':
    event[key] = value
    return True

  print('Unknown event_id: ' + event_id)
  return False


def parse_file(filename):
  actions = []
  with open(filename) as data:
    last_id = int('ffffffff', 16)
    event = {}
    for line in data:
      line = line.replace(']', ' ').strip('[ \n')
      timestamp, event_id, key, value = re.sub(' +', '\t', line).split('\t')

      if set_event(event, timestamp, event_id, key, value):
        continue

      if event.get('ABS_MT_TRACKING_ID', last_id) != last_id:
        actions.append([])
      actions[-1].append(event)
      event = {}

  return actions


def convert_to_inputs(actions):
  inputs = []
  prev_x = None
  prev_y = None
  for events in actions:
    action = events[0]
    if action.get('BTN_TOUCH') != 'DOWN':
      print('Unknown action')
      print(action)
      return

    x = action.get('ABS_MT_POSITION_X', prev_x)
    y = action.get('ABS_MT_POSITION_Y', prev_y)
    prev_x = x
    prev_y = y
    timestamp = action['__time__']
    args = ['touchscreen', 'tap', str(x), str(y)]
    inputs.append((timestamp, args))
  return inputs


def run_adb_input(inputs, count=None):
  prev_time = time.time()
  prev_timestamp = inputs[0][0]
  for timestamp, args in inputs:
    current_time = time.time()
    recorded_interval = timestamp - prev_timestamp
    replay_interval = current_time - prev_time
    prev_timestamp = timestamp
    prev_time = current_time
    if recorded_interval > replay_interval:
      interval = recorded_interval - replay_interval
      print(f'sleep: {interval}')
      time.sleep(interval)
    command = ['/usr/local/bin/adb', 'shell', 'input'] + args
    print(' '.join(args))
    subprocess.run(command)
  if count != None:
    print (f'Done: #{count}')


def show_help(inputs):
  print('events:')
  base_timestamp = inputs[0][0]
  for timestamp, args in inputs:
    print(f'{timestamp - base_timestamp:.2f}\t' + ' '.join(args))
  print()
  print('<empty>: run the events')
  print('<number>: repeat the events N times')
  print('exit: exit the command')
  print()


def console(inputs, interval=2):
  show_help(inputs)
  count = 0
  while True:
    command = input('> ')
    if command == 'exit':
      return

    if command == '':
      run_adb_input(inputs, count)
      count += 1

    elif re.match('^\d+$', command):
      for i in range(int(command)):
        run_adb_input(inputs, count)
        time.sleep(interval)
        count += 1

    else:
      show_help(inputs)


def main():
  args = parse_args()

  actions = parse_file(args.file)
  inputs = convert_to_inputs(actions)
  console(inputs, interval=args.interval)


main()
