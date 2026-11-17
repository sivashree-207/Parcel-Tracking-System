#!/bin/sh
mkdir -p out
javac -d out src/ParcelTrackingSystem.java && java -cp out ParcelTrackingSystem
