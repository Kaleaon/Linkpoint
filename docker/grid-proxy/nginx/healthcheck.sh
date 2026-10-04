#!/bin/sh
curl -s -f http://localhost/health >/dev/null || exit 1
curl -s -k -f https://localhost/health >/dev/null || exit 1
