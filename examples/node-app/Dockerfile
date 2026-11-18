FROM node:25-alpine@sha256:bdf2cca6fe3dabd014ea60163eca3f0f7015fbd5c7ee1b0e9ccb4ced6eb02ef4
WORKDIR /app
COPY package*.json /app/
RUN npm ci --omit=dev
COPY src /app/src
USER 10001:10001
CMD ["node", "src/index.js"]
