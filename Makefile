.PHONY: all
all: clean build audit test

.PHONY: clean
clean:
	mvn clean
	rm -rf ./target

.PHONY: build
build:
	mvn -Dmaven.test.skip -Dossindex.skip=true clean package dependency:copy-dependencies

.PHONY: test
test:
    mvn -Dossindex.skip=true test

.PHONY: audit
audit:
	mvn install -Dmaven.test.skip -Dossindex.skip=true
    mvn ossindex:audit
