# Makefile for RHash, remote object server stuff.

JAVAHOME=/usr/java
JAR=$(JAVAHOME)/bin/jar
# JAVAC=$(JAVAHOME)/bin/javac
JAVAC=jikes +P
JAVA=$(JAVAHOME)/bin/java
RMIC=$(JAVAHOME)/bin/rmic
MYLIB=$(HOME)/lib/java
DISTLIB=photo/WEB-INF/lib
SD=/System/Library/Frameworks/JavaVM.framework/Classes
S=$(SD)/classes.jar:$(SD)/ui.jar
C1=$(MYLIB)/jsdk.jar:$(DISTLIB)/spy.jar:$(DISTLIB)/postgresql.jar
C2=$(DISTLIB)/cos.jar:$(MYLIB)/resin.jar:$(MYLIB)/sax.jar:$(MYLIB)/dom.jar
C3=$(MYLIB)/xerces.jar:$(MYLIB)/xalan.jar:$S
C4=$(DISTLIB)/struts.jar
CLASSPATH=$(C1):$(C2):$(C3):$(C4):.
SERVLETRUNNER=$(MYLIB)/resin/bin/start_resin

RCLASSES=net/spy/rmi/ImageServerImpl_Skel.class \
	net/spy/rmi/ImageServerImpl_Stub.class \
	net/spy/rmi/ImageServerImpl.class \
	net/spy/rmi/ImageServerScaler.class \
	net/spy/photo/ImageWatcher.class \
	net/spy/photo/JpegEncoder.class \
	net/spy/rmi/JavaImageServerScaler.class \
	net/spy/rmi/ExternalImageServerScaler.class \
	net/spy/rmi/ImageServer.class

CLASSES=\
	net/spy/photo/sp/FindImagesByComments.java \
	net/spy/photo/sp/FindImagesByComments.class \
	net/spy/photo/PhotoException.class \
	net/spy/photo/XMLAble.class \
	net/spy/photo/Persistent.class \
	net/spy/photo/PhotoServlet.class \
	net/spy/photo/PhotoHelper.class \
	net/spy/photo/PhotoUtil.class \
	net/spy/photo/PhotoDimensions.class \
	net/spy/photo/PhotoDimensionsImpl.class \
	net/spy/photo/PhotoDimScaler.class \
	net/spy/photo/PhotoImageHelper.class \
	net/spy/photo/PhotoImage.class \
	net/spy/photo/PhotoLogFlusher.class \
	net/spy/photo/PhotoLogView.class \
	net/spy/photo/PhotoLogEntry.class \
	net/spy/photo/PhotoLogFuncEntry.class \
	net/spy/photo/PhotoLogUploadEntry.class \
	net/spy/photo/PhotoLogImageEntry.class \
	net/spy/photo/PhotoACLEntry.class \
	net/spy/photo/PhotoUser.class \
	net/spy/photo/Profile.class \
	net/spy/photo/PhotoSecurity.class \
	net/spy/photo/PhotoConfig.class \
	net/spy/photo/SavedSearch.class \
	net/spy/photo/PhotoSearch.class \
	net/spy/photo/PhotoSearch2.class \
	net/spy/photo/Comment.class \
	net/spy/photo/Vote.class \
	net/spy/photo/Category.class \
	net/spy/photo/PhotoSessionData.class \
	net/spy/photo/Mailer.class \
	net/spy/photo/PhotoImageObserver.class \
	net/spy/photo/PhotoImageScaler.class \
	net/spy/photo/PhotoSession.class \
	net/spy/photo/Cursor.class \
	net/spy/photo/PhotoImageData.class \
	net/spy/photo/PhotoSearchResult.class \
	net/spy/photo/PhotoSearchResults.class \
	net/spy/photo/PhotoReporting.class \
	net/spy/photo/PhotoAdmin.class \
	net/spy/photo/PhotoAheadFetcher.class \
	net/spy/photo/PhotoXSLT.class \
	net/spy/photo/xslt/ResinXSLT.class \
	net/spy/photo/xslt/ApacheXSLT.class \
	net/spy/photo/PhotoXML.class \
	net/spy/photo/SetPW.class \
	net/spy/photo/StopWatch.class \
	net/spy/photo/taglib/PhotoTag.class \
	net/spy/photo/taglib/GuestCheck.class \
	net/spy/photo/taglib/ImageLink.class \
	net/spy/photo/taglib/DisplayLink.class \
	net/spy/photo/taglib/InitSessionData.class \
	net/spy/photo/taglib/InitSessionDataExtraInfo.class \
	net/spy/photo/taglib/CategoryTag.class \
	net/spy/photo/taglib/CategoryTagExtraInfo.class \
	net/spy/photo/taglib/ImageDataTag.class \
	net/spy/photo/taglib/ImageDataTagExtraInfo.class \
	net/spy/photo/taglib/MetaInfo.class \
	net/spy/photo/struts/PhotoAction.class \
	net/spy/photo/struts/LoginForm.class \
	net/spy/photo/struts/LoginAction.class \
	net/spy/photo/struts/SearchForm.class \
	net/spy/photo/struts/SearchAction.class \
	net/spy/photo/util/BackupEntry.class \
	net/spy/photo/util/AlbumBackupEntry.class \
	net/spy/photo/util/PhotoBackup.class \
	net/spy/photo/util/PhotoRestore.class \
	net/spy/photo/util/PhotoCleanup.class \
	net/spy/photo/util/PhotoStorerThread.class \
	net/spy/photo/tools/CachePhoto.class \
	net/spy/photo/migration/PhotoMigration.class \
	net/spy/photo/migration/PhotoMigration01.class \
	net/spy/photo/migration/PhotoMigration02.class \
	net/spy/photo/migration/PhotoMigration03.class \
	net/spy/photo/migration/PhotoMigration04.class \
	net/spy/photo/migration/PhotoMigration05.class \
	$(RCLASSES)

.SUFFIXES: .spt .java .class .jar

.PHONY: test

photo.jar: $(CLASSES) net/spy/photo/photoresources.properties
	$(JAR) cvf $@ `find net/spy -name "*.class"` \
		`find net/spy -name "*.sql"` \
		`find net/spy -name "*.properties"`

all: $(CLASSES)

test: $(CLASSES)
	env CLASSPATH=/tmp $(SERVLETRUNNER) $(PWD)/etc/resin.conf

srtest: $(CLASSES)
	env CLASSPATH=$(CLASSPATH) $(JAVA) net.spy.SpyRunner etc/spyrunner.conf

setpw: net/spy/photo/SetPW.class
	env CLASSPATH=$(CLASSPATH) $(JAVA) net.spy.photo.SetPW

install: photo.jar
	cp photo.jar /afs/.spy.net/misc/web/root/photo/WEB-INF/lib

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

.spt.java: $<
	env CLASSPATH=$(CLASSPATH) $(JAVA) net.spy.util.SPGen $<
