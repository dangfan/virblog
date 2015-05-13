#!/usr/bin/env python

import csv
import sys
import socket
import psycopg2
from decimal import Decimal

if len(sys.argv) != 2:
    print '%s filename' % sys.argv[0]
    sys.exit(1)

filename = sys.argv[1]
ipv4_mode = True
conn = psycopg2.connect('dbname=yourdb user=youruser')
cur = conn.cursor()
cur.execute('DROP TABLE IF EXISTS "IP2COUNTRIES";')
cur.execute('CREATE TABLE "IP2COUNTRIES" ("IP" NUMERIC PRIMARY KEY, "COUNTRY" CHAR(2) NOT NULL);')

with open(filename, 'rb') as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    for row in reader:
        ip_end = 0
        if ipv4_mode:
            try:
                ip_end = Decimal(long(socket.inet_aton(row[1]).encode('hex'), 16))
            except socket.error:
                ipv4_mode = False
        if not ipv4_mode:
            ip_end = Decimal(long(socket.inet_pton(socket.AF_INET6, row[1]).encode('hex'), 16))
        country = row[2].lower()
        cur.execute('INSERT INTO "IP2COUNTRIES" ("IP", "COUNTRY") VALUES (%s, %s);',
                    (ip_end, country))

conn.commit()
cur.close()
conn.close()