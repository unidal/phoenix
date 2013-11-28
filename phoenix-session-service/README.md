# Protocol

    <body-len-in-integer, tabbed-body-in-utf-8>

# Client => Server

    <guid, sha1(url), sha1(refer-url), request-id, 0>


# Server => Server

    <guid, sha1(url), sha1(refer-url), request-id, 1>


# Server => File

    <request-id, refer-request-id>
