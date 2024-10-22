#!/usr/bin/env bash

E_NOERROR=0
E_ERROR=1
E_WARG=101
COLOR_NORMAL=""
COLOR_RED=""
COLOR_YELLOW=""
COLOR_GREEN=""
COLOR_GREY=""
STYLE_UNDERLINE=""
STYLE_BOLD=""

function level_verbose() {
  echo "0"
}
function level_debug() {
  echo "1"
}
function level_trace() {
  echo "2"
}
function level_diag() {
  echo "3"
}

function is_verbose_enabled() {
  if [[ "$verbose_count" -gt "$(level_verbose)" ]]; then
    return 0
  else
    return 1
  fi
}
function is_debug_enabled() {
  if [[ "$verbose_count" -gt "$(level_debug)" ]]; then
    return 0
  else
    return 1
  fi
}
function is_trace_enabled() {
  if [[ "$verbose_count" -gt "$(level_trace)" ]]; then
    return 0
  else
    return 1
  fi
}
function is_diag_enabled() {
  if [[ "$verbose_count" -gt "$(level_diag)" ]]; then
    return 0
  else
    return 1
  fi
}

function info() { echo -e "$*"; }
function verbose() {
  if is_verbose_enabled; then
    info "$@"
  fi
}
function debug() {
  if is_debug_enabled; then
    info "$@"
  fi
}
function trace() {
  if is_trace_enabled; then
    info "$@"
  fi
}
function diag() {
  if is_diagnostic_enabled; then
    info "$@"
  fi
}
function detail() { echo -e "${COLOR_GREY}$*${COLOR_NORMAL}"; }
function title() { echo -e "${STYLE_BOLD}${STYLE_UNDERLINE}$*${COLOR_NORMAL}"; }
function bold() { echo -e "${STYLE_BOLD}$*${COLOR_NORMAL}"; }
function underline() { echo -e "${STYLE_UNDERLINE}$*${COLOR_NORMAL}"; }
function warn() { echo -e "${STYLE_YELLOW}$*${COLOR_NORMAL}" 1>&2; }
function error() { echo -e "${COLOR_RED}ERROR: $*${COLOR_NORMAL}" 1>&2; }
function fatal() { error "$*"; exit $E_ERROR; }
function invalid_argument() {
  error "$1 [$2]"
  echo ""
  usage
  exit $E_WARG
}

function enable_colors() {
  if test -t 1; then
    # see if it supports colors...
    ncolors=$(tput colors)
    if test -n "$ncolors" && test $ncolors -ge 8; then
      COLOR_NORMAL="$(tput sgr0)"
      COLOR_RED="$(tput setaf 1)"
      COLOR_YELLOW="$(tput setaf 3)"
      COLOR_GREEN="$(tput setaf 2)"
      COLOR_GREY="$(tput setaf 8)"
      STYLE_BOLD="$(tput smso)$(tput bold)"
      STYLE_UNDERLINE="$(tput smul)"
      return
    fi
  fi
}

function extract_arg() {
  local key=$1
  if [[ $key = *"="* ]]; then
    echo "${key#*=}"
    return 1
  elif [[ ${#2} -gt 0 ]]; then
    echo "$2"
    return 2
  else
    echo ""
    return 1
  fi
}

function parse_common_args() {
  local key=$1
  case $key in
    --verbose)
      set_log_level "1"
      return 0
    ;;
    -v | -vv | -vvv | -vvvv)
      local verbose_flags="${key##-}"
      verbose_count="${#verbose_flags}"
      set_log_level "$verbose_count"
      return 0
    ;;
    -h | --help)
      usage
      exit $E_NOERROR
    ;;
    *)
      return 1
    ;;
  esac
}

function set_log_level() {
  verbose_count="${1:?}"
  if is_diag_enabled; then
    set -x
  fi
}

enable_colors