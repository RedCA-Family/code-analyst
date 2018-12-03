#!/bin/sh
openssl enc -d -in secretkey.gpg.enc -out secretkey.gpg -aes256 -pass "pass:$SECRET_KEY_DEC_KEY"