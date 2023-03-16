import argparse

from xml.dom import minidom

def ParseArguments():
  parser = argparse.ArgumentParser()
  parser.add_argument('--input')
  parser.add_argument('--output')
  return parser.parse_args()


def ModifySvg(base_svg, new_svg):
  doc = minidom.parse(base_svg)
  for path in doc.getElementsByTagName('path'):
    if path.hasAttribute('fill'):
      continue
    path.setAttribute('fill', 'white')
  with open(new_svg, 'w') as output:
    doc.writexml(output)


def main():
  args = ParseArguments()
  ModifySvg(args.input, args.output)


if __name__ == '__main__':
  main()
