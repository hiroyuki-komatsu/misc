import sys

name_list = []
with open(sys.argv[1]) as file:
  for line in file:
    if line.startswith('#'):
      continue
    items = line.rstrip().split('\t')
    if len(items) != 2:
      continue
    name_list.append(items)

output = 'case "$GIT_AUTHOR_NAME" in'

for mail, name in name_list:
  output += """
    "%s")
      GIT_AUTHOR_NAME="%s";
      GIT_COMMITTER_NAME="%s";
      GIT_AUTHOR_EMAIL="%s";
      GIT_COMMITTER_EMAIL="%s";
      ;;""" % (mail, name, name, mail, mail)

output += """
esac
git commit-tree "$@"
"""

print output
