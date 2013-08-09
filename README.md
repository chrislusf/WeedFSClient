WeedFSClient
============

This project is java client library for the Weed-FS REST interface.

# What is Weed-FS?

[Weed-FS](http://code.google.com/p/weed-fs/) is a simple and highly scalable distributed file system. It focuses on two objectives:
* storing billions of files!
* and serving them fast!

Weed-FS chose to implement only a key~file mapping instead of supporting full POSIX file system semantics. This can be called "NoFS". (Similar to "NoSQL")

Instead of managing all file metadata in a central master, Weed-FS manages file volumes in the central master, and allows volume servers
to manage files and the metadata. This relieves concurrency pressure from the central master and spreads file metadata into memory on the volume servers
allowing faster file access with just one disk read operation!

Weed-FS models after [Facebook's Haystack design paper](http://static.usenix.org/event/osdi10/tech/full_papers/Beaver.pdf) and costs only 40 bytes disk storage for each file's metadata. It is so simple with O(1) disk read that anyone is more than welcome to challenge the
performance with actual use cases.

