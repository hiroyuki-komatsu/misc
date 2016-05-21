#!/usr/bin/python

import os
import os.path
import subprocess
import sys

inputs = sys.argv[1:]
output = '%s-concat%s' % os.path.splitext(inputs[0])

### Call ffmpeg to concat videos.
command = ['ffmpeg']
for filename in inputs:
  command.extend(['-i', '"%s"' % filename])
command.extend(['-filter_complex', 'concat=n=%d:v=1:a=1' % len(inputs), output])

#print ' '.join(command)
subprocess.check_call(' '.join(command), shell=True)

### Change the concat file's times.
mtime = os.path.getmtime(inputs[-1])
atime = os.path.getatime(inputs[-1])
os.utime(output, (atime, mtime))
