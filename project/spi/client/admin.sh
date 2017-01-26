#@echo off
java -cp .:bin:files/VTJBean.jar:files/ssce.jar:files/jdic.jar:files/bsh-2.0b4.jar:files/jalopy.jar:files/log4j-1.2.13.jar -enableassertions client.DiaClient "--admin"
