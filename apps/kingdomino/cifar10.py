#!/usr/local/bin/python3
"""
./cifar10.py --datadir train/input --output data_batch_1.bin
./cifar10.py --dumpfile data_batch_1.bin --outdir data_dir
"""

import argparse
import numpy as np
import os
from PIL import Image


def cifar10ToImage(byte_array, width=32, height=32, depth=3):
  label = np.frombuffer(byte_array[0:1], dtype=np.uint8)

  np_array = np.frombuffer(byte_array[1:], dtype=np.uint8)
  reshaped = np_array.reshape((depth, width, height))
  transposed = reshaped.transpose((1, 2, 0))

  return label, Image.fromarray(transposed)


def imageToCifar10(label, image, width=32, height=32):
  np_array = np.asarray(image.resize((width, height)))
  transposed = np_array.transpose((2, 0, 1))

  return bytes([label]) + transposed.tobytes()


class Cifar10Data(object):
  def __init__(self, data=None, width=32, height=32, depth=3):
    self.width = width
    self.height = height
    self.depth = depth
    self.data = data or bytes([])
    self.count = 0

  def appendImage(self, label, image):
    self.data += imageToCifar10(label, image, self.width, self.height)
    self.count += 1

  def getImage(self, index):
    data_size = 1 + self.width * self.height * self.depth
    data = self.data[data_size * index : data_size * (index + 1)]
    return cifar10ToImage(data)

  def save(self, filepath):
    with open(filepath, mode='wb') as file:
      file.write(self.data)


def generateData(data_dir, output_file):
  labels = {'base': 0, 'forest': 1, 'grass': 2, 'pond': 3, 'rock': 4, 'sea': 5, 'weat': 6}
  cifar10 = Cifar10Data()

  for basedir, dirs, files in os.walk(data_dir):
    label = labels.get(os.path.basename(basedir), -1)
    if label == -1:
      continue
    for filename in files:
      if not filename.endswith('.jpg'):
        continue
      image = Image.open(os.path.join(basedir, filename))
      cifar10.appendImage(label, image)
  
  cifar10.save(output_file)
  print ('count: %d' % cifar10.count)


def dumpImages(dumpfile, outdir):
  with open(dumpfile, mode='rb') as src:
    cifar10 = Cifar10Data(src.read())

  os.makedirs(os.path.abspath(outdir), exist_ok=True)

  for index in range(2):
    label, image = cifar10.getImage(index)

    print('label: %d' % label)
    filename = 'cifar10-%02d-%d.png' % (index, label)
    image.save(os.path.join(outdir, filename), format='png')


def main():
  parser = argparse.ArgumentParser()
  parser.add_argument('--datadir')
  parser.add_argument('--output')

  parser.add_argument('--outdir')
  parser.add_argument('--dumpfile')

  args = parser.parse_args()

  if args.dumpfile:
    dumpImages(args.dumpfile, args.outdir)
  else:
    generateData(args.datadir, args.output)

main()