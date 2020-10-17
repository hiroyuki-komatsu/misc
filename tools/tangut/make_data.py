class TangutEntry:
  def __init__(self):
    self.char = ''
    self.unicode = ''
    self.xiahans = []  # 夏漢
    self.source = ''
    self.radical = ''
    self.strokes = ''
    self.english = []


def GetUnicodeChar(unicode):
  return chr(int(unicode.split('+')[1], 16))


class TangutData:
  def __init__(self):
    self.unicode_dict = {}
    self.xiahan_dict = {}

  def getDataFromUnicode(self, unicode):
    if unicode not in self.unicode_dict:
      entry = TangutEntry()
      entry.unicode = unicode
      self.unicode_dict[unicode] = entry
    return self.unicode_dict[unicode]

  def addData(self, unicode, xiahans=None, source=None, radical=None, strokes=None, english=None):
    entry = self.getDataFromUnicode(unicode)

    if source:
      entry.source = source
    if radical:
      entry.radical = radical
    if strokes:
      entry.strokes = strokes
    if xiahans:
      for xiahan in xiahans:
        if xiahan not in entry.xiahans:
          entry.xiahans.append(xiahan)
          self.xiahan_dict[xiahan] = entry  # Update the dict from Xiahan too.
    if english:
      entry.english.append(english)

  def addDataFromXiahan(self, xiahan, english=None):
    entry = self.xiahan_dict[xiahan]
    self.addData(unicode=entry.unicode, english=english)

  def printData(self):
    print('\t'.join([
      'character', 'unicode', 'source', 'radical character', 'radical id',
      'strokes', 'xia-han', 'definition']))
    for key in sorted(self.unicode_dict.keys()):
      data = self.unicode_dict[key]
      char = GetUnicodeChar(data.unicode)
      radical_char = chr(0x18800 + int(data.radical) - 1)
      output = [
          char, data.unicode, data.source, radical_char, data.radical,
          data.strokes, ' '.join(data.xiahans), ' // '.join(data.english)
      ]
      print('\t'.join(output))


def LoadUnicodeData(filename, data):
  """https://www.unicode.org/Public/UCD/latest/ucd/TangutSources.txt"""
  with open(filename) as file:
    for line in file:
      if line.startswith('#') or line == '\n':
        continue
      code, key, value = line.rstrip().split('\t')
      if key == 'kTGT_MergedSrc':
        xiahans = None
        category, *ids = value.split('-')
        if category in ('L1997', 'L2008'):
          xiahans = []
          for xiahan in ids:
            # Nomalize '2941A' to '2941'.
            if xiahan[-1] in ('A', 'B'):
              xiahan = xiahan[:-1]
            xiahans.append(xiahan)
        data.addData(unicode=code, source=value, xiahans=xiahans)
      elif key == 'kRSTUnicode':
        radical, strokes = value.split('.')
        data.addData(unicode=code, radical=radical, strokes=strokes)
      else:
        print('Unknown key: ' + key)


def LoadEnglishDictionary(filename, data):
  """https://www.babelstone.co.uk/Tangut/XHZD_2008_Definitions.txt"""
  with open(filename) as file:
    for line in file:
      xiahan, definition = line.lstrip('\ufeff').rstrip().split('\t')
      data.addDataFromXiahan(xiahan=xiahan, english=definition)


def LoadEnglishDictionaryHtml(filename, data):
  """https://www.babelstone.co.uk/Tangut/XHZD_EnglishIndex.html"""
  with open(filename) as file:
    for line in file:
      if not line.startswith('<tr><td>'):
        continue
      left, right = line.split('</td><td>')
      definition = left[8:]  # len('<tr><td>') = 8
      xiahans = right[:-11].split(' ')  # -1 - len('</td></tr>') = -11
      for xiahan in xiahans:
        if xiahan.endswith('?'):
          xiahan = xiahan[:-1]
          definition = '(' + definition + ')'
        data.addDataFromXiahan(xiahan=xiahan, english=definition)


def main():
  data = TangutData()
  LoadUnicodeData('TangutSources.txt', data)
  LoadEnglishDictionary('XHZD_2008_Definitions.txt', data)
  # LoadEnglishDictionaryHtml('XHZD_EnglishIndex.html', data)
  data.printData()


main() 