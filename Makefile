# Makefile for RHash, remote object server stuff.

JAVAHOME=/usr/java
JAR=$(JAVAHOME)/bin/jar
# JAVAC=$(JAVAHOME)/bin/javac
JAVAC=jikes +P
JAVA=$(JAVAHOME)/bin/java
RMIC=$(JAVAHOME)/bin/rmic
MYLIB=$(HOME)/lib/java
S=/System/Library/Frameworks/JavaVM.framework/Versions/1.3/Classes/classes.jar
C1=$(MYLIB)/jsdk.jar:$(MYLIB)/spy.jar:$(MYLIB)/postgresql.jar:$(MYLIB)/cos.jar
C2=$(MYLIB)/resin-xsl.jar:$(MYLIB)/sax.jar:$(MYLIB)/dom.jar
C3=$(MYLIB)/xerces.jar:$(MYLIB)/xalan.jar:$S
CLASSPATH=$(C1):$(C2):$(C3):.
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
	net/spy/photo/PhotoSession.class \
	net/spy/photo/PhotoSearchResult.class \
	net/spy/photo/PhotoSearchResults.class \
	net/spy/photo/PhotoReporting.class \
	net/spy/photo/PhotoAdmin.class \
	net/spy/photo/PhotoAheadFetcher.class \
	net/spy/photo/PhotoXSLT.class \
	net/spy/photo/PhotoXML.class \
	net/spy/photo/SetPW.class \
	net/spy/photo/StopWatch.class \
	net/spy/photo/util/BackupEntry.class \
	net/spy/photo/util/AlbumBackupEntry.class \
	net/spy/photo/util/PhotoBackup.class \
	net/spy/photo/util/PhotoRestore.class \
	net/spy/photo/util/PhotoCleanup.class \
	net/spy/photo/util/PhotoStorerThread.class \
	net/spy/photo/tools/CachePhoto.class \
	net/spy/photo/migration/PhotoMigration.class \
	net/spy/photo/migration/PhotoMigration01.class \
	$(RCLASSES)

.SUFFIXES: .java .class .jar

all: $(CLASSES)

photo.jar: $(CLASSES)
	$(JAR) cv0f $@ `find net/spy -name "*.class"`

test: $(CLASSES)
	env CLASSPATH=$(CLASSPATH) $(SERVLETRUNNER) -d $(PWD)

setpw: net/spy/photo/SetPW.class
	env CLASSPATH=$(CLASSPATH) $(JAVA) net.spy.photo.SetPW

release: photo.jar release.tgz

release.tgz:
	mkdir release
	cp photo.jar release
	cp $(HOME)/lib/java/spy.jar release
	cp -r etc doc release
	find release -name CVS -type d | xargs rm -rf
	tar cvf - release | gzip -9vc > release.tgz
	rm -rf release

dist: release
	mv release.tgz photoservlet-`date +%Y%m%d`.tgz

clean:
	rm -f photo.jar `find net/spy -name "*.class"`

# How to do RMI stuff.
net/spy/rmi/ImageServerImpl_Stub.class: net/spy/rmi/ImageServerImpl_Skel.class

net/spy/rmi/ImageServerImpl_Skel.class: net/spy/rmi/ImageServer.class \
		net/spy/rmi/ImageServerImpl.class
	env CLASSPATH=$(CLASSPATH) $(RMIC) -d . net.spy.rmi.ImageServerImpl


.java.class:
	env CLASSPATH=$(CLASSPATH) $(JAVAC) -deprecation $<
