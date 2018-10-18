package com.isa.java.mail.commons.service;

import org.apache.commons.mail.Email;

public interface EmailProvider<T extends Email> {

    T newEmail();

    T newEmailWithExplicitSession();
}
