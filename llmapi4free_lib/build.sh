#!/bin/bash

mvn clean compile assembly:single
if [ $? -ne 0 ]; then
  echo "Ошибка при выполнении mvn clean compile assembly:single"
  exit 1
fi
