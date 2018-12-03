FROM hypriot/rpi-node
RUN apt-get update
RUN apt-get install pigpio
RUN npm install johnny-five
RUN npm install raspi-io
COPY iotte.js .
CMD ["node iotte.js"]
