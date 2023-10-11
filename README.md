# SSH Tunnel Testing

[![license](https://img.shields.io/github/license/interlok-testing/testing_sshtunnel.svg)](https://github.com/interlok-testing/testing_sshtunnel/blob/develop/LICENSE)
[![Actions Status](https://github.com/interlok-testing/testing_sshtunnel/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/interlok-testing/testing_sshtunnel/actions/workflows/gradle-build.yml)

Uses a ssh tunnel to expose the MariaDB instance on _agbqhsbldd013v.agb.rbxd.ds (10.52.104.139)_

## Pre-Requisites

- VPN Access

## Configuration

- The configuration isn't secure, as credentials are checked in! (lame I know)
- Uses a JdbcDataQueryService to attach to the local mariadb instance as 'jenkins'.
    - Essentially does `select * from interlok_jenkins.liverpool_transfers` and exposes that as JSON_LINES
- Uses the mariadb jdbc driver... (which is why the URL is different)

## Config/Testing


```
$ ./gradlew clean build
$ (cd build/distribution && java -jar lib/interlok-boot.jar)
...
TRACE [SimpleBootstrap] [c.a.m.s.Tunnel._connect()] [{}] Trying to connect to agbqhsbldd013v.agb.rbxd.ds:22 as jenkins
TRACE [SimpleBootstrap] [c.a.m.s.Tunnel._connect()] [{}] Connected to agbqhsbldd013v.agb.rbxd.ds:22 as jenkins
TRACE [SimpleBootstrap] [c.a.m.s.Tunnel._start()] [{}] Creating Tunnel : localPort 3306 -> remote 3306
...

$ curl -si -XGET http://localhost:8080/api/liverpool_transfers
HTTP/1.1 200 OK
Content-Type: application/json
Transfer-Encoding: chunked

{"id":1,"type":"a","player":"b","club":"c","amount":1,"manager":"d","date":946684800000}
...
{"id":31,"type":"Sold","player":"Oyvind Leonhardsen","club":"Tottenham Hotspur F.C.","amount":2800000,"manager":"Gerard Houllier","date":933462000000}

```

- Disconnect from the VPN, and you should the auto-retry behaviour kick in (since you have to be on the VPN) at some point (after 60 seconds)
    - You will have to re-connect before it's successful of course.

```
WARN  [SshTunnelComponent-8-1] [c.a.m.s.Tunnel.run()] [{}] Tunnel is/was disconnected; attempting restart
TRACE [SshTunnelComponent-8-1] [c.a.m.s.Tunnel._connect()] [{}] Trying to connect to agbqhsbldd013v.agb.rbxd.ds:22 as jenkins
TRACE [SshTunnelComponent-8-1] [c.a.m.s.Tunnel._connect()] [{}] Connected to agbqhsbldd013v.agb.rbxd.ds:22 as jenkins
TRACE [SshTunnelComponent-8-1] [c.a.m.s.Tunnel._start()] [{}] Creating Tunnel : localPort 3306 -> remote 3306
```