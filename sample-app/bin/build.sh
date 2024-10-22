#!/usr/bin/env bash

source "$DEVBOX_PROJECT_ROOT"/bin/common.sh

function usage() {
    echo "Runs the build"
    echo "Usage: $0 [OPTIONS]"
    printf "    %-30s enables verbose logging (-vvv for more, -vvvv to enable script debugging)\n" "-v, --verbose"
    printf "    %-30s display this help message\n" "-h, --help"
}

function parse_args() {
  while [[ $# -gt 0 ]]; do
    local shift_count=1
    local key="$1"
    if ! parse_common_args "$key"; then
      case $key in
        *)
          invalid_argument "unsupported input parameter" "$key"
        ;;
      esac
    fi
    shift $shift_count
  done
}

function run() {
  parse_args "$@"

  title "\n==> Starting android build\n"

  local build_args=( "--no-daemon" ":release" )
  if is_verbose_enabled; then
    build_args+=( "--console=plain" )
  fi
  if is_trace_enabled; then
    build_args+=( "--debug" "--full-stacktrace" )
  elif is_debug_enabled; then
    build_args+=( "--info" "--stacktrace" )
  fi
  "$DEVBOX_PROJECT_ROOT"/gradlew "${build_args[@]}" || fatal "Failed to build"
}

run "$@"