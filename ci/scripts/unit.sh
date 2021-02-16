#!/bin/bash -eux

cwd=$(pwd)

pushd $cwd/dp-logging
  make test
popd