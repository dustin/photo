# Makefile for RHash, remote object server stuff.

JAVAHOME=/usr/pkg/java
JAR=$(JAVAHOME)/bin/jar
JAVAC=$(JAVAHOME)/bin/javac
JAVA=$(JAVAHOME)/bin/java
RMIC=$(JAVAHOME)/bin/rmic
MYLIB=/home/dustin/lib/java
C1=$(MYLIB)/jsdk.jar:$(MYLIB)/DBCB.jar
C2=$(MYLIB)/spy.jar:$(MYLIB)/postgresql.jar:$(MYLIB)/cos.jar
CLASSPATH=$(C1):$(C2):.
SERVLETRUNNER=/home/dustin/lib/java/JSDK2.0/bin/servletrunner

RCLASSES=net/spy/rmi/ImageServerImpl_Skel.class \
	net/spy/rmi/ImageServerImpl_Stub.class \
	net/spy/rmi/ImageServerImpl.class \
	net/spy/rmi/ImageServer.class

CLASSES=net/spy/photo/PhotoServlet.class net/spy/photo/PhotoHelper.class \
	net/spy/photo/PhotoUtil.class \
	net/spy/photo/PhotoImageHelper.class \
	net/spy/photo/PhotoImage.class \
	net/spy/photo/PhotoLogFlusher.class \
	net/spy/photo/PhotoLogView.class \
	net/spy/photo/PhotoLogImageEntry.class \
	net/spy/photo/PhotoUser.class \
	net/spy/photo/PhotoSecurity.class \
	net/spy/photo/PhotoConfig.class \
	net/spy/photo/PhotoSearch.class \
	net/spy/photo/PhotoStorerThread.class \
	net/spy/photo/PhotoSession.class \
	net/spy/photo/PhotoSearchResult.class \
	net/spy/photo/PhotoSearchResults.class \
	net/spy/photo/PhotoReporting.class \
	net/spy/photo/PhotoAdmin.class \
	net/spy/photo/SetPW.class \
	$(RCLASSES)

.SUFFIXES: .java .class .jar

all: photo.jar

photo.jar: $(CLASSES)
	$(JAR) cv0f $@ $(CLASSES)

test: all
	env CLASSPATH=$(CLASSPATH) $(SERVLETRUNNER) -d $(PWD)

setpw: net/spy/photo/SetPW.class
	env CLASSPATH=$(CLASSPATH) $(JAVA) net.spy.photo.SetPW

release: all release.tgz

release.tgz:
	mkdir release
	cp photo.jar release
	cp -r etc doc release
	find release -name CVS -type d | xargs rm -rf
	tar cvf - release | gzip -9vc > release.tgz
	rm -rf release

dist: release
	mv release.tgz photoservlet-`date +%Y%m%d`.tgz

clean:
	rm -f $(CLASSES) photo.jar

# How to do RMI stuff.
net/spy/rmi/ImageServerImpl_Stub.class: net/spy/rmi/ImageServerImpl_Skel.class

net/spy/rmi/ImageServerImpl_Skel.class: net/spy/rmi/ImageServer.class
	env CLASSPATH=$(CLASSPATH) $(RMIC) -d . net.spy.rmi.ImageServerImpl


.java.class:
	env CLASSPATH=$(CLASSPATH) $(JAVAC) $<
