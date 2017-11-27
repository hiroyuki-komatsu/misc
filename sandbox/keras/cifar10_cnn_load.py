#!/usr/local/bin/python3

import argparse
import keras
#from keras.datasets import cifar10
from keras.models import load_model

from PIL import Image

import numpy as np
import os

class Cifar10(object):
  def __init__(self, model_path):
    self.model = load_model(model_path)
    print('Loaded trained model at %s ' % model_path)

  def predictWithImage(self, image_path):
    image = Image.open(image_path)
    input_data = np.asarray([np.asarray(image)])
    return self.model.predict(input_data)

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument('--input')
  args = parser.parse_args()

  save_dir = os.path.join(os.getcwd(), 'saved_models')
  model_name = 'keras_cifar10_trained_model.h5'

  model_path = os.path.join(save_dir, model_name)
  cifar10 = Cifar10(model_path)
  result = cifar10.predictWithImage(args.input)
  print(result)

main()