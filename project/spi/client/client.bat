@echo off
java -cp .;bin;files/VTJBean.jar;files\ssce.jar;files\jdic.jar;files\bsh-2.0b4.jar;files\poi-3.0-FINAL.jar -enableassertions -XX:MaxPermSize=512m -XX:+CMSPermGenSweepingEnabled -XX:+CMSClassUnloadingEnabled client.DiaClient
