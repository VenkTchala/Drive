FROM ubuntu:latest
LABEL authors="siva"

ENTRYPOINT ["top", "-b"]