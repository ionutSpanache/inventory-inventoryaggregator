#!/bin/bash
set +e -o pipefail

PROG=${0##*/}
script_dir="$(cd "${0%/*}" && pwd)"

function main() {
  set_skaffold_environment
  case "$1" in
    "build")
      set_skaffold_options "${@:2}"
      skaffold_build
      ;;
    "deploy")
      set_skaffold_options "${@:2}"
      skaffold_deploy
      ;;
    "login")
      set_docker_options "${@:2}"
      docker_login
      ;;
    "push")
      set_skaffold_options "${@:2}"
      skaffold_push
      ;;
    "render")
      set_skaffold_options "${@:2}"
      skaffold_render
      ;;
    "test")
      set_skaffold_options "${@:2}"
      skaffold_test
      ;;
    *)
      help
      exit 1
      ;;

  esac
}

function help() {
  cat<<EOM
USAGE:
  ./${PROG} [ build | deploy | login | push | test ]

COMMANDS:
  build         build container image
  deploy        deploy a skaffold artifact
                flags:
                --default-repo  skaffold default repo
                --label         skaffold label
                --namespace     k8s namespace to deploy
  login         login to docker registry
                flags:
                --username
                --password
                --server
  push          push container image to repository
                flags:
                --default-repo  skaffold default repo
  render        renders the deployment templates
                flags:
                --default-repo   skaffold default repo
                --label          skaffold label
                --digest-source  to resolve the digest of the container images
                --profile        skaffold profile
                --output         filepath to store the rendered output
  test         execute tests
                flags:
                --profile       skaffold profile

CONFIGURED SKAFFOLD OPTIONS:
$(env | grep SKAFFOLD | indent)
EOM
}

indent() { sed 's/^/  /'; }

function set_skaffold_environment() {
  set -a
  source "$script_dir/pipeline.properties"
  set +a
  if [ "$CI" != "true" ]; then
    unset SKAFFOLD_DEFAULT_REPO
  fi
}

function set_skaffold_options() {
  while test $# -gt 0; do
    case $1 in
      --default-repo)
        shift
        export SKAFFOLD_DEFAULT_REPO=$1
        shift
        ;;
      --profile)
        shift
        export SKAFFOLD_PROFILE=$1
        shift
        ;;
      --label)
        shift
        export SKAFFOLD_LABEL=$1
        shift
        ;;
      --digest-source)
        shift
        export SKAFFOLD_DIGEST_SOURCE=$1
        shift
        ;;
      --output)
        shift
        export SKAFFOLD_OUTPUT=$1
        shift
        ;;
      --namespace)
        shift
        export SKAFFOLD_NAMESPACE=$1
        shift
        ;;
      *)
        shift
        ;;
    esac
  done
}

function set_docker_options() {
  while test $# -gt 0; do
    case $1 in
      --username)
        shift
        DOCKER_USERNAME=$1
        shift
        ;;
      --password)
        shift
        DOCKER_PASSWORD=$1
        shift
        ;;
      --server)
        shift
        DOCKER_SERVER=$1
        shift
        ;;
      *)
        shift
        ;;
    esac
  done
}

function skaffold_test() {
  error_msg=$(skaffold test -a <(skaffold build -q --dry-run) 3>&1 1>&2 2>&3 | tee /dev/stderr)
  exit_code=$?
  if [ $exit_code -ne 0 ]; then
    echo "::error ::WaaS: test: skaffold test failed"
    echo "::error ::$error_msg"
    exit $exit_code
  fi
}

function skaffold_render() {
  error_msg=$(skaffold render 3>&1 1>&2 2>&3 | tee /dev/stderr)
  exit_code=$?
  if [ $exit_code -ne 0 ]; then
    echo "::error ::WaaS: render: skaffold render failed"
    echo "::error ::$error_msg"
    exit $exit_code
  fi
}

function skaffold_deploy() {
  if [ -z "$SKAFFOLD_DEFAULT_REPO" ];
  then
    error_msg='Please provide --default-repo with deploy command'
    exit_code=1
  else
    error_msg=$(skaffold deploy -a <(skaffold build -q --dry-run) 3>&1 1>&2 2>&3 | tee /dev/stderr)
    exit_code=$?
  fi

  if [ $exit_code -ne 0 ]; then
    echo "::error ::WaaS: deploy: skaffold deploy failed"
    echo "::error ::$error_msg"
    exit $exit_code
  fi
}

function skaffold_build() {
  error_msg=$(skaffold build 3>&1 1>&2 2>&3 | tee /dev/stderr)
  exit_code=$?
  if [ $exit_code -ne 0 ]; then
    echo "::error ::WaaS: build: skaffold, building container image failed"
    echo "::error ::$error_msg"
    exit $exit_code
  fi
}

function skaffold_push() {
  if [ -z "$SKAFFOLD_DEFAULT_REPO" ];
  then
    error_msg='Please provide --default-repo with push command'
    exit_code=1
  else
    error_msg=$(skaffold build --profile push 3>&1 1>&2 2>&3 | tee /dev/stderr)
    exit_code=$?
  fi

  if [ $exit_code -ne 0 ]; then
    echo "::error ::WaaS: push: skaffold, pushing container image failed"
    echo "::error ::$error_msg"
    exit $exit_code
  fi
}

function docker_login() {
  echo "Login to docker registry $DOCKER_SERVER"
  echo -n "$DOCKER_PASSWORD" \
  | docker login "$DOCKER_SERVER" --username "$DOCKER_USERNAME" --password-stdin
  exit_code=$?
  if [ $exit_code -ne 0 ]; then
    echo "::error ::WaaS: build: login to docker registry failed"
    exit $exit_code
  fi
}

main "$@"