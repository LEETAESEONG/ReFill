FROM nginx

WORKDIR /app

RUN mkdir ./build

ADD ./build ./build

run rm /etc/nginx/conf.d/default.conf

COPY ./nginx.conf /etc/nginx/conf.d

EXPOSE 3000

CMD [ "nginx", "-g", "daemon off;" ]