FROM node:lts-alpine

WORKDIR /project

RUN apk update && \
    apk add --no-cache build-base python3 libtool autoconf automake && \
    chown node:node /project

USER node

COPY --chown=node:node . .

RUN npm install

CMD [ "npm","run","dev" ]