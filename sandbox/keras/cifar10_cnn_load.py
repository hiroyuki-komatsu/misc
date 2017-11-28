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

  def saveModelAndWeight(self, output_dir):
    os.makedirs(output_dir, exist_ok=True)
    self.model.save_weights(os.path.join(output_dir, 'model.hdf5'))

    with open(os.path.join(output_dir, 'model.json'), 'w') as f:
      f.write(self.model.to_json())

def main():
  parser = argparse.ArgumentParser()
  parser.add_argument('--input')
  parser.add_argument('--model_dir')
  parser.add_argument('--output_dir')
  args = parser.parse_args()

  model_name = 'keras_cifar10_trained_model.h5'

  model_path = os.path.join(args.model_dir, model_name)
  cifar10 = Cifar10(model_path)

  if args.input:
    result = cifar10.predictWithImage(args.input)
    print(result)

  if args.output_dir:
    cifar10.saveModelAndWeight(args.output_dir)

main()