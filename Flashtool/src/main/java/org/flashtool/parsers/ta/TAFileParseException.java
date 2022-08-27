package org.flashtool.parsers.ta;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TAFileParseException
extends Exception {
    private static final long serialVersionUID = 1;

    public TAFileParseException(String string) {
        super(string);
    }
}

