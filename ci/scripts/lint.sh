#!/bin/bash -eux

cwd=$(pwd)

pushd $cwd/dp-logging
  make lint
popd