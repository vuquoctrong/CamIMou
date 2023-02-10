#!/bin/bash
# version V1.0.0
# date 2022.03.22
# author 32722

######### Functions #########
# 日志打印
log() {
  echo "[$1] - $2"
  if [ $# -gt 2 ]; then
    echo ""
  fi
}

# Step 1. 编译命令权限调整
if [ -f gradlew ]; then
  chmod 755 gradlew
fi

# Step 2. 移除local.properties
rm -rf local.properties

# Step 3. gradle路径变更
if [ -f gradle/wrapper/gradle-wrapper.properties ]; then
  fromdos gradle/wrapper/gradle-wrapper.properties
  # 从配置文件读取 distributionUrl 配置
  distributionUrl=$(grep -m 1 'distributionUrl' gradle/wrapper/gradle-wrapper.properties | head -n1 | tr '\r' ' ' | awk '$1=$1')
  # 读取文件名
  gradleVersion=${distributionUrl##*/}
  log "INFO" "build with $gradleVersion"
  # 判断文件是否存在
  #set -ex
  if [ ! -f /opt/gradle/${gradleVersion} ]; then
    log "ERROR" "not found /opt/gradle/$gradleVersion"
    echo 'Please Ask the Megrez!!!'
    exit 1
  else
    # 替换文件内容
    sed -i "s#^distributionUrl=.*#distributionUrl=file\:/opt/gradle/${gradleVersion}#g" gradle/wrapper/gradle-wrapper.properties
  fi
fi

# Step 4. build.gradle maven路径修订
if [ -f build.gradle ]; then
  #set -ex
  fromdos build.gradle
  sed -i 's#url "C:.*#url "http://maven.aliyun.com/repository/google/"#g' build.gradle
  sed -i 's#url"B:.*#url "http://maven.aliyun.com/repository/google/"#g' build.gradle
  sed -i 's#url"D:.*#url "http://maven.aliyun.com/repository/google/"#g' build.gradle
fi

# Step 5. gradle.properties -Xmx属性
if [ -f gradle.properties ]; then
  fromdos gradle.properties
  sed -i 's/org.gradle.jvmargs=-Xmx3072m/org.gradle.jvmargs=-Xmx4096m/g' gradle.properties
  sed -i 's/org.gradle.jvmargs=-Xmx2048m/org.gradle.jvmargs=-Xmx4096m/g' gradle.properties
fi
