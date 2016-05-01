#!/usr/bin/python

import subprocess
import sys

command = ['ffmpeg']
inputs = sys.argv[1:-1]
output = sys.argv[-1]

for filename in inputs:
  command.extend(['-i', filename])
command.extend(['-filter_complex', 'concat=n=%d:v=1:a=1' % len(inputs), output])

print ' '.join(command)
