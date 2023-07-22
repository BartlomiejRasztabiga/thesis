#!/bin/sh

# This file is how Fly starts the server (configured in fly.toml).
# Learn more: https://community.fly.io/t/sqlite-not-getting-setup-properly/4386

set -ex
npm run start
