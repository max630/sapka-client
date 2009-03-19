
SRCS= Client.java GameIO.java Init.java Main.java Point.java

CLASSES=$(SRCS:%.java=%.class)

all: classes

TMP_DIR=build
contest.zip:
	rm -rf build/
	mkdir -p $(TMP_DIR)/contest
	echo "max630" >$(TMP_DIR)/contest/team
	echo "Maxim Kirillov <max630@gmail.com>" >$(TMP_DIR)/contest/contact
	mkdir -p $(TMP_DIR)/contest/bin
	javac -d $(TMP_DIR)/contest/bin $(SRCS)
	install -m 755 run $(TMP_DIR)/contest/bin
	mkdir -p $(TMP_DIR)/contest/src
	install -m 644 $(SRCS) Makefile $(TMP_DIR)/contest/src
	rm -f $@
	(cd $(TMP_DIR) && zip -r - contest) >$@

classes: $(SRCS)
	javac $^
