#!/usr/bin/env bash

function run() {
  if [[ "${DEVBOX_SHELL_ENABLED:-0}" != "1" ]]; then
    devbox run build "$@"
    return $?
  fi

  $DEVBOX_PROJECT_ROOT/bin/build.sh "$@"
}

run "$@"
