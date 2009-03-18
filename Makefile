
SRCS= Client.java GameIO.java Init.java Main.java Point.java
# DNA.java - disabled

CLASSES=$(SRCS:%.java=%.class)

all: $(CLASSES)

$(CLASSES): $(SRCS)
	javac $^
