# switchman-togglz-client
[![Build Status](https://api.travis-ci.org/ImmobilienScout24/switchman-togglz-client.svg?branch=master)](https://travis-ci.org/ImmobilienScout24/switchman-togglz-client)

IS24-switchman togglz client that can be used to merge local feature state with remote feature state.

## What is this for?
If you have a lot of microservices and you want to be able to switch on a feature not on a service but on your production
stage. This might be better than enabling the feature in all microservices or even worse send the feature switch state in your internal
APIs.

This client uses the Togglz framework and enhances this with a remote feature repository stored in IS24-Switchman service.

If this is configured correctly, it works like this:
- You define feature switches in an enum locally
- Via name, this client merges the feature state with the remote state from IS24 Switchman
- You still can have a local Togglz console. If you change the feature state there, a cookie overriding remote state
will be written (for local development use)

## HowTo configure
...TODO