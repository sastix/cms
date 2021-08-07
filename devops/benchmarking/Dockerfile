FROM ubuntu:20.04

WORKDIR /benchmarking

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y git build-essential make unzip vim

RUN git clone https://github.com/wg/wrk.git

WORKDIR /benchmarking/wrk

RUN make