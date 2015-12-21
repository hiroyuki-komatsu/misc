#!/usr/bin/python

import struct
import sys

data = open(sys.argv[1]).read()

bitmap_file_header = data[0:14]

print 'Bitmap file header'
for byte in bitmap_file_header:
    print '%02X' % ord(byte),
print '\n'

data = data[14:]

print 'DIB header'
size_of_dib_header = struct.unpack('<i', data[0:4])[0]
print 'size_of_dib_header: %d' % size_of_dib_header

if size_of_dib_header <= (46-14):
    sys.exit(1)

# BITMAPINFOHEADER
num_of_bits = struct.unpack('<h', data[(28-14):(28-14+2)])[0]
print 'num_of_bits: %d' % num_of_bits

compression = struct.unpack('<i', data[(30-14):(30-14+4)])[0]
print 'compression: %d' % compression

num_of_colors = struct.unpack('<i', data[(46-14):(46-14+4)])[0]
print 'num_of_colors: %d' % num_of_colors

dib_header = data[0:size_of_dib_header]
for byte in dib_header:
    print '%02X' % ord(byte),
print '\n'


data = data[size_of_dib_header:]

print 'data:'
bits = 0
for byte in data:
    print '%02X' % ord(byte),
    bits += 8
    if bits == num_of_bits:
        bits = 0
        print
