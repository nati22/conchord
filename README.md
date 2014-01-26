conchord
========

An app that allows for multiple devices to jam together!



UPDATE: ...or so I thought. I've come to a place where I'm unable to move forward with conchord in the way I'd like. The idea of the app is to have multiple devices play the different parts of a song (vocals, percussion, etc) at the same time, obviously.

However, I've been able to unsatisfactorily synchronize 2 (or more) devices to play a single note closely enough in time so that the human ear can't really tell. Although I was using a common NTP server to help synchronize the varying system times on different devices, the fact that I'm unable to predict the request/response latency time makes the NTP timestamps almost worthless by the time they get to each device. I've brought up the issue in a stackexchange post here: http://goo.gl/77Bif3. I'm going to move on to a different project for now as I look around for a high-speed (low latency) means of device-to-device communication. 