# $Id: .zshrc,v 1.5 2003/01/12 05:45:23 komatsu Exp $
unset GCONFIGFLAGS
alias ls='/bin/ls -Gap'
alias ll='ls -l'
alias la='ls -a'
alias cp='/bin/cp -pi'
alias scp='/usr/bin/scp -p'
#alias lv='/usr/bin/lv -c -Iu8'
alias lv='/usr/bin/less'
alias ifconfig='/sbin/ifconfig'

alias xemacs-vanilla='/usr/bin/xemacs -no-site-file -no-early-packages -user-init-file ~/src/elisp/initz/init-keys.el'

alias psgrep='ps aux | grep'
alias rwget='wget -T 10 -N -rnp'
alias clean='rm -f *~'

alias valgrind-mem='GLIBCPP_FORCE_NEW= valgrind --tool=memcheck --leak-check=yes'

alias ggrep='rgrep -ia'
csu () { cut -d\  -f $1 | sort | uniq }

alias bns_resolve='/home/build/static/projects/chubby/lockserv resolve '
alias geocode-client='/home/build/buildstatic/projects/location/waldo/tools/geocode-client'

function appendpath()  { test -d $1 && export PATH="$PATH:$1" }
function prependpath() { test -d $1 && export PATH="$1:$PATH" }

# For Mac
appendpath /Developer/usr/bin/

export GTK_IM_MODULE=uim
export LANG=ja_JP.UTF-8
# export LANG=ja_JP.eucJP
# export OUTPUT_CHARSET=EUC-JP
if [[ $HOST = "omega" ]] then
    export CVSROOT=/home/komatsu/CVS_DB
else
    export CVSROOT=www.taiyaki.org:/home/komatsu/CVS_DB
fi

export CVS_RSH=ssh

setopt prompt_subst

# wget -O- 'http://hsb17:10000/varz?var=argv&output=text' | sed 's/ /\n/g' | sort | uniq

check_remote_login() {
  if [[ $SSH_CLIENT = "" ]] then
    echo false
  else
    echo true
  fi
}

check_terminal_emacs() {
  if [[ $TERM = "emacs" ]] then
    echo true
  else
    echo false
  fi
}

autoload -Uz vcs_info
zstyle ':vcs_info:*' formats '[%b]'
zstyle ':vcs_info:*' actionformats '[%b|%a]'

set_prompt() {
  prompt_format='%~%# '
  rprompt_format='%n@%m'

  if [[ `check_terminal_emacs` = "true" ]] then
    PROMPT=$prompt_format
  else
    if [[ `check_remote_login` = "true" ]] then
      PROMPT="%F{red}%B"$prompt_format"%b%f"
    else
      PROMPT="%F{green}%B"$prompt_format"%b%f"
    fi

    # If vcs_info sets the variable, use it as RPROMPT.
    LANG=en_US.UTF-8 vcs_info
    if [[ -n "$vcs_info_msg_0_" ]] then
      RPROMPT="%F{green}%B"$vcs_info_msg_0_"%b%f"
    else
      RPROMPT=$rprompt_format
    fi
  fi
}

change_title() {
  if [[ `check_terminal_emacs` = "true" ]] then
    return
  else
    if [[ `check_remote_login` = "true" ]] then
      #print -Pn "\e]2;"$HOST":"$1"\a"
      print -Pn "\e]0;"$HOST":"$1"\a"
    else
      print -Pn "\e]0;"$1"\a"
      #print -Pn "\e]2;"$1"\a"
    fi
  fi
}

precmd() {
  [[ -t 1 ]] || return
  case `pwd` in
  $HOME)
    change_title "~    "
    ;;
  "/")
    change_title "/    "
    ;;
  *)
    change_title "%~"
    ;;
  esac
  set_prompt
}

preexec() {
  [[ -t 1 ]] || return
  change_title $1
}

#source $HOME/.zsh-bindkey.zsh
#
# bindkey -r "\C-I"
# bindkey -m
# bindkey "\C-I" expand-or-complete
# bindkey "\C-P" up-line-or-search
# bindkey "\C-N" down-line-or-search
bindkey -e
bindkey "\C-P" up-line-or-search
bindkey "\C-N" down-line-or-search

stty erase '^H'
stty stop undef
stty start undef
stty intr '^C'

setopt autocd
setopt correct
setopt globdots
setopt no_hup

# For Man
manpath=(/usr/man /usr/share/man /usr/X11R6/man /var/catman /usr/local/man)
export MANPATH

man_glob () {
   local a
   read -cA a
   if [[ $a[2] = -s ]] then         # Or [[ $a[2] = [0-9]* ]] for BSD
     reply=( ${^manpath}/man$a[3]/$1*$2(N:t:r) )
   else
     reply=( ${^manpath}/man*/$1*$2(N:t:r) )
   fi
}
compctl -K man_glob -x 'C[-1,-P]' -m - \
	'R[-*l*,;]' -/g '*.(man|[0-9nlpo](|[a-z]))' -- man

# For History
HISTSIZE=40950
if [ -f ~/.zsh_history ]
then
    HISTFILE=~/.zsh_history
fi
SAVEHIST=40950
DIRSTACKSIZE=255

setopt histignoredups append_history share_history
# source .zshrc-dabbrev

autoload -U compinit; compinit
