FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD password
ENV MYSQL_DATABASE mt_library
ENV MYSQL_USER testuser
ENV MYSQL_PASSWORD testpass

COPY ./config/my.conf /etc/mysql/conf.d/my.cnf