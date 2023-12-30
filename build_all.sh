#!/usr/bin/env sh
(cd ./delivery/ || exit ; sh ./gradlew build)
(cd ./e2e/ || exit ; sh ./gradlew build)
(cd ./order/ || exit ; sh ./gradlew build)
(cd ./payment/ || exit ; sh ./gradlew build)
(cd ./query/ || exit ; sh ./gradlew build)
(cd ./restaurant/ || exit ; sh ./gradlew build)
(cd ./saga/ || exit ; sh ./gradlew build)
(cd ./shared/ || exit ; sh ./gradlew build)
