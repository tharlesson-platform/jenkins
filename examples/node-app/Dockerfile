FROM node:26-alpine
WORKDIR /app
COPY package*.json /app/
RUN npm ci --omit=dev
COPY src /app/src
CMD ["node", "src/index.js"]
