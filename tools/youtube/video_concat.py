#!/usr/bin/python

import os
import os.path
import subprocess
import sys

def mp4_to_ts(mp4_file):
    ts_file = mp4_file + '.ts'
    command = ['ffmpeg', '-i', mp4_file, '-c', 'copy',
               '-bsf:v', 'h264_mp4toannexb', '-f', 'mpegts', ts_file]
    subprocess.check_call(' '.join(command), shell=True)
    return ts_file

def concat_ts(ts_files, output):
    command = ['ffmpeg', '-i', '"concat:%s"' % '|'.join(ts_files),
               '-codec', 'copy', output]
    subprocess.check_call(' '.join(command), shell=True)

inputs = sys.argv[1:]
output = '%s-concat.ts' % os.path.splitext(inputs[0])[0]

### Call ffmpeg to concat videos.
ts_files = []
src_files = []
for filename in inputs:
    if filename.endswith('.ts'):
        ts_files.append(filename)
    elif filename.endswith('.mp4'):
        ts_files.append(mp4_to_ts(filename))
        src_files.append(filename)
concat_ts(ts_files, output)

### Change the concat file's times.
mtime = os.path.getmtime(inputs[-1])
atime = os.path.getatime(inputs[-1])
os.utime(output, (atime, mtime))

### Move src files to /tmp/.
src_files += ts_files
moved_ext = '.concated'  # Need to be in the same volume.
for filename in src_files:
    os.rename(filename, filename + moved_ext)
