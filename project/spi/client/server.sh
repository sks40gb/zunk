# @echo off
java -cp bin:files/mysql-connector-java-3.1.13-bin.jar:files/ssce.jar:ssce -enableassertions -Xmx128m server.DiaServers
