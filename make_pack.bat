@echo off
SET PRODUCT_FLAVORS=%1

echo PRODUCT_FLAVORS = %1:1



gradlew --no-daemon clean assembleRelease

echo assemnle end

copy /Y app/build/outputs/apk/*.apk artifacts
copy /Y app/build/outputs/mapping/release/mapping.txt artifacts