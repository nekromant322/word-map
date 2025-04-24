FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf

COPY ./js /usr/share/nginx/js

COPY ./pages /usr/share/nginx/pages

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]