# SPDX-License-Identifier: AGPL-3.0-or-later

ANT_TARGET = $(ANT_ARG_BUILDINFO) deploy publish-local oauth-social-common-jar oauth-social-jar

all: build-ant

include build.mk

install:
	$(call mk_install_dir, lib/ext/zm-oauth-social)
	$(call mk_install_dir, lib/ext-common)
	cp build/zm-oauth-social-$(ZIMBRA_VERSION_MAJOR).*.jar $(INSTALL_DIR)/lib/ext/zm-oauth-social/zmoauthsocial.jar
	cp build/dist/*.jar                                    $(INSTALL_DIR)/lib/ext/zm-oauth-social
	cp build/zm-oauth-social-common*.jar                   $(INSTALL_DIR)/lib/ext-common/zm-oauth-social-common.jar

clean: clean-ant
