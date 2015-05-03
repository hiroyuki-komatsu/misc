# git_change_authors

This is a script to generate a shell script to change the authors after git-svn.


1. Write /tmp/authors.tsv
2. % python ./change_authors.py /tmp/authors.tsv > /tmp/filter.sh
3. Go to the git repository you want to change authors.
4. % FILTER=$(cat /tmp/filter.sh); git filter-branch -f --commit-filter $FILTER HEAD

## Tips

### How to check authors?
git log --format="%an %ae %cn %ce"

