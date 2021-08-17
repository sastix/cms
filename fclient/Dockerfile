FROM ubuntu:20.04 AS building-container

# Install dependencies without prompts for input

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update

RUN apt-get install -y curl \
                       git \
                       wget \
                       unzip \
                       libgconf-2-4 \
                       gdb \
                       libstdc++6 \
                       libglu1-mesa \
                       fonts-droid-fallback \
                       lib32stdc++6 \
                       python3

# Build the flutter tool

RUN  git clone https://github.com/flutter/flutter.git /usr/local/flutter -b stable

ENV PATH="/usr/local/flutter/bin:/usr/local/flutter/bin/cache/dart-sdk/bin:${PATH}"

RUN flutter upgrade

RUN flutter doctor -v

# Build the Flutter application

RUN mkdir /app/

COPY . /app/

WORKDIR /app/

RUN flutter pub get

RUN flutter build web

# Build final image with NGINX

FROM nginx:alpine

RUN apk add certbot certbot-nginx

COPY --from=building-container /app/build/web /usr/share/nginx/html
