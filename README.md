# A Custom_HTTP_Server_in_Java

Developers rarely, if ever, build HTTP servers from scratch. Applications use FrameWorks designed to handle HTTP requests in an abstracted and efficient way.<br>

But I am going to implement one<br>
Reference Docs
https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Overview

Targets

1. Make a simplex Version [basics of Network Programming, Buffers and File Management]<br>

[Raw TCP Stream]<br>
↓<br>
Read until you find `\r\n\r\n` → Header section ends<br>
↓<br>
Split → [Start-Line + Headers] and [Optional Body]<br>
↓<br>
Parse Start-Line → Method, Path, Version<br>
↓<br>
Parse Headers → Key: Value map<br>
↓<br>
If body exists → use Content-Length to extract it<br>

2. Introduce Multithreading to serve more clients

3. File Serving [MIME]

4. Using OpenSSL for security
