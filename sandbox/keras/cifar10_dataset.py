from __future__ import absolute_import
from keras.datasets.cifar import load_batch
from keras.utils.data_utils import get_file
from keras import backend as K
import numpy as np
import os
from PIL import Image

def cifar10ToImage(byte_array, width=32, height=32, depth=3):
  label = np.frombuffer(byte_array[0:1], dtype=np.uint8)

  np_array = np.frombuffer(byte_array[1:], dtype=np.uint8)
  reshaped = np_array.reshape((depth, width, height))
  transposed = reshaped.transpose((1, 2, 0))

  return label, Image.fromarray(transposed)

class Cifar10Data(object):
  def __init__(self, data=None, width=32, height=32, depth=3):
    self.width = width
    self.height = height
    self.depth = depth
    self.data = data or bytes([])
    self.image_size = 1 + self.width * self.height * self.depth
    self.count = int(len(self.data) / self.image_size)

  def appendImage(self, label, image):
    self.data += imageToCifar10(label, image, self.width, self.height)
    self.count += 1

  def getImage(self, index):
    data = self.data[self.image_size * index : self.image_size * (index + 1)]
    return cifar10ToImage(data)

  def save(self, filepath):
    with open(filepath, mode='wb') as file:
      file.write(self.data)

def load_batch(data_file):
  """Internal utility for parsing CIFAR data.
  # Arguments
      fpath: path the file to parse.
      label_key: key for label data in the retrieve
          dictionary.
  # Returns
      A tuple `(data, labels)`.
  """
  images = []
  labels = []
  with open(data_file, mode='rb') as src:
    cifar10 = Cifar10Data(src.read())

    print(cifar10.count)

    for i in range(cifar10.count):
      label, image = cifar10.getImage(i)
      labels.append(label)
      images.append(np.asarray(image))

  return np.asarray(images), np.asarray(labels)


def load_data(data_file):
  """Loads CIFAR10 dataset.
  # Returns
      Tuple of Numpy arrays: `(x_train, y_train), (x_test, y_test)`.
  """
  

  num_train_samples = 50000

  x_train, y_train = load_batch(data_file)

  if K.image_data_format() != 'channels_last':
      x_train = x_train.transpose(0, 3, 1, 2)

  return (x_train, y_train), (x_train, y_train)
