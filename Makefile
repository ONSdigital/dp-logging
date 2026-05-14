OSSINDEX_ERRORS = "Unable to contact OSS Index|authentication failed|401 Unauthorized|403 Forbidden|429 Too Many Requests|Too many requests|Rate limit|Unknown host|Connection refused|timed out|unreachable|402 Payment Required"

.PHONY: all
all: clean build audit test

.PHONY: clean
clean:
	mvn clean

.PHONY: build
build:
	mvn -Dmaven.test.skip -Dossindex.skip=true clean package dependency:copy-dependencies

.PHONY: test
test:
	mvn -Dossindex.skip=true test

.PHONY: audit
audit:
	@echo "🔍 Running OSS Index audit for dp-logging"
	@mkdir -p target
	@mvn ossindex:audit > target/ossindex-audit-dp-logging.log 2>&1; status=$$?; \
	cat target/ossindex-audit-dp-logging.log; \
	[ $$status -eq 0 ] && grep -Eiqn $(OSSINDEX_ERRORS) target/ossindex-audit-dp-logging.log && \
		{ echo "❌ OSS Index API/auth/network error (CMS) — see target/ossindex-audit-dp-logging.log"; exit 1; }; \
	exit $$status

.PHONY: lint
lint:
	exit
